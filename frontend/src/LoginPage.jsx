import React, { useState } from 'react';
import './auth.css';

export default function LoginPage({ onLogin, onSwitchToRegister }) {
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ usernameOrEmail, password })
      });
      if (!res.ok) throw new Error('Login failed');
      const data = await res.json();
      if (onLogin) onLogin(data.token);
    } catch {
      setError('Invalid credentials');
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-title">Sign In</div>
      <form className="auth-form" onSubmit={handleSubmit}>
        <input
          className="auth-input"
          type="text"
          placeholder="Username or Email"
          value={usernameOrEmail}
          onChange={e => setUsernameOrEmail(e.target.value)}
          required
        />
        <input
          className="auth-input"
          type="password"
          placeholder="Password"
          value={password}
          onChange={e => setPassword(e.target.value)}
          required
        />
        <button className="auth-btn" type="submit">Login</button>
      </form>
      {error && <div className="auth-error">{error}</div>}
      <div className="auth-link" onClick={onSwitchToRegister}>
        Don't have an account? Register
      </div>
    </div>
  );
}
