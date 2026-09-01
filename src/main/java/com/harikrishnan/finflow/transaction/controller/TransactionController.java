package com.harikrishnan.finflow.transaction.controller;

import com.harikrishnan.finflow.transaction.dto.GetTransactionRequest;
import com.harikrishnan.finflow.transaction.dto.TransactionRequest;
import com.harikrishnan.finflow.transaction.dto.TransactionResponse;
import com.harikrishnan.finflow.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> performTransaction (@Valid @RequestBody TransactionRequest transactionRequest) {
        log.info("Received endpoint request to perform transaction");
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.performTransaction(transactionRequest));
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions (@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int limit, @RequestParam(required = false) Long accountId, @Valid @RequestParam(required = false) Long categoryId, @Valid @RequestParam(required = false) LocalDate fromDate, @Valid @RequestParam(required = false) LocalDate toDate ) {
        log.info("Received endpoint request to get transaction: {}", pageNumber);

        Pageable pageable = PageRequest.of(pageNumber,limit);
        GetTransactionRequest transactionRequest = GetTransactionRequest.builder()
                .accountId(accountId)
                .categoryId(categoryId)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactions(pageable, transactionRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction (@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

}
