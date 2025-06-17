package io.github.danielreker.t1homeworks.service1.service;

import io.github.danielreker.t1homeworks.service1.model.enums.AccountStatus;
import io.github.danielreker.t1homeworks.service1.model.enums.ClientStatus;
import io.github.danielreker.t1homeworks.service1.repository.AccountRepository;
import io.github.danielreker.t1homeworks.service1.repository.ClientRepository;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlockedMetricsService {
    private AtomicInteger blockedClientsGauge;
    private AtomicInteger arrestedAccountsGauge;

    private final ClientRepository clientRepository;

    private final MeterRegistry meterRegistry;

    private final AccountRepository accountRepository;


    @PostConstruct
    public void registerMeters() {
        this.blockedClientsGauge = meterRegistry.gauge("blocked_clients", new AtomicInteger(0));
        this.arrestedAccountsGauge = meterRegistry.gauge("arrested_accounts", new AtomicInteger(0));
    }

    @Async
    @Scheduled(fixedRateString = "${spring.application.metrics.refresh-rate-ms}")
    public void updateBlockedClientsMetrics() {
        int blockedClients = clientRepository
                .countByStatusIn(List.of(ClientStatus.BLOCKED));
        log.debug("blocked_clients = {}", blockedClients);
        this.blockedClientsGauge.set(blockedClients);
    }

    @Async
    @Scheduled(fixedRateString = "${spring.application.metrics.refresh-rate-ms}")
    public void updateArrestedAccountsMetrics() {
        int arrestedAccounts = accountRepository
                .countByStatusIn(List.of(AccountStatus.ARRESTED));
        log.debug("arrested_accounts = {}", arrestedAccounts);
        this.arrestedAccountsGauge.set(arrestedAccounts);
    }
}
