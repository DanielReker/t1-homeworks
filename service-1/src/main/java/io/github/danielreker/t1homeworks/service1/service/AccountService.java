package io.github.danielreker.t1homeworks.service1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.danielreker.t1homeworks.service1.aop.annotation.Cached;
import io.github.danielreker.t1homeworks.metricsaspectsspringbootstarter.aop.annotation.Metric;
import io.github.danielreker.t1homeworks.service1.model.dto.AccountDto;
import io.github.danielreker.t1homeworks.metricsaspectsspringbootstarter.aop.annotation.LogDataSourceError;
import io.github.danielreker.t1homeworks.service1.mapper.AccountMapper;
import io.github.danielreker.t1homeworks.service1.model.Account;
import io.github.danielreker.t1homeworks.service1.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountMapper accountMapper;

    private final AccountRepository accountRepository;

    private final ObjectMapper objectMapper;

    @Metric
    @LogDataSourceError
    @Cached
    public Page<AccountDto> getAll(Pageable pageable) {
        Page<Account> accounts = accountRepository.findAll(pageable);
        return accounts.map(accountMapper::toAccountDto);
    }

    @Metric
    @LogDataSourceError
    @Cached
    public AccountDto getOne(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        return accountMapper.toAccountDto(accountOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Metric
    @LogDataSourceError
    @Cached
    public List<AccountDto> getMany(List<Long> ids) {
        List<Account> accounts = accountRepository.findAllById(ids);
        return accounts.stream()
                .map(accountMapper::toAccountDto)
                .toList();
    }

    @Metric
    @LogDataSourceError
    public AccountDto create(AccountDto dto) {
        Account account = accountMapper.toEntity(dto);
        Account resultAccount = accountRepository.save(account);
        return accountMapper.toAccountDto(resultAccount);
    }

    @Metric
    @LogDataSourceError
    public AccountDto patch(Long id, JsonNode patchNode) throws IOException {
        Account account = accountRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        AccountDto accountDto = accountMapper.toAccountDto(account);
        objectMapper.readerForUpdating(accountDto).readValue(patchNode);
        accountMapper.updateWithNull(accountDto, account);

        Account resultAccount = accountRepository.save(account);
        return accountMapper.toAccountDto(resultAccount);
    }

    @Metric
    @LogDataSourceError
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Account> accounts = accountRepository.findAllById(ids);

        for (Account account : accounts) {
            AccountDto accountDto = accountMapper.toAccountDto(account);
            objectMapper.readerForUpdating(accountDto).readValue(patchNode);
            accountMapper.updateWithNull(accountDto, account);
        }

        List<Account> resultAccounts = accountRepository.saveAll(accounts);
        return resultAccounts.stream()
                .map(Account::getId)
                .toList();
    }

    @Metric
    @LogDataSourceError
    public AccountDto delete(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            accountRepository.delete(account);
        }
        return accountMapper.toAccountDto(account);
    }

    @Metric
    @LogDataSourceError
    public void deleteMany(List<Long> ids) {
        accountRepository.deleteAllById(ids);
    }
}
