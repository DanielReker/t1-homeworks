package io.github.danielreker.t1homeworks.service1.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.util.List;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtResponse {
    String accessToken;
    String type = "Bearer";
    Long id;
    String username;
    String email;
    List<String> roles;
}
