import React, { useState, useRef, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate, Outlet, useLocation, useNavigate } from 'react-router-dom';
import LoginPage from './LoginPage';
import RegisterPage from './RegisterPage';
import DashboardPage from './DashboardPage';
import TeamsPage from './TeamsPage';
import TasksPage from './TasksPage';
import ProfilePage from './ProfilePage';
import { useAuth } from './useAuth.js';
import ProfileIcon from './ProfileIcon';
import HamburgerIcon from './HamburgerIcon';
import './App.css';

function RequireAuth() {
  const { isLoggedIn } = useAuth();
  const location = useLocation();
  return isLoggedIn ? <Outlet /> : <Navigate to="/login" state={{ from: location }} replace />;
}

function LoginPageWrapper({ login }) {
  const navigate = useNavigate();
  return (
    <LoginPage
      onLogin={token => { login(token); navigate('/'); }}
      onSwitchToRegister={() => navigate('/register')}
    />
  );
}

function RegisterPageWrapper() {
  const navigate = useNavigate();
  return (
    <RegisterPage
      onRegister={() => navigate('/login')}
      onSwitchToLogin={() => navigate('/login')}
    />
  );
}

function App() {
  const { isLoggedIn, logout, login } = useAuth();
  const [showDropdown, setShowDropdown] = useState(false);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    }
    if (showDropdown) {
      document.addEventListener('mousedown', handleClickOutside);
    } else {
      document.removeEventListener('mousedown', handleClickOutside);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [showDropdown]);

  const navLinks = (
    <>
      <a href="/" className="nav-link" onClick={() => setDrawerOpen(false)}>Dashboard</a>
      <a href="/teams" className="nav-link" onClick={() => setDrawerOpen(false)}>Teams</a>
      <a href="/tasks" className="nav-link" onClick={() => setDrawerOpen(false)}>Tasks</a>
    </>
  );

  return (
    <BrowserRouter>
      {isLoggedIn && (
        <>
          <nav className="navbar">
            <button className="hamburger-btn" onClick={() => setDrawerOpen(true)}>
              <HamburgerIcon />
            </button>
            <div className="nav-left">
              {navLinks}
            </div>
            <div className="nav-right">
              <div className="profile-dropdown" ref={dropdownRef}>
                <button className="profile-btn" onClick={() => setShowDropdown(v => !v)}>
                  <ProfileIcon />
                </button>
                {showDropdown && (
                  <div className="dropdown-menu">
                    <a href="/profile" className="dropdown-item">Profile</a>
                    <button className="dropdown-item" onClick={logout}>Logout</button>
                  </div>
                )}
              </div>
            </div>
          </nav>
          {/* Side Drawer for mobile */}
          <div className={`side-drawer${drawerOpen ? ' open' : ''}`} onClick={() => setDrawerOpen(false)}>
            <div className="drawer-content" onClick={e => e.stopPropagation()}>
              <button className="drawer-close" onClick={() => setDrawerOpen(false)}>&times;</button>
              <nav className="drawer-nav">
                {navLinks}
                <a href="/profile" className="nav-link" onClick={() => setDrawerOpen(false)}>Profile</a>
                <button className="nav-link" onClick={() => { setDrawerOpen(false); logout(); }}>Logout</button>
              </nav>
            </div>
          </div>
        </>
      )}
      <div className="main-content">
        <Routes>
          <Route path="/login" element={<LoginPageWrapper login={login} />} />
          <Route path="/register" element={<RegisterPageWrapper />} />
          <Route element={<RequireAuth />}>
            <Route path="/" element={<DashboardPage />} />
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/teams" element={<TeamsPage />} />
            <Route path="/tasks" element={<TasksPage />} />
            <Route path="/profile" element={<ProfilePage />} />
          </Route>
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
