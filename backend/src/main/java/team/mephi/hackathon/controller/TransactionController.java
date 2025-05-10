package team.mephi.hackathon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.mephi.hackathon.dto.TransactionFilterDto;
import team.mephi.hackathon.dto.TransactionRequestDto;
import team.mephi.hackathon.dto.TransactionResponseDto;

/**
 * REST-контроллер для работы с транзакциями. Обеспечивает CRUD операции и фильтрацию транзакций
 * через HTTP API.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Транзакции", description = "API для управления финансовыми транзакциями")
public class TransactionController {

  /** Сервис для работы с транзакциями. Инжектируется через конструктор. */
  private final TransactionService service;

  /** Логгер для записи информации об ошибках и событиях. */
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

  /**
   * Создаёт новую транзакцию.
   *
   * @param dto данные транзакции от клиента
   * @return созданная транзакция в виде {@link TransactionResponseDto}
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Создать транзакцию",
      description = "Принимает данные транзакции и сохраняет её в системе",
      responses = {
        @ApiResponse(responseCode = "201", description = "Транзакция успешно создана"),
        @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации входных данных",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Транзакция с таким ID уже существует",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  public TransactionResponseDto create(@Valid @RequestBody TransactionRequestDto dto) {
    LOGGER.debug("Received transaction: {}", dto);
    return service.createTransaction(dto);
  }

  /**
   * Получает список всех транзакций с возможностью фильтрации.
   *
   * @param senderBank имя банка-отправителя (опционально)
   * @param receiverBank имя банка-получателя (опционально)
   * @param dateFrom начало временного диапазона (ISO 8601, опционально)
   * @param dateTo конец временного диапазона (ISO 8601, опционально)
   * @param amountMin минимальная сумма транзакции (опционально)
   * @param amountMax максимальная сумма транзакции (опционально)
   * @param category категория транзакции (опционально)
   * @return список {@link TransactionResponseDto} — найденные транзакции
   */
  @ApiResponse(responseCode = "200", description = "Successful operation")
  @GetMapping
  @Operation(
      summary = "Получить все транзакции",
      description = "Возвращает список транзакций с возможностью фильтрации")
  public List<TransactionResponseDto> search(
      @RequestParam(required = false) String senderBank,
      @RequestParam(required = false) String receiverBank,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime dateFrom,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime dateTo,
      @RequestParam(required = false) BigDecimal amountMin,
      @RequestParam(required = false) BigDecimal amountMax,
      @RequestParam(required = false) String category) {
    TransactionFilterDto filter = new TransactionFilterDto();
    filter.setSenderBank(senderBank);
    filter.setReceiverBank(receiverBank);
    filter.setDateFrom(dateFrom);
    filter.setDateTo(dateTo);
    filter.setAmountMin(amountMin);
    filter.setAmountMax(amountMax);
    filter.setCategory(category);
    return service.searchTransactions(filter);
  }

  /**
   * Получает транзакцию по её идентификатору.
   *
   * @param id уникальный идентификатор транзакции
   * @return транзакция в виде {@link TransactionResponseDto}
   */
  @GetMapping("/{id}")
  @Operation(
      summary = "Получить транзакцию по ID",
      description = "Возвращает полную информацию о транзакции по её уникальному идентификатору",
      responses = {
        @ApiResponse(responseCode = "200", description = "Транзакция найдена"),
        @ApiResponse(
            responseCode = "404",
            description = "Транзакция не найдена",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  public TransactionResponseDto getById(@PathVariable UUID id) {
    return service.getTransaction(id);
  }

  /**
   * Обновляет существующую транзакцию.
   *
   * @param id идентификатор транзакции, которую нужно обновить
   * @param dto новые данные транзакции
   * @return обновлённая транзакция в виде {@link TransactionResponseDto}
   */
  @PutMapping("/{id}")
  @Operation(
      summary = "Обновить транзакцию",
      description =
          "Обновляет данные существующей транзакции, если она находится в редактируемом статусе",
      responses = {
        @ApiResponse(responseCode = "200", description = "Транзакция успешно обновлена"),
        @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации или недопустимый статус",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Транзакция не найдена",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  public TransactionResponseDto update(
      @PathVariable UUID id, @Valid @RequestBody TransactionRequestDto dto) {
    return service.updateTransaction(id, dto);
  }

  /**
   * Удаляет транзакцию по её идентификатору.
   *
   * @param id идентификатор транзакции, которую нужно удалить
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(
      summary = "Удалить транзакцию",
      description = "Логически или физически удаляет транзакцию из системы",
      responses = {
        @ApiResponse(responseCode = "204", description = "Транзакция удалена"),
        @ApiResponse(
            responseCode = "400",
            description = "Недопустимый статус для удаления",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Транзакция не найдена",
            content = @Content(schema = @Schema(implementation = String.class)))
      })
  public void delete(@PathVariable UUID id) {
    service.deleteTransaction(id);
  }
}
