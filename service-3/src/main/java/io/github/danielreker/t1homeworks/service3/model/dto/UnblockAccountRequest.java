package io.github.danielreker.t1homeworks.service3.model.dto;

import java.util.List;
import java.util.UUID;

public record UnblockAccountRequest(
        List<UUID> accountIds
) {
}
