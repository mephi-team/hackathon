import { createContext } from 'react';

export type AuthContextProps = {
  isLoggedIn: boolean;
  login: (credentials: { username: string; password: string }) => Promise<boolean>;
  logout: () => Promise<void>;
};

export const AuthContext = createContext<AuthContextProps>({
  isLoggedIn: false,
  login: () => Promise.resolve(false),
  logout: () => Promise.resolve()
});
