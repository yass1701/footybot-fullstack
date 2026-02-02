import React from 'react';
import './StandingsTable.css';

const StandingsTable = ({ standings }) => {
    if (!standings || standings.length === 0) {
        return <p>Loading standings...</p>;
    }

    return (
        <div className="standings-container">
            <div className="standings-header">
                <h3>Season 2025-26</h3>
            </div>
            <table className="standings-table">
                <thead>
                    <tr>
                        <th colSpan="2">Club</th>
                        <th>MP</th>
                        <th>W</th>
                        <th>D</th>
                        <th>L</th>
                        <th>GF</th>
                        <th>GA</th>
                        <th>GD</th>
                        <th>Pts</th>
                    </tr>
                </thead>
                <tbody>
                    {standings.map(team => (
                        <tr key={team.rank}>
                            <td>{team.rank}</td>
                            <td className="team-name">
                                <img src={team.crestUrl} alt={team.teamName} className="team-crest" />
                                {team.teamName}
                            </td>
                            <td>{team.played}</td>
                            <td>{team.win}</td>
                            <td>{team.draw}</td>
                            <td>{team.loss}</td>
                            <td>{team.goalsFor}</td>
                            <td>{team.goalsAgainst}</td>
                            <td>{team.goalDifference}</td>
                            <td className="points">{team.points}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default StandingsTable;