import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import './App.css'
import { Routes, Route } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import HomePage from "./pages/HomePage.jsx";
import RegisterSuccessPage from "./pages/RegisterSuccessPage.jsx"

function App() {
  const [count, setCount] = useState(0)

  return (
      <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/home" element={<HomePage />} />
          <Route path="/register-success" element={<RegisterSuccessPage />} />
      </Routes>
  )
}

export default App
