import React from 'react';
import { Routes, Route } from 'react-router-dom';

// Import Header and Footer components
import Header from './Header';
import Footer from './Footer';

// Import all your page components
import FootballData from './FootballData';
import MatchResults from './MatchResults';
import H2HPage from './H2HPage';
import AdminPage from './AdminPage';
import QuizPage from './QuizPage';
import LeaderboardPage from './LeaderboardPage';

const MainLayout = () => {
    return (
        <div className="app-layout">
            <Header />
            
            <main className="main-content">
                <Routes>
                    <Route path="/" element={<FootballData />} />
                    <Route path="/results" element={<MatchResults />} />
                    <Route path="/h2h" element={<H2HPage />} />
                    <Route path="/quiz" element={<QuizPage />} />
                    <Route path="/leaderboard" element={<LeaderboardPage />} />
                    <Route path="/admin" element={<AdminPage />} />
                </Routes>
            </main>
            
            <Footer />
        </div>
    );
};

export default MainLayout;