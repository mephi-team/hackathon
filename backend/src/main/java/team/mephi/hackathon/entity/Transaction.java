package team.mephi.hackathon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Сущность, представляющая транзакцию. Хранит информацию о финансовой операции между двумя
 * сторонами.
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
  /** Уникальный идентификатор транзакции. Генерируется автоматически при создании. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /** Тип участника: физическое или юридическое лицо. Допустимые значения: "PHYSICAL", "LEGAL". */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PersonType personType;

  /** Дата и время совершения операции. Формат: LocalDateTime. */
  @Column(nullable = false)
  private LocalDateTime operationDate;

  /** Тип транзакции: доход или расход. Допустимые значения: "INCOME", "OUTCOME". */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType transactionType;

  /** Комментарий к транзакции. Необязательное поле. Максимальная длина — 500 символов. */
  @Column(length = 500)
  private String comment;

  /** Сумма транзакции. Хранится как BigDecimal для обеспечения точности вычислений. */
  @Column(nullable = false, precision = 20, scale = 5)
  private BigDecimal amount;

  /**
   * Статус транзакции. Допустимые значения: "NEW", "IN_PROGRESS", "COMPLETED", "CANCELED",
   * "DELETED", "REFUND".
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionStatus status;

  /** Наименование банка-отправителя. Обязательное поле. */
  @Column(nullable = false)
  private String senderBank;

  /** Номер расчётного счёта отправителя. Обязательное поле. */
  @Column(nullable = false)
  private String account;

  /** Наименование банка-получателя. Обязательное поле. */
  @Column(nullable = false)
  private String receiverBank;

  /** ИНН (Идентификационный номер налогоплательщика) получателя. Может быть null. */
  @Column(length = 12)
  private String receiverInn;

  /** Номер расчётного счёта получателя. Обязательное поле. */
  @Column(nullable = false)
  private String receiverAccount;

  /** Категория транзакции. */
  @Column(nullable = false)
  private String category;

  /** Телефон получателя. Должен соответствовать формату: ^(\+7|8)\d{10}$. */
  @Column(length = 20)
  private String receiverPhone;
}
