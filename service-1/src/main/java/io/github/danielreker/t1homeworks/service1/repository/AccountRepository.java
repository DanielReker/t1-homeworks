package io.github.danielreker.t1homeworks.service1.repository;

import io.github.danielreker.t1homeworks.service1.model.Account;
import io.github.danielreker.t1homeworks.service1.model.enums.AccountStatus;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountId(UUID accountId);

    List<Account> findByStatusIn(
            Collection<AccountStatus> statuses,
            Limit limit
    );

    int countByStatusIn(Collection<AccountStatus> statuses);
}