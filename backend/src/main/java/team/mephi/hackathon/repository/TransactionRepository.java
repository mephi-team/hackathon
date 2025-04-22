package team.mephi.hackathon.repository;

import team.mephi.hackathon.model.Transaction;
import java.util.*;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
}

// InMemoryTransactionRepository.java
package team.mephi.hackathon.repository;

import team.mephi.hackathon.model.Transaction;
import org.springframework.stereotype.Repository;
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
}