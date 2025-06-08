package io.github.danielreker.t1homeworks.model.dto;

import io.github.danielreker.t1homeworks.model.enums.TransactionStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TransactionResultDto(
        TransactionStatus status,
        UUID accountId,
        UUID transactionId
) { }
