package com.harikrishnan.finflow.transaction.service;

import com.harikrishnan.finflow.account.domain.Account;
import com.harikrishnan.finflow.account.domain.AccountType;
import com.harikrishnan.finflow.account.domain.Currency;
import com.harikrishnan.finflow.account.repository.AccountRepository;
import com.harikrishnan.finflow.budget.domain.Budget;
import com.harikrishnan.finflow.budget.domain.BudgetStatus;
import com.harikrishnan.finflow.budget.repository.BudgetRepository;
import com.harikrishnan.finflow.category.domain.Category;
import com.harikrishnan.finflow.category.domain.CategoryType;
import com.harikrishnan.finflow.category.repository.CategoryRepository;
import com.harikrishnan.finflow.exceptions.ConflictException;
import com.harikrishnan.finflow.exceptions.InsufficientFundsException;
import com.harikrishnan.finflow.exceptions.ResourceNotFoundException;
import com.harikrishnan.finflow.transaction.domain.Transaction;
import com.harikrishnan.finflow.transaction.domain.TransactionType;
import com.harikrishnan.finflow.transaction.dto.GetTransactionRequest;
import com.harikrishnan.finflow.transaction.dto.TransactionRequest;
import com.harikrishnan.finflow.transaction.dto.TransactionResponse;
import com.harikrishnan.finflow.transaction.repository.TransactionRepository;
import com.harikrishnan.finflow.user.domain.Role;
import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.utils.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private TransactionService transactionService;


    @BeforeEach
    void setup () {
        User thisUser = User.builder()
                .emailId("test@test.com")
                .password("abc123")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(thisUser,"id",1L);
        when(securityUtils.getCurrentUser()).thenReturn(thisUser);
    }

    @Test
    void performTransaction_WithIncome_ShouldCreditAccountAndReturnResponse () {
      TransactionRequest transactionRequest =   TransactionRequest.builder()
                .transactionType(TransactionType.INCOME)
                .accountId(1L)
                .amount(BigDecimal.valueOf(100))
                .categoryId(1L)
                .transactionDate(LocalDate.parse("2026-08-21"))
                .description("abcd")
                .toAccountId(null)
                .build();



        Account account = Account.builder()
                .user(securityUtils.getCurrentUser())
                .name("ABC")
                .type(AccountType.SAVINGS)
                .currency(Currency.EUR)
                .balance(BigDecimal.valueOf(100))
                .build();

        ReflectionTestUtils.setField(account,"id",1L);

        Category category = Category.builder()
                .type(CategoryType.INCOME)
                .user(securityUtils.getCurrentUser())
                .name("CAT1")
                .build();
        ReflectionTestUtils.setField(category,"id",1L);

        Transaction transaction =   Transaction.builder()
                .transactionType(TransactionType.INCOME)
                .account(account)
                .amount(BigDecimal.valueOf(100))
                .category(category)
                .date(LocalDate.parse("2026-08-21"))
                .description("abcd")
                .toAccountId(null)
                .build();
        ReflectionTestUtils.setField(transaction,"id",1L);

      when(accountRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.of(account));
      when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
      when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

      TransactionResponse transactionResponse = transactionService.performTransaction(transactionRequest);
      assertThat(transactionResponse.getId()).isEqualTo(1L);
        assertThat(transactionResponse.getAccountId()).isEqualTo(1L);
        assertThat(transactionResponse.getCategoryId()).isEqualTo(1L);
        assertThat(transactionResponse.getDate()).isEqualTo(LocalDate.parse("2026-08-21"));
        assertThat(transactionResponse.getDescription()).isEqualTo("abcd");
        assertThat(transactionResponse.getToAccountId()).isEqualTo(null);
        System.out.println("account balance:"+ account.getBalance());
        assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(200));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void performTransaction_WithExpense_ShouldDebitAccountAndReturnResponse () {
        TransactionRequest transactionRequest =   TransactionRequest.builder()
                .transactionType(TransactionType.EXPENSE)
                .accountId(1L)
                .amount(BigDecimal.valueOf(100))
                .categoryId(1L)
                .transactionDate(LocalDate.parse("2026-08-21"))
                .description("abcd")
                .toAccountId(null)
                .build();



        Account account = Account.builder()
                .user(securityUtils.getCurrentUser())
                .name("ABC")
                .type(AccountType.SAVINGS)
                .currency(Currency.EUR)
                .balance(BigDecimal.valueOf(100))
                .build();

        ReflectionTestUtils.setField(account,"id",1L);

        Category category = Category.builder()
                .type(CategoryType.EXPENSE)
                .user(securityUtils.getCurrentUser())
                .name("CAT1")
                .build();
        ReflectionTestUtils.setField(category,"id",1L);

        Transaction transaction =   Transaction.builder()
                .transactionType(TransactionType.EXPENSE)
                .account(account)
                .amount(BigDecimal.valueOf(100))
                .category(category)
                .date(LocalDate.parse("2026-08-21"))
                .description("abcd")
                .toAccountId(null)
                .build();
        ReflectionTestUtils.setField(transaction,"id",1L);

        when(accountRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.of(account));
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponse transactionResponse = transactionService.performTransaction(transactionRequest);
        assertThat(transactionResponse.getId()).isEqualTo(1L);
        assertThat(transactionResponse.getAccountId()).isEqualTo(1L);
        assertThat(transactionResponse.getCategoryId()).isEqualTo(1L);
        assertThat(transactionResponse.getDate()).isEqualTo(LocalDate.parse("2026-08-21"));
        assertThat(transactionResponse.getDescription()).isEqualTo("abcd");
        assertThat(transactionResponse.getToAccountId()).isEqualTo(null);
        System.out.println("account balance:"+ account.getBalance());
        assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(0));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
void performTransaction_WithExpense_InsufficientFunds_ShouldThrowInsufficientFundsException () {
        TransactionRequest transactionRequest = TransactionRequest.builder()
                .transactionType(TransactionType.EXPENSE)
                .accountId(1L)
                .amount(BigDecimal.valueOf(100))
                .categoryId(1L)
                .transactionDate(LocalDate.parse("2026-08-21"))
                .description("abcd")
                .toAccountId(null)
                .build();


        Account account = Account.builder()
                .user(securityUtils.getCurrentUser())
                .name("ABC")
                .type(AccountType.SAVINGS)
                .currency(Currency.EUR)
                .balance(BigDecimal.valueOf(0))
                .build();

        ReflectionTestUtils.setField(account, "id", 1L);

        Category category = Category.builder()
                .type(CategoryType.EXPENSE)
                .user(securityUtils.getCurrentUser())
                .name("CAT1")
                .build();
        ReflectionTestUtils.setField(category, "id", 1L);


        when(accountRepository.findByIdAndUser(any(Long.class), any(User.class))).thenReturn(Optional.of(account));
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> transactionService.performTransaction(transactionRequest)).isInstanceOf(InsufficientFundsException.class);
}

@Test
void performTransaction_WithTransfer_ShouldDebitSourceAndCreditDestination () {
    TransactionRequest transactionRequest =   TransactionRequest.builder()
            .transactionType(TransactionType.TRANSFER)
            .accountId(1L)
            .amount(BigDecimal.valueOf(100))
            .categoryId(1L)
            .transactionDate(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(2L)
            .build();



    Account account = Account.builder()
            .user(securityUtils.getCurrentUser())
            .name("ABC")
            .type(AccountType.SAVINGS)
            .currency(Currency.EUR)
            .balance(BigDecimal.valueOf(100))
            .build();
    ReflectionTestUtils.setField(account,"id",1L);

    Account toAccount = Account.builder()
            .user(securityUtils.getCurrentUser())
            .name("ABC")
            .type(AccountType.SAVINGS)
            .currency(Currency.EUR)
            .balance(BigDecimal.valueOf(0))
            .build();

    ReflectionTestUtils.setField(toAccount,"id",2L);

    Category category = Category.builder()
            .type(CategoryType.EXPENSE)
            .user(securityUtils.getCurrentUser())
            .name("CAT1")
            .build();

    ReflectionTestUtils.setField(category,"id",1L);

    Transaction transaction =   Transaction.builder()
            .transactionType(TransactionType.TRANSFER)
            .account(account)
            .amount(BigDecimal.valueOf(100))
            .category(category)
            .date(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(2L)
            .build();
    ReflectionTestUtils.setField(transaction,"id",1L);

    when(accountRepository.findByIdAndUser(eq(1L),any(User.class))).thenReturn(Optional.of(account));
    when(accountRepository.findByIdAndUser(eq(2L),any(User.class))).thenReturn(Optional.of(toAccount));
    when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

    TransactionResponse transactionResponse = transactionService.performTransaction(transactionRequest);
    assertThat(transactionResponse.getId()).isEqualTo(1L);
    assertThat(transactionResponse.getAccountId()).isEqualTo(1L);
    assertThat(transactionResponse.getCategoryId()).isEqualTo(1L);
    assertThat(transactionResponse.getDate()).isEqualTo(LocalDate.parse("2026-08-21"));
    assertThat(transactionResponse.getDescription()).isEqualTo("abcd");
    assertThat(transactionResponse.getToAccountId()).isEqualTo(2L);
    assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(0));
    assertThat(toAccount.getBalance()).isEqualTo(BigDecimal.valueOf(100));
    verify(transactionRepository).save(any(Transaction.class));
}

@Test
void performTransaction_WithTransfer_MissingToAccountId_ShouldThrowConflictException () {
    TransactionRequest transactionRequest =   TransactionRequest.builder()
            .transactionType(TransactionType.TRANSFER)
            .accountId(null)
            .amount(BigDecimal.valueOf(100))
            .categoryId(1L)
            .transactionDate(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(null)
            .build();

    Account account = Account.builder()
            .user(securityUtils.getCurrentUser())
            .name("ABC")
            .type(AccountType.SAVINGS)
            .currency(Currency.EUR)
            .balance(BigDecimal.valueOf(100))
            .build();
    ReflectionTestUtils.setField(account,"id",1L);

    Category category = Category.builder()
            .type(CategoryType.EXPENSE)
            .user(securityUtils.getCurrentUser())
            .name("CAT1")
            .build();

    ReflectionTestUtils.setField(category,"id",1L);
    when(accountRepository.findByIdAndUser(any(),any(User.class))).thenReturn(Optional.of(account));
    when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
    assertThatThrownBy(() -> transactionService.performTransaction(transactionRequest)).isInstanceOf(ConflictException.class);

}

@Test
void performTransaction_WithInvalidCategory_ShouldThrowConflictException () {
    TransactionRequest transactionRequest =   TransactionRequest.builder()
            .transactionType(TransactionType.TRANSFER)
            .accountId(1L)
            .amount(BigDecimal.valueOf(100))
            .categoryId(1L)
            .transactionDate(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(null)
            .build();

    Account account = Account.builder()
            .user(securityUtils.getCurrentUser())
            .name("ABC")
            .type(AccountType.SAVINGS)
            .currency(Currency.EUR)
            .balance(BigDecimal.valueOf(100))
            .build();

    User differentUser = User.builder()
            .role(Role.USER)
            .emailId("abcd@gmaail.com")
            .password("ascasdasd")
            .build();
ReflectionTestUtils.setField(differentUser,"id", 2L);

    Category category = Category.builder()
            .type(CategoryType.EXPENSE)
            .user(differentUser)
            .name("CAT1")
            .build();

    when(accountRepository.findByIdAndUser(any(),any(User.class))).thenReturn(Optional.of(account));
    when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
    assertThatThrownBy(() -> transactionService.performTransaction(transactionRequest)).isInstanceOf(ConflictException.class);
}

@Test
void   performTransaction_WithCategoryNotOwnedByUser_ShouldThrowConflictException () {
    TransactionRequest transactionRequest =   TransactionRequest.builder()
            .transactionType(TransactionType.TRANSFER)
            .accountId(1L)
            .amount(BigDecimal.valueOf(100))
            .categoryId(1L)
            .transactionDate(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(null)
            .build();

    Account account = Account.builder()
            .user(securityUtils.getCurrentUser())
            .name("ABC")
            .type(AccountType.SAVINGS)
            .currency(Currency.EUR)
            .balance(BigDecimal.valueOf(100))
            .build();

    User differentUser = User.builder()
            .role(Role.USER)
            .emailId("abcd@gmaail.com")
            .password("ascasdasd")
            .build();
    ReflectionTestUtils.setField(differentUser,"id", 2L);

    Category category = Category.builder()
            .type(CategoryType.EXPENSE)
            .user(differentUser)
            .name("CAT1")
            .build();

    when(accountRepository.findByIdAndUser(any(),any(User.class))).thenReturn(Optional.of(account));
    when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
    assertThatThrownBy(() -> transactionService.performTransaction(transactionRequest)).isInstanceOf(ConflictException.class);
}

@Test
void  performTransaction_WithAccountNotOwnedByUser_ShouldThrowResourceNotFoundException () {
    TransactionRequest transactionRequest =   TransactionRequest.builder()
            .transactionType(TransactionType.TRANSFER)
            .accountId(1L)
            .amount(BigDecimal.valueOf(100))
            .categoryId(1L)
            .transactionDate(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(2L)
            .build();

    User differentUser = User.builder()
            .role(Role.USER)
            .emailId("abcd@gmaail.com")
            .password("ascasdasd")
            .build();
    ReflectionTestUtils.setField(differentUser,"id", 2L);

    Account account = Account.builder()
            .user(differentUser)
            .name("ABC")
            .type(AccountType.SAVINGS)
            .currency(Currency.EUR)
            .balance(BigDecimal.valueOf(100))
            .build();
    when(accountRepository.findByIdAndUser(any(),any(User.class))).thenReturn(Optional.of(account));

    assertThatThrownBy(() -> transactionService.performTransaction(transactionRequest)).isInstanceOf(ResourceNotFoundException.class);
}


@Test
    void  performTransaction_WithExpenseAndMatchingBudget_ShouldCallRecordSpend () {
    TransactionRequest transactionRequest =   TransactionRequest.builder()
            .transactionType(TransactionType.EXPENSE)
            .accountId(1L)
            .amount(BigDecimal.valueOf(100))
            .categoryId(1L)
            .transactionDate(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(null)
            .build();

    Account account = Account.builder()
            .user(securityUtils.getCurrentUser())
            .name("ABC")
            .type(AccountType.SAVINGS)
            .currency(Currency.EUR)
            .balance(BigDecimal.valueOf(100))
            .build();

    ReflectionTestUtils.setField(account,"id",1L);

    Category category = Category.builder()
            .type(CategoryType.EXPENSE)
            .user(securityUtils.getCurrentUser())
            .name("CAT1")
            .build();
    ReflectionTestUtils.setField(category,"id",1L);

    Budget budget = Budget.builder()
            .year(2026)
            .month(8)
            .budgetStatus(BudgetStatus.ACTIVE)
            .category(category)
            .limitAmount(BigDecimal.valueOf(500))
            .spentAmount(BigDecimal.valueOf(100))
            .build();

    Transaction transaction =   Transaction.builder()
            .transactionType(TransactionType.TRANSFER)
            .account(account)
            .amount(BigDecimal.valueOf(100))
            .category(category)
            .date(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(2L)
            .build();
    ReflectionTestUtils.setField(transaction,"id",1L);


    when(accountRepository.findByIdAndUser(any(),any(User.class))).thenReturn(Optional.of(account));
    when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
    when(budgetRepository.findByUserAndCategoryAndMonthAndYear(any(User.class),any(Category.class),any(Integer.class),any(Integer.class))).thenReturn(Optional.of(budget));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

    transactionService.performTransaction(transactionRequest);
    verify(budgetRepository).findByUserAndCategoryAndMonthAndYear(any(),any(),any(),any());
    assertThat(budget.getSpentAmount()).isEqualTo(BigDecimal.valueOf(200));
    }

    @Test
    void performTransaction_WithExpenseAndNoMatchingBudget_ShouldNotCallRecordSpend () {
        TransactionRequest transactionRequest =   TransactionRequest.builder()
                .transactionType(TransactionType.EXPENSE)
                .accountId(1L)
                .amount(BigDecimal.valueOf(100))
                .categoryId(1L)
                .transactionDate(LocalDate.parse("2026-08-21"))
                .description("abcd")
                .toAccountId(null)
                .build();

        Account account = Account.builder()
                .user(securityUtils.getCurrentUser())
                .name("ABC")
                .type(AccountType.SAVINGS)
                .currency(Currency.EUR)
                .balance(BigDecimal.valueOf(100))
                .build();

        ReflectionTestUtils.setField(account,"id",1L);

        Category category = Category.builder()
                .type(CategoryType.EXPENSE)
                .user(securityUtils.getCurrentUser())
                .name("CAT1")
                .build();
        ReflectionTestUtils.setField(category,"id",1L);

        Transaction transaction =   Transaction.builder()
                .transactionType(TransactionType.TRANSFER)
                .account(account)
                .amount(BigDecimal.valueOf(100))
                .category(category)
                .date(LocalDate.parse("2026-08-21"))
                .description("abcd")
                .toAccountId(2L)
                .build();
        ReflectionTestUtils.setField(transaction,"id",1L);

        when(accountRepository.findByIdAndUser(any(),any(User.class))).thenReturn(Optional.of(account));
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
        when(budgetRepository.findByUserAndCategoryAndMonthAndYear(any(User.class),any(Category.class),any(Integer.class),any(Integer.class))).thenReturn(Optional.empty());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        transactionService.performTransaction(transactionRequest);

        verify(budgetRepository).findByUserAndCategoryAndMonthAndYear(any(),any(),any(),any());

        assertThat(account.getBalance()).isEqualTo(BigDecimal.ZERO);

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void performTransaction_WithExpenseAndNullCategory_ShouldNotCheckBudget () {
        TransactionRequest transactionRequest =   TransactionRequest.builder()
                .transactionType(TransactionType.EXPENSE)
                .accountId(1L)
                .amount(BigDecimal.valueOf(100))
                .categoryId(null)
                .transactionDate(LocalDate.parse("2026-08-21"))
                .description("abcd")
                .toAccountId(null)
                .build();

        Account account = Account.builder()
                .user(securityUtils.getCurrentUser())
                .name("ABC")
                .type(AccountType.SAVINGS)
                .currency(Currency.EUR)
                .balance(BigDecimal.valueOf(100))
                .build();

        ReflectionTestUtils.setField(account,"id",1L);

        Transaction transaction =   Transaction.builder()
                .transactionType(TransactionType.TRANSFER)
                .account(account)
                .amount(BigDecimal.valueOf(100))
                .category(null)
                .date(LocalDate.parse("2026-08-21"))
                .description("abcd")
                .toAccountId(2L)
                .build();
        ReflectionTestUtils.setField(transaction,"id",1L);


        verify(budgetRepository, never()).findByUserAndCategoryAndMonthAndYear(any(),any(),any(),any());
    }

@Test
void  deleteTransaction_WithIncome_ShouldReverseDebitAndDelete () {

    Account account = Account.builder()
            .user(securityUtils.getCurrentUser())
            .name("ABC")
            .type(AccountType.SAVINGS)
            .currency(Currency.EUR)
            .balance(BigDecimal.valueOf(100))
            .build();
    ReflectionTestUtils.setField(account,"id",1L);

    Category category = Category.builder()
            .type(CategoryType.INCOME)
            .user(securityUtils.getCurrentUser())
            .name("CAT1")
            .build();
ReflectionTestUtils.setField(category,"id",1L);

    Transaction transaction =   Transaction.builder()
            .transactionType(TransactionType.INCOME)
            .account(account)
            .amount(BigDecimal.valueOf(100))
            .category(category)
            .date(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(null)
            .build();
    ReflectionTestUtils.setField(transaction,"id",1L);

    when(transactionRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.of(transaction));
    transactionService.deleteTransaction(1L);
    assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(0));
    verify(transactionRepository).delete(any(Transaction.class));

}

@Test
void deleteTransaction_WithExpense_ShouldReverseCreditAndDelete () {
    Account account = Account.builder()
            .user(securityUtils.getCurrentUser())
            .name("ABC")
            .type(AccountType.SAVINGS)
            .currency(Currency.EUR)
            .balance(BigDecimal.valueOf(100))
            .build();
    ReflectionTestUtils.setField(account,"id",1L);

    Category category = Category.builder()
            .type(CategoryType.EXPENSE)
            .user(securityUtils.getCurrentUser())
            .name("CAT1")
            .build();
    ReflectionTestUtils.setField(category,"id",1L);

    Transaction transaction =   Transaction.builder()
            .transactionType(TransactionType.EXPENSE)
            .account(account)
            .amount(BigDecimal.valueOf(100))
            .category(category)
            .date(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(null)
            .build();
    ReflectionTestUtils.setField(transaction,"id",1L);

    when(transactionRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.of(transaction));

    transactionService.deleteTransaction(1L);
    assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(200));
    verify(transactionRepository).delete(any(Transaction.class));
}

@Test
void deleteTransaction_WhenNotFound_ShouldThrowResourceNotFoundException () {
        when(transactionRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> transactionService.deleteTransaction(1L)).isInstanceOf(ResourceNotFoundException.class);
}

@Test
void getTransactions_ShouldReturnOnlyCurrentUserTransactions () {
        User differentUser = User.builder()
                .emailId("qwert@gmail.com")
                .password("zxcvb")
                .role(Role.USER)
                .build();

    Account account = Account.builder()
            .user(securityUtils.getCurrentUser())
            .name("ABC")
            .type(AccountType.SAVINGS)
            .currency(Currency.EUR)
            .balance(BigDecimal.valueOf(100))
            .build();
    ReflectionTestUtils.setField(account,"id",1L);

    Account account2 = Account.builder()
            .user(differentUser)
            .name("ABC")
            .type(AccountType.SAVINGS)
            .currency(Currency.EUR)
            .balance(BigDecimal.valueOf(100))
            .build();
    ReflectionTestUtils.setField(account2,"id",2L);

    Category category = Category.builder()
            .type(CategoryType.EXPENSE)
            .user(securityUtils.getCurrentUser())
            .name("CAT1")
            .build();
    ReflectionTestUtils.setField(category,"id",1L);

    Transaction transaction1 =   Transaction.builder()
            .transactionType(TransactionType.EXPENSE)
            .account(account)
            .amount(BigDecimal.valueOf(100))
            .category(category)
            .date(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(null)
            .build();
    ReflectionTestUtils.setField(transaction1,"id",1L);

    Transaction transaction2 =   Transaction.builder()
            .transactionType(TransactionType.EXPENSE)
            .account(account2)
            .amount(BigDecimal.valueOf(100))
            .category(category)
            .date(LocalDate.parse("2026-08-21"))
            .description("abcd")
            .toAccountId(null)
            .build();

    ReflectionTestUtils.setField(transaction2,"id",2L);

    Pageable pageable = PageRequest.of(1,10);

    when(transactionRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(new PageImpl<>(List.of(transaction1),pageable,1));


    GetTransactionRequest transactionRequest = GetTransactionRequest.builder()
            .build();

   Page<TransactionResponse> transactionResponsePage =  transactionService.getTransactions(pageable,transactionRequest);
    List<TransactionResponse> transactionResponses = transactionResponsePage.getContent();

    assertThat(transactionResponses.size()).isEqualTo(1);
    assertThat(transactionResponses.getFirst().getId()).isEqualTo(1L);
    assertThat(transactionResponses.getFirst().getAccountId()).isEqualTo(account.getId());
}

}
