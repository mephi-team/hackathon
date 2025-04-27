import React, {useEffect, useState} from "react";
import {isAuthenticated, login as fetchLogin, logout as fetchLogout} from "./fetchApi";
import {AuthContext} from "./AuthContext";

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

  const handleLogout = async () => {
    await fetchLogout(); // Используем fetchApi для выхода
    setIsLoggedIn(false);
  };

  return (
    <AuthContext.Provider value={{ isLoggedIn, login: handleLogin, logout: handleLogout }}>
      {children}
    </AuthContext.Provider>
  );
};
