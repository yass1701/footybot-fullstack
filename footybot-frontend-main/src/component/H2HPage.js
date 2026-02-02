// src/component/H2HPage.js
import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import MatchResultCard from './MatchResultCard';
import LastMatchComparison from './LastMatchComparison';
import './H2HPage.css';

const H2HPage = () => {
    const { token } = useAuth();
    const [teams, setTeams] = useState([]);
    const [team1, setTeam1] = useState('');
    const [team2, setTeam2] = useState('');
    const [h2hMatches, setH2hMatches] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('Select two teams to compare their match history.');

    useEffect(() => {
        const fetchTeams = async () => {
            if (!token) return;

            const headers = {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            };

            try {
                const response = await fetch('http://localhost:8080/api/football-data', { headers });
                const data = await response.json();
                if (data.teams) {
                    setTeams(data.teams);
                    if (data.teams.length > 1) {
                        setTeam1(data.teams[0].name);
                        setTeam2(data.teams[1].name);
                    }
                }
            } catch (err) {
                console.error("Failed to fetch teams:", err);
            }
        };
        fetchTeams();
    }, [token]);

    const handleFetchH2H = async () => {
        if (team1 === team2) {
            setMessage('Please select two different teams.');
            setH2hMatches([]);
            return;
        }
        setLoading(true);
        setMessage('');
        setH2hMatches([]);

        if (!token) {
            setMessage('No authentication token found');
            setLoading(false);
            return;
        }

        const headers = {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };

        try {
            const response = await fetch(`http://localhost:8080/api/matches/h2h?team1=${encodeURIComponent(team1)}&team2=${encodeURIComponent(team2)}`, { headers });
            if (!response.ok) throw new Error('Could not fetch H2H data.');
            
            const data = await response.json();
            setH2hMatches(data);

            if (data.length === 0) {
                setMessage(`No matches found between ${team1} and ${team2}.`);
            }
        } catch (err) {
            setMessage(err.message);
        } finally {
            setLoading(false);
        }
    };

    // Get the most recent match from the list
    const lastMatch = h2hMatches.length > 0 ? h2hMatches[0] : null;

    return (
        <div className="h2h-container">
            <h2>Head-to-Head Statistics</h2>
            <div className="h2h-selector">
                <select value={team1} onChange={(e) => setTeam1(e.target.value)}>
                    {teams.map(team => <option key={`t1-${team.id}`} value={team.name}>{team.name}</option>)}
                </select>
                <span className="vs">vs</span>
                <select value={team2} onChange={(e) => setTeam2(e.target.value)}>
                    {teams.map(team => <option key={`t2-${team.id}`} value={team.name}>{team.name}</option>)}
                </select>
                <button onClick={handleFetchH2H} disabled={loading}>
                    {loading ? 'Searching...' : 'Compare'}
                </button>
            </div>
            
            <div className="h2h-results">
                {/* USE the lastMatch variable here to render the comparison component */}
                {lastMatch && lastMatch.homePossession != null && <LastMatchComparison match={lastMatch} />}

                {h2hMatches.length > 0 ? (
                    <div className="h2h-history">
                      <h4>Match History</h4>
                      {h2hMatches.map(match => (
                          <MatchResultCard key={match.id} match={match} teams={teams} />
                      ))}
                    </div>
                ) : (
                    <p className="message">{message}</p>
                )}
            </div>
        </div>
    );
};

export default H2HPage;