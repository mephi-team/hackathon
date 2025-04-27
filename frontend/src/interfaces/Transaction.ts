// interfaces/Transaction.ts

export interface Transaction {
  id?: string | null; // Уникальный ID
  personType: string; // Тип лица
  operationDate: string; // Дата и время операции
  transactionType: string; // Тип транзакции
  comment: string; // Комментарий к операции
  amount: number; // Сумма с точностью до 5 знаков
  status: string; // Статус операции
  senderBank: string; // Банк отправителя
  account: string; // Счет поступления/списания
  receiverBank: string; // Банк получателя
  receiverInn: string; // ИНН получателя
  receiverAccount: string; // Расчетный счет получателя
  category: string; // Категория
  receiverPhone: string; // Телефон получателя
}
