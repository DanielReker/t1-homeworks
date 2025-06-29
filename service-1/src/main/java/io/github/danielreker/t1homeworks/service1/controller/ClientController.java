package io.github.danielreker.t1homeworks.service1.controller;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.danielreker.t1homeworks.service1.model.dto.ClientDto;
import io.github.danielreker.t1homeworks.service1.service.ClientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@SecurityRequirement(name = "JwtBearerAuth")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public PagedModel<ClientDto> getAll(@ParameterObject Pageable pageable) {
        Page<ClientDto> clientDtos = clientService.getAll(pageable);
        return new PagedModel<>(clientDtos);
    }

    @GetMapping("/{id}")
    public ClientDto getOne(@PathVariable Long id) {
        return clientService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<ClientDto> getMany(@RequestParam List<Long> ids) {
        return clientService.getMany(ids);
    }

    @PostMapping
    public ClientDto create(@RequestBody @Valid ClientDto dto) {
        return clientService.create(dto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ClientDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return clientService.patch(id, patchNode);
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public List<Long> patchMany(@RequestParam @Valid List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return clientService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ClientDto delete(@PathVariable Long id) {
        return clientService.delete(id);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteMany(@RequestParam List<Long> ids) {
        clientService.deleteMany(ids);
    }
}
