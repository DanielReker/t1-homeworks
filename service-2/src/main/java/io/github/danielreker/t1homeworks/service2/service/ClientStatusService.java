package io.github.danielreker.t1homeworks.service2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ClientStatusService {
    public boolean isClientBlocked(UUID clientId) {
        return clientId.hashCode() % 3 == 0;
    }
}
