package io.github.danielreker.t1homeworks.service1.service;

import io.github.danielreker.t1homeworks.service1.aop.annotation.Cached;
import io.github.danielreker.t1homeworks.service1.aop.annotation.LogDataSourceError;
import io.github.danielreker.t1homeworks.service1.aop.annotation.Metric;
import io.github.danielreker.t1homeworks.service1.kafka.TransactionAcceptProducer;
import io.github.danielreker.t1homeworks.service1.mapper.TransactionMapper;
import io.github.danielreker.t1homeworks.service1.model.Account;
import io.github.danielreker.t1homeworks.service1.model.Client;
import io.github.danielreker.t1homeworks.service1.model.Transaction;
import io.github.danielreker.t1homeworks.service1.model.dto.*;
import io.github.danielreker.t1homeworks.service1.model.enums.AccountStatus;
import io.github.danielreker.t1homeworks.service1.model.enums.ClientStatus;
import io.github.danielreker.t1homeworks.service1.model.enums.TransactionStatus;
import io.github.danielreker.t1homeworks.service1.repository.AccountRepository;
import io.github.danielreker.t1homeworks.service1.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {
    @Value("${spring.application.transactions.max-rejected}")
    private long maxRejectedTransactions;

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final TransactionMapper transactionMapper;

    private final TransactionAcceptProducer transactionAcceptProducer;
    private final ClientStatusService clientStatusService;


    @Metric
    @LogDataSourceError
    @Cached
    public Page<TransactionDto> getAll(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return transactions.map(transactionMapper::toTransactionDto);
    }

    @Metric
    @LogDataSourceError
    @Cached
    public TransactionDto getOne(Long id) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        return transactionMapper.toTransactionDto(transactionOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Metric
    @LogDataSourceError
    @Cached
    public List<TransactionDto> getMany(List<Long> ids) {
        List<Transaction> transactions = transactionRepository.findAllById(ids);
        return transactions.stream()
                .map(transactionMapper::toTransactionDto)
                .toList();
    }

    @Metric
    @LogDataSourceError
    @Transactional
    public TransactionDto create(CreateTransactionRequest dto) {
        Instant now = Instant.now();
        Account account = accountRepository
                .findById(dto.accountId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Account with id `%s` not found".formatted(dto.accountId())
                        )
                );

        if (account.getStatus() != AccountStatus.OPEN) {
            log.warn("Account with id {} is {}", dto.accountId(), account.getStatus());
            return null;
        }


        Client client = account.getClient();
        if (client.getStatus() == null) {
            ClientStatusResponseDto clientStatusResponseDto = clientStatusService
                    .getClientStatus(client.getClientId(), account.getAccountId());

            if (clientStatusResponseDto.isBlocked()) {
                client.setStatus(ClientStatus.BLOCKED);
            } else {
                client.setStatus(ClientStatus.OPEN);
            }
        }

        TransactionStatus transactionStatus = TransactionStatus.REQUESTED;
        if (client.getStatus() == ClientStatus.BLOCKED) {
            account.setStatus(AccountStatus.BLOCKED);
            transactionStatus = TransactionStatus.REJECTED;
            log.warn("Account with id {} is {}, rejecting incoming transaction",
                    account.getAccountId(), account.getStatus());
        } else {
            Long rejectedTransactions = transactionRepository
                    .countByStatusEqualsAndAccount_Client_ClientIdEquals(TransactionStatus.REJECTED, client.getClientId());
            if (rejectedTransactions > maxRejectedTransactions) {
                account.setStatus(AccountStatus.ARRESTED);
                transactionStatus = TransactionStatus.REJECTED;
            }
        }


        if (transactionStatus == TransactionStatus.REQUESTED) {
            account.setBalance(account.getBalance().add(dto.amount()));
        }

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionId(UUID.randomUUID())
                .amount(dto.amount())
                .time(now)
                .status(transactionStatus)
                .build();
        transaction = transactionRepository.save(transaction);


        if (transaction.getStatus() == TransactionStatus.REQUESTED) {
            TransactionAcceptDto transactionAcceptDto = TransactionAcceptDto.builder()
                    .transactionId(transaction.getTransactionId())
                    .clientId(account.getClient().getClientId())
                    .accountId(account.getAccountId())
                    .transactionId(transaction.getTransactionId())
                    .timestamp(now)
                    .accountBalance(account.getBalance())
                    .transactionAmount(dto.amount())
                    .build();
            transactionAcceptProducer.sendTransactionAccept(transactionAcceptDto);
        }

        return transactionMapper.toTransactionDto(transaction);
    }

    @Transactional
    public void processResult(TransactionResultDto dto) {
        Transaction transaction = transactionRepository.findByTransactionId((dto.transactionId()));
        if (transaction == null) {
            log.warn("Transaction with id `{}` not found", dto.transactionId());
            return;
        }

        log.info("Processing result of transaction with id `{}`, new status: {}",
                dto.transactionId(), dto.status());

        transaction.setStatus(dto.status());

        if (transaction.getStatus() == TransactionStatus.BLOCKED
                || transaction.getStatus() == TransactionStatus.REJECTED
                || transaction.getStatus() == TransactionStatus.CANCELLED
        ) {
            Account account = accountRepository.findByAccountId(dto.accountId());
            if (account == null) {
                log.warn("Account with id `{}` not found", dto.accountId());
                return;
            }

            account.setBalance(account.getBalance().subtract(transaction.getAmount()));

            if (transaction.getStatus() == TransactionStatus.BLOCKED) {
                account.setStatus(AccountStatus.BLOCKED);
                account.setFrozenAmount(account.getFrozenAmount().add(transaction.getAmount()));
            }
        }
    }
}
