package team.mephi.hackathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * DTO (Data Transfer Object) для создания и обновления транзакции. Используется как входной
 * параметр при работе с REST API.
 */
@Data
@Schema(description = "Данные для создания или обновления транзакции")
public class TransactionRequestDto {
  /** Уникальный идентификатор транзакции */
  @Schema(
      description = "Уникальный идентификатор транзакции",
      example = "550e8400-e29b-41d4-a716-446655440000")
  private UUID id;

  /**
   * Тип участника: физическое или юридическое лицо. Допустимые значения: "PHYSICAL", "LEGAL".
   * Обязательное поле.
   */
  @Schema(
      description = "Тип участника: PHYSICAL — физическое лицо, LEGAL — юридическое лицо",
      example = "LEGAL",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Тип лица обязателен")
  private String personType; // PHYSICAL / LEGAL

  /** Дата и время совершения операции. Формат даты — LocalDateTime. Обязательное поле. */
  @Schema(
      description = "Дата и время совершения операции (ISO 8601 формат)",
      example = "2025-04-05T12:30:00",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Дата операции обязательна")
  private LocalDateTime operationDate;

  /**
   * Тип транзакции: доход или расход. Допустимые значения: "INCOME", "OUTCOME". Обязательное поле.
   */
  @Schema(
      description = "Тип транзакции: INCOME — доход, OUTCOME — расход",
      example = "INCOME",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Тип транзакции обязателен")
  private String transactionType; // INCOME / OUTCOME

  /** Комментарий к транзакции. Необязательное поле. Максимальная длина — 500 символов. */
  @Schema(
      description = "Комментарий к транзакции (максимум 500 символов)",
      example = "Зарплата за март",
      maxLength = 500)
  @Size(max = 500, message = "Комментарий слишком длинный")
  private String comment;

  /** Сумма транзакции. Должна быть положительным числом. Обязательное поле. */
  @Schema(
      description = "Сумма транзакции (положительное число)",
      example = "1000.00",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull(message = "Сумма обязательна")
  @Digits(integer = 15, fraction = 5, message = "Некорректный формат суммы")
  private BigDecimal amount;

  /** Статус транзакции. */
  @Schema(
      description = "Статус транзакции: NEW, CONFIRMED и т.д.",
      example = "NEW",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Статус обязателен")
  private String status; // NEW, CONFIRMED и т.д.

  /** Наименование банка-отправителя. Обязательное поле. */
  @Schema(
      description = "Наименование банка-отправителя",
      example = "Alpha Bank",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Банк отправителя обязателен")
  private String senderBank;

  /** Номер расчётного счёта отправителя. Обязательное поле. */
  @Schema(
      description = "Номер расчётного счёта отправителя",
      example = "ACC123",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Счет обязателен")
  private String account;

  /** Наименование банка-получателя. Обязательное поле. */
  @Schema(
      description = "Наименование банка-получателя",
      example = "Beta Bank",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Банк получателя обязателен")
  private String receiverBank;

  /** ИНН (Идентификационный номер налогоплательщика) получателя. Обязательное поле. */
  @Schema(
      description = "ИНН получателя",
      example = "1234567890",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @Pattern(regexp = "\\d{10}|\\d{12}", message = "Некорректный ИНН")
  private String receiverInn;

  /** Номер расчётного счёта получателя. Обязательное поле. */
  @Schema(
      description = "Номер расчётного счёта получателя",
      example = "REC456",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Расчетный счет обязателен")
  private String receiverAccount;

  /** Категория транзакции. Обязательное поле. */
  @Schema(
      description = "Категория транзакции",
      example = "SALARY",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotBlank(message = "Категория обязательна")
  private String category;

  /** Телефон получателя. Должен соответствовать формату: ^(\+7|8)\d{10}$. Обязательное поле. */
  @Schema(
      description = "Телефон получателя в формате +7XXXXXXXXXX или 8XXXXXXXXXX",
      example = "+79876543210",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "Некорректный телефон")
  private String receiverPhone;
}
