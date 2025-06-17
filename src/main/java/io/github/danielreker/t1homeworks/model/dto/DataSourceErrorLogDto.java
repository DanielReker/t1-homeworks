package io.github.danielreker.t1homeworks.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;

/**
 * DTO for {@link io.github.danielreker.t1homeworks.model.DataSourceErrorLog}
 */
@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSourceErrorLogDto {
    String stacktrace;
    String message;
    String methodSignature;
}