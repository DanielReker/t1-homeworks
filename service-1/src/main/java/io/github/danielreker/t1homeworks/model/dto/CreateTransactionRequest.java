package io.github.danielreker.t1homeworks.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;

/**
 * DTO for {@link io.github.danielreker.t1homeworks.model.Transaction}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateTransactionRequest {
    Long accountId;
    @NotNull
    BigDecimal amount;
}