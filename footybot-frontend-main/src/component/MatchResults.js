// src/component/MatchResults.js
import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import MatchResultCard from './MatchResultCard';
import './MatchResults.css';

const MatchResults = () => {
    const { token } = useAuth();
    const [matches, setMatches] = useState([]);
    const [teams, setTeams] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchMatchData = async () => {
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
                // Fetch both matches and the team list (for logos)
                const [matchesRes, teamsRes] = await Promise.all([
                    fetch('http://localhost:8080/api/matches', { headers }),
                    fetch('http://localhost:8080/api/football-data', { headers })
                ]);

                if (!matchesRes.ok || !teamsRes.ok) {
                    throw new Error('Failed to fetch match data');
                }

                const matchesData = await matchesRes.json();
                const teamsData = await teamsRes.json();
                
                // Group matches by matchday
                const groupedMatches = matchesData.reduce((acc, match) => {
                    (acc[match.matchday] = acc[match.matchday] || []).push(match);
                    return acc;
                }, {});

                setMatches(groupedMatches);
                setTeams(teamsData.teams || []);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchMatchData();
    }, [token]);

    if (loading) return <p>Loading results...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div className="results-container">
            {Object.keys(matches).map(matchday => (
                <div key={matchday} className="matchday-group">
                    <h3>Matchday {matchday}</h3>
                    <div className="matches-grid">
                        {matches[matchday].map(match => (
                            <MatchResultCard key={match.id} match={match} teams={teams} />
                        ))}
                    </div>
                </div>
            ))}
        </div>
    );
};

export default MatchResults;