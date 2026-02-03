import React from 'react';
import './PlayerCard.css'; // 1. Import the new CSS file

const PlayerCard = ({ player }) => {
    const makeAvatarUrl = (name) => {
        const safeName = (name || '').trim() || 'Player';
        return `https://ui-avatars.com/api/?name=${encodeURIComponent(safeName)}&background=6c5ce7&color=ffffff&size=256`;
    };

    // If backend sends crest as photoUrl, show a player-style avatar instead.
    const rawPhotoUrl = player?.photoUrl || '';
    const looksLikeCrest = rawPhotoUrl.includes('crests.football-data.org');
    const playerImage = rawPhotoUrl && !looksLikeCrest ? rawPhotoUrl : makeAvatarUrl(player?.name);
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