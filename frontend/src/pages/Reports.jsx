import { useEffect, useState } from 'react'
import { getEmpGenderData, getEmpJobData } from '../api/report/emp.js'
import { getStudentCountData, getStudentDegreeData, getStudentSummaryData } from '../api/report/student.js'
import { PageHeader, Stat } from '../components/ui.jsx'
import { Chart, chartColors, chartText } from '../components/Chart.jsx'

const darkAxis = { axisLabel: chartText, axisLine: { lineStyle: { color: '#34556d' } }, splitLine: { lineStyle: { color: '#243c51' } } }
const pie = (data, label) => ({ color: chartColors, tooltip: { trigger: 'item', formatter: `{b}: {c} ${label} ({d}%)` }, legend: { bottom: 0, textStyle: chartText }, series: [{ type: 'pie', radius: ['42%', '67%'], center: ['50%', '45%'], data: data || [], label: { color: '#c8deea' } }] })
const bars = (names, values, rotate = 0) => ({ color: [chartColors[0]], tooltip: { trigger: 'axis' }, grid: { left: 42, right: 24, top: 28, bottom: rotate ? 90 : 48, containLabel: true }, xAxis: { type: 'category', data: names, ...darkAxis, axisLabel: { ...chartText, rotate, interval: 0 } }, yAxis: { type: 'value', minInterval: 1, ...darkAxis }, series: [{ type: 'bar', data: values, barMaxWidth: 50, itemStyle: { borderRadius: [5, 5, 0, 0] }, label: { show: true, position: 'top', color: '#c8deea' } }] })

export function EmployeeReport() {
  const [gender, setGender] = useState([])
  const [jobs, setJobs] = useState([])
  useEffect(() => { Promise.all([getEmpGenderData(), getEmpJobData()]).then(([a, b]) => { if (a.code === 1) setGender(a.data || []); if (b.code === 1) setJobs(b.data || []) }).catch(() => {}) }, [])
  const total = gender.reduce((count, row) => count + row.value, 0)
  return <><PageHeader eyebrow="REPORTS" title="Employee analytics" description="Explore team makeup by gender and role." />
    <div className="stat-grid"><Stat label="Total employees" value={total} /><Stat label="Male" value={gender.find(row => row.name === 'Male')?.value ?? 0} /><Stat label="Female" value={gender.find(row => row.name === 'Female')?.value ?? 0} /><Stat label="Role categories" value={jobs.length} /></div>
    <div className="chart-grid"><section className="panel chart-panel"><div className="panel-head"><h2>Employees by gender</h2></div><Chart option={pie(gender, 'employees')} /></section><section className="panel chart-panel"><div className="panel-head"><h2>Employees by role</h2></div><Chart option={bars(jobs.map(row => row.job), jobs.map(row => row.count), 20)} /></section></div>
  </>
}

export function LearnerReport() {
  const [degree, setDegree] = useState([])
  const [cohorts, setCohorts] = useState([])
  const [summary, setSummary] = useState(null)
  useEffect(() => { Promise.all([getStudentDegreeData(), getStudentCountData(), getStudentSummaryData()]).then(([a, b, c]) => { if (a.code === 1) setDegree(a.data || []); if (b.code === 1) setCohorts(b.data || []); if (c.code === 1) setSummary(c.data) }).catch(() => {}) }, [])
  return <><PageHeader eyebrow="REPORTS" title="Learner analytics" description="Review enrollment, cohort activity, and education levels." />
    <div className="stat-grid"><Stat label="Total learners" value={summary?.totalStudent} /><Stat label="Without a cohort" value={summary?.studentHaveNoClazz} tone="warning" /><Stat label="Total cohorts" value={summary?.totalClazz} /><Stat label="Active cohorts" value={summary?.openClazzCount} /><Stat label="Completed cohorts" value={summary?.endClazzCount} /><Stat label="Upcoming cohorts" value={summary?.notStartClazzCount} /></div>
    <div className="chart-grid"><section className="panel chart-panel"><div className="panel-head"><h2>Learners by education level</h2></div><Chart option={pie(degree, 'learners')} /></section><section className="panel chart-panel"><div className="panel-head"><h2>Learners by cohort</h2></div><Chart option={bars(cohorts.map(row => row.clazzName), cohorts.map(row => row.studentCount), 25)} /></section></div>
  </>
}
