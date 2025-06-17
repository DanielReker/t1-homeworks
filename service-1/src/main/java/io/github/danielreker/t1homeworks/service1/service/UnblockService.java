package io.github.danielreker.t1homeworks.service1.service;

import io.github.danielreker.t1homeworks.service1.model.Client;
import io.github.danielreker.t1homeworks.service1.model.dto.UnblockClientRequest;
import io.github.danielreker.t1homeworks.service1.model.dto.UnblockClientResponse;
import io.github.danielreker.t1homeworks.service1.model.enums.ClientStatus;
import io.github.danielreker.t1homeworks.service1.repository.AccountRepository;
import io.github.danielreker.t1homeworks.service1.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UnblockService {
    @Value("${spring.application.external-services.service-3.base-url}")
    private String service3Url;

    @Value("${spring.application.unblock.accounts-per-request}")
    private Long accountsPerRequest;

    @Value("${spring.application.unblock.clients-per-request}")
    private int clientsPerRequest;

    private final ClientRepository clientRepository;

    private final AccountRepository accountRepository;

    private final RestClient restClient;

    @Async
    @Scheduled(fixedRateString = "${spring.application.unblock.requests-period-ms}")
    @Transactional
    public void unblockClients() {
        log.info("Unblocking up to {} clients", clientsPerRequest);

        URI uri = UriComponentsBuilder.fromUriString(service3Url)
                .pathSegment("unblock", "clients")
                .build()
                .toUri();

        List<UUID> clientsToUnblock = clientRepository
                .findByStatusIn(List.of(ClientStatus.BLOCKED), Limit.of(clientsPerRequest))
                .stream()
                .map(Client::getClientId)
                .toList();
        UnblockClientRequest unblockClientRequest = new UnblockClientRequest(clientsToUnblock);

        UnblockClientResponse unblockClientResponse = restClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(unblockClientRequest)
                .retrieve()
                .body(UnblockClientResponse.class);

        Objects.requireNonNull(unblockClientResponse).unblockedClientIds()
                .forEach(clientId -> {
                    log.info("Unblocking client {}", clientId);
                    clientRepository.findByClientId(clientId).forEach(client ->
                            client.setStatus(ClientStatus.OPEN));
                });
    }
}
