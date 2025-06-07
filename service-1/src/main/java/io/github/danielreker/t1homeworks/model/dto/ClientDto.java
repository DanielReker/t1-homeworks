package io.github.danielreker.t1homeworks.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.UUID;

/**
 * DTO for {@link io.github.danielreker.t1homeworks.model.Client}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDto {
    @NotNull
    @NotBlank
    String firstName;
    @NotNull
    @NotBlank
    String lastName;
    String middleName;
    @NotNull
    UUID clientId;
}