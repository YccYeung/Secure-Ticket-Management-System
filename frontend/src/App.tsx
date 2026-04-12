import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LandingPage from './pages/LandingPage'
import SetupPage from './pages/SetupPage'
import DashboardPage from './pages/DashboardPage'
import TicketsPage from './pages/TicketsPage'
import AccountPage from './pages/AccountPage'
import ProtectedRoute from './components/ProtectedRoute'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/setup" element={<SetupPage />} />
        <Route path="/dashboard" element={
          <ProtectedRoute><DashboardPage /></ProtectedRoute>
        } />
        <Route path="/tickets" element={
          <ProtectedRoute><TicketsPage /></ProtectedRoute>
        } />
        <Route path="/account" element={
          <ProtectedRoute><AccountPage /></ProtectedRoute>
        } />
      </Routes>
    </BrowserRouter>
  )
}
export default App