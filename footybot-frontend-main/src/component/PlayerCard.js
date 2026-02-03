import React from 'react';
import './PlayerCard.css';

/**
 * This version does NOT require any local images.
 * Every player gets a generated avatar image based on their name,
 * so it works for all teams on Render/Vercel without extra assets.
 */
const PlayerCard = ({ player }) => {
    const makeAvatarUrl = (name) => {
        const safeName = (name || '').trim() || 'Player';
        return `https://ui-avatars.com/api/?name=${encodeURIComponent(
            safeName,
        )}&background=6c5ce7&color=ffffff&size=256`;
    };

    const avatarUrl = makeAvatarUrl(player?.name);

    // Backend already sets photoUrl to a ui-avatars URL, but keep a safe fallback.
    const rawPhotoUrl = player?.photoUrl || '';
    const playerImage = rawPhotoUrl || avatarUrl;

    const jerseyNumber =
        player.number && player.number > 0 ? player.number : '—';

    return (
        <div className="player-card">
            <img
                src={playerImage}
                alt={player.name}
                className="player-photo"
                onError={(e) => {
                    // If anything fails, always fall back to the generated avatar
                    if (e.target.src !== avatarUrl) {
                        e.target.src = avatarUrl;
                    }
                }}
            />
            <h3 className="player-name">{player.name}</h3>
            <div className="player-details">
                <p>
                    <strong>Position:</strong>{' '}
                    {player.position || 'N/A'}
                </p>
                <p>
                    <strong>Nationality:</strong>{' '}
                    {player.nationality || 'N/A'}
                </p>
                <p>
                    <strong>Jersey:</strong> {jerseyNumber}
                </p>
            </div>
        </div>
    );
};

export default PlayerCard;