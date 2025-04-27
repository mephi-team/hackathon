import React, { useContext } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { AuthContext, type AuthContextProps } from './AuthContext';

const Header: React.FC = () => {
  const context = useContext<AuthContextProps>(AuthContext);

  if (!context) {
    throw new Error('AuthContext must be used within an AuthProvider');
  }

  const { isLoggedIn, logout } = context;
  const location = useLocation();

  const handleLogout = () => {
    logout();
    window.location.href = '/login';
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light shadow-sm">
      <div className="container">
        <Link className="navbar-brand" to="/">
          <strong>Финансовый мониторинг</strong>
        </Link>
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto">
            {isLoggedIn && (
              <>
                <li className="nav-item">
                  <Link
                    className={`nav-link ${location.pathname.includes('/report') ? 'active' : ''}`}
                    to="/report"
                  >
                    Дашборды
                  </Link>
                </li>
                <li className="nav-item">
                  <Link
                    className={`nav-link ${location.pathname.includes('/transactions') ? 'active' : ''}`}
                    to="/transactions"
                  >
                    Транзакции
                  </Link>
                </li>
              </>
            )}
          </ul>
          {isLoggedIn && (
            <button className="btn btn-outline-danger" onClick={handleLogout}>
              Выйти
            </button>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Header;
