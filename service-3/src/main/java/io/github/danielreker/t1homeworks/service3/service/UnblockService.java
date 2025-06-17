package io.github.danielreker.t1homeworks.service3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.random.RandomGenerator;

@RequiredArgsConstructor
@Service
public class UnblockService {
    @Value("${spring.application.unblock.client-unblock-probability}")
    double clientUnlockProbability;

    @Value("${spring.application.unblock.account-unblock-probability}")
    double accountUnlockProbability;

    private final RandomGenerator randomGenerator;

    public boolean shouldUnblockClient(UUID clientId) {
        return randomGenerator.nextDouble(0, 1) < clientUnlockProbability;
    }

    public boolean shouldUnblockAccount(UUID accountId) {
        return randomGenerator.nextDouble(0, 1) < accountUnlockProbability;
    }
}
