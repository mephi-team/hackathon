package team.mephi.hackathon.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.mephi.hackathon.dto.TransactionDto;
import team.mephi.hackathon.dto.TransactionResponseDto;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.service.TransactionService;


import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDto createTransaction(@RequestBody TransactionDto transactionDto) {
        Transaction transaction = transactionService.createTransaction(transactionDto);
        return convertToResponseDto(transaction);
    }

    @GetMapping("/{id}")
    public TransactionResponseDto getTransaction(@PathVariable UUID id) {
        Transaction transaction = transactionService.getTransactionById(id);
        return convertToResponseDto(transaction);
    }

    @GetMapping
    public List<TransactionResponseDto> getAllTransactions() {
        return transactionService.getAllTransactions().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public TransactionResponseDto updateTransaction(
            @PathVariable UUID id,
            @RequestBody TransactionDto transactionDto
    ) {
        Transaction transaction = transactionService.updateTransaction(id, transactionDto);
        return convertToResponseDto(transaction);
    }

    private TransactionResponseDto convertToResponseDto(Transaction transaction) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setDescription(transaction.getDescription());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
}