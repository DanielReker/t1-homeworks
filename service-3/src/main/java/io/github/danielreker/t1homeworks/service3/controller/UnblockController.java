package io.github.danielreker.t1homeworks.service3.controller;

import io.github.danielreker.t1homeworks.service3.model.dto.UnblockAccountRequest;
import io.github.danielreker.t1homeworks.service3.model.dto.UnblockAccountResponse;
import io.github.danielreker.t1homeworks.service3.model.dto.UnblockClientRequest;
import io.github.danielreker.t1homeworks.service3.model.dto.UnblockClientResponse;
import io.github.danielreker.t1homeworks.service3.service.UnblockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/unblock")
public class UnblockController {
    private final UnblockService unblockService;

    @PostMapping("/clients")
    public UnblockClientResponse unblockClients(
            @Valid @RequestBody UnblockClientRequest unblockClientRequest
    ) {
        List<UUID> unblockedClients = unblockClientRequest.clientIds().stream()
                .filter(unblockService::shouldUnblockClient)
                .toList();
        return new UnblockClientResponse(unblockedClients);
    }

    @PostMapping("/accounts")
    public UnblockAccountResponse unblockAccounts(
            @Valid @RequestBody UnblockAccountRequest unblockAccountRequest
    ) {
        List<UUID> unblockedClients = unblockAccountRequest.accountIds().stream()
                .filter(unblockService::shouldUnblockAccount)
                .toList();
        return new UnblockAccountResponse(unblockedClients);
    }
}

