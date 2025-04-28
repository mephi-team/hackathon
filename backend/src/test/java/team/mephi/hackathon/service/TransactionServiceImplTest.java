package team.mephi.hackathon.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team.mephi.hackathon.controller.ValidationService;
import team.mephi.hackathon.dto.TransactionFilterDto;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;
import team.mephi.hackathon.entity.PersonType;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.entity.TransactionStatus;
import team.mephi.hackathon.entity.TransactionType;
import team.mephi.hackathon.exceptions.TransactionNotFoundException;
import team.mephi.hackathon.exceptions.ValidationException;
import team.mephi.hackathon.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionRequestDto fullDto;
    private Transaction existingTransaction;

    @BeforeEach
    void setup() {
        fullDto = new TransactionRequestDto();
        fullDto.setPersonType("LEGAL");
        fullDto.setTransactionType("INCOME");
        fullDto.setStatus("COMPLETED");
        fullDto.setAmount(BigDecimal.valueOf(1500));
        fullDto.setOperationDate(LocalDateTime.now());
        fullDto.setComment("Salary payment");
        fullDto.setSenderBank("AlphaBank");
        fullDto.setAccount("123ABC");
        fullDto.setReceiverBank("BetaBank");
        fullDto.setReceiverAccount("456DEF");
        fullDto.setReceiverInn("1234567890");
        fullDto.setCategory("SALARY");
        fullDto.setReceiverPhone("+79876543210");

        existingTransaction = new Transaction();
        existingTransaction.setId(UUID.randomUUID());
        existingTransaction.setPersonType(PersonType.LEGAL);
        existingTransaction.setTransactionType(TransactionType.INCOME);
        existingTransaction.setStatus(TransactionStatus.COMPLETED);
        existingTransaction.setAmount(BigDecimal.valueOf(1000));
        existingTransaction.setComment("Old comment");
        existingTransaction.setReceiverBank("OldBank");
        existingTransaction.setCategory("OldCategory");
    }

    @Test
    void createTransaction_ShouldValidateAndSave() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(existingTransaction);

        TransactionResponseDto response = transactionService.createTransaction(fullDto);

        verify(validationService).validateTransaction(fullDto);
        verify(transactionRepository).save(any(Transaction.class));
        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualTo(existingTransaction.getAmount());
    }

    @Test
    void getTransaction_ShouldReturnTransaction_WhenFound() {
        when(transactionRepository.findById(existingTransaction.getId())).thenReturn(Optional.of(existingTransaction));

        TransactionResponseDto response = transactionService.getTransaction(existingTransaction.getId());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(existingTransaction.getId());
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
        when(transactionRepository.findById(existingTransaction.getId())).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(existingTransaction);

        TransactionResponseDto response = transactionService.updateTransaction(existingTransaction.getId(), fullDto);

        verify(validationService).validateTransaction(fullDto);
        verify(transactionRepository).save(any(Transaction.class));
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(existingTransaction.getId());
    }

    @Test
    void deleteTransaction_ShouldSetDeletedTrue() {
        when(transactionRepository.findById(existingTransaction.getId())).thenReturn(Optional.of(existingTransaction));

        transactionService.deleteTransaction(existingTransaction.getId());

        assertThat(existingTransaction.isDeleted()).isTrue();
        verify(transactionRepository).save(existingTransaction);
    }

    @Test
    void searchTransactions_withFilter_returnsTransactions() {
        TransactionFilterDto filter = new TransactionFilterDto();
        filter.setStartDate(LocalDateTime.now().minusDays(5));
        filter.setEndDate(LocalDateTime.now());
        filter.setTransactionType("INCOME");
        filter.setStatus("COMPLETED");
        filter.setCategory("SALARY");

        when(transactionRepository.findAllByFilter(any(), any(), any(), any(), any()))
                .thenReturn(List.of(existingTransaction));

        var result = transactionService.searchTransactions(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void mapToEntity_and_mapToDto_fullFields() {
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDto dto = transactionService.createTransaction(fullDto);

        assertThat(dto.getPersonType()).isEqualTo(fullDto.getPersonType());
        assertThat(dto.getTransactionType()).isEqualTo(fullDto.getTransactionType());
        assertThat(dto.getStatus()).isEqualTo(fullDto.getStatus());
        assertThat(dto.getAmount()).isEqualTo(fullDto.getAmount());
        assertThat(dto.getOperationDate()).isEqualTo(fullDto.getOperationDate());
        assertThat(dto.getComment()).isEqualTo(fullDto.getComment());
        assertThat(dto.getSenderBank()).isEqualTo(fullDto.getSenderBank());
        assertThat(dto.getAccount()).isEqualTo(fullDto.getAccount());
        assertThat(dto.getReceiverBank()).isEqualTo(fullDto.getReceiverBank());
        assertThat(dto.getReceiverAccount()).isEqualTo(fullDto.getReceiverAccount());
        assertThat(dto.getReceiverInn()).isEqualTo(fullDto.getReceiverInn());
        assertThat(dto.getCategory()).isEqualTo(fullDto.getCategory());
        assertThat(dto.getReceiverPhone()).isEqualTo(fullDto.getReceiverPhone());
    }

    @Test
    void mapToEntity_and_mapToDto_nullableFieldsNull() {
        fullDto.setComment(null);
        fullDto.setReceiverBank(null);
        fullDto.setCategory(null);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDto dto = transactionService.createTransaction(fullDto);

        assertThat(dto.getComment()).isNull();
        assertThat(dto.getReceiverBank()).isNull();
        assertThat(dto.getCategory()).isNull();
    }

    @Test
    void validateTransaction_invalidInnAndPhone_shouldThrowForInnFirst() {
        TransactionRequestDto invalidDto = new TransactionRequestDto();
        invalidDto.setReceiverInn("12345A7890");
        invalidDto.setReceiverPhone("7123456789");

        doThrow(new ValidationException("ИНН должен содержать только цифры"))
                .when(validationService).validateTransaction(invalidDto);

        ValidationException ex = assertThrows(ValidationException.class, () ->
                transactionService.createTransaction(invalidDto));
        assertEquals("ИНН должен содержать только цифры", ex.getMessage());
    }

    @Test
    void updateTransaction_shouldReplaceAllFieldsCorrectly() {
        when(transactionRepository.findById(existingTransaction.getId())).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        fullDto.setComment(null);
        fullDto.setReceiverBank("NewBank");
        fullDto.setCategory(null);
        fullDto.setAmount(BigDecimal.valueOf(2000));

        TransactionResponseDto updated = transactionService.updateTransaction(existingTransaction.getId(), fullDto);

        assertThat(updated.getComment()).isNull();
        assertThat(updated.getReceiverBank()).isEqualTo("NewBank");
        assertThat(updated.getCategory()).isNull();
        assertThat(updated.getAmount()).isEqualTo(BigDecimal.valueOf(2000));
    }
}
