import { useEffect, useState } from 'react'
import { useKeycloak } from '@react-keycloak/web'
import Navbar from '../components/Navbar'
import { authFetch } from '../services/api'

function AccountPage() {
  const { keycloak } = useKeycloak()
  const [balance, setBalance] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)
  const [showDepositMsg, setShowDepositMsg] = useState(false)

  const headers = { 'Authorization': `Bearer ${keycloak.token}` }

  useEffect(() => {
    authFetch(keycloak, 'http://localhost:8080/api/account/balance')
      .then(r => r.json())
      .then(data => {
        setBalance(data.balance)
        setLoading(false)
      })
  }, [])

  const username = keycloak.tokenParsed?.preferred_username as string
  const email = keycloak.tokenParsed?.email as string

  if (loading) return (
    <div style={{ fontFamily: "'DM Sans', sans-serif" }}>
      <Navbar />
      <div style={{ padding: '80px 48px', textAlign: 'center', color: '#999' }}>Loading...</div>
    </div>
  )

  return (
    <div style={{ fontFamily: "'DM Sans', sans-serif", background: '#fafafa', minHeight: '100vh' }}>
      <Navbar />

      <div style={{ maxWidth: 800, margin: '0 auto', padding: '48px' }}>

        {/* Header */}
        <div style={{ marginBottom: 40 }}>
          <p style={{
            color: '#999', fontSize: 12, fontWeight: 700,
            letterSpacing: '1.5px', textTransform: 'uppercase', marginBottom: 8
          }}>
            My Account
          </p>
          <h1 style={{
            fontFamily: "'Bebas Neue', cursive",
            fontSize: 56, fontWeight: 400, letterSpacing: '2px',
            color: '#111', margin: 0
          }}>
            {username}
          </h1>
          {email && (
            <p style={{ color: '#999', fontSize: 15, marginTop: 8 }}>{email}</p>
          )}
        </div>

        {/* Balance card */}
        <div style={{
          background: '#C5050C', borderRadius: 20,
          padding: '40px', marginBottom: 20, color: '#fff'
        }}>
          <p style={{
            fontSize: 12, fontWeight: 700, letterSpacing: '1.5px',
            textTransform: 'uppercase', opacity: 0.7, marginBottom: 12
          }}>
            Account Balance
          </p>
          <p style={{
            fontFamily: "'Bebas Neue', cursive",
            fontSize: 64, letterSpacing: '2px', margin: '0 0 24px'
          }}>
            ${balance?.toFixed(2)}
          </p>
          <button
            onClick={() => setShowDepositMsg(true)}
            style={{
              padding: '12px 28px', background: 'rgba(255,255,255,0.2)',
              color: '#fff', border: '1.5px solid rgba(255,255,255,0.4)',
              borderRadius: 100, cursor: 'pointer',
              fontFamily: "'DM Sans', sans-serif", fontWeight: 700,
              fontSize: 14, transition: 'all 0.15s'
            }}
            onMouseOver={e => (e.target as HTMLButtonElement).style.background = 'rgba(255,255,255,0.3)'}
            onMouseOut={e => (e.target as HTMLButtonElement).style.background = 'rgba(255,255,255,0.2)'}
          >
            + Deposit funds
          </button>
          {showDepositMsg && (
            <p style={{ fontSize: 13, opacity: 0.8, marginTop: 12 }}>
              💳 Payment gateway coming soon. Stay tuned!
            </p>
          )}
        </div>

        {/* Account details */}
        <div style={{
          background: '#fff', borderRadius: 20,
          border: '1.5px solid #f0f0f0', overflow: 'hidden',
          marginBottom: 20
        }}>
          <div style={{ padding: '20px 28px', borderBottom: '1px solid #f0f0f0' }}>
            <p style={{
              fontSize: 12, fontWeight: 700, letterSpacing: '1.5px',
              textTransform: 'uppercase', color: '#999', margin: '0 0 16px'
            }}>
              Account Details
            </p>
            {[
              { label: 'Username', value: username },
              { label: 'Email', value: email || 'Not set' },
              { label: 'Account type', value: 'Standard' },
              { label: 'Member since', value: '2026' },
            ].map((item, i) => (
              <div key={i} style={{
                display: 'flex', justifyContent: 'space-between',
                alignItems: 'center', padding: '12px 0',
                borderBottom: i < 3 ? '1px solid #f8f8f8' : 'none'
              }}>
                <span style={{ fontSize: 14, color: '#999' }}>{item.label}</span>
                <span style={{ fontSize: 14, fontWeight: 600, color: '#111' }}>{item.value}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Security section */}
        <div style={{
          background: '#111', borderRadius: 20,
          padding: '28px', color: '#fff'
        }}>
          <p style={{
            fontSize: 12, fontWeight: 700, letterSpacing: '1.5px',
            textTransform: 'uppercase', opacity: 0.5, marginBottom: 16
          }}>
            Security
          </p>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            {[
              { icon: '🔒', label: 'Card encryption', value: 'AES-256 encrypted' },
              { icon: '🛡️', label: 'Authentication', value: 'Keycloak OAuth2/OIDC' },
              { icon: '✅', label: 'Session', value: 'JWT secured' },
            ].map((item, i) => (
              <div key={i} style={{
                display: 'flex', justifyContent: 'space-between',
                alignItems: 'center'
              }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <span style={{ fontSize: 16 }}>{item.icon}</span>
                  <span style={{ fontSize: 14, color: 'rgba(255,255,255,0.6)' }}>{item.label}</span>
                </div>
                <span style={{
                  fontSize: 12, fontWeight: 700, color: '#fff',
                  background: 'rgba(255,255,255,0.1)',
                  padding: '4px 12px', borderRadius: 100
                }}>
                  {item.value}
                </span>
              </div>
            ))}
          </div>
        </div>

      </div>
    </div>
  )
}

export default AccountPage