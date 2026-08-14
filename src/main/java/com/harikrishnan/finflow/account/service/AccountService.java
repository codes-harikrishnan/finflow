package com.harikrishnan.finflow.account.service;

import com.harikrishnan.finflow.account.domain.Account;
import com.harikrishnan.finflow.account.dto.AccountRequest;
import com.harikrishnan.finflow.account.dto.AccountResponse;
import com.harikrishnan.finflow.account.dto.UpdateAccountRequest;
import com.harikrishnan.finflow.account.repository.AccountRepository;
import com.harikrishnan.finflow.exceptions.ConflictException;
import com.harikrishnan.finflow.exceptions.ResourceNotFoundException;
import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    private final SecurityUtils securityUtils;

    @Transactional
    public AccountResponse createAccount (AccountRequest accountRequest) {
        log.info("Processing request to create an account");
       if(accountRepository.existsByNameAndUser(accountRequest.getName(), securityUtils.getCurrentUser())) {
           throw new ConflictException("An account already exists with the same name for the user");
       }

        Account newAccount = Account.builder()
                .name(accountRequest.getName())
                .balance(accountRequest.getBalance())
                .currency(accountRequest.getCurrency())
                .type(accountRequest.getType())
                .user(securityUtils.getCurrentUser())
                .build();
        log.info("Saving new account");
        Account account =   accountRepository.save(newAccount);
        log.info("Returning created account information with id: {}", account.getId());
          return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .type(account.getType())
                .build();
    }

    public List<AccountResponse> getAllAccounts () {

        User currentUser = securityUtils.getCurrentUser();
        log.info("Processing request to get all accounts");
        return accountRepository.findAllByUser(currentUser).stream().map(account -> {
            return AccountResponse.builder()
                    .id(account.getId())
                    .name(account.getName())
                    .balance(account.getBalance())
                    .currency(account.getCurrency())
                    .type(account.getType())
                    .userId(currentUser.getId())
                    .build();
        }).toList();
    }


    @Cacheable(key = "#id", value = "accounts")
    public AccountResponse getAccountById (Long id) {
        log.info("Processing request to get an account with Id: {}", id);

        User currentUser = securityUtils.getCurrentUser();

        Account account = accountRepository.findByIdAndUser(id,currentUser).orElseThrow(() -> new ResourceNotFoundException("Unable to find account with an id:" + id));

            return AccountResponse.builder()
                    .id(account.getId())
                    .name(account.getName())
                    .balance(account.getBalance())
                    .currency(account.getCurrency())
                    .type(account.getType())
                    .userId(currentUser.getId())
                    .build();
    }


    @CacheEvict(key = "#id", value = "accounts")
    @Transactional
    public AccountResponse updateAccountById (Long id, UpdateAccountRequest accountRequest) {
        log.info("Processing request to update an account with Id: {}", id);
        User currentUser = securityUtils.getCurrentUser();
        Account account = accountRepository.findByIdAndUser(id,currentUser).orElseThrow(() -> new ResourceNotFoundException("Unable to find account with an id:" + id));
        account.updateName(accountRequest.getName());
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .type(account.getType())
                .userId(currentUser.getId())
                .build();
    }

}
