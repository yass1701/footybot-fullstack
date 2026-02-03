import React from 'react';
import './PlayerCard.css'; // 1. Import the new CSS file

const PlayerCard = ({ player }) => {
    // Fallback image if photoUrl is missing
    const playerImage = player.photoUrl || 'https://via.placeholder.com/100';
    const jerseyNumber = player.number && player.number > 0 ? player.number : '—';

    return (
        // 2. Add the main 'player-card' div
        <div className="player-card">
            <img src={playerImage} alt={player.name} className="player-photo" />
            <h3 className="player-name">{player.name}</h3>
            <div className="player-details">
                <p><strong>Position:</strong> {player.position || 'N/A'}</p>
                <p><strong>Nationality:</strong> {player.nationality || 'N/A'}</p>
                <p><strong>Jersey:</strong> {jerseyNumber}</p>
            </div>
        </div>
    );
};

export default PlayerCard;