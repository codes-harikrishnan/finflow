package com.harikrishnan.finflow.user.repository;

import com.harikrishnan.finflow.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByEmailId(String emailId);
}
