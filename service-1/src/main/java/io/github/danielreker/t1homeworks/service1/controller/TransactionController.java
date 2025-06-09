package io.github.danielreker.t1homeworks.service1.controller;

import io.github.danielreker.t1homeworks.service1.model.dto.CreateTransactionRequest;
import io.github.danielreker.t1homeworks.service1.model.dto.TransactionDto;
import io.github.danielreker.t1homeworks.service1.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public PagedModel<TransactionDto> getAll(@ParameterObject Pageable pageable) {
        Page<TransactionDto> transactionDtos = transactionService.getAll(pageable);
        return new PagedModel<>(transactionDtos);
    }

    @GetMapping("/{id}")
    public TransactionDto getOne(@PathVariable Long id) {
        return transactionService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<TransactionDto> getMany(@RequestParam List<Long> ids) {
        return transactionService.getMany(ids);
    }

    @PostMapping
    public TransactionDto create(@RequestBody @Valid CreateTransactionRequest dto) {
        return transactionService.create(dto);
    }
}
