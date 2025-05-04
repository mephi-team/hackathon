package team.mephi.hackathon.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import team.mephi.hackathon.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("""
        SELECT t FROM Transaction t
        WHERE (:dateFrom IS NULL OR CAST(:dateFrom AS timestamp) <= t.operationDate)
            AND (:dateTo IS NULL OR CAST(:dateTo AS timestamp) >= t.operationDate)
            AND (:transactionType IS NULL OR :transactionType = t.transactionType)
            AND (:status IS NULL OR :status = t.status)
            AND (:category IS NULL OR :category = t.category)
            AND t.deleted = false
    """)
    List<Transaction> findAllByFilter(
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            @Param("transactionType") TransactionType transactionType,
            @Param("status") TransactionStatus status,
            @Param("category") String category
    );

    @Query("SELECT t FROM Transaction t WHERE " +
            "t.deleted = false")
    List<Transaction> findAllActive();

    List<Transaction> findAll(Specification<Transaction> specification);

    Optional<Transaction> findById(@Param("id") UUID id);
}