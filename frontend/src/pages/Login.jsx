import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router'
import { loginApi } from '../api/login.js'
import { notify } from '../utils/request.js'
import { Field } from '../components/ui.jsx'
import background from '../assets/login-bg02.jpeg'

export function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const [form, setForm] = useState({ username: '', password: '' })
  const [loading, setLoading] = useState(false)
  const submit = async event => {
    event.preventDefault()
    setLoading(true)
    try {
      const result = await loginApi(form)
      if (result.code !== 1 || !result.data?.token) return notify(result.msg || 'Sign-in failed.', 'error')
      localStorage.setItem('loginUser', JSON.stringify(result.data))
      notify('Signed in successfully.')
      navigate(location.state?.from || '/index', { replace: true })
    } catch { /* The request interceptor shows the error. */ }
    finally { setLoading(false) }
  }
  return <main className="login-page" style={{ backgroundImage: `linear-gradient(90deg, rgba(5,15,28,.8), rgba(5,15,28,.36)), url(${background})` }}>
    <div className="login-intro"><span className="eyebrow">TLIAS / TRAINING OPERATIONS</span><h1>Keep your training program moving.</h1><p>People, cohorts, and reports in one workspace.</p></div>
    <form className="login-card" onSubmit={submit}>
      <div className="login-mark">T</div><h2>Sign in</h2><p>Access the operations dashboard.</p>
      <Field label="Username"><input autoFocus autoComplete="username" value={form.username} minLength={3} maxLength={20} onChange={e => setForm({ ...form, username: e.target.value })} required /></Field>
      <Field label="Password"><input type="password" autoComplete="current-password" value={form.password} minLength={3} maxLength={20} onChange={e => setForm({ ...form, password: e.target.value })} required /></Field>
      <button className="button full" disabled={loading}>{loading ? 'Signing in…' : 'Sign in'}</button>
    </form>
  </main>
}
