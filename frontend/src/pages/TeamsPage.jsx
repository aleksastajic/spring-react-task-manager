import React, { useEffect, useState } from 'react'
import { apiGetProfile, apiListTeamsForUser, apiCreateTeam } from '../api'
import Card from '../components/Card'
import Button from '../components/Button'

export default function TeamsPage() {
  const [loading, setLoading] = useState(true)
  const [teams, setTeams] = useState([])
  const [error, setError] = useState(null)
  const [creating, setCreating] = useState(false)
  const [form, setForm] = useState({ name: '', description: '' })

  useEffect(() => {
    let mounted = true
    async function load() {
      setLoading(true)
      try {
        const profile = await apiGetProfile()
        const list = await apiListTeamsForUser(profile.id)
        if (mounted) setTeams(list || [])
      } catch (err) {
        if (mounted) setError(err.message || 'Failed to load teams')
      } finally {
        if (mounted) setLoading(false)
      }
    }
    load()
    return () => { mounted = false }
  }, [])

  return (
    <div className="app-container">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-semibold">Teams</h1>
          <div className="text-sm text-gray-600">Create and manage teams</div>
        </div>
        <div className="hidden md:block">
          <Button variant="primary" onClick={() => setCreating(c => !c)}>{creating ? 'Close' : 'New Team'}</Button>
        </div>
      </div>

      {/* Create form (inline on small screens, card on desktop) */}
      {creating && (
        <div className="mb-4">
          <Card className="p-4">
            <form className="grid grid-cols-1 md:grid-cols-3 gap-3 items-end" onSubmit={async (e) => {
              e.preventDefault();
              setError(null);
              try {
                await apiCreateTeam(form);
                setForm({ name: '', description: '' });
                const profile = await apiGetProfile();
                const list = await apiListTeamsForUser(profile.id);
                setTeams(list || []);
              } catch (err) { setError(err.message || 'Failed to create team'); }
            }}>
              <div className="md:col-span-1">
                <label className="text-sm">Name</label>
                <input required value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} className="mt-1 w-full border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent text-text" />
              </div>
              <div className="md:col-span-1">
                <label className="text-sm">Description</label>
                <input value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} className="mt-1 w-full border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent text-text" />
              </div>
              <div className="md:col-span-1 flex gap-2">
                <Button variant="primary" type="submit">Create</Button>
                <Button variant="ghost" onClick={() => { setCreating(false); setForm({ name: '', description: '' }); }}>Cancel</Button>
              </div>
            </form>
            {error && <div className="text-sm text-red-600 mt-2">{error}</div>}
          </Card>
        </div>
      )}

      {loading && <div className="text-sm text-gray-500">Loading teams…</div>}
      {error && <div className="text-sm text-red-600">{error}</div>}

      {!loading && !error && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {teams.length === 0 && <div className="text-gray-600">No teams yet.</div>}
          {teams.map(t => (
            <Card key={t.id} className="p-4">
              <div className="flex items-start justify-between">
                <div>
                  <div className="font-semibold text-lg">{t.name}</div>
                  <div className="text-gray-600 mt-2">{t.description}</div>
                  <div className="text-sm text-gray-500 mt-3">Members: {t.members ? t.members.length : 0}</div>
                </div>
                <div className="flex flex-col gap-2 ml-4">
                  <Button variant="ghost" className="whitespace-nowrap">View</Button>
                  <Button variant="ghost" className="whitespace-nowrap">Manage</Button>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}