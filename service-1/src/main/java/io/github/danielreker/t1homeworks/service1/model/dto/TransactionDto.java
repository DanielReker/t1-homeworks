package io.github.danielreker.t1homeworks.service1.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.danielreker.t1homeworks.service1.model.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link io.github.danielreker.t1homeworks.service1.model.Transaction}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {
    AccountDto account;
    @NotNull
    BigDecimal amount;
    @NotNull
    Instant time;
    @NotNull
    UUID transactionId;
    @NotNull
    TransactionStatus status;
}