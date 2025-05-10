package team.mephi.hackathon.specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;
import team.mephi.hackathon.entity.Transaction;

public class TransactionSpecification {
  public static Specification<Transaction> hasSenderBank(String senderBank) {
    return (root, query, criteriaBuilder) ->
        senderBank == null ? null : criteriaBuilder.equal(root.get("senderBank"), senderBank);
  }

  public static Specification<Transaction> hasReceiverBank(String receiverBank) {
    return (root, query, criteriaBuilder) ->
        receiverBank == null ? null : criteriaBuilder.equal(root.get("receiverBank"), receiverBank);
  }

  public static Specification<Transaction> hasDateBetween(LocalDateTime from, LocalDateTime to) {
    return (root, query, criteriaBuilder) -> {
      if (from == null && to == null) {
        return null;
      } else if (from == null) {
        return criteriaBuilder.lessThanOrEqualTo(root.get("operationDate"), to);
      } else if (to == null) {
        return criteriaBuilder.greaterThanOrEqualTo(root.get("operationDate"), from);
      } else {
        return criteriaBuilder.between(root.get("operationDate"), from, to);
      }
    };
  }

  public static Specification<Transaction> hasAmountBetween(BigDecimal min, BigDecimal max) {
    return (root, query, criteriaBuilder) -> {
      if (min == null && max == null) {
        return null;
      } else if (min == null) {
        return criteriaBuilder.lessThanOrEqualTo(root.get("amount"), max);
      } else if (max == null) {
        return criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), min);
      } else {
        return criteriaBuilder.between(root.get("amount"), min, max);
      }
    };
  }

  public static Specification<Transaction> hasCategory(String category) {
    return (root, query, criteriaBuilder) ->
        category == null ? null : criteriaBuilder.equal(root.get("category"), category);
  }
}
