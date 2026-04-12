import { useEffect, useState } from 'react'
import { useKeycloak } from '@react-keycloak/web'
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

interface UserTicket {
  gameName: string
  quantity: number
  price: number
  location: string
  eventDate: string
}

function TicketsPage() {
  const { keycloak } = useKeycloak()
  const [tickets, setTickets] = useState<Ticket[]>([])
  const [myTickets, setMyTickets] = useState<UserTicket[]>([])
  const [quantities, setQuantities] = useState<Record<string, number>>({})
  const [sellQuantities, setSellQuantities] = useState<Record<string, number>>({})
  const [loading, setLoading] = useState(true)
  const [feedback, setFeedback] = useState<{ message: string; success: boolean } | null>(null)

  const headers = {
    'Authorization': `Bearer ${keycloak.token}`,
    'Content-Type': 'application/json'
  }

  const fetchData = () => {
    Promise.all([
      authFetch(keycloak, 'http://localhost:8080/api/tickets').then(r => r.json()),
      authFetch(keycloak, 'http://localhost:8080/api/tickets/my').then(r => r.json())
    ]).then(([allTickets, userTickets]) => {
      setTickets(allTickets)
      setMyTickets(userTickets)
      setLoading(false)
    })
  }

  useEffect(() => { fetchData() }, [])

  useEffect(() => { fetchData() }, [])

  const showFeedback = (message: string, success: boolean) => {
    setFeedback({ message, success })
    setTimeout(() => setFeedback(null), 3000)
  }

  const handleBuy = async (ticketName: string) => {
    const quantity = quantities[ticketName] || 1
    const res = await authFetch(keycloak, 'http://localhost:8080/api/tickets/buy', {
      method: 'POST',
      body: JSON.stringify({ ticketName, quantity })
  })
    const success = await res.json()
    if (success) {
      showFeedback(`Bought ${quantity} ticket(s) for ${ticketName}!`, true)
      fetchData()
    } else {
      showFeedback('Purchase failed. Check your balance or ticket availability.', false)
    }
  }

  const handleSell = async (ticketName: string) => {
    const quantity = sellQuantities[ticketName] || 1
    const res = await authFetch(keycloak, 'http://localhost:8080/api/tickets/sell', {
      method: 'POST',
      body: JSON.stringify({ ticketName, quantity })
  })
    const success = await res.json()
    if (success) {
      showFeedback(`Sold ${quantity} ticket(s) for ${ticketName}!`, true)
      fetchData()
    } else {
      showFeedback('Sale failed. Check your ticket holdings.', false)
    }
  }

  const isHome = (location: string) => location.includes('Camp Randall')

  if (loading) return (
    <div style={{ fontFamily: "'DM Sans', sans-serif" }}>
      <Navbar />
      <div style={{ padding: '80px 48px', textAlign: 'center', color: '#999' }}>Loading...</div>
    </div>
  )

  return (
    <div style={{ fontFamily: "'DM Sans', sans-serif", background: '#fafafa', minHeight: '100vh' }}>
      <Navbar />

      {/* Feedback toast */}
      {feedback && (
        <div style={{
          position: 'fixed', top: 80, right: 24, zIndex: 999,
          background: feedback.success ? '#111' : '#C5050C',
          color: '#fff', padding: '14px 24px', borderRadius: 12,
          fontWeight: 600, fontSize: 14, boxShadow: '0 4px 20px rgba(0,0,0,0.15)',
          transition: 'all 0.3s'
        }}>
          {feedback.message}
        </div>
      )}

      <div style={{ maxWidth: 1100, margin: '0 auto', padding: '48px' }}>

        {/* My Tickets section */}
        {myTickets.length > 0 && (
          <div style={{ marginBottom: 56 }}>
            <p style={{
              color: '#999', fontSize: 12, fontWeight: 700,
              letterSpacing: '1.5px', textTransform: 'uppercase', marginBottom: 8
            }}>Your Holdings</p>
            <h2 style={{
              fontFamily: "'Bebas Neue', cursive",
              fontSize: 40, fontWeight: 400, letterSpacing: '1px',
              color: '#111', margin: '0 0 24px'
            }}>My Tickets</h2>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              {myTickets.map(ticket => (
                <div key={ticket.gameName} style={{
                  background: '#fff', borderRadius: 16,
                  padding: '20px 28px', border: '1.5px solid #f0f0f0',
                  display: 'flex', justifyContent: 'space-between',
                  alignItems: 'center', flexWrap: 'wrap', gap: 16
                }}>
                  <div>
                    <p style={{ fontWeight: 700, fontSize: 16, color: '#111', margin: 0 }}>{ticket.gameName}</p>
                    <p style={{ fontSize: 13, color: '#999', margin: '2px 0 0' }}>
                      {ticket.location} · {ticket.eventDate}
                    </p>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                    <div style={{
                      background: '#C5050C', color: '#fff',
                      padding: '4px 14px', borderRadius: 100,
                      fontSize: 13, fontWeight: 700
                    }}>
                      {ticket.quantity} owned
                    </div>
                    <p style={{ fontWeight: 700, fontSize: 15, color: '#111', margin: 0 }}>
                      ${(ticket.price * ticket.quantity).toFixed(2)} value
                    </p>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <select
                        value={sellQuantities[ticket.gameName] || 1}
                        onChange={e => setSellQuantities(prev => ({ ...prev, [ticket.gameName]: Number(e.target.value) }))}
                        style={{
                          padding: '8px 12px', border: '1.5px solid #e0e0e0',
                          borderRadius: 8, fontFamily: "'DM Sans', sans-serif",
                          fontSize: 14, background: '#fff', cursor: 'pointer'
                        }}
                      >
                        {Array.from({ length: ticket.quantity }, (_, i) => i + 1).map(n => (
                          <option key={n} value={n}>{n}</option>
                        ))}
                      </select>
                      <button
                        onClick={() => handleSell(ticket.gameName)}
                        style={{
                          padding: '8px 20px', background: 'transparent',
                          color: '#C5050C', border: '1.5px solid #C5050C',
                          borderRadius: 100, cursor: 'pointer',
                          fontFamily: "'DM Sans', sans-serif", fontWeight: 700,
                          fontSize: 13, transition: 'all 0.15s'
                        }}
                        onMouseOver={e => {
                          (e.target as HTMLButtonElement).style.background = '#C5050C'
                          ;(e.target as HTMLButtonElement).style.color = '#fff'
                        }}
                        onMouseOut={e => {
                          (e.target as HTMLButtonElement).style.background = 'transparent'
                          ;(e.target as HTMLButtonElement).style.color = '#C5050C'
                        }}
                      >
                        Sell
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* All games section */}
        <div>
          <p style={{
            color: '#999', fontSize: 12, fontWeight: 700,
            letterSpacing: '1.5px', textTransform: 'uppercase', marginBottom: 8
          }}>2026 Season</p>
          <h2 style={{
            fontFamily: "'Bebas Neue', cursive",
            fontSize: 40, fontWeight: 400, letterSpacing: '1px',
            color: '#111', margin: '0 0 24px'
          }}>All Games</h2>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            {tickets.map(ticket => (
              <div key={ticket.id} style={{
                background: '#fff', borderRadius: 16,
                padding: '20px 28px', border: '1.5px solid #f0f0f0',
                display: 'flex', justifyContent: 'space-between',
                alignItems: 'center', flexWrap: 'wrap', gap: 16
              }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                  <div style={{
                    width: 8, height: 8, borderRadius: '50%',
                    background: isHome(ticket.location) ? '#C5050C' : '#bbb',
                    flexShrink: 0
                  }} />
                  <div>
                    <p style={{ fontWeight: 700, fontSize: 16, color: '#111', margin: 0 }}>{ticket.name}</p>
                    <p style={{ fontSize: 13, color: '#999', margin: '2px 0 0' }}>
                      {ticket.location} · {ticket.event_date}
                    </p>
                  </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                  <div style={{ textAlign: 'right' }}>
                    <p style={{ fontWeight: 700, fontSize: 18, color: '#111', margin: 0 }}>${ticket.price}</p>
                    <p style={{ fontSize: 12, color: '#bbb', margin: '2px 0 0' }}>{ticket.quantity} left</p>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <select
                      value={quantities[ticket.name] || 1}
                      onChange={e => setQuantities(prev => ({ ...prev, [ticket.name]: Number(e.target.value) }))}
                      style={{
                        padding: '8px 12px', border: '1.5px solid #e0e0e0',
                        borderRadius: 8, fontFamily: "'DM Sans', sans-serif",
                        fontSize: 14, background: '#fff', cursor: 'pointer'
                      }}
                    >
                      {[1, 2, 3, 4, 5].map(n => (
                        <option key={n} value={n}>{n}</option>
                      ))}
                    </select>
                    <button
                      onClick={() => handleBuy(ticket.name)}
                      style={{
                        padding: '10px 24px', background: '#111', color: '#fff',
                        border: 'none', borderRadius: 100, cursor: 'pointer',
                        fontFamily: "'DM Sans', sans-serif", fontWeight: 700,
                        fontSize: 14, whiteSpace: 'nowrap', transition: 'background 0.15s'
                      }}
                      onMouseOver={e => (e.target as HTMLButtonElement).style.background = '#C5050C'}
                      onMouseOut={e => (e.target as HTMLButtonElement).style.background = '#111'}
                    >
                      Buy
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Legend */}
          <div style={{ display: 'flex', gap: 20, marginTop: 20 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <div style={{ width: 8, height: 8, borderRadius: '50%', background: '#C5050C' }} />
              <span style={{ fontSize: 12, color: '#999' }}>Home game</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <div style={{ width: 8, height: 8, borderRadius: '50%', background: '#bbb' }} />
              <span style={{ fontSize: 12, color: '#999' }}>Away game</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default TicketsPage