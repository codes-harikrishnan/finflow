package com.harikrishnan.finflow.account.repository;

import com.harikrishnan.finflow.account.domain.Account;
import com.harikrishnan.finflow.account.dto.AccountResponse;
import com.harikrishnan.finflow.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {


    List<Account> findAllByUser(User user);

    Optional<Account> findByIdAndUser(Long id, User user);

    boolean existsByNameAndUser(String name, User user);
}
