package com.harikrishnan.finflow.transaction.repository;
import com.harikrishnan.finflow.transaction.domain.Transaction;
import com.harikrishnan.finflow.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction,Long>, JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByIdAndUser(Long id, User user);


}
