import React from 'react';
import {Transaction} from "../interfaces/Transaction";

interface TransactionListProps {
  transactions: Transaction[];
  deleteTransaction: (id: string) => void;
  onEdit: (transaction: Transaction) => void; // Новая функция для редактирования
}

const TransactionList: React.FC<TransactionListProps> = ({
                                                           transactions,
                                                           deleteTransaction,
                                                           onEdit,
                                                         }) => {
  return (
    <div className="card shadow-sm mb-4">
      <div className="card-body">
        <h4 className="card-title">Список транзакций</h4>
        {transactions.length === 0 ? (
          <p className="text-muted">Нет транзакций</p>
        ) : (
          <ul className="list-group">
            {transactions.map((t) => (
              <li key={t.id} className="list-group-item d-flex justify-content-between align-items-center">
                <div>
                  <span className={`badge bg-${t.transactionType === 'income' ? 'success' : 'danger'} me-2`}>
                    {t.transactionType === 'income' ? '+ ' : '- '}
                  </span>
                  <span>{t.amount} ₽</span>
                  <small className="text-muted ms-2">({t.category})</small>
                  <small className="text-muted ms-2">{t.dateTime}</small>
                </div>
                <div>
                  <button
                    className="btn btn-sm btn-outline-primary me-2"
                    onClick={() => onEdit(t)} // Передаем данные транзакции
                  >
                    Редактировать
                  </button>
                  <button
                    className="btn btn-sm btn-outline-danger"
                    onClick={() => deleteTransaction(t.id || "")}
                  >
                    Удалить
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default TransactionList;
