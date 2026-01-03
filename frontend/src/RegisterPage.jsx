import React, { useState } from 'react';
import './auth.css';

export default function RegisterPage({ onRegister, onSwitchToLogin }) {
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    displayName: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess(false);
    try {
      const res = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form)
      });
      if (!res.ok) throw new Error('Registration failed');
      setSuccess(true);
      if (onRegister) onRegister();
    } catch {
      setError('Registration failed');
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-title">Create Account</div>
      <form className="auth-form" onSubmit={handleSubmit}>
        <input
          className="auth-input"
          name="username"
          placeholder="Username"
          value={form.username}
          onChange={handleChange}
          required
        />
        <input
          className="auth-input"
          name="email"
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          required
        />
        <input
          className="auth-input"
          name="password"
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
          required
        />
        <input
          className="auth-input"
          name="displayName"
          placeholder="Display Name"
          value={form.displayName}
          onChange={handleChange}
          required
        />
        <button className="auth-btn" type="submit">Register</button>
      </form>
      {error && <div className="auth-error">{error}</div>}
      {success && <div className="auth-success">Registration successful! You can now log in.</div>}
      <div className="auth-link" onClick={onSwitchToLogin}>
        Already have an account? Login
      </div>
    </div>
  );
}
