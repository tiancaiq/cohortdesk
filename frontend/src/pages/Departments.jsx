import { useCallback, useEffect, useState } from 'react'
import { addDeptApi, deleteDeptApi, queryAllApi, queryInfoApi, updateDeptApi } from '../api/dept.js'
import { notify } from '../utils/request.js'
import { Field, formatTime, Modal, PageHeader } from '../components/ui.jsx'

export function Departments() {
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [editing, setEditing] = useState(false)
  const [form, setForm] = useState(null)
  const load = useCallback(async () => {
    setLoading(true)
    try { const result = await queryAllApi(); if (result.code === 1) setRows(result.data || []) }
    catch { /* API errors appear in the global notice. */ }
    finally { setLoading(false) }
  }, [])
  useEffect(() => { load() }, [load])

  const edit = async id => {
    try { const result = await queryInfoApi(id); if (result.code === 1) { setForm({ id, name: result.data.name }); setEditing(true) } }
    catch { /* Already reported. */ }
  }
  const save = async event => {
    event.preventDefault()
    try {
      const result = editing ? await updateDeptApi(form) : await addDeptApi({ name: form.name })
      if (result.code !== 1) return notify(result.msg || 'Could not save department.', 'error')
      notify(editing ? 'Department updated.' : 'Department added.')
      setForm(null); load()
    } catch { /* Already reported. */ }
  }
  const remove = async row => {
    if (!window.confirm(`Delete department “${row.name}”?`)) return
    try { const result = await deleteDeptApi(row.id); if (result.code === 1) { notify('Department deleted.'); load() } }
    catch { /* Already reported. */ }
  }

  return <><PageHeader eyebrow="ORGANIZATION" title="Departments" description="Manage the teams behind the training program." actions={<button className="button" onClick={() => { setEditing(false); setForm({ name: '' }) }}>Add department</button>} />
    <section className="panel"><div className="panel-head"><h2>Department directory</h2><span>{rows.length} departments</span></div><div className="table-scroll"><table><thead><tr><th>Name</th><th>Created</th><th>Updated</th><th>Actions</th></tr></thead><tbody>{rows.map(row => <tr key={row.id}><td className="emphasis">{row.name}</td><td>{formatTime(row.createTime)}</td><td>{formatTime(row.updateTime)}</td><td className="row-actions"><button className="link-button" onClick={() => edit(row.id)}>Edit</button><button className="link-button danger-text" onClick={() => remove(row)}>Delete</button></td></tr>)}</tbody></table>{loading ? <p className="empty">Loading departments…</p> : !rows.length && <p className="empty">No departments yet. Add one to get started.</p>}</div></section>
    <Modal open={form !== null} title={editing ? 'Edit department' : 'Add department'} onClose={() => setForm(null)}><form onSubmit={save} className="form-stack"><Field label="Department name"><input autoFocus value={form?.name || ''} onChange={e => setForm({ ...form, name: e.target.value })} required maxLength={10} /></Field><div className="modal-actions"><button type="button" className="button secondary" onClick={() => setForm(null)}>Cancel</button><button className="button">Save department</button></div></form></Modal>
  </>
}
