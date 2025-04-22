package team.mephi.hackathon.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.mephi.hackathon.dto.TransactionDto;
import team.mephi.hackathon.exceptions.TransactionNotFoundException;
import team.mephi.hackathon.model.Transaction;
import team.mephi.hackathon.service.TransactionService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    @GetMapping("/transactions")
    public ResponseEntity<String> getTransactions() {
        logger.info("Test log record");
        return ResponseEntity.ok().body("ok");
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable UUID id,
            @RequestBody TransactionDto transactionDto
    ) {
        logger.info("Updating transaction with ID: {}", id);
        Transaction updatedTransaction = transactionService.updateTransaction(id, transactionDto);
        return ResponseEntity.ok(updatedTransaction);
    }
}