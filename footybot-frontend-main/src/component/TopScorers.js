// src/component/TopScorers.js
import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import './TopScorers.css';

const TopScorers = () => {
    const { token } = useAuth();
    const [scorers, setScorers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchScorers = async () => {
            if (!token) {
                setError('No authentication token found');
                setLoading(false);
                return;
            }

            const headers = {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            };

            try {
                const response = await fetch('http://localhost:8080/api/top-scorers', { headers });
                if (!response.ok) {
                    throw new Error('Failed to fetch top scorers');
                }
                const data = await response.json();
                setScorers(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchScorers();
    }, [token]);

    if (loading) return <p>Loading top scorers...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div className="scorers-container">
            <h2>Top Goal Scorers</h2>
            {scorers.length > 0 ? (
                <ul className="scorers-list">
                    {scorers.map(scorer => (
                        <li key={scorer.rank}>
                            <span className="rank">{scorer.rank}</span>
                            <img src={scorer.crestUrl} alt={scorer.playerTeam} className="scorer-crest" />
                            <div className="scorer-info">
                                <span className="scorer-name">{scorer.playerName}</span>
                                <span className="scorer-team">{scorer.playerTeam}</span>
                            </div>
                            <span className="scorer-goals">{scorer.goals}</span>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No goal scorers yet. Add a match with scorers in the admin panel!</p>
            )}
        </div>
    );
};

export default TopScorers;