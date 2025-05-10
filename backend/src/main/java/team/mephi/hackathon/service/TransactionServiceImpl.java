package team.mephi.hackathon.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import team.mephi.hackathon.controller.TransactionService;
import team.mephi.hackathon.controller.ValidationService;
import team.mephi.hackathon.dto.TransactionFilterDto;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;
import team.mephi.hackathon.entity.Transaction;
import team.mephi.hackathon.entity.TransactionStatus;
import team.mephi.hackathon.entity.TransactionType;
import team.mephi.hackathon.exceptions.EntityNotFoundException;
import team.mephi.hackathon.exceptions.ValidationException;
import team.mephi.hackathon.repository.TransactionRepository;

/**
 * Реализация сервисного слоя для работы с транзакциями. Обеспечивает бизнес-логику создания,
 * чтения, обновления и удаления транзакций.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
  /** Репозиторий для работы с сущностью {@link Transaction}. Инжектируется через конструктор. */
  private final TransactionRepository repository;

  /** Сервис валидации данных транзакции. Инжектируется через конструктор. */
  private final ValidationService validationService;

  /** Маппер для преобразования между DTO и Entity. Инжектируется через конструктор. */
  private final ModelMapper modelMapper;

  public List<Transaction> getTransactions(Specification<Transaction> specification) {
    return repository.findAll(specification);
  }

  /**
   * Создаёт новую транзакцию на основе переданных данных.
   *
   * @param dto данные о новой транзакции
   * @return созданная транзакция в виде {@link TransactionResponseDto}
   */
  public TransactionResponseDto createTransaction(TransactionRequestDto dto) {
    validationService.validateTransaction(dto);
    Transaction entity = mapToEntity(dto);
    return mapToDto(repository.save(entity));
  }

  /**
   * Возвращает транзакцию по её идентификатору.
   *
   * @param id идентификатор транзакции
   * @return транзакция в виде {@link TransactionResponseDto}
   * @throws EntityNotFoundException если транзакция не найдена
   */
  public TransactionResponseDto getTransaction(UUID id) {
    return mapToDto(getExistingTransactionOrThrowException(id));
  }

  /**
   * Получает список всех транзакций, соответствующих заданным фильтрам.
   *
   * @param filter фильтр поиска
   * @return список транзакций
   */
  public List<TransactionResponseDto> searchTransactions(TransactionFilterDto filter) {
    Specification<Transaction> spec = Specification.where(null);

    if (filter.getDateFrom() != null)
      spec =
          spec.and(
              (root, q, cb) ->
                  cb.greaterThanOrEqualTo(root.get("operationDate"), filter.getDateFrom()));

    if (filter.getDateTo() != null)
      spec =
          spec.and(
              (root, q, cb) -> cb.lessThanOrEqualTo(root.get("operationDate"), filter.getDateTo()));

    if (filter.getTransactionType() != null)
      spec =
          spec.and(
              (root, q, cb) ->
                  cb.equal(
                      root.get("transactionType"),
                      TransactionType.valueOf(filter.getTransactionType())));

    if (filter.getStatus() != null)
      spec =
          spec.and(
              (root, q, cb) ->
                  cb.equal(root.get("status"), TransactionStatus.valueOf(filter.getStatus())));

    if (filter.getCategory() != null)
      spec = spec.and((root, q, cb) -> cb.equal(root.get("category"), filter.getCategory()));

    if (filter.getAmountMin() != null)
      spec =
          spec.and(
              (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), filter.getAmountMin()));

    if (filter.getAmountMax() != null)
      spec =
          spec.and(
              (root, q, cb) -> cb.lessThanOrEqualTo(root.get("amount"), filter.getAmountMax()));

    // Фильтрация только по не удалённым
    spec = spec.and((root, q, cb) -> cb.notEqual(root.get("status"), "DELETED"));

    return repository.findAll(spec).stream().map(this::mapToDto).collect(Collectors.toList());
  }

  /**
   * Обновляет существующую транзакцию.
   *
   * @param id идентификатор транзакции, которую нужно обновить
   * @param dto новые данные транзакции
   * @return обновлённая транзакция в виде {@link TransactionResponseDto}
   * @throws ValidationException если статус транзакции запрещает редактирование
   */
  public TransactionResponseDto updateTransaction(UUID id, TransactionRequestDto dto) {
    Transaction entity = getExistingTransactionOrThrowException(id);

    switch (entity.getStatus()) {
      case COMPLETED:
      case CONFIRMED:
      case IN_PROGRESS:
      case CANCELED:
      case DELETED:
      case REFUND:
        throw new ValidationException(
            "Cannot update " + entity.getStatus().name() + " transaction");
    }

    validationService.validateTransaction(dto);
    updateEntity(entity, dto);

    return mapToDto(repository.save(entity));
  }

  /**
   * Удаляет транзакцию по её идентификатору.
   *
   * @param id идентификатор транзакции, которую нужно удалить
   * @throws ValidationException если транзакция находится в статусе, запрещающем удаление
   */
  public void deleteTransaction(UUID id) {
    Transaction entity = getExistingTransactionOrThrowException(id);

    switch (entity.getStatus()) {
      case IN_PROGRESS:
      case CANCELED:
      case CONFIRMED:
      case COMPLETED:
      case REFUND:
        throw new ValidationException(
            "Cannot delete " + entity.getStatus().name() + " transaction");
    }

    entity.setStatus(TransactionStatus.DELETED);
    repository.save(entity);
  }

  /**
   * Преобразует DTO в сущность {@link Transaction}.
   *
   * @param dto исходные данные
   * @return объект {@link Transaction}
   */
  Transaction mapToEntity(TransactionRequestDto dto) {
    return modelMapper.map(dto, Transaction.class);
  }

  /**
   * Обновляет поля сущности транзакции из DTO.
   *
   * @param entity целевая сущность
   * @param dto источник новых данных
   */
  void updateEntity(Transaction entity, TransactionRequestDto dto) {
    modelMapper.map(dto, entity);
  }

  /**
   * Преобразует сущность {@link Transaction} в DTO.
   *
   * @param entity исходная транзакция
   * @return объект {@link TransactionResponseDto}
   */
  TransactionResponseDto mapToDto(Transaction entity) {
    return modelMapper.map(entity, TransactionResponseDto.class);
  }

  /**
   * Возвращает существующую транзакцию или выбрасывает исключение.
   *
   * @param id идентификатор транзакции
   * @return найденная транзакция
   * @throws EntityNotFoundException если транзакция не найдена
   */
  private Transaction getExistingTransactionOrThrowException(UUID id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Transaction " + id + " not found."));
  }
}
