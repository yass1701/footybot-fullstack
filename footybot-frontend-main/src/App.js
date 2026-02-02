import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './AuthContext';
import './App.css';

// We only need to import the top-level pages here
import LoginPage from './component/LoginPage';
import RegistrationPage from './component/RegistrationPage';
import MainLayout from './component/MainLayout';

function App() {
  const { token } = useAuth();

  return (
    <Routes>
      {token ? (
        // If logged in, show the main layout for all paths
        <>
          <Route path="/*" element={<MainLayout />} />
          <Route path="/login" element={<Navigate to="/" />} />
          <Route path="/register" element={<Navigate to="/" />} />
        </>
      ) : (
        // If not logged in, show login/register pages
        <>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegistrationPage />} />
          <Route path="/*" element={<Navigate to="/login" />} />
        </>
      )}
    </Routes>
  );
}

export default App;