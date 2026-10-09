import { lazy, Suspense, useEffect, useState } from 'react'
import { Navigate, Route, Routes, useLocation } from 'react-router'
import { Layout } from './components/Layout.jsx'
import { Dashboard } from './pages/Dashboard.jsx'
import { Login } from './pages/Login.jsx'
import { Departments } from './pages/Departments.jsx'
import { Employees } from './pages/Employees.jsx'
import { Cohorts } from './pages/Cohorts.jsx'
import { Learners } from './pages/Learners.jsx'
import { NotFound } from './pages/NotFound.jsx'

const EmployeeReport = lazy(() => import('./pages/Reports.jsx').then(module => ({ default: module.EmployeeReport })))
const LearnerReport = lazy(() => import('./pages/Reports.jsx').then(module => ({ default: module.LearnerReport })))
const LoginLog = lazy(() => import('./pages/Logs.jsx').then(module => ({ default: module.LoginLog })))
const OperationLog = lazy(() => import('./pages/Logs.jsx').then(module => ({ default: module.OperationLog })))

function RequireAuth({ children }) {
  const location = useLocation()
  try {
    if (JSON.parse(localStorage.getItem('loginUser'))?.token) return children
  } catch { /* A malformed session is treated as signed out. */ }
  return <Navigate to="/login" replace state={{ from: location.pathname }} />
}

export default function App() {
  const [notice, setNotice] = useState(null)
  useEffect(() => {
    const show = event => setNotice({ id: Date.now(), ...event.detail })
    window.addEventListener('app:notice', show)
    return () => window.removeEventListener('app:notice', show)
  }, [])
  useEffect(() => {
    if (!notice) return
    const timer = setTimeout(() => setNotice(null), 4500)
    return () => clearTimeout(timer)
  }, [notice])

  return <>
    <Suspense fallback={<div className="route-loading">Loading…</div>}><Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<RequireAuth><Layout /></RequireAuth>}>
        <Route index element={<Navigate to="/index" replace />} />
        <Route path="index" element={<Dashboard />} />
        <Route path="clazz" element={<Cohorts />} />
        <Route path="student" element={<Learners />} />
        <Route path="dept" element={<Departments />} />
        <Route path="emp" element={<Employees />} />
        <Route path="report/emp" element={<EmployeeReport />} />
        <Route path="report/student" element={<LearnerReport />} />
        <Route path="log/login" element={<LoginLog />} />
        <Route path="log/operate" element={<OperationLog />} />
      </Route>
      <Route path="*" element={<NotFound />} />
    </Routes></Suspense>
    {notice && <div role="status" className={`toast ${notice.type || 'info'}`} onClick={() => setNotice(null)}>{notice.message}</div>}
  </>
}
