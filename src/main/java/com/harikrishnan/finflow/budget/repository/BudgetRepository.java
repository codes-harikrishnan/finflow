package com.harikrishnan.finflow.budget.repository;

import com.harikrishnan.finflow.budget.domain.Budget;
import com.harikrishnan.finflow.category.domain.Category;
import com.harikrishnan.finflow.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository <Budget,Long>, JpaSpecificationExecutor<Budget> {

    Optional<Budget> findByUserAndCategoryAndMonthAndYear(User user, Category category, Integer month, Integer year);

    boolean existsByUserAndCategoryAndMonthAndYear(User user, Category category, Integer month, Integer year);

   Optional <Budget> findByIdAndUser(Long id, User currentUser);
}
