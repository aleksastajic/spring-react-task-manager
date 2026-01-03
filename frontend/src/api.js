// Central API utility for all backend calls
// Handles JWT, errors, and provides functions for all endpoints

const API_BASE = '/api';

function getToken() {
  return localStorage.getItem('jwt');
}

function authHeaders() {
  const token = getToken();
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function handleResponse(res) {
  if (!res.ok) {
    let msg = 'Unknown error';
    try { msg = (await res.json()).message || res.statusText; } catch { /* ignore */ }
    throw new Error(msg);
  }
  if (res.status === 204) return null;
  return res.json();
}

// Auth
export async function apiLogin({ usernameOrEmail, password }) {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ usernameOrEmail, password })
  });
  return handleResponse(res);
}

export async function apiRegister({ username, email, password, displayName }) {
  const res = await fetch(`${API_BASE}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, email, password, displayName })
  });
  return handleResponse(res);
}

export async function apiGetProfile() {
  const res = await fetch(`${API_BASE}/users/me`, {
    headers: { ...authHeaders() }
  });
  return handleResponse(res);
}

export async function apiUpdateProfile({ username, displayName, password }) {
  const res = await fetch(`${API_BASE}/users/me`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json', ...authHeaders() },
    body: JSON.stringify({ username, displayName, password })
  });
  return handleResponse(res);
}

export async function apiDeleteProfile() {
  const res = await fetch(`${API_BASE}/users/me`, {
    method: 'DELETE',
    headers: { ...authHeaders() }
  });
  return handleResponse(res);
}

// Add more endpoints (teams, tasks, etc.) as needed
