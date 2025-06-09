package io.github.danielreker.t1homeworks.service1.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO for {@link io.github.danielreker.t1homeworks.service1.model.Transaction}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateTransactionRequest(
        Long accountId,
        @NotNull BigDecimal amount
) { }