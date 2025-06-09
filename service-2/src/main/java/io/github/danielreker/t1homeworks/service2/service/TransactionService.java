package io.github.danielreker.t1homeworks.service2.service;

import io.github.danielreker.t1homeworks.service2.kafka.TransactionResultProducer;
import io.github.danielreker.t1homeworks.service2.model.Transaction;
import io.github.danielreker.t1homeworks.service2.model.dto.TransactionAcceptDto;
import io.github.danielreker.t1homeworks.service2.model.dto.TransactionResultDto;
import io.github.danielreker.t1homeworks.service2.model.enums.TransactionStatus;
import io.github.danielreker.t1homeworks.service2.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {
    @Value("${spring.application.limits.transactions.time-period-ms}")
    private long timePeriodMs;

    @Value("${spring.application.limits.transactions.max-transactions-in-period}")
    private Long maxTransactionsInPeriod;

    private final TransactionResultProducer transactionResultProducer;

    private final TransactionRepository transactionRepository;

    public void processTransaction(TransactionAcceptDto acceptDto) {
        Instant now = Instant.now();

        List<Transaction> transactionsInPeriod =
                transactionRepository.findAllByTimestampGreaterThanAndAccountId(
                        now.minus(Duration.ofMillis(timePeriodMs)),
                        acceptDto.accountId()
                );

        TransactionStatus status = TransactionStatus.ACCEPTED;

        if (transactionsInPeriod.size() >= maxTransactionsInPeriod) {
            status = TransactionStatus.BLOCKED;
        } else if (acceptDto.accountBalance().add(acceptDto.transactionAmount()).compareTo(BigDecimal.ZERO) < 0) {
            status = TransactionStatus.REJECTED;
        }

        if (status == TransactionStatus.BLOCKED) {
            transactionsInPeriod.forEach(transaction -> {
                TransactionStatus newPreviousTransactionsStatus = TransactionStatus.BLOCKED;

                transaction.setStatus(newPreviousTransactionsStatus);

                TransactionResultDto result = TransactionResultDto.builder()
                        .status(transaction.getStatus())
                        .accountId(transaction.getAccountId())
                        .transactionId(transaction.getTransactionId())
                        .build();
                transactionResultProducer.sendTransactionResult(result);
            });
        }

        Transaction transaction = Transaction.builder()
                .status(status)
                .accountId(acceptDto.accountId())
                .transactionId(acceptDto.transactionId())
                .timestamp(acceptDto.timestamp())
                .build();

        transactionRepository.save(transaction);

        TransactionResultDto result = TransactionResultDto.builder()
                .status(status)
                .accountId(acceptDto.accountId())
                .transactionId(transaction.getTransactionId())
                .build();

        transactionResultProducer.sendTransactionResult(result);
    }
}
