import React from 'react';
import './PlayerCard.css';

// Map backend team names to the folder names you have in public/images
const TEAM_FOLDER_MAP = {
    'arsenal fc': 'arsenal',
    'aston villa fc': 'aston-villa',
    'afc bournemouth': 'bournemouth',
    'brentford fc': 'brentford',
    'brighton & hove albion fc': 'brighton',
    'chelsea fc': 'chelsea',
    'crystal palace fc': 'crystal-palace',
    'everton fc': 'everton',
    'fulham fc': 'fulham',
    'ipswich town fc': 'ipswich-town',
    'leicester city fc': 'leicester',
    'liverpool fc': 'liverpool',
    'manchester city fc': 'man-city',
    'manchester united fc': 'manchester-united',
    'newcastle united fc': 'newcastle',
    'nottingham forest fc': 'nottingham-forest',
    'southampton fc': 'southampton',
    'tottenham hotspur fc': 'tottenham',
    'west ham united fc': 'west-ham',
    'wolverhampton wanderers fc': 'wolves',
};

// Best‑effort slug for player image file names, e.g. "Bukayo Saka" -> "bukayo-saka"
const slugifyPlayerName = (name = '') => {
    return name
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '') // strip accents
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, '-') // non letters/digits -> hyphen
        .replace(/^-+|-+$/g, '') // trim hyphens
        .replace(/--+/g, '-'); // collapse multiple hyphens
};

const normalizeTeamKey = (teamName = '') =>
    teamName.trim().toLowerCase();

const PlayerCard = ({ player }) => {
    const makeAvatarUrl = (name) => {
        const safeName = (name || '').trim() || 'Player';
        return `https://ui-avatars.com/api/?name=${encodeURIComponent(
            safeName,
        )}&background=6c5ce7&color=ffffff&size=256`;
    };

    // Try to build a local image path from /public/images/<team>/<player>.jpg
    const buildLocalImagePath = () => {
        if (!player?.name || !player?.team) return null;

        const teamKey = normalizeTeamKey(player.team);
        const folder = TEAM_FOLDER_MAP[teamKey];
        if (!folder) return null;

        const fileName = `${slugifyPlayerName(player.name)}.jpg`;
        return `/images/${folder}/${fileName}`;
    };

    const avatarUrl = makeAvatarUrl(player?.name);

    // If backend sends crest as photoUrl, show a player-style avatar instead.
    const rawPhotoUrl = player?.photoUrl || '';
    const looksLikeCrest = rawPhotoUrl.includes('crests.football-data.org');

    const localImage = buildLocalImagePath();

    // Priority: local image from /images -> backend photoUrl (if not crest) -> generated avatar
    const initialPlayerImage =
        localImage || (rawPhotoUrl && !looksLikeCrest ? rawPhotoUrl : avatarUrl);
    const jerseyNumber =
        player.number && player.number > 0 ? player.number : '—';

    return (
        <div className="player-card">
            <img
                src={initialPlayerImage}
                alt={player.name}
                className="player-photo"
                onError={(e) => {
                    // If local image or backend URL fails, fall back to avatar
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