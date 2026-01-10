import React, { useEffect, useState } from 'react'
import { apiGetProfile, apiListTasksByUser, apiListTasksByTeam, apiCreateTask, apiListTeamsForUser } from '../api'
import Card from '../components/Card'
import Button from '../components/Button'

export default function TasksPage() {
  const [loading, setLoading] = useState(true)
  const [tasks, setTasks] = useState([])
  const [error, setError] = useState(null)
  const [creating, setCreating] = useState(false)
  const [form, setForm] = useState({ title: '', description: '', teamId: '', priority: 'MEDIUM' })
  const [teams, setTeams] = useState([])

  useEffect(() => {
    let mounted = true
    async function load() {
      setLoading(true)
      try {
        const profile = await apiGetProfile()
        const teamList = await apiListTeamsForUser(profile.id).catch(() => [])
        setTeams(teamList || [])
        // prefer listing tasks assigned to user; fallback to tasks in first team
        let list = []
        try { list = await apiListTasksByUser(profile.id) } catch  { /* ignore */ }
        if ((!list || list.length === 0) && profile.teams && profile.teams.length > 0) {
          const teamId = profile.teams[0].id
          list = await apiListTasksByTeam(teamId)
        }
        if (mounted) setTasks(list || [])
      } catch (err) {
        if (mounted) setError(err.message || 'Failed to load tasks')
      } finally {
        if (mounted) setLoading(false)
      }
    }
    load()
    return () => { mounted = false }
  }, [])

  return (
    <div className="tasks-container app-container">
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-semibold">Tasks</h1>
        <form className="flex gap-2 items-center" onSubmit={async (e) => {
          e.preventDefault();
          setCreating(true); setError(null);
          try {
            const dto = { title: form.title, description: form.description, teamId: form.teamId ? parseInt(form.teamId) : undefined, priority: form.priority, assigneeIds: [] };
            const profile = await apiGetProfile();
            if (!dto.assigneeIds || dto.assigneeIds.length === 0) dto.assigneeIds = [profile.id];
            await apiCreateTask(dto);
            setForm({ title: '', description: '', teamId: '', priority: 'MEDIUM' });
            // reload tasks
            const updated = await apiListTasksByUser(profile.id).catch(async () => {
              if (dto.teamId) return apiListTasksByTeam(dto.teamId)
              return []
            })
            setTasks(updated || [])
          } catch (err) { setError(err.message || 'Failed to create task') }
          setCreating(false);
        }}>
          <input placeholder="Title" value={form.title} onChange={e => setForm({ ...form, title: e.target.value })} className="border rounded px-2 py-1" required />
          <select value={form.teamId} onChange={e => setForm({ ...form, teamId: e.target.value })} className="border rounded px-2 py-1">
            <option value="">Unassigned</option>
            {teams.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
          </select>
          <Button type="submit" variant="primary">{creating ? 'Creating...' : 'Create'}</Button>
        </form>
      </div>
      {loading && <div className="text-sm text-gray-500">Loading tasks…</div>}
      {error && <div className="text-sm text-red-600">{error}</div>}
      {!loading && !error && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {tasks.length === 0 && <div className="text-gray-600">No tasks found.</div>}
          {tasks.map(t => (
            <Card key={t.id} className="p-4">
              <div className="font-semibold text-lg">{t.title}</div>
              <div className="text-gray-600 mt-2">{t.description}</div>
              <div className="text-sm text-gray-500 mt-3">Status: {t.status} • Priority: {t.priority}</div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}

