import { useKeycloak } from '@react-keycloak/web'
import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

function LandingPage() {
  const { keycloak, initialized } = useKeycloak()
  const navigate = useNavigate()

  useEffect(() => {
    if (initialized && keycloak.authenticated) {
      navigate('/dashboard')
    }
  }, [initialized, keycloak.authenticated])

  return (
    <div style={{ 
        fontFamily: "'DM Sans', sans-serif", 
        background: '#fff', 
        minHeight: '100vh', 
        color: '#111',
        width: '100%',
        overflowX: 'hidden'
      }}>

      {/* Navbar */}
      <nav style={{
        display: 'flex', justifyContent: 'space-between', alignItems: 'center',
        padding: '20px 48px', position: 'sticky', top: 0, background: 'rgba(255,255,255,0.95)',
        backdropFilter: 'blur(8px)', zIndex: 100, borderBottom: '1px solid #f0f0f0'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{
            width: 32, height: 32, background: '#C5050C', borderRadius: 6,
            display: 'flex', alignItems: 'center', justifyContent: 'center'
          }}>
            <span style={{ color: '#fff', fontFamily: "'Bebas Neue', cursive", fontSize: 16 }}>B</span>
          </div>
          <span style={{ fontSize: 22, fontFamily: "'Bebas Neue', cursive", letterSpacing: '1px' }}>BadgerPass</span>
        </div>
        <div style={{ display: 'flex', gap: 12 }}>
          <button
            onClick={() => keycloak.login()}
            style={{
              padding: '10px 24px', border: '1.5px solid #111', borderRadius: 100,
              background: 'transparent', cursor: 'pointer', fontWeight: 600,
              fontSize: 14, fontFamily: "'DM Sans', sans-serif", transition: 'all 0.2s'
            }}
            onMouseOver={e => { (e.target as HTMLButtonElement).style.background = '#111'; (e.target as HTMLButtonElement).style.color = '#fff' }}
            onMouseOut={e => { (e.target as HTMLButtonElement).style.background = 'transparent'; (e.target as HTMLButtonElement).style.color = '#111' }}
          >
            Sign in
          </button>
          <button
            onClick={() => navigate('/register')}
            style={{
              padding: '10px 24px', border: '1.5px solid #C5050C', borderRadius: 100,
              background: '#C5050C', cursor: 'pointer', fontWeight: 600,
              fontSize: 14, fontFamily: "'DM Sans', sans-serif", color: '#fff', transition: 'all 0.2s'
            }}
            onMouseOver={e => { (e.target as HTMLButtonElement).style.background = '#a00009' }}
            onMouseOut={e => { (e.target as HTMLButtonElement).style.background = '#C5050C' }}
          >
            Get started
          </button>
        </div>
      </nav>

      {/* Hero */}
      <section style={{ padding: '80px 48px 60px', maxWidth: 1100, margin: '0 auto' }}>
        <div style={{
          display: 'inline-block', background: '#C5050C', color: '#fff',
          padding: '4px 14px', borderRadius: 100, fontSize: 12,
          fontWeight: 700, letterSpacing: '1px', textTransform: 'uppercase', marginBottom: 28
        }}>
          2026 Season Now Available
        </div>

        <h1 style={{
          fontFamily: "'Bebas Neue', cursive",
          fontSize: 'clamp(72px, 12vw, 140px)',
          fontWeight: 400, lineHeight: 0.95, letterSpacing: '2px',
          marginBottom: 32, maxWidth: 800
        }}>
          Wisconsin<br />
          <span style={{ color: '#C5050C' }}>Badgers</span><br />
          Tickets.
        </h1>

        <p style={{
          fontSize: 20, color: '#555', maxWidth: 480,
          lineHeight: 1.6, marginBottom: 40, fontWeight: 400
        }}>
          Buy, sell and manage your Camp Randall tickets — securely, in one place. No hidden fees, no nonsense.
        </p>

        <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
          <button
            onClick={() => navigate('/register')}
            style={{
              padding: '16px 36px', background: '#C5050C', color: '#fff',
              border: 'none', borderRadius: 100, cursor: 'pointer',
              fontWeight: 700, fontSize: 16, fontFamily: "'DM Sans', sans-serif",
              transition: 'transform 0.15s, background 0.15s'
            }}
            onMouseOver={e => { (e.target as HTMLButtonElement).style.transform = 'translateY(-2px)'; (e.target as HTMLButtonElement).style.background = '#a00009' }}
            onMouseOut={e => { (e.target as HTMLButtonElement).style.transform = 'translateY(0)'; (e.target as HTMLButtonElement).style.background = '#C5050C' }}
          >
            Get started free
          </button>
          <button
            onClick={() => keycloak.login()}
            style={{
              padding: '16px 36px', background: 'transparent', color: '#111',
              border: '1.5px solid #ddd', borderRadius: 100, cursor: 'pointer',
              fontWeight: 600, fontSize: 16, fontFamily: "'DM Sans', sans-serif",
              transition: 'all 0.15s'
            }}
            onMouseOver={e => { (e.target as HTMLButtonElement).style.borderColor = '#111' }}
            onMouseOut={e => { (e.target as HTMLButtonElement).style.borderColor = '#ddd' }}
          >
            Sign in →
          </button>
        </div>
      </section>

      {/* Red Schedule Banner */}
      <section style={{ background: '#C5050C', padding: '48px', margin: '0 48px', borderRadius: 24 }}>
        <div style={{ maxWidth: 1004, margin: '0 auto' }}>
          <p style={{
            color: 'rgba(255,255,255,0.7)', fontSize: 12, fontWeight: 700,
            letterSpacing: '1.5px', textTransform: 'uppercase', marginBottom: 16
          }}>
            2026 Schedule
          </p>
          <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
            {[
              { opp: 'Notre Dame', date: 'Sep 6', home: false },
              { opp: 'Western Illinois', date: 'Sep 12', home: true },
              { opp: 'Eastern Michigan', date: 'Sep 19', home: true },
              { opp: 'Penn State', date: 'Sep 26', home: false },
              { opp: 'Michigan State', date: 'Oct 3', home: true },
              { opp: 'USC', date: 'Oct 24', home: true },
            ].map((game, i) => (
              <div key={i} style={{
                background: 'rgba(255,255,255,0.12)', borderRadius: 12,
                padding: '14px 20px', flex: '1 1 140px', minWidth: 140
              }}>
                <div style={{ color: 'rgba(255,255,255,0.6)', fontSize: 11, marginBottom: 4 }}>
                  {game.date} · {game.home ? 'HOME' : 'AWAY'}
                </div>
                <div style={{ color: '#fff', fontFamily: "'Bebas Neue', cursive", letterSpacing: '1px', fontSize: 20 }}>
                  {game.home ? 'vs ' : 'at '}{game.opp}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Features */}
      <section style={{ padding: '80px 48px', maxWidth: 1100, margin: '0 auto' }}>
        <p style={{
          color: '#999', fontSize: 12, fontWeight: 700,
          letterSpacing: '1.5px', textTransform: 'uppercase', marginBottom: 16
        }}>
          Why BadgerPass
        </p>
        <h2 style={{
          fontFamily: "'Bebas Neue', cursive",
          fontSize: 64, fontWeight: 400, letterSpacing: '2px', marginBottom: 48
        }}>
          Built for Badger fans
        </h2>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: 20 }}>
          {[
            { icon: '🎟️', title: 'Buy & Sell Tickets', desc: 'Browse the full 2026 schedule and get your seats to any home or away game.' },
            { icon: '🔒', title: 'Secure Payments', desc: 'Your card details are encrypted with AES-256. We never store raw card numbers.' },
            { icon: '⚡', title: 'Instant Transfer', desc: 'Buy or sell in seconds. Your account balance updates immediately after every transaction.' },
            { icon: '🏟️', title: 'Camp Randall Ready', desc: 'Home games, away games — manage your full season ticket portfolio in one place.' },
          ].map((f, i) => (
            <div key={i} style={{
              background: i === 0 ? '#111' : '#f8f8f8',
              borderRadius: 20, padding: '32px',
              color: i === 0 ? '#fff' : '#111'
            }}>
              <div style={{ fontSize: 32, marginBottom: 16 }}>{f.icon}</div>
              <h3 style={{
                fontFamily: "'Bebas Neue', cursive",
                fontSize: 28, fontWeight: 400, letterSpacing: '1px', marginBottom: 10
              }}>{f.title}</h3>
              <p style={{
                fontSize: 15, lineHeight: 1.6,
                color: i === 0 ? 'rgba(255,255,255,0.65)' : '#666',
                fontWeight: 400
              }}>{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section style={{
        margin: '0 48px 80px', background: '#111',
        borderRadius: 24, padding: '64px 48px',
        display: 'flex', justifyContent: 'space-between',
        alignItems: 'center', flexWrap: 'wrap', gap: 32
      }}>
        <div>
          <h2 style={{
            fontFamily: "'Bebas Neue', cursive",
            color: '#fff', fontSize: 56, fontWeight: 400,
            letterSpacing: '2px', marginBottom: 12
          }}>
            Ready for game day?
          </h2>
          <p style={{ color: 'rgba(255,255,255,0.5)', fontSize: 16 }}>
            Create your account in under a minute.
          </p>
        </div>
        <button
          onClick={() => navigate('/register')}
          style={{
            padding: '18px 40px', background: '#C5050C', color: '#fff',
            border: 'none', borderRadius: 100, cursor: 'pointer',
            fontWeight: 700, fontSize: 16, fontFamily: "'DM Sans', sans-serif",
            whiteSpace: 'nowrap', transition: 'background 0.15s'
          }}
          onMouseOver={e => { (e.target as HTMLButtonElement).style.background = '#a00009' }}
          onMouseOut={e => { (e.target as HTMLButtonElement).style.background = '#C5050C' }}
        >
          Get started free →
        </button>
      </section>

      {/* Footer */}
      <footer style={{
        padding: '32px 48px', borderTop: '1px solid #f0f0f0',
        display: 'flex', justifyContent: 'space-between', alignItems: 'center',
        flexWrap: 'wrap', gap: 16
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <div style={{
            width: 24, height: 24, background: '#C5050C', borderRadius: 4,
            display: 'flex', alignItems: 'center', justifyContent: 'center'
          }}>
            <span style={{ color: '#fff', fontFamily: "'Bebas Neue', cursive", fontSize: 13 }}>B</span>
          </div>
          <span style={{ fontSize: 18, fontFamily: "'Bebas Neue', cursive", letterSpacing: '1px' }}>BadgerPass</span>
        </div>
        <p style={{ color: '#999', fontSize: 13 }}>
          © 2026 BadgerPass Platform. Go Badgers! 🦡
        </p>
      </footer>
    </div>
  )
}

export default LandingPage