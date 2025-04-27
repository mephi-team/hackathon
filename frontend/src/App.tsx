// App.tsx

import React, { useState } from 'react';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from 'react-router-dom';
import Header from './components/Header';
import TransactionForm from './components/TransactionForm';
import TransactionList from './components/TransactionList';
import Footer from './components/Footer';
import LoginPage from './components/LoginPage';
import { AuthProvider } from './components/AuthProvider';
import ProtectedRoutes from './components/ProtectedRoutes';
import DashboardPage from './components/DashboardPage';
import TransactionListPage from './components/TransactionListPage';
import { Transaction } from './interfaces/Transaction';

const App: React.FC = () => {
  const [editingTransaction, setEditingTransaction] = useState<Transaction | null>(null); // Состояние для редактируемой транзакции

  const handleEdit = (transaction: Transaction) => {
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
                          editingTransaction={editingTransaction} // Передаем редактируемую транзакцию
                        />
                        <TransactionList
                          onEdit={handleEdit} // Передаем функцию для редактирования
                        />
                      </>
                    }
                  />
                  <Route
                    path="transactions"
                    element={<TransactionListPage />}
                  />
                  <Route
                    path="report"
                    element={<DashboardPage />}
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
