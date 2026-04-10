import { useKeycloak } from '@react-keycloak/web'
import { Navigate } from 'react-router-dom'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
    const { keycloak, initialized } = useKeycloak()

    if (!initialized) return <div>Loading...</div>
    if (!keycloak.authenticated) return <Navigate to="/" />
    
    return <>{children}</>
}

export default ProtectedRoute