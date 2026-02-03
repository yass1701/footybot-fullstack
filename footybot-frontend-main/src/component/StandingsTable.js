import React from 'react';
import './StandingsTable.css';

const StandingsTable = ({ standings }) => {
    if (!standings) {
        return <p>Loading standings...</p>;
    }
    
    if (standings.length === 0) {
        return (
            <div style={{ textAlign: 'center', padding: '20px' }}>
                <p style={{ color: '#666' }}>No standings data available.</p>
                <p style={{ color: '#999', fontSize: '14px' }}>
                    The standings table is empty. This might be because no matches have been added yet.
                </p>
            </div>
        );
    }

    return (
        <div className="standings-container">
            <div className="standings-header">
                <div>
                    <h3>Premier League Table</h3>
                    <span className="standings-season">Season 2025–26</span>
                </div>
                <span className="standings-badge">PL</span>
            </div>

            <div className="standings-table-wrapper">
                <table className="standings-table">
                    <thead>
                        <tr>
                            <th colSpan="2">Club</th>
                            <th>MP</th>
                            <th>W</th>
                            <th>D</th>
                            <th>L</th>
                            <th className="hide-sm">GF</th>
                            <th className="hide-sm">GA</th>
                            <th>GD</th>
                            <th>Pts</th>
                        </tr>
                    </thead>
                    <tbody>
                        {standings.map((team, index) => {
                            const isTopFour = index < 4;
                            const isRelegation = index >= standings.length - 3;
                            const rowClass = [
                                'standings-row',
                                isTopFour ? 'top-four' : '',
                                isRelegation ? 'relegation' : '',
                            ]
                                .filter(Boolean)
                                .join(' ');

                            return (
                                <tr key={team.rank} className={rowClass}>
                                    <td className="rank-cell">{team.rank}</td>
                                    <td className="team-name">
                                        <img
                                            src={team.crestUrl}
                                            alt={team.teamName}
                                            className="team-crest"
                                        />
                                        <span className="team-text">{team.teamName}</span>
                                    </td>
                                    <td>{team.played}</td>
                                    <td>{team.win}</td>
                                    <td>{team.draw}</td>
                                    <td>{team.loss}</td>
                                    <td className="hide-sm">{team.goalsFor}</td>
                                    <td className="hide-sm">{team.goalsAgainst}</td>
                                    <td>{team.goalDifference}</td>
                                    <td className="points">{team.points}</td>
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default StandingsTable;