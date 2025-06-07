package io.github.danielreker.t1homeworks.controller;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.danielreker.t1homeworks.model.dto.ClientDto;
import io.github.danielreker.t1homeworks.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public PagedModel<ClientDto> getAll(Pageable pageable) {
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
    public ClientDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return clientService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam @Valid List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return clientService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public ClientDto delete(@PathVariable Long id) {
        return clientService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        clientService.deleteMany(ids);
    }
}
