// components/TransactionListPage.tsx

import React, { useState, useEffect } from 'react';
import TransactionFilter from './TransactionFilter';
import { Transaction } from '../interfaces/Transaction';
import { FilterData } from '../interfaces/FilterData';
import { fetchTransactions } from '../components/fetchApi';

const TransactionListPage: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [filteredTransactions, setFilteredTransactions] =
    useState<Transaction[]>(transactions);
  // Загрузка транзакций при монтировании
  useEffect(() => {
    const loadTransactions = async () => {
      const data = await fetchTransactions();
      setTransactions(data);
    };
    loadTransactions();
  }, []);

  // Логика фильтрации
  const handleFilter = (filters: FilterData) => {
    const filtered = transactions.filter((t) => {
      const matchesSenderBank =
        !filters.senderBank || t.senderBank === filters.senderBank;

      const matchesReceiverBank =
        !filters.receiverBank || t.receiverBank === filters.receiverBank;

      // Обработка дат
      const matchesDate =
        (!filters.date?.[0] || new Date(t.operationDate) >= new Date(filters.date?.[0] || new Date(t.operationDate))) &&
        (!filters.date?.[1] || new Date(t.operationDate) <= new Date(filters.date?.[1] || new Date(t.operationDate)));

      const matchesStatus =
        !filters.status || t.status === filters.status;

      const matchesInn =
        !filters.inn || t.receiverInn === filters.inn;

      const matchesAmountRange =
        !filters.amountRange ||
        (t.amount >= filters.amountRange[0] && t.amount <= filters.amountRange[1]);

      const matchesTransactionType =
        !filters.transactionType || t.transactionType === filters.transactionType;

      const matchesCategory =
        !filters.category || t.category === filters.category;

      return (
        matchesSenderBank &&
        matchesReceiverBank &&
        matchesDate &&
        matchesStatus &&
        matchesInn &&
        matchesAmountRange &&
        matchesTransactionType &&
        matchesCategory
      );
    });
    setFilteredTransactions(filtered);
  };

  // Сброс фильтров
  const handleReset = () => {
    setFilteredTransactions(transactions);
  };

  return (
    <div className="container">
      <h3 className="mb-4">Список транзакций</h3>

      {/* Фильтр */}
      <TransactionFilter onFilter={handleFilter} onReset={handleReset} />

      {/* Таблица транзакций */}
      <div className="card shadow-sm mt-4">
        <div className="card-body">
          <table className="table table-striped">
            <thead>
              <tr>
                <th>ID</th>
                <th>Дата и время</th>
                <th>Тип операции</th>
                <th>Сумма</th>
                <th>Статус</th>
                <th>Банк отправителя</th>
                <th>Банк получателя</th>
                <th>Категория</th>
              </tr>
            </thead>
            <tbody>
              {filteredTransactions.length > 0 ? (
                filteredTransactions.map((t) => (
                  <tr key={t.id}>
                    <td>{t.id}</td>
                    <td>{t.operationDate}</td>
                    <td>{t.transactionType}</td>
                    <td>{t.amount}</td>
                    <td>{t.status}</td>
                    <td>{t.senderBank}</td>
                    <td>{t.receiverBank}</td>
                    <td>{t.category}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={8} className="text-center">
                    Нет данных
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default TransactionListPage;
