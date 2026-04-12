import { useKeycloak } from "@react-keycloak/web"
import { useState } from "react"
import { useNavigate } from 'react-router-dom'

function SetupPage() {
    const { keycloak } = useKeycloak()
    const navigate = useNavigate()
    const [cardNumber, setCardNumber] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    const handleSubmit = async () => {
        setLoading(true)
        setError('')
        const res = await fetch('http://localhost:8080/api/account/setup', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${keycloak.token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ cardNumber })
        })
        if (res.ok) {
            navigate('/dashboard')
        } else {
            setError('Invalid card number. Must be 16 digits.')
            setLoading(false)
        }
    }

    return (
        <div style={{
            fontFamily: "'DM Sans', sans-serif",
            minHeight: '100vh', background: '#fafafa',
            display: 'flex', flexDirection: 'column'
        }}>
            {/* Minimal navbar */}
            <nav style={{
                display: 'flex', alignItems: 'center', gap: 10,
                padding: '20px 48px', borderBottom: '1px solid #f0f0f0',
                background: '#fff'
            }}>
                <div style={{
                    width: 32, height: 32, background: '#C5050C', borderRadius: 6,
                    display: 'flex', alignItems: 'center', justifyContent: 'center'
                }}>
                    <span style={{ color: '#fff', fontFamily: "'Bebas Neue', cursive", fontSize: 16 }}>B</span>
                </div>
                <span style={{ fontFamily: "'Bebas Neue', cursive", fontSize: 22, letterSpacing: '1px' }}>
                    BadgerPass
                </span>
            </nav>

            {/* Main content */}
            <div style={{
                flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center',
                padding: '48px'
            }}>
                <div style={{ width: '100%', maxWidth: 480 }}>

                    {/* Step indicator */}
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 32 }}>
                        <div style={{
                            width: 24, height: 24, borderRadius: '50%',
                            background: '#C5050C', display: 'flex',
                            alignItems: 'center', justifyContent: 'center'
                        }}>
                            <span style={{ color: '#fff', fontSize: 11, fontWeight: 700 }}>1</span>
                        </div>
                        <div style={{ height: 1, width: 40, background: '#e0e0e0' }} />
                        <div style={{
                            width: 24, height: 24, borderRadius: '50%',
                            background: '#e0e0e0', display: 'flex',
                            alignItems: 'center', justifyContent: 'center'
                        }}>
                            <span style={{ color: '#999', fontSize: 11, fontWeight: 700 }}>2</span>
                        </div>
                        <span style={{ fontSize: 13, color: '#999', marginLeft: 8 }}>Complete your profile</span>
                    </div>

                    <h1 style={{
                        fontFamily: "'Bebas Neue', cursive",
                        fontSize: 52, letterSpacing: '2px',
                        color: '#111', margin: '0 0 8px'
                    }}>
                        One last step.
                    </h1>
                    <p style={{ color: '#888', fontSize: 16, lineHeight: 1.6, marginBottom: 40 }}>
                        Add a payment card to start buying and selling tickets. Your card number is encrypted and stored securely.
                    </p>

                    {/* Card input */}
                    <div style={{ marginBottom: 20 }}>
                        <label style={{
                            display: 'block', fontSize: 13, fontWeight: 700,
                            color: '#555', letterSpacing: '0.5px',
                            textTransform: 'uppercase', marginBottom: 8
                        }}>
                            Card Number
                        </label>
                        <input
                            type="text"
                            placeholder="1234 5678 9012 3456"
                            value={cardNumber}
                            maxLength={16}
                            onChange={e => setCardNumber(e.target.value.replace(/\D/g, ''))}
                            style={{
                                width: '100%', padding: '16px 20px',
                                border: error ? '1.5px solid #C5050C' : '1.5px solid #e0e0e0',
                                borderRadius: 12, fontSize: 18,
                                fontFamily: "'DM Sans', sans-serif",
                                letterSpacing: '2px', outline: 'none',
                                transition: 'border 0.15s', boxSizing: 'border-box',
                                background: '#fff'
                            }}
                            onFocus={e => {
                                if (!error) e.target.style.borderColor = '#111'
                            }}
                            onBlur={e => {
                                if (!error) e.target.style.borderColor = '#e0e0e0'
                            }}
                        />
                        {error && (
                            <p style={{ color: '#C5050C', fontSize: 13, marginTop: 8, fontWeight: 500 }}>
                                {error}
                            </p>
                        )}
                        <p style={{ color: '#bbb', fontSize: 12, marginTop: 8 }}>
                            {cardNumber.length}/16 digits
                        </p>
                    </div>

                    {/* Security note */}
                    <div style={{
                        background: '#f8f8f8', borderRadius: 12,
                        padding: '14px 18px', marginBottom: 28,
                        display: 'flex', gap: 10, alignItems: 'flex-start'
                    }}>
                        <span style={{ fontSize: 16 }}>🔒</span>
                        <p style={{ fontSize: 13, color: '#888', margin: 0, lineHeight: 1.5 }}>
                            Your card number is encrypted with AES-256 before being stored. We never store raw card data.
                        </p>
                    </div>

                    {/* Submit button */}
                    <button
                        onClick={handleSubmit}
                        disabled={cardNumber.length !== 16 || loading}
                        style={{
                            width: '100%', padding: '16px',
                            background: cardNumber.length === 16 && !loading ? '#C5050C' : '#e0e0e0',
                            color: cardNumber.length === 16 && !loading ? '#fff' : '#aaa',
                            border: 'none', borderRadius: 100, cursor: cardNumber.length === 16 ? 'pointer' : 'not-allowed',
                            fontFamily: "'DM Sans', sans-serif", fontWeight: 700, fontSize: 16,
                            transition: 'all 0.15s'
                        }}
                    >
                        {loading ? 'Setting up...' : 'Complete setup →'}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default SetupPage