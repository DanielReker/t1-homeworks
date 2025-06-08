package io.github.danielreker.t1homeworks.service1.service;

import io.github.danielreker.t1homeworks.service1.aop.annotation.Cached;
import io.github.danielreker.t1homeworks.service1.aop.annotation.LogDataSourceError;
import io.github.danielreker.t1homeworks.service1.aop.annotation.Metric;
import io.github.danielreker.t1homeworks.service1.kafka.TransactionAcceptProducer;
import io.github.danielreker.t1homeworks.service1.mapper.TransactionMapper;
import io.github.danielreker.t1homeworks.service1.model.Account;
import io.github.danielreker.t1homeworks.service1.model.Transaction;
import io.github.danielreker.t1homeworks.service1.model.dto.CreateTransactionRequest;
import io.github.danielreker.t1homeworks.service1.model.dto.TransactionAcceptDto;
import io.github.danielreker.t1homeworks.service1.model.dto.TransactionDto;
import io.github.danielreker.t1homeworks.service1.model.dto.TransactionResultDto;
import io.github.danielreker.t1homeworks.service1.model.enums.AccountStatus;
import io.github.danielreker.t1homeworks.service1.model.enums.TransactionStatus;
import io.github.danielreker.t1homeworks.service1.repository.AccountRepository;
import io.github.danielreker.t1homeworks.service1.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final TransactionMapper transactionMapper;

    private final TransactionAcceptProducer transactionAcceptProducer;


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


        if (account.getStatus() == AccountStatus.OPEN) {

            account.setBalance(account.getBalance().add(dto.amount()));
            Account updatedAccount = accountRepository.save(account);

            Transaction transaction = Transaction.builder()
                    .account(updatedAccount)
                    .transactionId(dto.transactionId())
                    .amount(dto.amount())
                    .time(now)
                    .status(TransactionStatus.REQUESTED)
                    .build();

            TransactionAcceptDto transactionAcceptDto = TransactionAcceptDto.builder()
                    .transactionId(transaction.getTransactionId())
                    .clientId(account.getClient().getClientId())
                    .accountId(updatedAccount.getAccountId())
                    .transactionId(transaction.getTransactionId())
                    .timestamp(now)
                    .accountBalance(updatedAccount.getBalance())
                    .transactionAmount(dto.amount())
                    .build();

            transactionAcceptProducer.sendTransactionAccept(transactionAcceptDto);

            Transaction resultTransaction = transactionRepository.save(transaction);
            return transactionMapper.toTransactionDto(resultTransaction);

        } else {
            log.warn("Account with id {} is {}", dto.accountId(), account.getStatus());
            return null;
        }
    }

    @Transactional
    public void processResult(TransactionResultDto dto) {
        Transaction transaction = transactionRepository.findByTransactionId((dto.transactionId()));
        if (transaction == null) {
            log.warn("Transaction with id `{}` not found", dto.transactionId());
            return;
        }

        transaction.setStatus(dto.status());
        if (transaction.getStatus() == TransactionStatus.BLOCKED
                || transaction.getStatus() == TransactionStatus.REJECTED) {

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
