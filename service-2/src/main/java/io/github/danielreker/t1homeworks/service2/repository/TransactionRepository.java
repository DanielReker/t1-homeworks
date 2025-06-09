package io.github.danielreker.t1homeworks.service2.repository;

import io.github.danielreker.t1homeworks.service2.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}