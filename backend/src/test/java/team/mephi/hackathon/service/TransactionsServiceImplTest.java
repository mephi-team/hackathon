package team.mephi.hackathon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import team.mephi.hackathon.controller.ValidationService;
import team.mephi.hackathon.dto.TransactionFilterDto;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;
import team.mephi.hackathon.entity.PersonType;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.entity.TransactionStatus;
import team.mephi.hackathon.entity.TransactionType;
import team.mephi.hackathon.exceptions.TransactionNotFoundException;
import team.mephi.hackathon.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionRequestDto requestDto;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        requestDto = new TransactionRequestDto();
        requestDto.setPersonType("LEGAL");
        requestDto.setTransactionType("INCOME");
        requestDto.setStatus("COMPLETED");
        requestDto.setAmount(BigDecimal.valueOf(1000));
        requestDto.setReceiverInn("1234567890");
        requestDto.setReceiverPhone("+71234567890");

        transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(requestDto.getAmount());
        transaction.setPersonType(PersonType.LEGAL);
        transaction.setTransactionType(TransactionType.INCOME);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setOperationDate(LocalDateTime.now());
    }

    @Test
    void createTransaction_ShouldValidateAndSave() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        TransactionResponseDto response = transactionService.createTransaction(requestDto);
        verify(validationService).validateTransaction(requestDto);
        verify(transactionRepository).save(any(Transaction.class));
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(transaction.getId());
        assertThat(response.getAmount()).isEqualTo(transaction.getAmount());
    }

    @Test
    void getTransaction_ShouldReturnTransaction_WhenFound() {
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        TransactionResponseDto response = transactionService.getTransaction(transaction.getId());
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(transaction.getId());
    }

    @Test
    void getTransaction_ShouldThrow_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> transactionService.getTransaction(id))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void updateTransaction_ShouldValidateAndSave() {
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        TransactionResponseDto response = transactionService.updateTransaction(transaction.getId(), requestDto);
        verify(validationService).validateTransaction(requestDto);
        verify(transactionRepository).save(any(Transaction.class));
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(transaction.getId());
    }

    @Test
    void updateTransaction_ShouldThrow_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> transactionService.updateTransaction(id, requestDto))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void deleteTransaction_ShouldSetDeletedTrue() {
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));
        transactionService.deleteTransaction(transaction.getId());
        assertThat(transaction.isDeleted()).isTrue();
        verify(transactionRepository).save(transaction);
    }

    @Test
    void deleteTransaction_ShouldThrow_WhenNotFound() {
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> transactionService.deleteTransaction(id))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void searchTransactions_withFilter_returnsTransactions() {
        TransactionFilterDto filter = new TransactionFilterDto();
        filter.setStartDate(LocalDateTime.now().minusDays(5));
        filter.setEndDate(LocalDateTime.now());
        filter.setTransactionType("INCOME");
        filter.setStatus("COMPLETED");
        filter.setCategory("SALARY");

        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100.0));
        transaction.setPersonType(PersonType.LEGAL);
        transaction.setTransactionType(TransactionType.INCOME);
        transaction.setStatus(TransactionStatus.COMPLETED);

        when(transactionRepository.findAllByFilter(any(), any(), any(), any(), any())).thenReturn(List.of(transaction));

        var result = transactionService.searchTransactions(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository).findAllByFilter(eq(filter.getStartDate()), eq(filter.getEndDate()), eq(filter.getTransactionType()), eq(filter.getStatus()), eq(filter.getCategory()));
    }

    @Test
    void searchTransactions_withEmptyFilter_returnsEmptyList() {
        TransactionFilterDto filter = new TransactionFilterDto();
        when(transactionRepository.findAllByFilter(any(), any(), any(), any(), any())).thenReturn(List.of());
        var result = transactionService.searchTransactions(filter);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactions_withSpecification_returnsTransactions() {
        Specification<Transaction> spec = mock(Specification.class);
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(200.0));
        when(transactionRepository.findAll(spec)).thenReturn(List.of(transaction));
        var result = transactionService.getTransactions(spec);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(transactionRepository).findAll(spec);
    }
}
