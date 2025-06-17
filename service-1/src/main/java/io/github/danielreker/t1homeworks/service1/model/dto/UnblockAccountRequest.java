package io.github.danielreker.t1homeworks.service1.model.dto;

import java.util.List;
import java.util.UUID;

public record UnblockAccountRequest(
        List<UUID> accountIds
) {
}
