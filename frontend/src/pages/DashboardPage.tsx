import { useEffect, useState } from 'react'
import { useKeycloak } from '@react-keycloak/web'
import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import { authFetch } from '../services/api'

interface Ticket {
  id: string
  name: string
  location: string
  price: number
  quantity: number
  event_date: string
}

function DashboardPage() {
  const { keycloak } = useKeycloak()
  const navigate = useNavigate()
  const [balance, setBalance] = useState<number | null>(null)
  const [tickets, setTickets] = useState<Ticket[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      authFetch(keycloak, 'http://localhost:8080/api/account/balance').then(r => r.json()),
      authFetch(keycloak, 'http://localhost:8080/api/tickets').then(r => r.json())
    ]).then(([balanceData, ticketData]) => {
      setBalance(balanceData.balance)
      setTickets(ticketData)
      setLoading(false)
    })
  }, [])

  const isHome = (location: string) => location.includes('Camp Randall')

  if (loading) return (
    <div style={{ fontFamily: "'DM Sans', sans-serif" }}>
      <Navbar />
      <div style={{ padding: '80px 48px', textAlign: 'center', color: '#999' }}>Loading...</div>
    </div>
  )

  const upcomingGames = tickets.slice(0, 3)

  return (
    <div style={{ fontFamily: "'DM Sans', sans-serif", background: '#fafafa', minHeight: '100vh' }}>
      <Navbar />

      <div style={{ maxWidth: 1100, margin: '0 auto', padding: '48px 48px' }}>

        {/* Header */}
        <div style={{ marginBottom: 40 }}>
          <p style={{
            color: '#999', fontSize: 12, fontWeight: 700,
            letterSpacing: '1.5px', textTransform: 'uppercase', marginBottom: 8
          }}>
            Welcome back
          </p>
          <h1 style={{
            fontFamily: "'Bebas Neue', cursive",
            fontSize: 56, fontWeight: 400, letterSpacing: '2px',
            color: '#111', margin: 0
          }}>
            {keycloak.tokenParsed?.preferred_username}
          </h1>
        </div>

        {/* Stats row */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 20, marginBottom: 40 }}>
          {/* Balance card */}
          <div style={{
            background: '#C5050C', borderRadius: 20, padding: '32px',
            color: '#fff'
          }}>
            <p style={{ fontSize: 12, fontWeight: 700, letterSpacing: '1.5px', textTransform: 'uppercase', opacity: 0.7, marginBottom: 12 }}>
              Account Balance
            </p>
            <p style={{ fontFamily: "'Bebas Neue', cursive", fontSize: 48, letterSpacing: '1px', margin: 0 }}>
              ${balance?.toFixed(2)}
            </p>
            <p style={{ fontSize: 13, opacity: 0.6, marginTop: 8 }}>Available to spend</p>
          </div>

          {/* Season games card */}
          <div style={{ background: '#111', borderRadius: 20, padding: '32px', color: '#fff' }}>
            <p style={{ fontSize: 12, fontWeight: 700, letterSpacing: '1.5px', textTransform: 'uppercase', opacity: 0.5, marginBottom: 12 }}>
              2026 Season
            </p>
            <p style={{ fontFamily: "'Bebas Neue', cursive", fontSize: 48, letterSpacing: '1px', margin: 0 }}>
              {tickets.length} Games
            </p>
            <p style={{ fontSize: 13, opacity: 0.4, marginTop: 8 }}>
              {tickets.filter(t => isHome(t.location)).length} home · {tickets.filter(t => !isHome(t.location)).length} away
            </p>
          </div>

          {/* CTA card */}
          <div style={{
            background: '#fff', borderRadius: 20, padding: '32px',
            border: '1.5px solid #f0f0f0', display: 'flex',
            flexDirection: 'column', justifyContent: 'space-between'
          }}>
            <p style={{ fontSize: 12, fontWeight: 700, letterSpacing: '1.5px', textTransform: 'uppercase', color: '#999', marginBottom: 12 }}>
              Quick Actions
            </p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
              <button
                onClick={() => navigate('/tickets')}
                style={{
                  padding: '12px 20px', background: '#C5050C', color: '#fff',
                  border: 'none', borderRadius: 100, cursor: 'pointer',
                  fontFamily: "'DM Sans', sans-serif", fontWeight: 700, fontSize: 14,
                  transition: 'background 0.15s'
                }}
                onMouseOver={e => (e.target as HTMLButtonElement).style.background = '#a00009'}
                onMouseOut={e => (e.target as HTMLButtonElement).style.background = '#C5050C'}
              >
                Buy Tickets
              </button>
              <button
                onClick={() => navigate('/account')}
                style={{
                  padding: '12px 20px', background: 'transparent', color: '#111',
                  border: '1.5px solid #ddd', borderRadius: 100, cursor: 'pointer',
                  fontFamily: "'DM Sans', sans-serif", fontWeight: 600, fontSize: 14,
                  transition: 'all 0.15s'
                }}
                onMouseOver={e => (e.target as HTMLButtonElement).style.borderColor = '#111'}
                onMouseOut={e => (e.target as HTMLButtonElement).style.borderColor = '#ddd'}
              >
                My Account
              </button>
            </div>
          </div>
        </div>

        {/* Upcoming games */}
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
            <h2 style={{
              fontFamily: "'Bebas Neue', cursive",
              fontSize: 32, fontWeight: 400, letterSpacing: '1px', color: '#111', margin: 0
            }}>
              Upcoming Games
            </h2>
            <button
              onClick={() => navigate('/tickets')}
              style={{
                background: 'none', border: 'none', cursor: 'pointer',
                color: '#C5050C', fontWeight: 700, fontSize: 14,
                fontFamily: "'DM Sans', sans-serif"
              }}
            >
              View all →
            </button>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            {upcomingGames.map(ticket => (
              <div key={ticket.id} style={{
                background: '#fff', borderRadius: 16, padding: '20px 28px',
                border: '1.5px solid #f0f0f0',
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                flexWrap: 'wrap', gap: 16
              }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                  <div style={{
                    width: 8, height: 8, borderRadius: '50%',
                    background: isHome(ticket.location) ? '#C5050C' : '#999',
                    flexShrink: 0
                  }} />
                  <div>
                    <p style={{ fontWeight: 700, fontSize: 16, color: '#111', margin: 0 }}>{ticket.name}</p>
                    <p style={{ fontSize: 13, color: '#999', margin: '2px 0 0' }}>{ticket.location}</p>
                  </div>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
                  <div style={{ textAlign: 'right' }}>
                    <p style={{ fontSize: 12, color: '#999', margin: 0 }}>{ticket.event_date}</p>
                    <p style={{ fontWeight: 700, fontSize: 16, color: '#111', margin: '2px 0 0' }}>${ticket.price}</p>
                  </div>
                  <button
                    onClick={() => navigate('/tickets')}
                    style={{
                      padding: '8px 20px', background: '#111', color: '#fff',
                      border: 'none', borderRadius: 100, cursor: 'pointer',
                      fontFamily: "'DM Sans', sans-serif", fontWeight: 600, fontSize: 13,
                      whiteSpace: 'nowrap', transition: 'background 0.15s'
                    }}
                    onMouseOver={e => (e.target as HTMLButtonElement).style.background = '#C5050C'}
                    onMouseOut={e => (e.target as HTMLButtonElement).style.background = '#111'}
                  >
                    Get tickets
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

export default DashboardPage