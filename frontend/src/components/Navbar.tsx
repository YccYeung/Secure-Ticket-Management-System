import { useKeycloak } from '@react-keycloak/web'
import { useNavigate, useLocation } from 'react-router-dom'

function Navbar() {
  const { keycloak } = useKeycloak()
  const navigate = useNavigate()
  const location = useLocation()

  const links = [
    { label: 'Dashboard', path: '/dashboard' },
    { label: 'Tickets', path: '/tickets' },
    { label: 'Account', path: '/account' },
  ]

  return (
    <nav style={{
      display: 'flex', justifyContent: 'space-between', alignItems: 'center',
      padding: '16px 48px', background: 'rgba(255,255,255,0.95)',
      backdropFilter: 'blur(8px)', borderBottom: '1px solid #f0f0f0',
      position: 'sticky', top: 0, zIndex: 100
    }}>
      {/* Logo */}
      <div
        onClick={() => navigate('/dashboard')}
        style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer' }}
      >
        <div style={{
          width: 32, height: 32, background: '#C5050C', borderRadius: 6,
          display: 'flex', alignItems: 'center', justifyContent: 'center'
        }}>
          <span style={{ color: '#fff', fontFamily: "'Bebas Neue', cursive", fontSize: 16 }}>B</span>
        </div>
        <span style={{ fontFamily: "'Bebas Neue', cursive", fontSize: 22, letterSpacing: '1px' }}>
          BadgerPass
        </span>
      </div>

      {/* Nav links */}
      <div style={{ display: 'flex', gap: 8 }}>
        {links.map(link => (
          <button
            key={link.path}
            onClick={() => navigate(link.path)}
            style={{
              padding: '8px 20px',
              background: location.pathname === link.path ? '#111' : 'transparent',
              color: location.pathname === link.path ? '#fff' : '#555',
              border: 'none', borderRadius: 100, cursor: 'pointer',
              fontFamily: "'DM Sans', sans-serif", fontWeight: 600, fontSize: 14,
              transition: 'all 0.15s'
            }}
            onMouseOver={e => {
              if (location.pathname !== link.path) {
                (e.target as HTMLButtonElement).style.background = '#f4f4f4'
                ;(e.target as HTMLButtonElement).style.color = '#111'
              }
            }}
            onMouseOut={e => {
              if (location.pathname !== link.path) {
                (e.target as HTMLButtonElement).style.background = 'transparent'
                ;(e.target as HTMLButtonElement).style.color = '#555'
              }
            }}
          >
            {link.label}
          </button>
        ))}
      </div>

      {/* User + logout */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
        <span style={{
          fontFamily: "'DM Sans', sans-serif", fontSize: 14,
          color: '#555', fontWeight: 500
        }}>
          {keycloak.tokenParsed?.preferred_username}
        </span>
        <button
          onClick={() => keycloak.logout()}
          style={{
            padding: '8px 20px', border: '1.5px solid #ddd', borderRadius: 100,
            background: 'transparent', cursor: 'pointer',
            fontFamily: "'DM Sans', sans-serif", fontWeight: 600, fontSize: 14,
            color: '#555', transition: 'all 0.15s'
          }}
          onMouseOver={e => {
            (e.target as HTMLButtonElement).style.borderColor = '#C5050C'
            ;(e.target as HTMLButtonElement).style.color = '#C5050C'
          }}
          onMouseOut={e => {
            (e.target as HTMLButtonElement).style.borderColor = '#ddd'
            ;(e.target as HTMLButtonElement).style.color = '#555'
          }}
        >
          Sign out
        </button>
      </div>
    </nav>
  )
}

export default Navbar