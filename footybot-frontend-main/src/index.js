  // src/index.js
  import React from 'react';
  import ReactDOM from 'react-dom/client';
  import { BrowserRouter } from 'react-router-dom'; // 1. Make sure this is imported
  import './index.css';
  import App from './App';
  import { AuthProvider } from './AuthContext';

  const root = ReactDOM.createRoot(document.getElementById('root'));
  root.render(
    <React.StrictMode>
      {/* 2. Make sure <App /> is wrapped like this */}
      <BrowserRouter>
      <AuthProvider>
        <App />
        </AuthProvider>
      </BrowserRouter>
    </React.StrictMode>
  );