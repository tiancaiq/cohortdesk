import { Link, useLocation } from 'react-router'

export function NotFound() {
  const location = useLocation()
  return <main className="not-found"><div><span className="eyebrow">PAGE NOT FOUND</span><h1>404</h1><p>There is no page at <code>{location.pathname}</code>.</p><Link className="button" to="/index">Go to dashboard</Link></div></main>
}
