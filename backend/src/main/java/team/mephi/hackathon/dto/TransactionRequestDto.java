package team.mephi.hackathon.dto;

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
public class TransactionRequestDto {
  /** Уникальный идентификатор транзакции */
  private UUID id;

  /**
   * Тип участника: физическое или юридическое лицо. Допустимые значения: "PHYSICAL", "LEGAL".
   * Обязательное поле.
   */
  @NotBlank(message = "Тип лица обязателен")
  private String personType; // PHYSICAL / LEGAL

  /** Дата и время совершения операции. Формат даты — LocalDateTime. Обязательное поле. */
  @NotNull(message = "Дата операции обязательна")
  private LocalDateTime operationDate;

  /**
   * Тип транзакции: доход или расход. Допустимые значения: "INCOME", "OUTCOME". Обязательное поле.
   */
  @NotBlank(message = "Тип транзакции обязателен")
  private String transactionType; // INCOME / OUTCOME

  /** Комментарий к транзакции. Необязательное поле. Максимальная длина — 500 символов. */
  @Size(max = 500, message = "Комментарий слишком длинный")
  private String comment;

  /** Сумма транзакции. Должна быть положительным числом. Обязательное поле. */
  @NotNull(message = "Сумма обязательна")
  @Digits(integer = 15, fraction = 5, message = "Некорректный формат суммы")
  private BigDecimal amount;

  /** Статус транзакции. */
  @NotBlank(message = "Статус обязателен")
  private String status; // NEW, CONFIRMED и т.д.

  /** Наименование банка-отправителя. Обязательное поле. */
  @NotBlank(message = "Банк отправителя обязателен")
  private String senderBank;

  /** Номер расчётного счёта отправителя. Обязательное поле. */
  @NotBlank(message = "Счет обязателен")
  private String account;

  /** Наименование банка-получателя. Обязательное поле. */
  @NotBlank(message = "Банк получателя обязателен")
  private String receiverBank;

  /** ИНН (Идентификационный номер налогоплательщика) получателя. Обязательное поле. */
  @Pattern(regexp = "\\d{10}|\\d{12}", message = "Некорректный ИНН")
  private String receiverInn;

  /** Номер расчётного счёта получателя. Обязательное поле. */
  @NotBlank(message = "Расчетный счет обязателен")
  private String receiverAccount;

  /** Категория транзакции. Обязательное поле. */
  @NotBlank(message = "Категория обязательна")
  private String category;

  /** Телефон получателя. Должен соответствовать формату: ^(\+7|8)\d{10}$. Обязательное поле. */
  @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "Некорректный телефон")
  private String receiverPhone;
}
