package team.mephi.hackathon.specification;

import org.springframework.data.jpa.domain.Specification;
import team.mephi.hackathon.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionSpecification {

    /* --------- helpers --------- */
    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    public static Specification<Transaction> hasSenderBank(String senderBank) {
        return (root, q, cb) ->
                isBlank(senderBank) ? null : cb.equal(root.get("senderBank"), senderBank.trim());
    }

    public static Specification<Transaction> hasReceiverBank(String receiverBank) {
        return (root, q, cb) ->
                isBlank(receiverBank) ? null : cb.equal(root.get("receiverBank"), receiverBank.trim());
    }

    public static Specification<Transaction> hasTransactionType(String type) {
        return (root, q, cb) ->
                isBlank(type) ? null : cb.equal(root.get("transactionType"), type.trim());
    }

    public static Specification<Transaction> hasCategory(String category) {
        return (root, q, cb) ->
                isBlank(category) ? null : cb.equal(root.get("category"), category.trim());
    }

    public static Specification<Transaction> hasDateBetween(LocalDateTime from, LocalDateTime to) {
        return (root, q, cb) -> {
            if (from == null && to == null) return null;
            if (from == null) return cb.lessThanOrEqualTo(root.get("operationDate"), to);
            if (to == null)   return cb.greaterThanOrEqualTo(root.get("operationDate"), from);
            return cb.between(root.get("operationDate"), from, to);
        };
    }

    public static Specification<Transaction> hasAmountBetween(BigDecimal min, BigDecimal max) {
        return (root, q, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("amount"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("amount"), min);
            return cb.between(root.get("amount"), min, max);
        };
    }
}
