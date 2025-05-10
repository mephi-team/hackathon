package team.mephi.hackathon.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для фильтрации транзакций по различным критериям. Используется в
 * контроллере и сервисе транзакций при выполнении поисковых запросов.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterDto {
  /** Название банка-отправителя. Фильтрует транзакции по отправителю. */
  private String senderBank;

  /** Название банка-получателя. Фильтрует транзакции по получателю. */
  private String receiverBank;

  /** Категория транзакции. Позволяет фильтровать по типам операций. */
  private String category;

  /** Тип транзакции: "INCOME" или "OUTCOME". Фильтрует по направлению денежного потока. */
  private String transactionType;

  /**
   * Статус транзакции: "NEW", "COMPLETED", "CANCELED" и др. Фильтрует транзакции по их текущему
   * состоянию.
   */
  private String status;

  /** Начальная дата периода фильтрации (включительно). Формат: LocalDateTime. */
  private LocalDateTime dateFrom;

  /** Конечная дата периода фильтрации (включительно). Формат: LocalDateTime. */
  private LocalDateTime dateTo;

  /**
   * Минимальная сумма транзакции для фильтрации. Используется для поиска операций выше заданной
   * суммы.
   */
  private BigDecimal amountMin;

  /**
   * Максимальная сумма транзакции для фильтрации. Используется для поиска операций ниже заданной
   * суммы.
   */
  private BigDecimal amountMax;

  // Lombok генерирует getter'ы, setter'ы и toString()
}
