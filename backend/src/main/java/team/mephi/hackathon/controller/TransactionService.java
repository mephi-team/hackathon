package team.mephi.hackathon.controller;

import org.springframework.data.jpa.domain.Specification;
import team.mephi.hackathon.dto.TransactionFilterDto;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;
import team.mephi.hackathon.entity.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponseDto createTransaction(TransactionRequestDto dto);
    List<Transaction> getTransactions(Specification<Transaction> specification);
    TransactionResponseDto getTransaction(UUID id);
    List<TransactionResponseDto> searchTransactions(TransactionFilterDto filter);
    TransactionResponseDto updateTransaction(UUID id, TransactionRequestDto dto);
    void deleteTransaction(UUID id);
}
