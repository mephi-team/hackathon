package team.mephi.hackathon.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team.mephi.hackathon.dto.TransactionDto;
import team.mephi.hackathon.exceptions.TransactionNotFoundException;
import team.mephi.hackathon.model.Transaction;
import team.mephi.hackathon.repository.TransactionRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private final UUID testId = UUID.randomUUID();
    private final TransactionDto testDto = new TransactionDto(100.0, "USD", "Test");
    private final Transaction testTransaction = Transaction.builder()
            .id(testId)
            .amount(100.0)
            .currency("USD")
            .description("Test")
            .build();

    @Test
    void createTransaction_ShouldSaveNewTransaction() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionService.createTransaction(testDto);

        assertNotNull(result.getId());
        assertEquals(100.0, result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertEquals("Test", result.getDescription());
    }

    @Test
    void updateTransaction_ShouldUpdateExisting() {
        when(transactionRepository.findById(testId)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionDto updateDto = new TransactionDto(200.0, "EUR", "Updated");
        Transaction result = transactionService.updateTransaction(testId, updateDto);

        assertEquals(200.0, result.getAmount());
        assertEquals("EUR", result.getCurrency());
        assertEquals("Updated", result.getDescription());
    }

    @Test
    void updateTransaction_NotFound_ShouldThrow() {
        when(transactionRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.updateTransaction(testId, testDto));
    }

    @Test
    void deleteTransaction_ShouldMarkAsDeleted() {
        testTransaction.setDeleted(false);
        when(transactionRepository.findById(testId)).thenReturn(Optional.of(testTransaction));

        transactionService.deleteTransaction(testId);

        assertTrue(testTransaction.isDeleted());
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void deleteTransaction_NotFound_ShouldThrow() {
        when(transactionRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.deleteTransaction(testId));
    }
}