package team.mephi.hackathon.repository;

import team.mephi.hackathon.model.Transaction;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(UUID id);
}

// InMemoryTransactionRepository.java
package team.mephi.hackathon.repository;

import org.springframework.stereotype.Repository;
import team.mephi.hackathon.model.Transaction;
import java.util.*;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {
    private final Map<UUID, Transaction> storage = new HashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            transaction.setId(UUID.randomUUID());
        }
        storage.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }
}