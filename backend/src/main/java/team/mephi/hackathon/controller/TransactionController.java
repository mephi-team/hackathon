package team.mephi.hackathon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import team.mephi.hackathon.dto.TransactionFilterDto;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;
import team.mephi.hackathon.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(
            @RequestParam(required = false) String senderBank,
            @RequestParam(required = false) String receiverBank,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) BigDecimal amountMin,
            @RequestParam(required = false) BigDecimal amountMax,
            @RequestParam(required = false) String category) {

        Specification<Transaction> specification = Specification
                .where(team.mephi.hackathon.specification.TransactionSpecification.hasSenderBank(senderBank))
                .and(team.mephi.hackathon.specification.TransactionSpecification.hasReceiverBank(receiverBank))
                .and(team.mephi.hackathon.specification.TransactionSpecification.hasDateBetween(dateFrom, dateTo))
                .and(team.mephi.hackathon.specification.TransactionSpecification.hasAmountBetween(amountMin, amountMax))
                .and(team.mephi.hackathon.specification.TransactionSpecification.hasCategory(category));

        return service.getTransactions(specification);
    }

    @GetMapping("/{id}")
    public TransactionResponseDto getById(@PathVariable UUID id) {
        return service.getTransaction(id);
    }

    @GetMapping
    public List<TransactionResponseDto> search(@Valid TransactionFilterDto filter) {
        return service.searchTransactions(filter);
    }

    @PutMapping("/{id}")
    public TransactionResponseDto update(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionRequestDto dto
    ) {
        return service.updateTransaction(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.deleteTransaction(id);
    }
}