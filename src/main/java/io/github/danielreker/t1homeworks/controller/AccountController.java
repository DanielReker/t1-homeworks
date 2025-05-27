package io.github.danielreker.t1homeworks.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.danielreker.t1homeworks.AccountDto;
import io.github.danielreker.t1homeworks.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public PagedModel<AccountDto> getAll(@ParameterObject Pageable pageable) {
        Page<AccountDto> accountDtos = accountService.getAll(pageable);
        return new PagedModel<>(accountDtos);
    }

    @GetMapping("/{id}")
    public AccountDto getOne(@PathVariable Long id) {
        return accountService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<AccountDto> getMany(@RequestParam List<Long> ids) {
        return accountService.getMany(ids);
    }

    @PostMapping
    public AccountDto create(@RequestBody @Valid AccountDto dto) {
        return accountService.create(dto);
    }

    @PatchMapping("/{id}")
    public AccountDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return accountService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam @Valid List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return accountService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public AccountDto delete(@PathVariable Long id) {
        return accountService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        accountService.deleteMany(ids);
    }
}
