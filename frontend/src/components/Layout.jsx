import { useState } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router'
import { updatePasswordApi } from '../api/emp.js'
import { notify } from '../utils/request.js'
import { Field, Modal } from './ui.jsx'

const groups = [
  { label: 'Overview', links: [['/index', 'Dashboard']] },
  { label: 'Training', links: [['/clazz', 'Cohorts'], ['/student', 'Learners']] },
  { label: 'Organization', links: [['/dept', 'Departments'], ['/emp', 'Employees']] },
  { label: 'Reports', links: [['/report/emp', 'Employee analytics'], ['/report/student', 'Learner analytics']] },
  { label: 'Activity', links: [['/log/operate', 'Operation log'], ['/log/login', 'Login log']] }
]

export function Layout() {
  const navigate = useNavigate()
  const [passwordOpen, setPasswordOpen] = useState(false)
  const [passwords, setPasswords] = useState({ oldPassword: '', newPassword: '' })
  const [saving, setSaving] = useState(false)
  let user = null
  try { user = JSON.parse(localStorage.getItem('loginUser')) } catch { /* No active session. */ }

  const signOut = () => {
    if (!window.confirm('Sign out now?')) return
    localStorage.removeItem('loginUser')
    navigate('/login', { replace: true })
  }
  const changePassword = async event => {
    event.preventDefault()
    if (!passwords.oldPassword || !passwords.newPassword) return notify('Enter both passwords.', 'error')
    setSaving(true)
    try {
      const result = await updatePasswordApi(passwords)
      if (result.code !== 1) return notify(result.msg || 'Could not change password.', 'error')
      notify('Password changed.')
      setPasswordOpen(false)
      setPasswords({ oldPassword: '', newPassword: '' })
    } catch { /* The request interceptor shows the error. */ }
    finally { setSaving(false) }
  }

  return <div className="shell">
    <aside className="sidebar">
      <div className="brand"><span className="brand-mark">T</span><div><strong>TLIAS</strong><small>TRAINING OPERATIONS</small></div></div>
      <nav aria-label="Main navigation">
        {groups.map(group => <div className="nav-group" key={group.label}>
          <p>{group.label}</p>
          {group.links.map(([path, label]) => <NavLink key={path} to={path} className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>{label}</NavLink>)}
        </div>)}
      </nav>
    </aside>
    <div className="shell-main">
      <header className="topbar">
        <span className="topbar-label">Operations workspace <span className="online-dot" /></span>
        <div className="top-actions"><span className="user-name">{user?.name || user?.username}</span><button className="text-button" onClick={() => setPasswordOpen(true)}>Change password</button><button className="text-button" onClick={signOut}>Sign out</button></div>
      </header>
      <main className="content"><Outlet /></main>
    </div>
    <Modal open={passwordOpen} title="Change password" onClose={() => setPasswordOpen(false)}>
      <form onSubmit={changePassword} className="form-stack">
        <Field label="Current password"><input type="password" value={passwords.oldPassword} onChange={e => setPasswords({ ...passwords, oldPassword: e.target.value })} required /></Field>
        <Field label="New password"><input type="password" value={passwords.newPassword} onChange={e => setPasswords({ ...passwords, newPassword: e.target.value })} required /></Field>
        <div className="modal-actions"><button type="button" className="button secondary" onClick={() => setPasswordOpen(false)}>Cancel</button><button className="button" disabled={saving}>{saving ? 'Saving…' : 'Update password'}</button></div>
      </form>
    </Modal>
  </div>
}
