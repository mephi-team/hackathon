package team.mephi.hackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import team.mephi.hackathon.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("SELECT t FROM Transaction t WHERE " +
            "(:startDate IS NULL OR t.operationDate >= :startDate) AND " +
            "(:endDate IS NULL OR t.operationDate <= :endDate) AND " +
            "(:transactionType IS NULL OR t.transactionType = :transactionType) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:category IS NULL OR t.category = :category) AND " +
            "t.deleted = false")
    List<Transaction> findAllByFilter(
            LocalDateTime startDate,
            LocalDateTime endDate,
            String transactionType,
            String status,
            String category);
}