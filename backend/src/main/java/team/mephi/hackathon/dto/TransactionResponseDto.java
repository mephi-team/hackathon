package team.mephi.hackathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * DTO (Data Transfer Object) для передачи данных о транзакции клиенту. Используется как ответ на
 * запросы, связанные с операциями над транзакциями.
 */
@Data
@Schema(description = "Модель транзакции, возвращаемая клиенту")
public class TransactionResponseDto {
  /** Уникальный идентификатор транзакции. Генерируется автоматически при создании транзакции. */
  @Schema(
      description = "Уникальный идентификатор транзакции",
      example = "550e8400-e29b-41d4-a716-446655440000")
  private UUID id;

  /**
   * Тип участника, совершившего транзакцию. Может быть "PHYSICAL" (физическое лицо) или "LEGAL"
   * (юридическое лицо).
   */
  @Schema(
      description = "Тип участника: PHYSICAL — физическое лицо, LEGAL — юридическое лицо",
      example = "LEGAL")
  private String personType;

  /** Дата и время совершения транзакции. Хранится в формате LocalDateTime. */
  @Schema(
      description = "Дата и время совершения операции (ISO 8601 формат)",
      example = "2025-04-05T12:30:00")
  private LocalDateTime operationDate;

  /** Тип транзакции. Может быть "INCOME" (доход) или "OUTCOME" (расход). */
  @Schema(description = "Тип транзакции: INCOME — доход, OUTCOME — расход", example = "INCOME")
  private String transactionType;

  /** Комментарий к транзакции. Опциональное поле, может быть null. */
  @Schema(description = "Комментарий к транзакции", example = "Зарплата за март")
  private String comment;

  /** Сумма транзакции. Хранится в виде BigDecimal для точности вычислений. */
  @Schema(description = "Сумма транзакции", example = "1000.00")
  private BigDecimal amount;

  /** Статус транзакции. */
  @Schema(description = "Статус транзакции:  NEW, CONFIRMED и т.д", example = "NEW")
  private String status;

  /** Наименование банка-отправителя. Указывает банк, с которого была совершена транзакция. */
  @Schema(description = "Наименование банка-отправителя", example = "Alpha Bank")
  private String senderBank;

  /** Номер расчётного счёта отправителя. Используется для идентификации счёта в системе банка. */
  @Schema(description = "Номер расчётного счёта отправителя", example = "ACC123")
  private String account;

  /** Наименование банка-получателя. Указывает банк, на который была совершена транзакция. */
  @Schema(description = "Наименование банка-получателя", example = "Beta Bank")
  private String receiverBank;

  /**
   * ИНН (Идентификационный номер налогоплательщика) получателя. Обязательно для юридических лиц.
   */
  @Schema(description = "ИНН получателя", example = "1234567890")
  private String receiverInn;

  /** Номер расчётного счёта получателя. Используется для идентификации счёта получателя. */
  @Schema(description = "Номер расчётного счёта получателя", example = "REC456")
  private String receiverAccount;

  /** Категория транзакции. */
  @Schema(description = "Категория транзакции", example = "SALARY")
  private String category;

  /** Телефон получателя. Должен соответствовать формату: ^(\+7|8)\d{10}$. */
  @Schema(
      description = "Телефон получателя в формате +7XXXXXXXXXX или 8XXXXXXXXXX",
      example = "+79876543210")
  private String receiverPhone;

  // Lombok генерирует getter'ы, setter'ы и toString()
}
