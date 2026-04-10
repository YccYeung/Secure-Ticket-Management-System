import { useKeycloak } from "@react-keycloak/web"
import { useState } from "react"
import { useNavigate } from 'react-router-dom'

function SetupPage() {
    const {keycloak} = useKeycloak()
    const navigate = useNavigate()
    const [cardNumber, setCardNumber] = useState('')
    const [error, setError] = useState('')

    const handleSubmit = async () => {
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
      }
  }

  return (
    <div>
        <h1>Complete Your Profile</h1>
        <input
            type="text"
            placeholder="16-digit card number"
            value={cardNumber}
            onChange={e => setCardNumber(e.target.value)}
        />
        {error && <p>{error}</p>}
        <button onClick={handleSubmit}>Continue</button>
    </div>
  )
}
  
export default SetupPage