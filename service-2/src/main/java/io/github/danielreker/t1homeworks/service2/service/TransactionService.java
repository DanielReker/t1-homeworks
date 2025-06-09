package io.github.danielreker.t1homeworks.service2.service;

import io.github.danielreker.t1homeworks.service2.kafka.TransactionResultProducer;
import io.github.danielreker.t1homeworks.service2.model.Transaction;
import io.github.danielreker.t1homeworks.service2.model.dto.TransactionAcceptDto;
import io.github.danielreker.t1homeworks.service2.model.dto.TransactionResultDto;
import io.github.danielreker.t1homeworks.service2.model.enums.TransactionStatus;
import io.github.danielreker.t1homeworks.service2.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionResultProducer transactionResultProducer;

    private final TransactionRepository transactionRepository;

    public void processTransaction(TransactionAcceptDto dto) {
        Transaction transaction = Transaction.builder()
                .transactionId(dto.transactionId())
                .timestamp(dto.timestamp())
                .status(TransactionStatus.ACCEPTED)
                .build();

        transactionRepository.save(transaction);

        TransactionResultDto result = TransactionResultDto.builder()
                .accountId(dto.accountId())
                .status(transaction.getStatus())
                .transactionId(transaction.getTransactionId())
                .build();

        transactionResultProducer.sendTransactionResult(result);
    }
}
