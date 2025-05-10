package team.mephi.hackathon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * DTO (Data Transfer Object) для передачи данных о транзакции клиенту. Используется как ответ на
 * запросы, связанные с операциями над транзакциями.
 */
@Data
public class TransactionResponseDto {
  /** Уникальный идентификатор транзакции. Генерируется автоматически при создании транзакции. */
  private UUID id;

  /**
   * Тип участника, совершившего транзакцию. Может быть "PHYSICAL" (физическое лицо) или "LEGAL"
   * (юридическое лицо).
   */
  private String personType;

  /** Дата и время совершения транзакции. Хранится в формате LocalDateTime. */
  private LocalDateTime operationDate;

  /** Тип транзакции. Может быть "INCOME" (доход) или "OUTCOME" (расход). */
  private String transactionType;

  /** Комментарий к транзакции. Опциональное поле, может быть null. */
  private String comment;

  /** Сумма транзакции. Хранится в виде BigDecimal для точности вычислений. */
  private BigDecimal amount;

  /** Статус транзакции. */
  private String status;

  /** Наименование банка-отправителя. Указывает банк, с которого была совершена транзакция. */
  private String senderBank;

  /** Номер расчётного счёта отправителя. Используется для идентификации счёта в системе банка. */
  private String account;

  /** Наименование банка-получателя. Указывает банк, на который была совершена транзакция. */
  private String receiverBank;

  /**
   * ИНН (Идентификационный номер налогоплательщика) получателя. Обязательно для юридических лиц.
   */
  private String receiverInn;

  /** Номер расчётного счёта получателя. Используется для идентификации счёта получателя. */
  private String receiverAccount;

  /** Категория транзакции. */
  private String category;

  /** Телефон получателя. Должен соответствовать формату: ^(\+7|8)\d{10}$. */
  private String receiverPhone;

  // Lombok генерирует getter'ы, setter'ы и toString()
}
