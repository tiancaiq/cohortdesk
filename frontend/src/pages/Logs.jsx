import { useCallback, useEffect, useState } from 'react'
import { getLoginLogSummaryData, pageLoginLog } from '../api/log/loginLog.js'
import { getPageApi, getSummaryDataApi } from '../api/log/operateLog.js'
import { queryAllApi as listEmployees } from '../api/emp.js'
import { Field, formatTime, PageHeader, Pager, Select, Stat } from '../components/ui.jsx'
import { Chart, chartColors, chartText } from '../components/Chart.jsx'

const defaultLogin = () => ({ username: '', beginDate: '', endDate: '' })
const defaultOperation = () => ({ operateEmpId: '', beginDate: '', endDate: '' })
const pie = data => ({ color: chartColors, tooltip: { trigger: 'item' }, legend: { bottom: 0, textStyle: chartText }, series: [{ type: 'pie', radius: ['42%', '67%'], center: ['50%', '45%'], data: data || [], label: { color: '#c8deea' } }] })
const bar = (names, values, unit = '') => ({ color: [chartColors[0]], tooltip: { trigger: 'axis' }, grid: { left: 45, right: 20, top: 30, bottom: 50, containLabel: true }, xAxis: { type: 'category', data: names, axisLabel: chartText }, yAxis: { type: 'value', minInterval: 1, axisLabel: chartText, splitLine: { lineStyle: { color: '#243c51' } } }, series: [{ type: 'bar', data: values, barMaxWidth: 50, itemStyle: { borderRadius: [5, 5, 0, 0] }, label: { show: true, position: 'top', formatter: `{c}${unit}`, color: '#c8deea' } }] })

export function LoginLog() {
  const [draft, setDraft] = useState(defaultLogin)
  const [filter, setFilter] = useState(defaultLogin)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [rows, setRows] = useState([])
  const [total, setTotal] = useState(0)
  const [summary, setSummary] = useState(null)
  const [loading, setLoading] = useState(true)
  const load = useCallback(async () => {
    setLoading(true)
    try { const result = await pageLoginLog({ ...filter, page, pageSize }); if (result.code === 1) { setRows(result.data.rows || []); setTotal(result.data.total || 0) } }
    catch { /* Already reported. */ }
    finally { setLoading(false) }
  }, [filter, page, pageSize])
  useEffect(() => { load() }, [load])
  useEffect(() => { getLoginLogSummaryData().then(result => { if (result.code === 1) setSummary(result.data) }).catch(() => {}) }, [])

  return <><PageHeader eyebrow="ACTIVITY" title="Login log" description="Review sign-in outcomes and recent attempts." />
    <div className="stat-grid"><Stat label="Sign-ins today" value={summary?.todayLoginCount} /><Stat label="Failed attempts today" value={summary?.todayFailCount} tone="warning" /></div>
    <div className="chart-grid"><section className="panel chart-panel"><div className="panel-head"><h2>Login outcomes</h2></div><Chart option={pie(summary?.totalSuccessFail)} /></section><section className="panel chart-panel"><div className="panel-head"><h2>Failed attempts by username</h2></div><Chart option={bar(summary?.failRank?.map(row => row.username) || [], summary?.failRank?.map(row => row.count) || [])} /></section></div>
    <section className="panel filters"><form className="filter-row" onSubmit={e => { e.preventDefault(); setPage(1); setFilter({ ...draft }) }}><Field label="Username"><input value={draft.username} onChange={e => setDraft({ ...draft, username: e.target.value })} placeholder="Search username" /></Field><Field label="From"><input type="date" value={draft.beginDate} onChange={e => setDraft({ ...draft, beginDate: e.target.value })} /></Field><Field label="Through"><input type="date" value={draft.endDate} onChange={e => setDraft({ ...draft, endDate: e.target.value })} /></Field><button className="button">Search</button><button type="button" className="button secondary" onClick={() => { setDraft(defaultLogin()); setFilter(defaultLogin()); setPage(1) }}>Reset</button></form></section>
    <section className="panel"><div className="panel-head"><h2>Recent login activity</h2><span>{total} records</span></div><div className="table-scroll"><table><thead><tr><th>Username</th><th>Login time</th><th>Result</th><th>Duration</th></tr></thead><tbody>{rows.map(row => <tr key={row.id}><td className="emphasis">{row.username || '—'}</td><td>{formatTime(row.loginTime)}</td><td><span className={`pill ${row.isSuccess === 1 ? 'green' : 'red'}`}>{row.isSuccess === 1 ? 'Success' : 'Failure'}</span></td><td>{row.costTime ?? '—'} ms</td></tr>)}</tbody></table>{loading ? <p className="empty">Loading login activity…</p> : !rows.length && <p className="empty">No login records match your search.</p>}</div><Pager page={page} pageSize={pageSize} total={total} onPage={setPage} onSize={size => { setPageSize(size); setPage(1) }} /></section>
  </>
}

export function OperationLog() {
  const [draft, setDraft] = useState(defaultOperation)
  const [filter, setFilter] = useState(defaultOperation)
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const [rows, setRows] = useState([])
  const [total, setTotal] = useState(0)
  const [summary, setSummary] = useState(null)
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(true)
  const load = useCallback(async () => {
    setLoading(true)
    try { const result = await getPageApi({ ...filter, page, pageSize }); if (result.code === 1) { setRows(result.data.rows || []); setTotal(result.data.total || 0) } }
    catch { /* Already reported. */ }
    finally { setLoading(false) }
  }, [filter, page, pageSize])
  useEffect(() => { load() }, [load])
  useEffect(() => { getSummaryDataApi().then(result => { if (result.code === 1) setSummary(result.data) }).catch(() => {}); listEmployees().then(result => { if (result.code === 1) setEmployees(result.data || []) }).catch(() => {}) }, [])

  return <><PageHeader eyebrow="ACTIVITY" title="Operation log" description="Review changes made throughout the workspace." />
    <div className="stat-grid"><Stat label="Operations today" value={summary?.todayCount} /><Stat label="Total operations" value={summary?.totalCount} /></div>
    <div className="chart-grid"><section className="panel chart-panel"><div className="panel-head"><h2>Average response time</h2></div><Chart option={bar(summary?.avgCostTimes?.map(row => row.type) || [], summary?.avgCostTimes?.map(row => row.avgTime) || [], ' ms')} /></section><section className="panel chart-panel"><div className="panel-head"><h2>Operations by action</h2></div><Chart option={pie(summary?.totalTypeCounts?.map(row => ({ name: row.type, value: row.count })))} /></section></div>
    <section className="panel filters"><form className="filter-row" onSubmit={e => { e.preventDefault(); setPage(1); setFilter({ ...draft }) }}><Field label="Employee"><Select value={draft.operateEmpId} onChange={value => setDraft({ ...draft, operateEmpId: value })} options={employees.map(row => [row.id, row.name])} /></Field><Field label="From"><input type="date" value={draft.beginDate} onChange={e => setDraft({ ...draft, beginDate: e.target.value })} /></Field><Field label="Through"><input type="date" value={draft.endDate} onChange={e => setDraft({ ...draft, endDate: e.target.value })} /></Field><button className="button">Search</button><button type="button" className="button secondary" onClick={() => { setDraft(defaultOperation()); setFilter(defaultOperation()); setPage(1) }}>Reset</button></form></section>
    <section className="panel"><div className="panel-head"><h2>Recent operations</h2><span>{total} records</span></div><div className="table-scroll"><table><thead><tr><th>ID</th><th>Employee</th><th>Action</th><th>Response time</th><th>Operation time</th></tr></thead><tbody>{rows.map(row => <tr key={row.id}><td>{row.id}</td><td className="emphasis">{row.operateEmpName || '—'}</td><td>{row.className?.split('.').pop() || '—'} · {row.methodName || '—'}</td><td>{row.costTime ?? '—'} ms</td><td>{formatTime(row.operateTime)}</td></tr>)}</tbody></table>{loading ? <p className="empty">Loading operations…</p> : !rows.length && <p className="empty">No operations match your search.</p>}</div><Pager page={page} pageSize={pageSize} total={total} onPage={setPage} onSize={size => { setPageSize(size); setPage(1) }} /></section>
  </>
}
