package team.mephi.hackathon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.mephi.hackathon.dto.TransactionDto;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.exception.TransactionNotFoundException;
import team.mephi.hackathon.repository.TransactionRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Transaction createTransaction(TransactionDto dto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setCurrency(dto.getCurrency());
        transaction.setDescription(dto.getDescription());
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllActive();
    }

    public Transaction updateTransaction(UUID id, TransactionDto dto) {
        Transaction transaction = getTransactionById(id);
        transaction.setAmount(dto.getAmount());
        transaction.setCurrency(dto.getCurrency());
        transaction.setDescription(dto.getDescription());
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(UUID id) {
        Transaction transaction = getTransactionById(id);
        transaction.setDeleted(true);
        transactionRepository.save(transaction);
    }
