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
                WHERE (COALESCE(:startDate, t.operationDate) <= t.operationDate)
                  AND (COALESCE(:endDate, t.operationDate) >= t.operationDate)
                  AND (COALESCE(:transactionType, t.transactionType) = t.transactionType)
                  AND (COALESCE(:status, t.status) = t.status)
                  AND (COALESCE(:category, t.category) = t.category)
                  AND t.deleted = false
    """)
    List<Transaction> findAllByFilter(
            LocalDateTime startDate,
            LocalDateTime endDate,
            String transactionType,
            String status,
            String category);

    @Query("SELECT t FROM Transaction t WHERE " +
            "t.deleted = false")
    List<Transaction> findAllActive();

    List<Transaction> findAll(Specification<Transaction> specification);

    Optional<Transaction> findById(@Param("id") UUID id);
}