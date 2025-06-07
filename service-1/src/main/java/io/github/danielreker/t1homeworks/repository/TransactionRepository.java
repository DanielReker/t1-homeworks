package io.github.danielreker.t1homeworks.repository;

import io.github.danielreker.t1homeworks.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}