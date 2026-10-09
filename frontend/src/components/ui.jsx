import { useEffect } from 'react'

export const formatTime = value => value ? String(value).replace('T', ' ').slice(0, 19) : '—'
export const formatDate = value => value ? String(value).slice(0, 10) : '—'
export const genders = { 1: 'Male', 2: 'Female' }
export const degrees = { 1: 'Middle School', 2: 'High School', 3: 'Associate Degree', 4: 'Bachelor Degree', 5: 'Master Degree', 6: 'Doctorate' }
export const jobs = { 1: 'Cohort Lead', 2: 'Instructor', 3: 'Student Affairs Manager', 4: 'Curriculum Manager', 5: 'Advisor' }
export const subjects = { 1: 'Java', 2: 'Frontend', 3: 'Data Engineering', 4: 'Python', 5: 'Go', 6: 'Embedded Systems' }

export function PageHeader({ eyebrow, title, description, actions }) {
  return <header className="page-header"><div><span className="eyebrow">{eyebrow}</span><h1>{title}</h1>{description && <p>{description}</p>}</div>{actions && <div className="header-actions">{actions}</div>}</header>
}

export function Field({ label, children, wide }) {
  return <label className={`field ${wide ? 'wide' : ''}`}><span>{label}</span>{children}</label>
}

export function Select({ value, onChange, options, placeholder = 'All', required = false }) {
  return <select value={value ?? ''} onChange={e => onChange(e.target.value === '' ? null : Number(e.target.value))} required={required}>
    <option value="">{placeholder}</option>
    {options.map(([key, label]) => <option value={key} key={key}>{label}</option>)}
  </select>
}

export function Modal({ open, title, onClose, children, wide }) {
  useEffect(() => {
    if (!open) return
    const close = e => { if (e.key === 'Escape') onClose() }
    window.addEventListener('keydown', close)
    return () => window.removeEventListener('keydown', close)
  }, [open, onClose])
  if (!open) return null
  return <div className="modal-backdrop" onMouseDown={e => { if (e.target === e.currentTarget) onClose() }}>
    <section className={`modal ${wide ? 'modal-wide' : ''}`} role="dialog" aria-modal="true" aria-label={title}>
      <header><h2>{title}</h2><button type="button" className="icon-button" aria-label="Close" onClick={onClose}>×</button></header>
      {children}
    </section>
  </div>
}

export function Pager({ page, pageSize, total, onPage, onSize }) {
  const pages = Math.max(1, Math.ceil(total / pageSize))
  return <div className="pager"><span>{total} records</span><div><button className="button secondary small" disabled={page <= 1} onClick={() => onPage(page - 1)}>Previous</button><span>Page {page} of {pages}</span><button className="button secondary small" disabled={page >= pages} onClick={() => onPage(page + 1)}>Next</button></div>{onSize && <select aria-label="Rows per page" value={pageSize} onChange={e => onSize(Number(e.target.value))}><option value="10">10 / page</option><option value="20">20 / page</option><option value="50">50 / page</option></select>}</div>
}

export function Stat({ label, value, tone }) {
  return <div className={`stat ${tone || ''}`}><span>{label}</span><strong>{value ?? '—'}</strong></div>
}

