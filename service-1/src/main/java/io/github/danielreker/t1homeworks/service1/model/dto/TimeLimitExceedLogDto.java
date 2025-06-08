package io.github.danielreker.t1homeworks.service1.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * DTO for {@link io.github.danielreker.t1homeworks.service1.model.TimeLimitExceedLog}
 */
@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeLimitExceedLogDto {
    @NotNull
    String methodSignature;
    @NotNull
    Double measuredTimeMs;
    @NotNull
    Instant loggedAt;
}