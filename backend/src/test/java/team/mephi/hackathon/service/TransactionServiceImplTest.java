package team.mephi.hackathon.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import team.mephi.hackathon.controller.ValidationService;
import team.mephi.hackathon.dto.TransactionFilterDto;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;
import team.mephi.hackathon.entity.PersonType;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.entity.TransactionStatus;
import team.mephi.hackathon.entity.TransactionType;
import team.mephi.hackathon.exceptions.EntityNotFoundException;
import team.mephi.hackathon.exceptions.ValidationException;
import team.mephi.hackathon.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

  @Mock private TransactionRepository transactionRepository;

  @Mock private ValidationService validationService;

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
    existingTransaction.setOperationDate(LocalDateTime.now());
    existingTransaction.setTransactionType(TransactionType.INCOME);
    existingTransaction.setAmount(BigDecimal.valueOf(1000));
    existingTransaction.setStatus(TransactionStatus.NEW);
    existingTransaction.setComment("Old comment");
    existingTransaction.setSenderBank("OldBank");
    existingTransaction.setAccount("OldAcc");
    existingTransaction.setReceiverBank("OldReceiver");
    existingTransaction.setReceiverAccount("OldRecAcc");
    existingTransaction.setReceiverInn("0000000000");
    existingTransaction.setCategory("OldCategory");
    existingTransaction.setReceiverPhone("80000000000");

    ModelMapper modelMapper = new ModelMapper();
    transactionService =
        new TransactionServiceImpl(transactionRepository, validationService, modelMapper);
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
    when(transactionRepository.findById(existingTransaction.getId()))
        .thenReturn(Optional.of(existingTransaction));

    TransactionResponseDto response =
        transactionService.getTransaction(existingTransaction.getId());

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(existingTransaction.getId());
  }

  @Test
  void getTransaction_ShouldThrow_WhenNotFound() {
    UUID id = UUID.randomUUID();
    when(transactionRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> transactionService.getTransaction(id))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining(id.toString());
  }

  @Test
  void updateTransaction_ShouldValidateAndSave() {
    when(transactionRepository.findById(existingTransaction.getId()))
        .thenReturn(Optional.of(existingTransaction));
    when(transactionRepository.save(any(Transaction.class))).thenReturn(existingTransaction);

    TransactionResponseDto response =
        transactionService.updateTransaction(existingTransaction.getId(), fullDto);

    verify(validationService).validateTransaction(fullDto);
    verify(transactionRepository).save(any(Transaction.class));
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(existingTransaction.getId());
  }

  @Test
  void searchTransactions_withFilter_returnsTransactions() {
    TransactionFilterDto filter = new TransactionFilterDto();
    filter.setDateFrom(LocalDateTime.now().minusDays(5));
    filter.setDateTo(LocalDateTime.now());
    filter.setTransactionType("INCOME");
    filter.setStatus("COMPLETED");
    filter.setCategory("SALARY");

    when(transactionRepository.findAll(any(Specification.class)))
        .thenReturn(List.of(existingTransaction));

    var result = transactionService.searchTransactions(filter);

    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  void mapToEntity_and_mapToDto_fullFields() {
    when(transactionRepository.save(any(Transaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

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

    when(transactionRepository.save(any(Transaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

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
        .when(validationService)
        .validateTransaction(invalidDto);

    ValidationException ex =
        assertThrows(
            ValidationException.class, () -> transactionService.createTransaction(invalidDto));
    assertThat(ex.getMessage()).isEqualTo("ИНН должен содержать только цифры");
  }

  @Test
  void updateTransaction_shouldReplaceAllFieldsCorrectly() {
    when(transactionRepository.findById(existingTransaction.getId()))
        .thenReturn(Optional.of(existingTransaction));
    when(transactionRepository.save(any(Transaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    fullDto.setComment(null);
    fullDto.setReceiverBank("NewBank");
    fullDto.setCategory(null);
    fullDto.setAmount(BigDecimal.valueOf(2000));

    TransactionResponseDto updated =
        transactionService.updateTransaction(existingTransaction.getId(), fullDto);

    assertThat(updated.getComment()).isNull();
    assertThat(updated.getReceiverBank()).isEqualTo("NewBank");
    assertThat(updated.getCategory()).isNull();
    assertThat(updated.getAmount()).isEqualTo(BigDecimal.valueOf(2000));
  }

  // Не допущены к редактированию операции со статусами: платеж выполнен
  @Test
  void updateTransaction_forbiddenStatus_shouldThrow_onCompleted() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.COMPLETED);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));

    assertThatThrownBy(() -> transactionService.updateTransaction(id, fullDto))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("COMPLETED");
  }

  // Не допущены к редактированию операции со статусами: подтвержденная
  @Test
  void updateTransaction_forbiddenStatusConfirmed_shouldThrow() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.CONFIRMED);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));

    assertThatThrownBy(() -> transactionService.updateTransaction(id, fullDto))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("CONFIRMED");
  }

  // Не допущены к редактированию операции со статусами: в обработке
  @Test
  void updateTransaction_forbiddenStatusInProgress_shouldThrow() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.IN_PROGRESS);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));

    assertThatThrownBy(() -> transactionService.updateTransaction(id, fullDto))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("IN_PROGRESS");
  }

  // Не допущены к редактированию операции со статусами: отменена
  @Test
  void updateTransaction_forbiddenStatusCanceled_shouldThrow() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.CANCELED);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));

    assertThatThrownBy(() -> transactionService.updateTransaction(id, fullDto))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("CANCELED");
  }

  // Не допущены к редактированию операции со статусами: платеж удален
  @Test
  void updateTransaction_forbiddenStatusDeleted_shouldThrow() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.DELETED);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));

    assertThatThrownBy(() -> transactionService.updateTransaction(id, fullDto))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("DELETED");
  }

  // Не допущены к редактированию операции со статусами: возврат
  @Test
  void updateTransaction_forbiddenStatusRefund_shouldThrow() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.REFUND);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));

    assertThatThrownBy(() -> transactionService.updateTransaction(id, fullDto))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("REFUND");
  }

  // Статусы транзакций, невозможные к удалению: подтвержденная
  @Test
  void deleteTransaction_forbiddenStatusConfirmed_shouldThrow() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.CONFIRMED);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));

    assertThatThrownBy(() -> transactionService.deleteTransaction(id))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("CONFIRMED");
  }

  // Статусы транзакций, невозможные к удалению: в обработке,
  @Test
  void deleteTransaction_forbiddenStatusInProgress_shouldThrow() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.IN_PROGRESS);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));

    assertThatThrownBy(() -> transactionService.deleteTransaction(id))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("IN_PROGRESS");
  }

  // Статусы транзакций, невозможные к удалению: отменена
  @Test
  void deleteTransaction_forbiddenStatusCanceled_shouldThrow() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.CANCELED);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));

    assertThatThrownBy(() -> transactionService.deleteTransaction(id))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("CANCELED");
  }

  // Статусы транзакций, невозможные к удалению: платеж выполнен
  @Test
  void deleteTransaction_forbiddenStatusCompleted_shouldThrow() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.COMPLETED);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));

    assertThatThrownBy(() -> transactionService.deleteTransaction(id))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("COMPLETED");
  }

  // Статусы транзакций, невозможные к удалению: возврат
  @Test
  void deleteTransaction_forbiddenStatusRefund_shouldThrow() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.REFUND);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));

    assertThatThrownBy(() -> transactionService.deleteTransaction(id))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("REFUND");
  }

  @Test
  void deleteTransaction_allowedStatus_shouldMarkPaymentDeleted() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.NEW);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));
    when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    transactionService.deleteTransaction(id);

    assertThat(existingTransaction.getStatus()).isEqualTo(TransactionStatus.DELETED);
    verify(transactionRepository).save(existingTransaction);
  }

  @Test
  void updateTransaction_allEditableFields_shouldReplaceCorrectly() {
    UUID id = existingTransaction.getId();
    existingTransaction.setStatus(TransactionStatus.NEW);
    when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));
    when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    TransactionRequestDto dto = new TransactionRequestDto();
    dto.setPersonType("PHYSICAL");
    LocalDateTime newDate = LocalDateTime.now().minusDays(1);
    dto.setOperationDate(newDate);
    dto.setTransactionType("OUTCOME");
    dto.setComment("Updated comment");
    dto.setAmount(BigDecimal.valueOf(999.99));
    dto.setStatus("IN_PROGRESS");
    dto.setSenderBank("NewSender");
    dto.setAccount("NewAccount");
    dto.setReceiverBank("NewReceiver");
    dto.setReceiverAccount("NewRecAcc");
    dto.setReceiverInn("0987654321");
    dto.setCategory("NewCat");
    dto.setReceiverPhone("81230000000");

    TransactionResponseDto response = transactionService.updateTransaction(id, dto);

    assertThat(response.getPersonType()).isEqualTo("PHYSICAL");
    assertThat(response.getOperationDate()).isEqualTo(newDate);
    assertThat(response.getTransactionType()).isEqualTo("OUTCOME");
    assertThat(response.getComment()).isEqualTo("Updated comment");
    assertThat(response.getAmount()).isEqualTo(BigDecimal.valueOf(999.99));
    assertThat(response.getStatus()).isEqualTo("IN_PROGRESS");
    assertThat(response.getSenderBank()).isEqualTo("NewSender");
    assertThat(response.getAccount()).isEqualTo("NewAccount");
    assertThat(response.getReceiverBank()).isEqualTo("NewReceiver");
    assertThat(response.getReceiverAccount()).isEqualTo("NewRecAcc");
    assertThat(response.getReceiverInn()).isEqualTo("0987654321");
    assertThat(response.getCategory()).isEqualTo("NewCat");
    assertThat(response.getReceiverPhone()).isEqualTo("81230000000");
  }
}
