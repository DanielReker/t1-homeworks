package io.github.danielreker.t1homeworks.service1.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.util.Set;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    String username;

    @NotBlank
    @Size(max = 50)
    @Email
    String email;

    Set<String> roles;

    @NotBlank
    @Size(min = 6, max = 40)
    String password;
}
