package team.mephi.hackathon.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team.mephi.hackathon.entity.Transaction;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionSpecificationTest {

    @Mock
    private Root<Transaction> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Predicate predicate;

    // hasSenderBank
    @Test
    void hasSenderBank_null_returnsNull() {
        assertNull(TransactionSpecification.hasSenderBank(null)
                .toPredicate(root, query, cb));
        verifyNoInteractions(cb);
    }

    @Test
    void hasSenderBank_nonNull_returnsPredicate() {
        String bank = "TestBank";
        when(cb.equal(root.get("senderBank"), bank)).thenReturn(predicate);
        Predicate result = TransactionSpecification.hasSenderBank(bank)
                .toPredicate(root, query, cb);
        assertSame(predicate, result);
        verify(cb).equal(root.get("senderBank"), bank);
    }

    // hasReceiverBank
    @Test
    void hasReceiverBank_null_returnsNull() {
        assertNull(TransactionSpecification.hasReceiverBank(null)
                .toPredicate(root, query, cb));
        verifyNoInteractions(cb);
    }

    @Test
    void hasReceiverBank_nonNull_returnsPredicate() {
        String bank = "ReceiverBank";
        when(cb.equal(root.get("receiverBank"), bank)).thenReturn(predicate);
        Predicate result = TransactionSpecification.hasReceiverBank(bank)
                .toPredicate(root, query, cb);
        assertSame(predicate, result);
        verify(cb).equal(root.get("receiverBank"), bank);
    }

    // hasDateBetween
    @Test
    void hasDateBetween_bothNull_returnsNull() {
        assertNull(TransactionSpecification.hasDateBetween(null, null)
                .toPredicate(root, query, cb));
        verifyNoInteractions(cb);
    }

    @Test
    void hasDateBetween_fromOnly_returnsGreaterThanOrEqual() {
        var from = java.time.LocalDateTime.now();
        when(cb.greaterThanOrEqualTo(root.get("operationDate"), from)).thenReturn(predicate);
        Predicate result = TransactionSpecification.hasDateBetween(from, null)
                .toPredicate(root, query, cb);
        assertSame(predicate, result);
        verify(cb).greaterThanOrEqualTo(root.get("operationDate"), from);
    }

    @Test
    void hasDateBetween_toOnly_returnsLessThanOrEqual() {
        var to = java.time.LocalDateTime.now();
        when(cb.lessThanOrEqualTo(root.get("operationDate"), to)).thenReturn(predicate);
        Predicate result = TransactionSpecification.hasDateBetween(null, to)
                .toPredicate(root, query, cb);
        assertSame(predicate, result);
        verify(cb).lessThanOrEqualTo(root.get("operationDate"), to);
    }

    @Test
    void hasDateBetween_bothNonNull_returnsBetween() {
        var from = java.time.LocalDateTime.now().minusDays(1);
        var to = java.time.LocalDateTime.now();
        when(cb.between(root.get("operationDate"), from, to)).thenReturn(predicate);
        Predicate result = TransactionSpecification.hasDateBetween(from, to)
                .toPredicate(root, query, cb);
        assertSame(predicate, result);
        verify(cb).between(root.get("operationDate"), from, to);
    }

    // hasAmountBetween
    @Test
    void hasAmountBetween_bothNull_returnsNull() {
        assertNull(TransactionSpecification.hasAmountBetween(null, null)
                .toPredicate(root, query, cb));
        verifyNoInteractions(cb);
    }

    @Test
    void hasAmountBetween_minOnly_returnsGreaterThanOrEqual() {
        var min = new java.math.BigDecimal("100");
        when(cb.greaterThanOrEqualTo(root.get("amount"), min)).thenReturn(predicate);
        Predicate result = TransactionSpecification.hasAmountBetween(min, null)
                .toPredicate(root, query, cb);
        assertSame(predicate, result);
        verify(cb).greaterThanOrEqualTo(root.get("amount"), min);
    }

    @Test
    void hasAmountBetween_maxOnly_returnsLessThanOrEqual() {
        var max = new java.math.BigDecimal("200");
        when(cb.lessThanOrEqualTo(root.get("amount"), max)).thenReturn(predicate);
        Predicate result = TransactionSpecification.hasAmountBetween(null, max)
                .toPredicate(root, query, cb);
        assertSame(predicate, result);
        verify(cb).lessThanOrEqualTo(root.get("amount"), max);
    }

    @Test
    void hasAmountBetween_bothNonNull_returnsBetween() {
        var min = new java.math.BigDecimal("100");
        var max = new java.math.BigDecimal("200");
        when(cb.between(root.get("amount"), min, max)).thenReturn(predicate);
        Predicate result = TransactionSpecification.hasAmountBetween(min, max)
                .toPredicate(root, query, cb);
        assertSame(predicate, result);
        verify(cb).between(root.get("amount"), min, max);
    }

    // hasCategory
    @Test
    void hasCategory_null_returnsNull() {
        assertNull(TransactionSpecification.hasCategory(null)
                .toPredicate(root, query, cb));
        verifyNoInteractions(cb);
    }

    @Test
    void hasCategory_nonNull_returnsPredicate() {
        String category = "CAT";
        when(cb.equal(root.get("category"), category)).thenReturn(predicate);
        Predicate result = TransactionSpecification.hasCategory(category)
                .toPredicate(root, query, cb);
        assertSame(predicate, result);
        verify(cb).equal(root.get("category"), category);
    }

    @Test
    void constructorTransactionSpecification() {
        new TransactionSpecification();
    }

}
