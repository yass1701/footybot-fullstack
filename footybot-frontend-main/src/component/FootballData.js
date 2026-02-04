import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import PlayerCard from './PlayerCard';
import TeamCard from "./TeamCard"; 
import StandingsTable from './StandingsTable';
import API_BASE from '../config/api';
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
    const [teamFilter, setTeamFilter] = useState('all'); // all | big6 | london | top4
    const [loadingPlayers, setLoadingPlayers] = useState(false);
    const [standings, setStandings] = useState([]);
    const [viewMode, setViewMode] = useState('teams'); // 'teams' or 'standings'
    const [showLoadingProgress, setShowLoadingProgress] = useState(false);

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
                // Show a subtle progress indicator if loading takes a bit longer
                const progressTimer = setTimeout(() => {
                    setShowLoadingProgress(true);
                }, 2000);

                console.log('Fetching data from:', API_BASE);
                console.log('Token present:', !!token);
                
                const [teamsRes, standingsRes] = await Promise.all([
                    fetch(`${API_BASE}/api/football-data`, { headers }),
                    fetch(`${API_BASE}/api/standings`, { headers })
                ]);
                
                console.log('Teams response status:', teamsRes.status, teamsRes.ok);
                console.log('Standings response status:', standingsRes.status, standingsRes.ok);
                
                if (!teamsRes.ok) {
                    const errorText = await teamsRes.text();
                    console.error('Teams API error:', errorText);
                    throw new Error(`Failed to fetch teams: ${teamsRes.status} ${errorText}`);
                }
                
                if (!standingsRes.ok) {
                    const errorText = await standingsRes.text();
                    console.error('Standings API error:', errorText);
                    throw new Error(`Failed to fetch standings: ${standingsRes.status} ${errorText}`);
                }
                
                const teamsData = await teamsRes.json();
                const standingsData = await standingsRes.json();

                console.log('Teams data:', teamsData);
                console.log('Standings data:', standingsData);
                console.log('Standings array length:', Array.isArray(standingsData) ? standingsData.length : 'not an array');

                if (teamsData.teams) setTeams(teamsData.teams);
                setStandings(Array.isArray(standingsData) ? standingsData : []);

            } catch (err) {
                console.error('Error fetching initial data:', err);
                setError(`Failed to load data: ${err.message}. Please check your connection and try again.`);
            } finally {
                setLoading(false);
                setShowLoadingProgress(false);
            }
        };
        fetchInitialData();
    }, [token]);

    const filteredPlayers = players.filter(p =>
        p.name.toLowerCase().includes(playerSearchTerm.toLowerCase())
    );

    const BIG_SIX = [
        'Arsenal FC',
        'Chelsea FC',
        'Liverpool FC',
        'Manchester City FC',
        'Manchester United FC',
        'Tottenham Hotspur FC',
    ];

    const LONDON_CLUBS = [
        'Arsenal FC',
        'Chelsea FC',
        'Tottenham Hotspur FC',
        'West Ham United FC',
    ];

    const applyTeamFilter = (teamList) => {
        switch (teamFilter) {
            case 'big6':
                return teamList.filter((t) => BIG_SIX.includes(t.name));
            case 'london':
                return teamList.filter((t) => LONDON_CLUBS.includes(t.name));
            case 'top4':
                return teamList
                    .slice()
                    .sort((a, b) => (a.position || 99) - (b.position || 99))
                    .slice(0, 4);
            default:
                return teamList;
        }
    };

    const filteredTeams = applyTeamFilter(
        teams.filter((t) =>
            t.name.toLowerCase().includes(teamSearchTerm.toLowerCase())
        )
    );

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
            const response = await fetch(`${API_BASE}/api/players/team/${encodeURIComponent(team.name)}`, { headers });
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

    if (loading) {
        return (
            <div className="football-loading">
                <div className="football-loading-card">
                    <div className="football-loading-icon">
                        <span className="football-ball">⚽</span>
                    </div>
                    <p className="football-loading-title">
                        Loading your football universe...
                    </p>
                    <p className="football-loading-subtitle">
                        Fetching teams, standings and players.
                    </p>

                    <div className="skeleton-grid">
                        {[1, 2, 3, 4].map((i) => (
                            <div key={i} className="skeleton-card">
                                <div className="skeleton-logo" />
                                <div className="skeleton-line skeleton-line-lg" />
                                <div className="skeleton-line" />
                                <div className="skeleton-line skeleton-line-sm" />
                            </div>
                        ))}
                    </div>

                    {showLoadingProgress && (
                        <div className="football-progress">
                            <div className="football-progress-track">
                                <div className="football-progress-bar" />
                            </div>
                            <span className="football-progress-text">
                                Still working on live data...
                            </span>
                        </div>
                    )}
                </div>
            </div>
        );
    }
    if (error) {
        return (
            <div style={{ textAlign: 'center', padding: '20px' }}>
                <p style={{ color: 'red', fontSize: '18px', fontWeight: 'bold' }}>Error: {error}</p>
                <p style={{ color: '#666', marginTop: '10px' }}>Check the browser console (F12) for more details.</p>
                <button 
                    onClick={() => window.location.reload()} 
                    style={{ marginTop: '15px', padding: '10px 20px', cursor: 'pointer' }}
                >
                    Retry
                </button>
            </div>
        );
    }

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
                    {loadingPlayers ? (
                        <div className="teams-grid">
                            {[1, 2, 3].map((i) => (
                                <div key={i} className="skeleton-card">
                                    <div className="skeleton-logo" />
                                    <div className="skeleton-line skeleton-line-lg" />
                                    <div className="skeleton-line" />
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="teams-grid">
                            {filteredPlayers.length > 0 ? (
                                filteredPlayers.map((player) => (
                                    <PlayerCard key={player.id} player={player} />
                                ))
                            ) : (
                                <p>No players found.</p>
                            )}
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
                            <div className="home-strip">
                                <div className="home-strip-main">
                                    <span className="home-strip-title">
                                        Premier League
                                    </span>
                                    <span className="home-strip-meta">
                                        Season 2025–26 · {teams.length} teams
                                    </span>
                                </div>
                                <span className="home-strip-badge">PL</span>
                            </div>

                            <h2 className="teams-section">
                                Premier League Teams ({filteredTeams.length})
                            </h2>
                            <div className="search-container">
                                <input
                                    type="text"
                                    placeholder="Search for a team..."
                                    className="search-input"
                                    value={teamSearchTerm}
                                    onChange={(e) =>
                                        setTeamSearchTerm(e.target.value)
                                    }
                                />
                            </div>
                            <div className="filter-chips">
                                <button
                                    type="button"
                                    className={
                                        teamFilter === 'all'
                                            ? 'filter-chip active'
                                            : 'filter-chip'
                                    }
                                    onClick={() => setTeamFilter('all')}
                                >
                                    All teams
                                </button>
                                <button
                                    type="button"
                                    className={
                                        teamFilter === 'big6'
                                            ? 'filter-chip active'
                                            : 'filter-chip'
                                    }
                                    onClick={() => setTeamFilter('big6')}
                                >
                                    Big 6
                                </button>
                                <button
                                    type="button"
                                    className={
                                        teamFilter === 'london'
                                            ? 'filter-chip active'
                                            : 'filter-chip'
                                    }
                                    onClick={() => setTeamFilter('london')}
                                >
                                    London clubs
                                </button>
                                <button
                                    type="button"
                                    className={
                                        teamFilter === 'top4'
                                            ? 'filter-chip active'
                                            : 'filter-chip'
                                    }
                                    onClick={() => setTeamFilter('top4')}
                                >
                                    Top 4
                                </button>
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