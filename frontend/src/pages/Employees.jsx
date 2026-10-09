import { useCallback, useEffect, useState } from 'react'
import { addApi, deleteApi, queryInfoApi, queryPageApi, updateApi } from '../api/emp.js'
import { queryAllApi as listDepartments } from '../api/dept.js'
import request, { notify } from '../utils/request.js'
import { Field, formatDate, formatTime, genders, jobs, Modal, PageHeader, Pager, Select } from '../components/ui.jsx'

const emptyFilter = () => ({ name: '', gender: '', begin: '', end: '' })
const blank = () => ({ username: '', name: '', gender: 1, phone: '', image: '', deptId: '', entryDate: '', job: '', salary: '', exprList: [] })

export function Employees() {
  const [draft, setDraft] = useState(emptyFilter)
  const [filter, setFilter] = useState(emptyFilter)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [rows, setRows] = useState([])
  const [total, setTotal] = useState(0)
  const [departments, setDepartments] = useState([])
  const [selected, setSelected] = useState([])
  const [loading, setLoading] = useState(true)
  const [uploading, setUploading] = useState(false)
  const [form, setForm] = useState(null)
  const [editing, setEditing] = useState(false)

  const load = useCallback(async () => {
    setLoading(true)
    try { const result = await queryPageApi(filter.name, filter.gender, filter.begin, filter.end, page, pageSize); if (result.code === 1) { setRows(result.data.rows || []); setTotal(result.data.total || 0); setSelected([]) } }
    catch { /* Already reported. */ }
    finally { setLoading(false) }
  }, [filter, page, pageSize])
  useEffect(() => { load() }, [load])
  useEffect(() => { listDepartments().then(result => { if (result.code === 1) setDepartments(result.data || []) }).catch(() => {}) }, [])

  const edit = async id => {
    try { const result = await queryInfoApi(id); if (result.code === 1) { setForm({ ...blank(), ...result.data, exprList: result.data.exprList || [] }); setEditing(true) } }
    catch { /* Already reported. */ }
  }
  const remove = async ids => {
    if (!ids.length || !window.confirm(`Delete ${ids.length} employee${ids.length === 1 ? '' : 's'}?`)) return
    try { const result = await deleteApi(ids.join(',')); if (result.code === 1) { notify('Employees deleted.'); if (rows.length === ids.length && page > 1) setPage(page - 1); else load() } }
    catch { /* Already reported. */ }
  }
  const save = async event => {
    event.preventDefault()
    try {
      const payload = { ...form, job: form.job || null, deptId: form.deptId || null, salary: form.salary === '' ? null : form.salary, entryDate: form.entryDate || null }
      const result = editing ? await updateApi(payload) : await addApi(payload)
      if (result.code !== 1) return notify(result.msg || 'Could not save employee.', 'error')
      notify(editing ? 'Employee updated.' : 'Employee added.')
      setForm(null); load()
    } catch { /* Already reported. */ }
  }
  const upload = async file => {
    if (!file) return
    if (!file.type.startsWith('image/') || file.size > 2 * 1024 * 1024) return notify('Choose an image smaller than 2 MB.', 'error')
    setUploading(true)
    try { const data = new FormData(); data.append('file', file); const result = await request.post('/upload', data); if (result.code === 1) { setForm(current => ({ ...current, image: result.data })); notify('Photo uploaded.') } else notify(result.msg || 'Upload failed.', 'error') }
    catch { /* Already reported. */ }
    finally { setUploading(false) }
  }
  const update = (key, value) => setForm(current => ({ ...current, [key]: value }))
  const updateExpr = (index, key, value) => setForm(current => ({ ...current, exprList: current.exprList.map((item, i) => i === index ? { ...item, [key]: value } : item) }))
  const toggle = id => setSelected(current => current.includes(id) ? current.filter(value => value !== id) : [...current, id])
  const allSelected = rows.length > 0 && rows.every(row => selected.includes(row.id))

  return <><PageHeader eyebrow="ORGANIZATION" title="Employees" description="Manage staff profiles and work history." actions={<button className="button" onClick={() => { setEditing(false); setForm(blank()) }}>Add employee</button>} />
    <section className="panel filters"><form className="filter-row" onSubmit={e => { e.preventDefault(); setPage(1); setFilter({ ...draft }) }}><Field label="Name"><input placeholder="Employee name" value={draft.name} onChange={e => setDraft({ ...draft, name: e.target.value })} /></Field><Field label="Gender"><Select value={draft.gender} onChange={value => setDraft({ ...draft, gender: value })} options={Object.entries(genders)} /></Field><Field label="Hired from"><input type="date" value={draft.begin} onChange={e => setDraft({ ...draft, begin: e.target.value })} /></Field><Field label="Hired through"><input type="date" value={draft.end} onChange={e => setDraft({ ...draft, end: e.target.value })} /></Field><button className="button">Search</button><button type="button" className="button secondary" onClick={() => { setDraft(emptyFilter()); setFilter(emptyFilter()); setPage(1) }}>Reset</button></form></section>
    <section className="panel"><div className="panel-head"><h2>Employee directory</h2><button className="button danger small" disabled={!selected.length} onClick={() => remove(selected)}>Delete selected ({selected.length})</button></div><div className="table-scroll"><table><thead><tr><th><input type="checkbox" aria-label="Select all employees" checked={allSelected} onChange={() => setSelected(allSelected ? [] : rows.map(row => row.id))} /></th><th>Name</th><th>Username</th><th>Gender</th><th>Phone</th><th>Role</th><th>Salary</th><th>Department</th><th>Hired</th><th>Updated</th><th>Actions</th></tr></thead><tbody>{rows.map(row => <tr key={row.id}><td><input type="checkbox" aria-label={`Select ${row.name}`} checked={selected.includes(row.id)} onChange={() => toggle(row.id)} /></td><td className="emphasis"><span className="person-cell">{row.image ? <img src={row.image} alt="" /> : <span className="avatar-fallback">{row.name?.[0] || '?'}</span>}{row.name}</span></td><td>{row.username}</td><td>{genders[row.gender] || '—'}</td><td>{row.phone || '—'}</td><td>{jobs[row.job] || '—'}</td><td>{row.salary ?? '—'}</td><td>{row.deptName || '—'}</td><td>{formatDate(row.entryDate)}</td><td>{formatTime(row.updateTime)}</td><td className="row-actions"><button className="link-button" onClick={() => edit(row.id)}>Edit</button><button className="link-button danger-text" onClick={() => remove([row.id])}>Delete</button></td></tr>)}</tbody></table>{loading ? <p className="empty">Loading employees…</p> : !rows.length && <p className="empty">No employees match your search.</p>}</div><Pager page={page} pageSize={pageSize} total={total} onPage={setPage} onSize={size => { setPageSize(size); setPage(1) }} /></section>
    <Modal open={form !== null} title={editing ? 'Edit employee' : 'Add employee'} onClose={() => setForm(null)} wide><form onSubmit={save}><div className="photo-row"><span className="avatar-preview">{form?.image ? <img src={form.image} alt="Employee" /> : form?.name?.[0] || '?'}</span><label className="button secondary upload-button">{uploading ? 'Uploading…' : 'Upload photo'}<input type="file" accept="image/*" disabled={uploading} onChange={e => upload(e.target.files?.[0])} /></label><span className="hint">Optional · image under 2 MB</span></div><div className="form-grid"><Field label="Username"><input value={form?.username || ''} onChange={e => update('username', e.target.value)} required /></Field><Field label="Name"><input value={form?.name || ''} onChange={e => update('name', e.target.value)} required /></Field><Field label="Mobile number"><input value={form?.phone || ''} onChange={e => update('phone', e.target.value)} required /></Field><Field label="Gender"><Select value={form?.gender} onChange={value => update('gender', value)} options={Object.entries(genders)} required placeholder="Select gender" /></Field><Field label="Role"><Select value={form?.job} onChange={value => update('job', value)} options={Object.entries(jobs)} placeholder="Select role" /></Field><Field label="Salary"><input type="number" min="0" value={form?.salary ?? ''} onChange={e => update('salary', e.target.value === '' ? null : Number(e.target.value))} /></Field><Field label="Department"><Select value={form?.deptId} onChange={value => update('deptId', value)} options={departments.map(row => [row.id, row.name])} placeholder="Select department" /></Field><Field label="Hire date"><input type="date" value={form?.entryDate || ''} onChange={e => update('entryDate', e.target.value)} /></Field></div><div className="subform-head"><h3>Work experience</h3><button type="button" className="button secondary small" onClick={() => update('exprList', [...form.exprList, { company: '', job: '', begin: '', end: '' }])}>Add experience</button></div>{form?.exprList?.map((item, index) => <div className="experience-row" key={index}><input aria-label="Company" placeholder="Company" value={item.company || ''} onChange={e => updateExpr(index, 'company', e.target.value)} /><input aria-label="Role" placeholder="Role" value={item.job || ''} onChange={e => updateExpr(index, 'job', e.target.value)} /><input aria-label="Start date" type="date" value={item.begin || ''} onChange={e => updateExpr(index, 'begin', e.target.value)} /><input aria-label="End date" type="date" value={item.end || ''} onChange={e => updateExpr(index, 'end', e.target.value)} /><button type="button" className="icon-button" aria-label="Remove experience" onClick={() => update('exprList', form.exprList.filter((_, i) => i !== index))}>×</button></div>)}<div className="modal-actions"><button type="button" className="button secondary" onClick={() => setForm(null)}>Cancel</button><button className="button">Save employee</button></div></form></Modal>
  </>
}
