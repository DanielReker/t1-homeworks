package io.github.danielreker.t1homeworks.service1.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest {
    @NotBlank
    String username;

    @NotBlank
    String password;

}
