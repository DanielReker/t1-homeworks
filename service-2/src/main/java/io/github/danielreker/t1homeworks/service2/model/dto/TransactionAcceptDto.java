package io.github.danielreker.t1homeworks.service2.model.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record TransactionAcceptDto(
        UUID clientId,
        UUID accountId,
        UUID transactionId,
        Instant timestamp,
        BigDecimal transactionAmount,
        BigDecimal accountBalance
) { }
