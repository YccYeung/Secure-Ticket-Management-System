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
        <div>
            <button onClick={() => keycloak.login()}>Login</button>
            <button onClick={() => keycloak.register()}>Register</button>
        </div>
    )
}

export default LandingPage