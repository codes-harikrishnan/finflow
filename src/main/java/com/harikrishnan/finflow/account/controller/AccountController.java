package com.harikrishnan.finflow.account.controller;
import com.harikrishnan.finflow.account.dto.AccountRequest;
import com.harikrishnan.finflow.account.dto.AccountResponse;
import com.harikrishnan.finflow.account.dto.UpdateAccountRequest;
import com.harikrishnan.finflow.account.service.AccountService;
import com.harikrishnan.finflow.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final SecurityUtils securityUtils;

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAccounts () {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAllAccounts());
    }


    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount (@Valid @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAccountById(id));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAnAccount (@Valid @RequestBody AccountRequest accountRequest) {
        log.info("Received endpoint request to create an account");
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(accountRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAnAccount(@Valid @PathVariable Long id,@Valid @RequestBody UpdateAccountRequest accountRequest) {
        log.info("Received endpoint request to update an account");
        return ResponseEntity.status(HttpStatus.OK).body(accountService.updateAccountById(id, accountRequest ));
    }

}
