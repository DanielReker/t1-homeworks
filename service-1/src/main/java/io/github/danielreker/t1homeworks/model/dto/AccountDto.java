package io.github.danielreker.t1homeworks.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.danielreker.t1homeworks.model.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.math.BigDecimal;

/**
 * DTO for {@link io.github.danielreker.t1homeworks.model.Account}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {
    ClientDto client;
    @NotNull
    AccountType type;
    @NotNull
    BigDecimal balance;
}