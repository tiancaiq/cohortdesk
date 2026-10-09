import { useCallback, useEffect, useState } from 'react'
import { addStudent, deleteStudents, getStudentById, getStudentList, updateStudent } from '../api/student.js'
import { getAllClazz } from '../api/clazz.js'
import { notify } from '../utils/request.js'
import { degrees, Field, formatTime, genders, Modal, PageHeader, Pager, Select } from '../components/ui.jsx'

const blank = () => ({ name: '', no: '', gender: 1, phone: '', degree: '', clazzId: '', idCard: '', isCollege: 0, address: '', graduationDate: '', violationCount: 0, violationScore: 0 })
const emptyFilter = () => ({ name: '', degree: '', clazzId: '' })

export function Learners() {
  const [draft, setDraft] = useState(emptyFilter)
  const [filter, setFilter] = useState(emptyFilter)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [rows, setRows] = useState([])
  const [total, setTotal] = useState(0)
  const [cohorts, setCohorts] = useState([])
  const [selected, setSelected] = useState([])
  const [loading, setLoading] = useState(true)
  const [form, setForm] = useState(null)
  const [editing, setEditing] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    try { const result = await getStudentList({ ...filter, page, pageSize }); if (result.code === 1) { setRows(result.data.rows || []); setTotal(result.data.total || 0); setSelected([]) } }
    catch { /* Already reported. */ }
    finally { setLoading(false) }
  }, [filter, page, pageSize])
  useEffect(() => { load() }, [load])
  useEffect(() => { getAllClazz().then(result => { if (result.code === 1) setCohorts(result.data || []) }).catch(() => {}) }, [])

  const edit = async id => {
    try { const result = await getStudentById(id); if (result.code === 1) { setEditing(true); setForm({ ...blank(), ...result.data }) } }
    catch { /* Already reported. */ }
  }
  const remove = async ids => {
    if (!ids.length || !window.confirm(`Delete ${ids.length} learner${ids.length === 1 ? '' : 's'}? This cannot be undone.`)) return
    try { const result = await deleteStudents(ids); if (result.code === 1) { notify('Learner records deleted.'); if (rows.length === ids.length && page > 1) setPage(page - 1); else load() } }
    catch { /* Already reported. */ }
  }
  const save = async event => {
    event.preventDefault()
    try {
      const payload = { ...form, graduationDate: form.graduationDate || null }
      const result = editing ? await updateStudent(payload) : await addStudent(payload)
      if (result.code !== 1) return notify(result.msg || 'Could not save learner.', 'error')
      notify(editing ? 'Learner updated.' : 'Learner added.')
      setForm(null); load()
    } catch { /* Already reported. */ }
  }
  const update = (key, value) => setForm(current => ({ ...current, [key]: value }))
  const toggle = id => setSelected(current => current.includes(id) ? current.filter(value => value !== id) : [...current, id])
  const allSelected = rows.length > 0 && rows.every(row => selected.includes(row.id))

  return <><PageHeader eyebrow="TRAINING" title="Learners" description="Find, enroll, and update learner records." actions={<button className="button" onClick={() => { setEditing(false); setForm(blank()) }}>Add learner</button>} />
    <section className="panel filters"><form className="filter-row" onSubmit={e => { e.preventDefault(); setPage(1); setFilter({ ...draft }) }}><Field label="Name"><input placeholder="Learner name" value={draft.name} onChange={e => setDraft({ ...draft, name: e.target.value })} /></Field><Field label="Education"><Select value={draft.degree} onChange={value => setDraft({ ...draft, degree: value })} options={Object.entries(degrees)} /></Field><Field label="Cohort"><Select value={draft.clazzId} onChange={value => setDraft({ ...draft, clazzId: value })} options={cohorts.map(row => [row.id, row.name])} /></Field><button className="button">Search</button><button className="button secondary" type="button" onClick={() => { setDraft(emptyFilter()); setFilter(emptyFilter()); setPage(1) }}>Reset</button></form></section>
    <section className="panel"><div className="panel-head"><h2>Learner directory</h2><button className="button danger small" disabled={!selected.length} onClick={() => remove(selected)}>Delete selected ({selected.length})</button></div><div className="table-scroll"><table><thead><tr><th><input type="checkbox" aria-label="Select all learners" checked={allSelected} onChange={() => setSelected(allSelected ? [] : rows.map(row => row.id))} /></th><th>Name</th><th>Learner ID</th><th>Cohort</th><th>Gender</th><th>Phone</th><th>Education</th><th>Updated</th><th>Actions</th></tr></thead><tbody>{rows.map(row => <tr key={row.id}><td><input type="checkbox" aria-label={`Select ${row.name}`} checked={selected.includes(row.id)} onChange={() => toggle(row.id)} /></td><td className="emphasis">{row.name}</td><td>{row.no}</td><td>{row.clazzName || '—'}</td><td>{genders[row.gender] || '—'}</td><td>{row.phone || '—'}</td><td>{degrees[row.degree] || '—'}</td><td>{formatTime(row.updateTime)}</td><td className="row-actions"><button className="link-button" onClick={() => edit(row.id)}>Edit</button><button className="link-button danger-text" onClick={() => remove([row.id])}>Delete</button></td></tr>)}</tbody></table>{loading ? <p className="empty">Loading learners…</p> : !rows.length && <p className="empty">No learners match your search.</p>}</div><Pager page={page} pageSize={pageSize} total={total} onPage={setPage} onSize={size => { setPageSize(size); setPage(1) }} /></section>
    <Modal open={form !== null} title={editing ? 'Edit learner' : 'Add learner'} onClose={() => setForm(null)} wide><form onSubmit={save}><div className="form-grid"><Field label="Name"><input value={form?.name || ''} onChange={e => update('name', e.target.value)} required /></Field><Field label="Learner ID"><input value={form?.no || ''} onChange={e => update('no', e.target.value)} required /></Field><Field label="Gender"><Select value={form?.gender} onChange={value => update('gender', value)} options={Object.entries(genders)} required placeholder="Select gender" /></Field><Field label="Mobile number"><input value={form?.phone || ''} onChange={e => update('phone', e.target.value)} required /></Field><Field label="Education"><Select value={form?.degree} onChange={value => update('degree', value)} options={Object.entries(degrees)} required placeholder="Select education" /></Field><Field label="Cohort"><Select value={form?.clazzId} onChange={value => update('clazzId', value)} options={cohorts.map(row => [row.id, row.name])} required placeholder="Select cohort" /></Field><Field label="ID number"><input value={form?.idCard || ''} onChange={e => update('idCard', e.target.value)} /></Field><Field label="Graduation date"><input type="date" value={form?.graduationDate || ''} onChange={e => update('graduationDate', e.target.value)} /></Field><Field label="College learner"><Select value={form?.isCollege} onChange={value => update('isCollege', value)} options={[[1, 'Yes'], [0, 'No']]} /></Field><Field label="Address"><input value={form?.address || ''} onChange={e => update('address', e.target.value)} /></Field>{editing && <><Field label="Incident count"><input type="number" min="0" value={form?.violationCount ?? 0} onChange={e => update('violationCount', Number(e.target.value))} /></Field><Field label="Incident points"><input type="number" min="0" value={form?.violationScore ?? 0} onChange={e => update('violationScore', Number(e.target.value))} /></Field></>}</div><div className="modal-actions"><button type="button" className="button secondary" onClick={() => setForm(null)}>Cancel</button><button className="button">Save learner</button></div></form></Modal>
  </>
}
