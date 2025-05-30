package team.mephi.hackathon.entity;

/**
 * Перечисление, представляющее типы транзакций. Используется для классификации операций как
 * доходные или расходные.
 */
public enum TransactionType {
  /** Доходная транзакция (INCOME). Обозначает поступление средств на счёт. */
  INCOME,
  /** Расходная транзакция (OUTCOME). Обозначает списание средств со счёта. */
  OUTCOME
}
