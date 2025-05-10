package team.mephi.hackathon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Фильтр для поиска транзакций по различным критериям")
public class TransactionFilterDto {
  /** Название банка-отправителя. Фильтрует транзакции по отправителю. */
  @Schema(description = "Имя банка-отправителя", example = "Alpha Bank")
  private String senderBank;

  /** Название банка-получателя. Фильтрует транзакции по получателю. */
  @Schema(description = "Имя банка-получателя", example = "Beta Bank")
  private String receiverBank;

  /** Категория транзакции. Позволяет фильтровать по типам операций. */
  @Schema(description = "Категория транзакции", example = "SALARY")
  private String category;

  /** Тип транзакции: "INCOME" или "OUTCOME". Фильтрует по направлению денежного потока. */
  @Schema(description = "Тип транзакции: INCOME или OUTCOME", example = "INCOME")
  private String transactionType;

  /**
   * Статус транзакции: "NEW", "COMPLETED", "CANCELED" и др. Фильтрует транзакции по их текущему
   * состоянию.
   */
  @Schema(description = "Статус транзакции", example = "NEW")
  private String status;

  /** Начальная дата периода фильтрации (включительно). Формат: LocalDateTime. */
  @Schema(description = "Начальная дата периода (включительно)", example = "2025-04-01T12:00:00")
  private LocalDateTime dateFrom;

  /** Конечная дата периода фильтрации (включительно). Формат: LocalDateTime. */
  @Schema(description = "Конечная дата периода (включительно)", example = "2025-04-30T12:00:00")
  private LocalDateTime dateTo;

  /**
   * Минимальная сумма транзакции для фильтрации. Используется для поиска операций выше заданной
   * суммы.
   */
  @Schema(description = "Минимальная сумма транзакции", example = "100.00")
  private BigDecimal amountMin;

  /**
   * Максимальная сумма транзакции для фильтрации. Используется для поиска операций ниже заданной
   * суммы.
   */
  @Schema(description = "Максимальная сумма транзакции", example = "10000.00")
  private BigDecimal amountMax;

  // Lombok генерирует getter'ы, setter'ы и toString()
}
