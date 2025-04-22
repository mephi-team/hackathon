package team.mephi.hackathon.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.mephi.hackathon.dto.TransactionDto;
import team.mephi.hackathon.exceptions.TransactionNotFoundException;
import team.mephi.hackathon.model.Transaction;
import team.mephi.hackathon.repository.TransactionRepository;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.dto.TransactionDto;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction createTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCurrency(transactionDto.getCurrency());
        transaction.setDescription(transactionDto.getDescription());
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(UUID id, TransactionDto transactionDto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        transaction.setAmount(transactionDto.getAmount());
        transaction.setCurrency(transactionDto.getCurrency());
        transaction.setDescription(transactionDto.getDescription());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        transaction.setDeleted(true);
        transactionRepository.save(transaction);
    }

    public List<Transaction> getFilteredTransactions(Double minAmount,
                                                     Double maxAmount,
                                                     String currency,
                                                     String descriptionContains) {
        // Реализация фильтрации через JPA Specification или @Query
        return transactionRepository.findAllActive();
    }
}