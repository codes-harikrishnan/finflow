package com.harikrishnan.finflow.category.repository;

import com.harikrishnan.finflow.category.domain.Category;
import com.harikrishnan.finflow.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    boolean existsByNameAndUser(String name, User user);

    List<Category> findByUser(User user);

    Optional<Category> findByIdAndUser(Long id, User user);

    List<Category> findByUserIsNullOrUser(User user);
}
