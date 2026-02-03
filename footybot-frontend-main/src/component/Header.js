import React from 'react';
import { NavLink, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../AuthContext';
import './Header.css';

const Header = () => {
    const { logout, roles } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <header className="app-header">
            {/* Top Bar with Logo and User Actions */}
            <div className="header-top">
                <div className="logo-section">
                    <div className="logo-icon">⚽</div>
                    <div className="logo-text">
                        <h1>FutQuiz</h1>
                        <span className="tagline">Your Football Companion</span>
                    </div>
                </div>
                
                <div className="header-actions">
                    {roles && roles.includes('ROLE_ADMIN') && (
                        <Link to="/admin" className="admin-link">
                            <span className="admin-icon">⚙️</span>
                            Admin Panel
                        </Link>
                    )}
                    <button onClick={handleLogout} className="logout-btn">
                        <span className="logout-icon">🚪</span>
                        Logout
                    </button>
                </div>
            </div>

            {/* Main Navigation */}
            <nav className="main-navigation">
                <div className="nav-container">
                    <NavLink to="/" className="nav-item nav-btn">
                        <span className="nav-icon">🏆</span>
                        <span className="nav-text">Standings</span>
                    </NavLink>
                    <NavLink to="/results" className="nav-item nav-btn">
                        <span className="nav-icon">📊</span>
                        <span className="nav-text">Results</span>
                    </NavLink>
                    <NavLink to="/h2h" className="nav-item nav-btn">
                        <span className="nav-icon">⚔️</span>
                        <span className="nav-text">H2H Stats</span>
                    </NavLink>
                    <NavLink to="/quiz" className="nav-item nav-btn">
                        <span className="nav-icon">🧠</span>
                        <span className="nav-text">Quiz</span>
                    </NavLink>
                    <NavLink to="/leaderboard" className="nav-item nav-btn">
                        <span className="nav-icon">🏅</span>
                        <span className="nav-text">Leaderboard</span>
                    </NavLink>
                </div>
            </nav>
        </header>
    );
};

export default Header;
