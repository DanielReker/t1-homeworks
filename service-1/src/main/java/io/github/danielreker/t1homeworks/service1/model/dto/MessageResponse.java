package io.github.danielreker.t1homeworks.service1.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageResponse {
    String message;
}
