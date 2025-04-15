import React, { createContext, useState, useEffect } from 'react';
import { login as mockLogin, logout as mockLogout, isAuthenticated } from './mockApi';

export const AuthContext = createContext<any | undefined>(undefined);

interface AuthProviderProps {
  children?: React.ReactNode; // Добавьте тип для children
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(isAuthenticated());

  useEffect(() => {
    setIsLoggedIn(isAuthenticated());
  }, []);

  const handleLogin = async (credentials: { username: string; password: string }) => {
    const success = await mockLogin(credentials); // Используем mockApi для входа
    if (success) {
      setIsLoggedIn(true);
    }
    return success;
  };

  const handleLogout = () => {
    mockLogout(); // Используем mockApi для выхода
    setIsLoggedIn(false);
  };

  return (
    <AuthContext.Provider value={{ isLoggedIn, login: handleLogin, logout: handleLogout }}>
      {children}
    </AuthContext.Provider>
  );
};
