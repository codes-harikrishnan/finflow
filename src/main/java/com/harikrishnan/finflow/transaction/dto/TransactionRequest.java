package com.harikrishnan.finflow.transaction.dto;
import com.harikrishnan.finflow.transaction.domain.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    @NotNull(message = "Account id should not be null")
    private Long accountId;

    @NotNull(message = "Transaction type should not be null")
    private TransactionType transactionType;

    @NotNull(message = "Amount should not be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private LocalDate transactionDate;

    private String description;

    private Long categoryId;

    private Long toAccountId;

}
