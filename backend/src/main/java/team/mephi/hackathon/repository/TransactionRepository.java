package team.mephi.hackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team.mephi.hackathon.model.Transaction;
import java.util.List;
import java.util.UUID;
import team.mephi.hackathon.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE t.deleted = false")
    List<Transaction> findAllActive();
}