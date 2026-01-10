import React from 'react';

export default function DashboardPage() {
  return (
    <div className="app-container">
      <div className="grid grid-cols-1 gap-4">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-semibold">Dashboard</h1>
          <div className="text-sm text-gray-600">Welcome back</div>
        </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="card-surface p-4">Quick stats (coming soon)</div>
          <div className="card-surface p-4">Recent activity (coming soon)</div>
        </div>
      </div>
    </div>
  );
}
