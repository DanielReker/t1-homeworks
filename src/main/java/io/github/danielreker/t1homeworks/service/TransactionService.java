package io.github.danielreker.t1homeworks.service;

import io.github.danielreker.t1homeworks.aop.annotation.LogDataSourceError;
import io.github.danielreker.t1homeworks.aop.annotation.Metric;
import io.github.danielreker.t1homeworks.mapper.TransactionMapper;
import io.github.danielreker.t1homeworks.model.Account;
import io.github.danielreker.t1homeworks.model.Transaction;
import io.github.danielreker.t1homeworks.model.dto.CreateTransactionRequest;
import io.github.danielreker.t1homeworks.model.dto.TransactionDto;
import io.github.danielreker.t1homeworks.repository.AccountRepository;
import io.github.danielreker.t1homeworks.repository.TransactionRepository;
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

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final TransactionMapper transactionMapper;


    @Metric
    @LogDataSourceError
    public Page<TransactionDto> getAll(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return transactions.map(transactionMapper::toTransactionDto);
    }

    @Metric
    @LogDataSourceError
    public TransactionDto getOne(Long id) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        return transactionMapper.toTransactionDto(transactionOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Metric
    @LogDataSourceError
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
                .findById(dto.getAccountId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Account with id `%s` not found".formatted(dto.getAccountId())
                        )
                );
        account.setBalance(account.getBalance().add(dto.getAmount()));
        Account updatedAccount = accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(updatedAccount)
                .amount(dto.getAmount())
                .time(now)
                .build();

        Transaction resultTransaction = transactionRepository.save(transaction);
        return transactionMapper.toTransactionDto(resultTransaction);
    }
}
