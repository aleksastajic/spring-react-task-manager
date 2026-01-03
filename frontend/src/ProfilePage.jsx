import React, { useEffect, useState } from 'react';
import './profile.css';
import { useAuth } from './useAuth.js';

export default function ProfilePage() {
  const { token, logout } = useAuth();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [editData, setEditData] = useState({ username: '', displayName: '', password: '' });
  const [deleteError, setDeleteError] = useState(null);

  useEffect(() => {
    async function fetchProfile() {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch('/api/users/me', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (!res.ok) throw new Error('Failed to fetch user profile');
        const data = await res.json();
        setUser(data);
        setEditData({
          username: data.username || '',
          displayName: data.displayName || '',
          password: '',
        });
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    if (token) fetchProfile();
  }, [token]);

  async function handleDelete() {
    setDeleteError(null);
    if (!window.confirm('Are you sure you want to delete your account? This cannot be undone.')) return;
    try {
      const res = await fetch('/api/users/me', {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok && res.status !== 204) throw new Error('Failed to delete account');
      logout();
      window.location.href = '/login';
    } catch (err) {
      setDeleteError(err.message);
    }
  }

  async function handleEditSave(e) {
    e.preventDefault();
    setError(null);
    try {
      const res = await fetch('/api/users/me', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          username: editData.username,
          displayName: editData.displayName,
          password: editData.password || undefined,
        }),
      });
      if (!res.ok) throw new Error('Failed to update profile');
      const data = await res.json();
      setUser(data);
      setEditMode(false);
      setEditData({ ...editData, password: '' });
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="profile-container">
      <h1 className="profile-title">User Profile</h1>
      {loading && <div className="profile-info">Loading...</div>}
      {error && <div className="profile-info" style={{ color: '#d32f2f' }}>{error}</div>}
      {deleteError && <div className="profile-info" style={{ color: '#d32f2f' }}>{deleteError}</div>}
      {user && !editMode && (
        <div className="profile-info">
          <div><strong>Username:</strong> {user.username}</div>
          <div><strong>Display Name:</strong> {user.displayName}</div>
          <div><strong>Email:</strong> {user.email}</div>
          {user.roles && <div><strong>Roles:</strong> {user.roles.join(', ')}</div>}
          <div style={{ marginTop: '2rem', display: 'flex', gap: '1rem' }}>
            <button className="profile-btn edit" onClick={() => setEditMode(true)}>Edit Profile</button>
            <button className="profile-btn delete" onClick={handleDelete}>Delete Account</button>
          </div>
        </div>
      )}
      {user && editMode && (
        <form className="profile-info" onSubmit={handleEditSave} style={{ gap: '1rem', display: 'flex', flexDirection: 'column' }}>
          <label>
            Username:
            <input
              type="text"
              value={editData.username}
              placeholder={user.username}
              onChange={e => setEditData({ ...editData, username: e.target.value })}
              required
            />
          </label>
          <label>
            Display Name:
            <input
              type="text"
              value={editData.displayName}
              placeholder={user.displayName || ''}
              onChange={e => setEditData({ ...editData, displayName: e.target.value })}
            />
          </label>
          <label>
            New Password:
            <input
              type="password"
              value={editData.password}
              placeholder="Leave blank to keep current password"
              onChange={e => setEditData({ ...editData, password: e.target.value })}
            />
          </label>
          <div style={{ display: 'flex', gap: '1rem' }}>
            <button className="profile-btn edit" type="submit">Save</button>
            <button className="profile-btn delete" type="button" onClick={() => { setEditMode(false); setEditData({ ...editData, password: '' }); }}>Cancel</button>
          </div>
        </form>
      )}
    </div>
  );
}
