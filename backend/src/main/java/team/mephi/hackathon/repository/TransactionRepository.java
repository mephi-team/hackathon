package team.mephi.hackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.mephi.hackathon.entity.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findAllByDeletedFalse();
}