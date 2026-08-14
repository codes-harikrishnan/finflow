package com.harikrishnan.finflow.account.service;
import com.harikrishnan.finflow.account.domain.Account;
import com.harikrishnan.finflow.account.domain.AccountType;
import com.harikrishnan.finflow.account.domain.Currency;
import com.harikrishnan.finflow.account.dto.AccountRequest;
import com.harikrishnan.finflow.account.dto.AccountResponse;
import com.harikrishnan.finflow.account.dto.UpdateAccountRequest;
import com.harikrishnan.finflow.account.repository.AccountRepository;
import com.harikrishnan.finflow.exceptions.ConflictException;
import com.harikrishnan.finflow.exceptions.ResourceNotFoundException;
import com.harikrishnan.finflow.user.domain.Role;
import com.harikrishnan.finflow.user.domain.User;
import com.harikrishnan.finflow.utils.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private  AccountRepository accountRepository;

    @Mock
    private  SecurityUtils securityUtils;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_WithValidRequest_ReturnCreatedAccountDto () {

        User thisUser = User.builder()
                .emailId("test@test.com")
                .password("abc123")
                .role(Role.USER)
                .build();

        when(securityUtils.getCurrentUser()).thenReturn(thisUser);
        AccountRequest newAccountRequest = AccountRequest.builder()
                .name("abc")
                .balance(BigDecimal.valueOf(1000.0))
                .currency(Currency.EUR)
                .type(AccountType.SAVINGS)
                .build();

        Account account = Account.builder()
                .user(thisUser)
                .name("abc")
                .balance(BigDecimal.valueOf(1000.0))
                .currency(Currency.EUR)
                .type(AccountType.SAVINGS)
                .build();

        when(accountRepository.save(any(Account.class))).thenReturn(account);

     AccountResponse accountResponse =   accountService.createAccount(newAccountRequest);
     assertThat(accountResponse.getName()).isEqualTo("abc");
     assertThat(accountResponse.getBalance()).isEqualTo(BigDecimal.valueOf(1000.0));
     assertThat(accountResponse.getCurrency()).isEqualTo(Currency.EUR);
     assertThat(accountResponse.getType()).isEqualTo(AccountType.SAVINGS);
     verify(accountRepository).save(any(Account.class));

    }

    @Test
    void createAccount_WithAnAlreadyExistingNameForTheUser_shouldThrowConflictException () {
        User thisUser = User.builder()
                .emailId("test@test.com")
                .password("abc123")
                .role(Role.USER)
                .build();

        AccountRequest newAccountRequest = AccountRequest.builder()
                .name("abc")
                .balance(BigDecimal.valueOf(1000.0))
                .currency(Currency.EUR)
                .type(AccountType.SAVINGS)
                .build();

        when(securityUtils.getCurrentUser()).thenReturn(thisUser);
        when(accountRepository.existsByNameAndUser(any(String.class),any(User.class))).thenReturn(true);
        assertThatThrownBy(() -> accountService.createAccount(newAccountRequest)).isInstanceOf(ConflictException.class);
    }

    @Test
    void createAccount_ShouldAssignCurrentUserToAccount () {
        User thisUser = User.builder()
                .emailId("test@test.com")
                .password("abc123")
                .role(Role.USER)
                .build();

        when(securityUtils.getCurrentUser()).thenReturn(thisUser);
        AccountRequest newAccountRequest = AccountRequest.builder()
                .name("abc")
                .balance(BigDecimal.valueOf(1000.0))
                .currency(Currency.EUR)
                .type(AccountType.SAVINGS)
                .build();

        Account account = Account.builder()
                .user(thisUser)
                .name("abc")
                .balance(BigDecimal.valueOf(1000.0))
                .currency(Currency.EUR)
                .type(AccountType.SAVINGS)
                .build();

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponse accountResponse =   accountService.createAccount(newAccountRequest);
        verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(thisUser);

    }

    @Test
    void getAllAccounts_ShouldReturnOnlyCurrentUserAccounts() {
        User thisUser = User.builder()
                .emailId("test@test.com")
                .password("abc123")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(thisUser,"id",1L);
        when(securityUtils.getCurrentUser()).thenReturn(thisUser);

        Account account = Account.builder()
                .name("abc")
                .balance(BigDecimal.valueOf(1000.0))
                .currency(Currency.EUR)
                .type(AccountType.SAVINGS)
                .build();

        when(accountRepository.findAllByUser(any(User.class))).thenReturn(List.of(account));

        List<AccountResponse> accountResponses =  accountService.getAllAccounts();
        assertThat(accountResponses.size()).isEqualTo(1);
        assertThat(accountResponses.getFirst().getUserId()).isEqualTo(1L);
        assertThat(accountResponses.getFirst().getName()).isEqualTo("abc");
        assertThat(accountResponses.getFirst().getType()).isEqualTo(AccountType.SAVINGS);
        assertThat(accountResponses.getFirst().getCurrency()).isEqualTo(Currency.EUR);
    }

    @Test
    void getAccountById_WhenExists_ShouldReturnAccountResponse () {
        User thisUser = User.builder()
                .emailId("test@test.com")
                .password("abc123")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(thisUser,"id",1L);
        when(securityUtils.getCurrentUser()).thenReturn(thisUser);

        Account account = Account.builder()
                .name("abc")
                .balance(BigDecimal.valueOf(1000.0))
                .currency(Currency.EUR)
                .type(AccountType.SAVINGS)
                .build();

        ReflectionTestUtils.setField(account,"id",2L);

        when(accountRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.of(account));
       AccountResponse accountResponse =  accountService.getAccountById(1L);
        assertThat(accountResponse.getId()).isEqualTo(2L);
        assertThat(accountResponse.getUserId()).isEqualTo(1L);
        assertThat(accountResponse.getName()).isEqualTo("abc");
        assertThat(accountResponse.getType()).isEqualTo(AccountType.SAVINGS);
        assertThat(accountResponse.getCurrency()).isEqualTo(Currency.EUR);
    }

    @Test
    void getAccountById_WhenNotFound_ShouldThrowResourceNotFoundException() {
        User thisUser = User.builder()
                .emailId("test@test.com")
                .password("abc123")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(thisUser,"id",1L);
        when(securityUtils.getCurrentUser()).thenReturn(thisUser);
        when(accountRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById(1L)).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void getAccountById_WhenNotOwnedByUser_ShouldThrowResourceNotFoundException () {
        User thisUser = User.builder()
                .emailId("test@test.com")
                .password("abc123")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(thisUser,"id",1L);
        when(securityUtils.getCurrentUser()).thenReturn(thisUser);
        when(accountRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accountService.getAccountById(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateAccountById_WithValidRequest_ShouldUpdateName() {
        UpdateAccountRequest accountRequest = UpdateAccountRequest.builder()
                .name("xyz")
                .build();

        User thisUser = User.builder()
                .emailId("test@test.com")
                .password("abc123")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(thisUser,"id",1L);

        Account account = Account.builder()
                .name("abc")
                .balance(BigDecimal.valueOf(1000.0))
                .currency(Currency.EUR)
                .type(AccountType.SAVINGS)
                .build();
        ReflectionTestUtils.setField(account,"id",2L);

        when(securityUtils.getCurrentUser()).thenReturn(thisUser);
        when(accountRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.of(account));
        AccountResponse accountResponse = accountService.updateAccountById(2L,accountRequest);
        assertThat(accountResponse.getId()).isEqualTo(2L);
        assertThat(accountResponse.getUserId()).isEqualTo(1L);
        assertThat(accountResponse.getName()).isEqualTo("xyz");

    }

    @Test
    void updateAccountById_WhenNotFound_ShouldThrowResourceNotFoundException() {
        UpdateAccountRequest accountRequest = UpdateAccountRequest.builder()
                .name("xyz")
                .build();

        User thisUser = User.builder()
                .emailId("test@test.com")
                .password("abc123")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(thisUser,"id",1L);
        when(securityUtils.getCurrentUser()).thenReturn(thisUser);
        when(accountRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accountService.updateAccountById(2L, accountRequest)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateAccountById_WhenNotOwnedByUser_ShouldThrowResourceNotFoundException () {
        UpdateAccountRequest accountRequest = UpdateAccountRequest.builder()
                .name("xyz")
                .build();
        User thisUser = User.builder()
                .emailId("test@test.com")
                .password("abc123")
                .role(Role.USER)
                .build();
        ReflectionTestUtils.setField(thisUser,"id",1L);
        when(securityUtils.getCurrentUser()).thenReturn(thisUser);
        when(accountRepository.findByIdAndUser(any(Long.class),any(User.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> accountService.updateAccountById(2L, accountRequest)).isInstanceOf(ResourceNotFoundException.class);
    }
}
