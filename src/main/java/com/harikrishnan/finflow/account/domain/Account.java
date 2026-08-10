package com.harikrishnan.finflow.account.domain;

import com.harikrishnan.finflow.exceptions.InsufficientFundsException;
import com.harikrishnan.finflow.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.naming.InsufficientResourcesException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accounts")
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @Builder
    public Account(String name, AccountType type, Currency currency, BigDecimal balance, User user) {
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.balance = balance;
        this.user = user;
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void debit (BigDecimal amount) {
        if(amount.compareTo(this.balance) > 0 ) {
            throw new InsufficientFundsException("Amount cannot be greater than the balance amount");
        }
        this.balance = this.balance.subtract(amount);
    }
}
