import React, { createContext, useState, useEffect } from 'react';
import { login as fetchLogin, logout as fetchLogout, isAuthenticated } from './fetchApi';

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
    const success = await fetchLogin(credentials); // Используем fetchApi для входа
    if (success) {
      setIsLoggedIn(true);
    }
    return success;
  };

  const handleLogout = () => {
    fetchLogout(); // Используем fetchApi для выхода
    setIsLoggedIn(false);
  };

  return (
    <AuthContext.Provider value={{ isLoggedIn, login: handleLogin, logout: handleLogout }}>
      {children}
    </AuthContext.Provider>
  );
};
