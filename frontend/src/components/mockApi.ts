// Моковые данные
import { Transaction } from "../interfaces/Transaction";

let mockTransactions : Transaction[] = [
  {
    id: "1",
    personType: "",
    operationDate: '2023-11-01',
    transactionType: 'income',
    comment: "",
    amount: 5000,
    status: "",
    senderBank: "Bank1",
    account: "",
    receiverBank: "Bank2",
    receiverInn: "11111",
    receiverAccount: "",
    category: 'Зарплата',
    receiverPhone: 'Октябрьская зарплата',
  },
  {
    id: "2",
    personType: "",
    operationDate: '2023-11-01',
    transactionType: 'income',
    comment: "",
    amount: 1000,
    status: "",
    senderBank: "Bank1",
    account: "",
    receiverBank: "Bank2",
    receiverInn: "11111",
    receiverAccount: "",
    category: 'Зарплата',
    receiverPhone: 'Октябрьская зарплата',
  },
  {
    id: "3",
    personType: "",
    operationDate: '2023-10-01',
    transactionType: 'expense',
    comment: "",
    amount: 3000,
    status: "",
    senderBank: "Bank2",
    account: "",
    receiverBank: "Bank1",
    receiverInn: "11111",
    receiverAccount: "",
    category: 'Зарплата',
    receiverPhone: 'Октябрьская зарплата',
  },
  {
    id: "4",
    personType: "",
    operationDate: '2023-10-01',
    transactionType: 'expense',
    comment: "",
    amount: 500,
    status: "",
    senderBank: "Bank1",
    account: "",
    receiverBank: "Bank3",
    receiverInn: "11111",
    receiverAccount: "",
    category: 'Зарплата',
    receiverPhone: 'Октябрьская зарплата',
  },
  {
    id: "5",
    personType: "",
    operationDate: '2023-10-01',
    transactionType: 'expense',
    comment: "",
    amount: 50,
    status: "",
    senderBank: "Bank3",
    account: "",
    receiverBank: "Bank1",
    receiverInn: "11111",
    receiverAccount: "",
    category: 'Зарплата',
    receiverPhone: 'Октябрьская зарплата',
  },
  {
    id: "6",
    personType: "",
    operationDate: '2023-10-01',
    transactionType: 'expense',
    comment: "",
    amount: 2000,
    status: "",
    senderBank: "Bank4",
    account: "",
    receiverBank: "Bank1",
    receiverInn: "11111",
    receiverAccount: "",
    category: 'Зарплата',
    receiverPhone: 'Октябрьская зарплата',
  },
];

// Моковые данные для пользователей
const mockUsers = [
  { username: 'user1', password: 'password123' },
  { username: 'admin', password: 'admin123' },
];

// Задержка для имитации сетевого запроса
const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

// Моковые функции API
export const fetchTransactions = async () => {
  console.log('Вызов fetchTransactions'); // Отладочный лог
  await delay(500); // Имитация задержки сети
  return [...mockTransactions]; // Возвращаем копию массива для предотвращения мутаций
};

export const addTransaction = async (newTransaction: Transaction) => {
  console.log('Добавление транзакции:', newTransaction); // Отладочный лог
  await delay(500); // Имитация задержки сети
  mockTransactions.push(newTransaction);
};

export const updateTransaction = async (updatedTransaction: Transaction) => {
  await delay(500); // Имитация задержки сети
  const index = mockTransactions.findIndex((t) => t.id === updatedTransaction.id);
  if (index !== -1) {
    mockTransactions[index] = updatedTransaction; // Обновляем транзакцию
  }
};

export const deleteTransaction = async (id: string) => {
  console.log('Удаление транзакции с ID:', id); // Отладочный лог
  await delay(500); // Имитация задержки сети
  mockTransactions = mockTransactions.filter((t) => t.id !== id);
};


// Функция для имитации входа
export const login = async ({ username, password }: { username: string; password: string }) => {
  await delay(500); // Имитация задержки сети
  const user = mockUsers.find(
    (u) => u.username === username && u.password === password
  );
  if (user) {
    localStorage.setItem('isLoggedIn', 'true'); // Сохраняем статус авторизации
    return true;
  }
  return false;
};

// Проверка авторизации
export const isAuthenticated = () => {
  return localStorage.getItem('isLoggedIn') === 'true';
};

// Выход из системы
export const logout = () => {
  localStorage.removeItem('isLoggedIn');
};
