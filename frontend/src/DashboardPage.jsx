import React from 'react';
import './dashboard.css';

export default function DashboardPage() {
  return (
    <div className="dashboard-container">
      <h1 className="dashboard-title">Dashboard</h1>
      <div className="dashboard-welcome">Welcome to your task manager dashboard!</div>
      <div className="dashboard-cards">
        <div className="dashboard-card">
          <h2>Teams</h2>
          <p>View and manage your teams.</p>
        </div>
        <div className="dashboard-card">
          <h2>Tasks</h2>
          <p>See your tasks and their status.</p>
        </div>
        <div className="dashboard-card">
          <h2>Profile</h2>
          <p>View and edit your user profile.</p>
        </div>
      </div>
    </div>
  );
}
