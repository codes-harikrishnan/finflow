package com.harikrishnan.finflow.account.dto;


import com.harikrishnan.finflow.account.domain.AccountType;
import com.harikrishnan.finflow.account.domain.Currency;
import com.harikrishnan.finflow.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequest {

    @NotBlank(message = "Account name should not be blank")
    private String name;

    @NotNull(message = "Account type is required")
    private AccountType type;

    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "Balance should not be null")
    @Min(value = 0)
    private BigDecimal balance;

}
