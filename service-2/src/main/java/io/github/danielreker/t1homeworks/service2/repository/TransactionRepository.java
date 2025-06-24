package io.github.danielreker.t1homeworks.service2.repository;

import io.github.danielreker.t1homeworks.service2.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByTimestampGreaterThanAndAccountId(Instant timestampIsGreaterThan, UUID accountId);
}