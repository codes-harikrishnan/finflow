package com.harikrishnan.finflow.account.dto;

import com.harikrishnan.finflow.account.domain.AccountType;
import com.harikrishnan.finflow.account.domain.Currency;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Builder
@Data
public class AccountResponse implements Serializable {

    private final Long id;


    private final String name;


    private final AccountType type;


    private final Currency currency;


    private final BigDecimal balance;


    private final Long userId;
}
