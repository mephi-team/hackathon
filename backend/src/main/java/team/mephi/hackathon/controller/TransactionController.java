package team.mephi.hackathon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import team.mephi.hackathon.dto.TransactionFilterDto;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;
import team.mephi.hackathon.service.TransactionService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDto create(@Valid @RequestBody TransactionRequestDto dto) {
        return service.createTransaction(dto);
    }

    @GetMapping("/{id}")
    public TransactionResponseDto getById(@PathVariable Long id) {
        return service.getTransaction(id);
    }

    @GetMapping
    public List<TransactionResponseDto> search(@Valid TransactionFilterDto filter) {
        return service.searchTransactions(filter);
    }

    @PutMapping("/{id}")
    public TransactionResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDto dto
    ) {
        return service.updateTransaction(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteTransaction(id);
    }
}