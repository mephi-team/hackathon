package team.mephi.hackathon.service;

import org.springframework.stereotype.Service;
import team.mephi.hackathon.dto.TransactionDto;
import team.mephi.hackathon.model.Transaction;
import team.mephi.hackathon.repository.TransactionRepository;

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
}