package io.github.danielreker.t1homeworks.service2.model.dto;

import io.github.danielreker.t1homeworks.service2.model.enums.TransactionStatus;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class TransactionResultDto {
    TransactionStatus status;
    UUID accountId;
    UUID transactionId;
}
