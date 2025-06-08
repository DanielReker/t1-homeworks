package io.github.danielreker.t1homeworks.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for {@link io.github.danielreker.t1homeworks.model.Transaction}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateTransactionRequest(
        Long accountId,
        @NotNull BigDecimal amount,
        @NotNull UUID transactionId
) { }