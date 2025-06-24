package io.github.danielreker.t1homeworks.service1.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.danielreker.t1homeworks.service1.model.enums.AccountStatus;
import io.github.danielreker.t1homeworks.service1.model.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for {@link io.github.danielreker.t1homeworks.service1.model.Account}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {
    ClientDto client;
    @NotNull
    AccountType type;
    @NotNull
    BigDecimal balance;
    @NotNull
    UUID accountId;
    @NotNull
    AccountStatus status;
    @NotNull
    BigDecimal frozenAmount;
}