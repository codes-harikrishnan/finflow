package com.harikrishnan.finflow.budget.domain;

import com.harikrishnan.finflow.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "budget_alerts")
@EntityListeners(AuditingEntityListener.class)
public class BudgetAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Budget budget;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public BudgetAlert(String message, User user, Budget budget) {
        this.message = message;
        this.user = user;
        this.budget = budget;
    }
}
