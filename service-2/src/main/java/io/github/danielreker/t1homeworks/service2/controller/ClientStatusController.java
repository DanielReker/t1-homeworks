package io.github.danielreker.t1homeworks.service2.controller;

import io.github.danielreker.t1homeworks.service2.model.dto.ClientStatusResponseDto;
import io.github.danielreker.t1homeworks.service2.service.ClientStatusService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientStatusController {

    private final ClientStatusService clientStatusService;

    @GetMapping("/clients/{clientId}")
    public ClientStatusResponseDto getClientStatus(
            @NotNull @PathVariable UUID clientId,
            @NotNull @RequestParam UUID accountId
    ) {
        return new ClientStatusResponseDto(
                clientStatusService.isClientBlocked(clientId)
        );
    }
}

