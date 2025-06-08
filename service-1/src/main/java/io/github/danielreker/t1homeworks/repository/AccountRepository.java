package io.github.danielreker.t1homeworks.repository;

import io.github.danielreker.t1homeworks.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountId(UUID accountId);
}