import { StrictMode } from 'react'
import ReactDOM from 'react-dom/client'
import { ReactKeycloakProvider } from '@react-keycloak/web'
import App from './App'
import keycloak from './keycloak'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <ReactKeycloakProvider 
    authClient={keycloak}
    initOptions={{ onLoad: 'check-sso' }}
  >
    <App />
  </ReactKeycloakProvider>
)