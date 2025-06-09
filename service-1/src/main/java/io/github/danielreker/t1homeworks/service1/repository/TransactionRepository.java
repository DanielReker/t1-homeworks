package io.github.danielreker.t1homeworks.service1.repository;

import io.github.danielreker.t1homeworks.service1.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction findByTransactionId(UUID transactionId);
}