import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import PlayerCard from './PlayerCard';
import TeamCard from "./TeamCard"; 
import StandingsTable from './StandingsTable';
import './FootballData.css';

const FootballData = () => {
    const { token } = useAuth();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [teams, setTeams] = useState([]);
    const [players, setPlayers] = useState([]);
    const [selectedTeam, setSelectedTeam] = useState(null);
    const [playerSearchTerm, setPlayerSearchTerm] = useState('');
    const [teamSearchTerm, setTeamSearchTerm] = useState('');
    const [loadingPlayers, setLoadingPlayers] = useState(false);
    const [standings, setStandings] = useState([]);
    const [viewMode, setViewMode] = useState('teams'); // 'teams' or 'standings'

    useEffect(() => {
        const fetchInitialData = async () => {
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
                const [teamsRes, standingsRes] = await Promise.all([
                    fetch('http://localhost:8080/api/football-data', { headers }),
                    fetch('http://localhost:8080/api/standings', { headers })
                ]);
                
                if (!teamsRes.ok || !standingsRes.ok) {
                    throw new Error('Failed to fetch initial data from the server.');
                }
                
                const teamsData = await teamsRes.json();
                const standingsData = await standingsRes.json();

                if (teamsData.teams) setTeams(teamsData.teams);
                setStandings(standingsData);

            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchInitialData();
    }, [token]);

    const filteredPlayers = players.filter(p => p.name.toLowerCase().includes(playerSearchTerm.toLowerCase()));
    const filteredTeams = teams.filter(t => t.name.toLowerCase().includes(teamSearchTerm.toLowerCase()));

    const fetchPlayers = async (team) => {
        setSelectedTeam(team);
        setLoadingPlayers(true);
        setPlayers([]);
        
        if (!token) {
            setError('No authentication token found');
            setLoadingPlayers(false);
            return;
        }

        const headers = {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };

        try {
            const response = await fetch(`http://localhost:8080/api/players/team/${encodeURIComponent(team.name)}`, { headers });
            const data = await response.json();
            setPlayers(Array.isArray(data) ? data : []);
        } catch (err) {
            setError("Failed to fetch players for " + team.name);
        } finally {
            setLoadingPlayers(false);
        }
    };

    const handleBackClick = () => {
        setSelectedTeam(null);
        setPlayers([]);
        setPlayerSearchTerm(''); 
    };

    if (loading) return <p style={{ textAlign: 'center' }}>Loading all data...</p>;
    if (error) return <p style={{ color: 'red', textAlign: 'center' }}>Error: {error}</p>;

    return (
        <div className="football-container">
            {selectedTeam ? (
                // Player View
                <div className="player-view">
                    <button onClick={handleBackClick} className="back-button">Back to Teams</button>
                    <h2 className="teams-section">Players for {selectedTeam.name} ({filteredPlayers.length})</h2>
                    <div className="search-container">
                        <input type="text" placeholder="Search for a player..." className="search-input" value={playerSearchTerm} onChange={(e) => setPlayerSearchTerm(e.target.value)} />
                    </div>
                    {loadingPlayers ? <p style={{ textAlign: 'center' }}>Loading players...</p> : (
                        <div className="teams-grid">
                            {filteredPlayers.length > 0 ? filteredPlayers.map((player) => (
                                <PlayerCard key={player.id} player={player} />
                            )) : <p>No players found.</p>}
                        </div>
                    )}
                </div>
            ) : (
                // Main View (Toggles between Teams and Standings)
                <div>
                    <div className="view-toggle">
                        <button onClick={() => setViewMode(viewMode === 'teams' ? 'standings' : 'teams')}>
                            {viewMode === 'teams' ? 'View Standings' : 'View All Teams'}
                        </button>
                    </div>

                    {viewMode === 'teams' ? (
                        <>
                            <h2 className="teams-section">Premier League Teams ({filteredTeams.length})</h2>
                            <div className="search-container">
                                <input type="text" placeholder="Search for a team..." className="search-input" value={teamSearchTerm} onChange={(e) => setTeamSearchTerm(e.target.value)} />
                            </div>
                            <div className="teams-grid">
                                {filteredTeams.map((team) => (
                                    <TeamCard key={team.id} team={team} onClick={() => fetchPlayers(team)} />
                                ))}
                            </div>
                        </>
                    ) : (
                        <StandingsTable standings={standings} />
                    )}
                </div>
            )}
        </div>
    );
};

export default FootballData;