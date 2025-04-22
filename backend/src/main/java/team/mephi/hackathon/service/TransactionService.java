package team.mephi.hackathon.service;

import org.springframework.stereotype.Service;
import team.mephi.hackathon.dto.TransactionDto;
import team.mephi.hackathon.exceptions.TransactionNotFoundException;
import team.mephi.hackathon.model.Transaction;
import team.mephi.hackathon.repository.TransactionRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCurrency(transactionDto.getCurrency());
        transaction.setDescription(transactionDto.getDescription());
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(UUID id, TransactionDto transactionDto) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isEmpty()) {
            throw new TransactionNotFoundException("Transaction with ID " + id + " not found");
        }

        Transaction transaction = optionalTransaction.get();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCurrency(transactionDto.getCurrency());
        transaction.setDescription(transactionDto.getDescription());

        return transactionRepository.save(transaction);
    }
}