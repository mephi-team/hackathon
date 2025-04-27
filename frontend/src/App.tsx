// App.tsx

import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Header from './components/Header';
import TransactionForm from './components/TransactionForm';
import TransactionList from './components/TransactionList';
import Footer from './components/Footer';
import LoginPage from './components/LoginPage';
import { AuthProvider } from './components/AuthContext';
import {
  fetchTransactions,
  addTransaction as fetchAddTransaction,
  updateTransaction as fetchUpdateTransaction,
  deleteTransaction as fetchDeleteTransaction,
} from './components/fetchApi';
import ProtectedRoutes from "./components/ProtectedRoutes";
import DashboardPage from "./components/DashboardPage";
import TransactionListPage from "./components/TransactionListPage";
import {Transaction} from "./interfaces/Transaction";

const App: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [editingTransaction, setEditingTransaction] = useState(null); // Состояние для редактируемой транзакции

  // Загрузка транзакций при монтировании
  useEffect(() => {
    const loadTransactions = async () => {
      const data = await fetchTransactions();
      setTransactions(data);
    };
    loadTransactions();
  }, []);

  const handleAddTransaction = async (newTransaction: any) => {
    try {
      await fetchAddTransaction(newTransaction);
      setTransactions((prev) => [...prev, newTransaction]);
    } catch (error) {
      console.error('Ошибка при добавлении транзакции:', error);
    }
  };

  const handleUpdateTransaction = async (updatedTransaction: any) => {
    try {
      await fetchUpdateTransaction(updatedTransaction); // Обновляем через fetchApi
      setTransactions((prev) =>
        prev.map((t) => (t.id === updatedTransaction.id ? updatedTransaction : t))
      ); // Обновляем состояние
      setEditingTransaction(null); // Сбрасываем редактируемую транзакцию
    } catch (error) {
      console.error('Ошибка при обновлении транзакции:', error);
    }
  };

  const handleDeleteTransaction = async (id: string) => {
    try {
      await fetchDeleteTransaction(id); // Удаляем через fetchApi
      setTransactions((prev) => prev.filter((t) => t.id !== id)); // Обновляем состояние
    } catch (error) {
      console.error('Ошибка при удалении транзакции:', error);
    }
  };

  const handleEdit = (transaction: any) => {
    setEditingTransaction(transaction); // Устанавливаем редактируемую транзакцию
  };

  return (
    <AuthProvider>
      <Router>
        <div className="d-flex flex-column min-vh-100">
          <Header />
          <main className="flex-grow-1 d-flex justify-content-center align-items-center p-4">{/* Растягиваем основной контент */}
            <div className="container w-100 h-100">
              <Routes>
                {/* Страница логина */}
                <Route path="/login" element={<LoginPage />} />

                {/* Защищенные маршруты */}
                <Route path="/" element={<ProtectedRoutes />}>
                  <Route
                    index
                    element={
                      <>
                        <TransactionForm
                          addTransaction={handleAddTransaction}
                          updateTransaction={handleUpdateTransaction}
                          editingTransaction={editingTransaction} // Передаем редактируемую транзакцию
                        />
                        <TransactionList
                          transactions={transactions}
                          deleteTransaction={handleDeleteTransaction}
                          onEdit={handleEdit} // Передаем функцию для редактирования
                        />
                      </>
                    }
                  />
                  <Route
                    path="transactions"
                    element={<TransactionListPage transactions={transactions} />}
                  />
                  <Route
                    path="report"
                    element={<DashboardPage transactions={transactions} />}
                  />
                  {/*<Route index element={<Navigate to="transactions" />} />*/}
                </Route>

                {/* Перенаправление на логин по умолчанию */}
                <Route path="*" element={<Navigate to="/login" />} />
              </Routes>
            </div>
          </main>
          <Footer />
        </div>
      </Router>
    </AuthProvider>
  );
};

export default App;
