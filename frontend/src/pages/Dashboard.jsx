import { Link } from 'react-router'
import { PageHeader } from '../components/ui.jsx'

const modules = [
  ['Cohorts', 'Plan courses, dates, rooms, and cohort leads.', '/clazz', 'Training', 'CH'],
  ['Learners', 'Keep learner records and cohort assignments in one place.', '/student', 'Training', 'LR'],
  ['Employees', 'Manage staff profiles, roles, and work history.', '/emp', 'Organization', 'EM'],
  ['Departments', 'Maintain the teams behind training operations.', '/dept', 'Organization', 'DP'],
  ['Learner analytics', 'Explore enrollment and education level charts.', '/report/student', 'Reports', 'LA'],
  ['Employee analytics', 'Review staff distribution by role and gender.', '/report/emp', 'Reports', 'EA']
]

export function Dashboard() {
  return <div className="dashboard-page"><PageHeader eyebrow="COHORTDESK / OPERATIONS" title="Training operations" description="Manage cohorts, people, and reports from one workspace." />
    <div className="section-heading"><span>WORK AREAS</span><span>06 MODULES</span></div>
    <div className="module-grid">{modules.map(([title, description, path, group, symbol]) => <Link to={path} key={path} className="module-card"><div className="module-top"><span>{group}</span><span className="module-arrow">↗</span></div><div><span className="module-symbol">{symbol}</span><h2>{title}</h2><p>{description}</p></div></Link>)}</div>
    <div className="dashboard-footer"><span>Need an audit trail?</span><Link to="/log/operate">View operation log →</Link></div>
  </div>
}
