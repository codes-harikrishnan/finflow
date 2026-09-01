package com.harikrishnan.finflow.transaction.service;
import com.harikrishnan.finflow.account.domain.Account;
import com.harikrishnan.finflow.account.repository.AccountRepository;
import com.harikrishnan.finflow.category.domain.Category;
import com.harikrishnan.finflow.category.repository.CategoryRepository;
import com.harikrishnan.finflow.exceptions.ConflictException;
import com.harikrishnan.finflow.exceptions.ResourceNotFoundException;
import com.harikrishnan.finflow.transaction.domain.Transaction;
import com.harikrishnan.finflow.transaction.domain.TransactionType;
import com.harikrishnan.finflow.transaction.dto.GetTransactionRequest;
import com.harikrishnan.finflow.transaction.dto.TransactionRequest;
import com.harikrishnan.finflow.transaction.dto.TransactionResponse;
import com.harikrishnan.finflow.transaction.repository.TransactionRepository;
import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.utils.SecurityUtils;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final CategoryRepository categoryRepository;

    private final SecurityUtils securityUtils;

    private Transaction buildTransaction (TransactionRequest transactionRequest, Account account, Category category, User user) {

        return Transaction.builder()
                .transactionType(transactionRequest.getTransactionType())
                .date(transactionRequest.getTransactionDate() != null ? transactionRequest.getTransactionDate() : LocalDate.now())
                .amount(transactionRequest.getAmount())
                .account(account)
                .category(category)
                .description(transactionRequest.getDescription())
                .toAccountId(transactionRequest.getToAccountId())
                .user(user)
                .build();
    }

    @Transactional
    public TransactionResponse performTransaction (TransactionRequest transactionRequest) {

        User user = securityUtils.getCurrentUser();

        Account account =   accountRepository.findByIdAndUser(transactionRequest.getAccountId(), user).orElseThrow(() -> new ResourceNotFoundException("Account id does not align with the current user"));

        Category category = null;

        if (transactionRequest.getCategoryId() != null) {
            category =  categoryRepository.findById(transactionRequest.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Unable to find the category"));
            if(category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
                throw new ConflictException("Unable to find the category for the specified user");
            }
        }


            if(transactionRequest.getTransactionType() == TransactionType.INCOME) {
                account.credit(transactionRequest.getAmount());
            }

            else if(transactionRequest.getTransactionType() == TransactionType.EXPENSE) {
                account.debit(transactionRequest.getAmount());
            }

            else  {
                if(transactionRequest.getToAccountId() == null) {
                    throw new ConflictException("toAccountId is required for TRANSFER transactions");
                }
                Account toAccount = accountRepository.findByIdAndUser(transactionRequest.getToAccountId(),user).orElseThrow(() -> new ResourceNotFoundException("Unable to find an account to where the amount has to be transffered"));
                account.debit(transactionRequest.getAmount());
                toAccount.credit(transactionRequest.getAmount());
            }

            Transaction requestingTransaction = buildTransaction(transactionRequest,account,category,user);
            Transaction transaction =  transactionRepository.save(requestingTransaction);

      return TransactionResponse.builder()
              .id(transaction.getId())
              .transactionType(transaction.getTransactionType())
              .accountId(transaction.getAccount().getId())
              .amount(transaction.getAmount())
              .categoryId(category != null ? transaction.getCategory().getId() : null)
              .date(transaction.getDate())
              .description(transaction.getDescription())
              .toAccountId(transaction.getToAccountId())
              .build();

    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactions (Pageable pageable, GetTransactionRequest transactionRequest) {
      log.info("Get all transactions of the page: {}", pageable.getPageNumber());

        User user = securityUtils.getCurrentUser();
        Specification specification = (root,query,cb) -> cb.equal(root.get("user"),user);

        if(transactionRequest.getAccountId() != null) {
            log.info("Account id: {}" , transactionRequest.getAccountId());
            specification =  specification.and((root,query,cb) -> cb.equal(root.get("account").get("id"),transactionRequest.getAccountId()));
        }

        if(transactionRequest.getCategoryId() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category").get("id"), transactionRequest.getCategoryId()));
        }

        if (transactionRequest.getFromDate() != null && transactionRequest.getToDate() != null && transactionRequest.getFromDate().isAfter(transactionRequest.getToDate())) {
            throw new ConflictException("Transaction request from date should not be after to date");
        }

        if ( transactionRequest.getFromDate() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("date"), transactionRequest.getFromDate()));
        }

        if(transactionRequest.getToDate() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("date"), transactionRequest.getToDate()));
        }


      Page<Transaction> transactions = transactionRepository.findAll(specification,pageable);

      List<TransactionResponse> transactionResponses =  transactions.getContent().stream().map(transaction -> {
            return TransactionResponse.builder()
                    .id(transaction.getId())
                    .transactionType(transaction.getTransactionType())
                    .accountId(transaction.getAccount().getId())
                    .amount(transaction.getAmount())
                    .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null )
                    .date(transaction.getDate())
                    .description(transaction.getDescription())
                    .toAccountId(transaction.getToAccountId())
                    .build();
        }).toList();
      return new  PageImpl<>(transactionResponses,pageable,transactions.getTotalElements());

    }

    @Transactional
    public void deleteTransaction (Long id) {

        User currentUser = securityUtils.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndUser(id,currentUser).orElseThrow(() -> new ResourceNotFoundException("Unable to find transaction with id: "+ id));

        if(transaction.getAccount() == null) {
            throw new ResourceNotFoundException("Unable to find the account for reverting the transaction");
        }

        if(transaction.getTransactionType() == TransactionType.INCOME) {
            transaction.getAccount().debit(transaction.getAmount());
        }
        else if(transaction.getTransactionType() == TransactionType.EXPENSE) {
            transaction.getAccount().credit(transaction.getAmount());
        }
        else {
            Account toAccount= accountRepository.findByIdAndUser(transaction.getToAccountId(),currentUser).orElseThrow(( ) -> new ResourceNotFoundException("Unable to find the account from where the amount has to be transffered back"));
            toAccount.debit(transaction.getAmount());
            transaction.getAccount().credit(transaction.getAmount());
        }

        transactionRepository.delete(transaction);
    }

}
