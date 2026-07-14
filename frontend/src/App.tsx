import { Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import UserProfilePage from './pages/UserProfilePage'
import UserAddressPage from './pages/UserAddressPage'

function HomePage() {
  return (
    <div style={{ padding: 40, textAlign: 'center' }}>
      <h1>校园闲置交易平台</h1>
      <p>欢迎使用校园闲置交易平台</p>
    </div>
  )
}

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/user/profile" element={<UserProfilePage />} />
      <Route path="/user/address" element={<UserAddressPage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default App
