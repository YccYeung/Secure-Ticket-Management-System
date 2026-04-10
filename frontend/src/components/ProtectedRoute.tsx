import { useKeycloak } from '@react-keycloak/web'
import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
    const { keycloak, initialized } = useKeycloak()
    const [ setupComplete, setSetupComplete ] = useState<boolean | null>(null)

    useEffect(() => {
        if (initialized && keycloak.authenticated) {
            fetch('http://localhost:8080/api/account/me', {
                headers: { 'Authorization': `Bearer ${keycloak.token}` }
            })
            .then(res => res.json())
            .then(data => setSetupComplete(data.setupComplete))
        }
    }, [initialized, keycloak.authenticated])

    if (!initialized) return <div>Loading...</div>
    if (!keycloak.authenticated) return <Navigate to="/" />
    if (setupComplete === null) return <div>Loading...</div>
    if (!setupComplete) return <Navigate to="/setup" />
    
    return <>{children}</>
}

export default ProtectedRoute