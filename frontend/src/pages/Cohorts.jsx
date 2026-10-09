import { useCallback, useEffect, useState } from 'react'
import { addClazz, deleteClazz, getClazzById, getClazzList, updateClazz } from '../api/clazz.js'
import { queryAllApi as listEmployees } from '../api/emp.js'
import { notify } from '../utils/request.js'
import { Field, formatDate, formatTime, Modal, PageHeader, Pager, Select, subjects } from '../components/ui.jsx'

const blank = () => ({ name: '', room: '', beginDate: '', endDate: '', subject: '', masterId: '' })

export function Cohorts() {
  const [draft, setDraft] = useState({ name: '', begin: '', end: '' })
  const [filter, setFilter] = useState({ name: '', begin: '', end: '' })
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [rows, setRows] = useState([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(true)
  const [employees, setEmployees] = useState([])
  const [form, setForm] = useState(null)
  const [editing, setEditing] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    try { const result = await getClazzList({ ...filter, page, pageSize }); if (result.code === 1) { setRows(result.data.rows || []); setTotal(result.data.total || 0) } }
    catch { /* Already reported. */ }
    finally { setLoading(false) }
  }, [filter, page, pageSize])
  useEffect(() => { load() }, [load])
  useEffect(() => { listEmployees().then(result => { if (result.code === 1) setEmployees(result.data || []) }).catch(() => {}) }, [])

  const edit = async id => {
    try { const result = await getClazzById(id); if (result.code === 1) { setForm({ ...blank(), ...result.data }); setEditing(true) } }
    catch { /* Already reported. */ }
  }
  const save = async event => {
    event.preventDefault()
    if (form.endDate < form.beginDate) return notify('End date must be on or after start date.', 'error')
    try {
      const payload = { ...form, masterId: form.masterId || null }
      const result = editing ? await updateClazz(payload) : await addClazz(payload)
      if (result.code !== 1) return notify(result.msg || 'Could not save cohort.', 'error')
      notify(editing ? 'Cohort updated.' : 'Cohort added.')
      setForm(null); load()
    } catch { /* Already reported. */ }
  }
  const remove = async row => {
    if (!window.confirm(`Delete cohort “${row.name}”?`)) return
    try { const result = await deleteClazz(row.id); if (result.code === 1) { notify('Cohort deleted.'); if (rows.length === 1 && page > 1) setPage(page - 1); else load() } }
    catch { /* Already reported. */ }
  }
  const update = (key, value) => setForm(current => ({ ...current, [key]: value }))

  return <><PageHeader eyebrow="TRAINING" title="Cohorts" description="Schedule courses and assign cohort leads." actions={<button className="button" onClick={() => { setEditing(false); setForm(blank()) }}>Add cohort</button>} />
    <section className="panel filters"><form onSubmit={e => { e.preventDefault(); setPage(1); setFilter({ ...draft }) }} className="filter-row"><Field label="Cohort name"><input value={draft.name} onChange={e => setDraft({ ...draft, name: e.target.value })} placeholder="Search cohorts" /></Field><Field label="End date from"><input type="date" value={draft.begin} onChange={e => setDraft({ ...draft, begin: e.target.value })} /></Field><Field label="End date through"><input type="date" value={draft.end} onChange={e => setDraft({ ...draft, end: e.target.value })} /></Field><button className="button">Search</button><button type="button" className="button secondary" onClick={() => { const empty = { name: '', begin: '', end: '' }; setDraft(empty); setFilter(empty); setPage(1) }}>Reset</button></form></section>
    <section className="panel"><div className="panel-head"><h2>Cohort list</h2><span>{total} records</span></div><div className="table-scroll"><table><thead><tr><th>Name</th><th>Room</th><th>Lead</th><th>Subject</th><th>Start</th><th>End</th><th>Status</th><th>Updated</th><th>Actions</th></tr></thead><tbody>{rows.map(row => <tr key={row.id}><td className="emphasis">{row.name}</td><td>{row.room || '—'}</td><td>{row.masterName || '—'}</td><td>{subjects[row.subject] || '—'}</td><td>{formatDate(row.beginDate)}</td><td>{formatDate(row.endDate)}</td><td><span className={`pill ${row.status === 'In Progress' ? 'green' : row.status === 'Not Started' ? 'blue' : ''}`}>{row.status || '—'}</span></td><td>{formatTime(row.updateTime)}</td><td className="row-actions"><button className="link-button" onClick={() => edit(row.id)}>Edit</button><button className="link-button danger-text" onClick={() => remove(row)}>Delete</button></td></tr>)}</tbody></table>{loading ? <p className="empty">Loading cohorts…</p> : !rows.length && <p className="empty">No cohorts match your search.</p>}</div><Pager page={page} pageSize={pageSize} total={total} onPage={setPage} onSize={size => { setPageSize(size); setPage(1) }} /></section>
    <Modal open={form !== null} title={editing ? 'Edit cohort' : 'Add cohort'} onClose={() => setForm(null)} wide><form onSubmit={save}><div className="form-grid"><Field label="Cohort name"><input value={form?.name || ''} onChange={e => update('name', e.target.value)} required maxLength={30} /></Field><Field label="Classroom"><input value={form?.room || ''} onChange={e => update('room', e.target.value)} required maxLength={20} /></Field><Field label="Start date"><input type="date" value={form?.beginDate || ''} onChange={e => update('beginDate', e.target.value)} required /></Field><Field label="End date"><input type="date" value={form?.endDate || ''} onChange={e => update('endDate', e.target.value)} required /></Field><Field label="Subject"><Select value={form?.subject} onChange={value => update('subject', value)} options={Object.entries(subjects)} placeholder="Select a subject" required /></Field><Field label="Cohort lead"><Select value={form?.masterId} onChange={value => update('masterId', value)} options={employees.map(employee => [employee.id, employee.name])} placeholder="Select a lead" /></Field></div><div className="modal-actions"><button type="button" className="button secondary" onClick={() => setForm(null)}>Cancel</button><button className="button">Save cohort</button></div></form></Modal>
  </>
}
