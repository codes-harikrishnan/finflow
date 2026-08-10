package com.harikrishnan.finflow.budget.domain;

import com.harikrishnan.finflow.category.domain.Category;
import com.harikrishnan.finflow.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Category category;

    @Column(name = "limit_amount", nullable = false)
    private BigDecimal limitAmount;

    @Column(name = "spent_amount", nullable = false)
    private BigDecimal spentAmount;

    @Enumerated(EnumType.STRING)
    private BudgetStatus budgetStatus;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Budget(Category category, BigDecimal limitAmount, BigDecimal spentAmount, BudgetStatus budgetStatus, Integer year, Integer month, User user) {
        this.category =  category;
        this.limitAmount = limitAmount;
        this.spentAmount = spentAmount;
        this.budgetStatus = budgetStatus;
        this.year = year;
        this.month = month;
        this.user = user;
    }

    public void recordSpend (BigDecimal amount) {
       this.spentAmount = this.spentAmount.add(amount);
       if(this.spentAmount.compareTo(this.limitAmount) >= 0) {
           this.budgetStatus = BudgetStatus.EXCEEDED;
       }
    }

}
