import {Transaction} from "../interfaces/Transaction";

export const fetchPdf = async () => {
  console.log('Вызов fetchPdf');
  const response = await fetch("http://localhost:8000/api/reports/transactions/pdf", {
    headers: {
      "Authorization": `Bearer ${localStorage.getItem("access_token")}`
    }
  });
  return await response.blob();
};

export const fetchExcel = async () => {
  console.log('Вызов fetchExcel');
  const response = await fetch("http://localhost:8000/api/reports/transactions/excel", {
    headers: {
      "Authorization": `Bearer ${localStorage.getItem("access_token")}`
    }
  });
  return await response.blob();
};

export const fetchTransactions = async () => {
  console.log('Вызов fetchTransactions');
  const response = await fetch("http://localhost:8000/api/transactions", {
    headers: {
      "Authorization": `Bearer ${localStorage.getItem("access_token")}`
    }
  });
  return await response.json();
};

export const addTransaction = async (newTransaction: Transaction) => {
  console.log('Добавление транзакции:', newTransaction); // Отладочный лог
  await fetch("http://localhost:8000/api/transactions", {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${localStorage.getItem("access_token")}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(newTransaction)
  });
};

export const updateTransaction = async (updatedTransaction: Transaction) => {
  await fetch(`http://localhost:8000/api/transactions/${updatedTransaction.id}`, {
    method: "PUT",
    headers: {
      "Authorization": `Bearer ${localStorage.getItem("access_token")}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(updatedTransaction)
  });
};

export const deleteTransaction = async (id: string) => {
  console.log('Удаление транзакции с ID:', id); // Отладочный лог
  await fetch(`http://localhost:8000/api/transactions/${id}`, {
    method: "DELETE",
    headers: {
      "Authorization": `Bearer ${localStorage.getItem("access_token")}`,
      "Content-Type": "application/json"
    }
  });
};


// Функция для имитации входа
export const login = async ({ username, password }: { username: string; password: string }) => {
  const response = await fetch("http://localhost:8080/realms/hackathon-realm/protocol/openid-connect/token", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
    },
    body: `client_id=hackathon-frontend&grant_type=password&username=${username}&password=${password}`
  })
  const json = await response.json();
  if (response.status === 200) {
    localStorage.setItem('isLoggedIn', 'true'); // Сохраняем статус авторизации
    localStorage.setItem('access_token', json["access_token"]);
    return true;
  }
  return false;
};

// Проверка авторизации
export const isAuthenticated = () => {
  return localStorage.getItem('isLoggedIn') === 'true';
};

// Выход из системы
export const logout = async () => {
  localStorage.removeItem('isLoggedIn');
  localStorage.removeItem('access_token');
};
