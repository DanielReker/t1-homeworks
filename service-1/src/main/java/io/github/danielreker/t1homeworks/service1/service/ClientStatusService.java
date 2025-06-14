package io.github.danielreker.t1homeworks.service1.service;

import io.github.danielreker.t1homeworks.service1.model.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientStatusService {
    @Value("${spring.application.external-services.service-2.base-url}")
    private String service2Url;

    private final RestClient restClient;

    public ClientStatusResponseDto getClientStatus(UUID clientId, UUID accountId) {
        URI uri = UriComponentsBuilder.fromUriString(service2Url)
                .pathSegment("clients", clientId.toString())
                .queryParam("accountId", accountId.toString())
                .build()
                .toUri();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(ClientStatusResponseDto.class);
    }
}
