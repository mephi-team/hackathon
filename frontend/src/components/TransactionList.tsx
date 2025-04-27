import React, {useState, useEffect, use} from 'react';
import { Transaction } from '../interfaces/Transaction';
import {
  fetchTransactions,
  deleteTransaction as fetchDeleteTransaction,
} from '../components/fetchApi';

interface TransactionListProps {
  onEdit: (transaction: Transaction) => void; // Новая функция для редактирования
}

const TransactionList: React.FC<TransactionListProps> = ({ onEdit }) => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [editingTransaction, setEditingTransaction] = useState(false)
  // Загрузка транзакций при монтировании
  useEffect(() => {
    const loadTransactions = async () => {
      const data = await fetchTransactions();
      setTransactions(data);
      setEditingTransaction(false);
    };
    loadTransactions();
  }, [editingTransaction]);

  const handleDeleteTransaction = async (id: string) => {
    try {
      await fetchDeleteTransaction(id); // Удаляем через fetchApi
      setTransactions((prev) => prev.filter((t) => t.id !== id)); // Обновляем состояние
    } catch (error) {
      console.error("Ошибка при удалении транзакции:", error);
    }
  };

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
                  <small className="text-muted ms-2">{t.operationDate}</small>
                </div>
                <div>
                  <button
                    className="btn btn-sm btn-outline-primary me-2"
                    onClick={() => {onEdit(t);setEditingTransaction(true);}} // Передаем данные транзакции
                  >
                    Редактировать
                  </button>
                  <button
                    className="btn btn-sm btn-outline-danger"
                    onClick={() => handleDeleteTransaction(t.id || "")}
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
