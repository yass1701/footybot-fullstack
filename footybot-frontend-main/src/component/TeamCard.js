import React from 'react';
import './TeamCard.css';

// Simple map to give each team a subtle accent color on the card
const TEAM_PRIMARY_COLORS = {
  'Arsenal FC': '#e11d48',
  'Aston Villa FC': '#7c2d12',
  'Chelsea FC': '#2563eb',
  'Everton FC': '#1d4ed8',
  'Liverpool FC': '#b91c1c',
  'Manchester City FC': '#0ea5e9',
  'Manchester United FC': '#dc2626',
  'Newcastle United FC': '#111827',
  'Tottenham Hotspur FC': '#4b5563',
  'West Ham United FC': '#7c2d12',
};

// Clean, compact team card for quick scanning
const TeamCard = ({ team, onClick }) => {
  const coachName = team?.coach?.name || '-';
  const primaryColor =
    (team && TEAM_PRIMARY_COLORS[team.name]) || '#2563eb';

  return (
    <div
      className="team-card"
      onClick={onClick}
      style={{ borderBottom: `3px solid ${primaryColor}` }}
    >
      <div className="team-logo-wrapper">
        <img
          className="team-logo"
          src={team?.crest || ''}
          alt={team?.name || 'team'}
        />
      </div>

      <h3 className="team-name">
        {team?.name || '-'}
        <span className="league-badge">Premier League</span>
      </h3>

      <div className="team-details">
        <p>🏟 {team?.venue ?? '-'}</p>
        <p>👤 {coachName}</p>
        <div className="team-meta-badges">
          <span className="meta-badge">
            📅 Est. {team?.founded ?? '-'}
          </span>
          <span className="meta-badge">
            🎨 {team?.clubColors ?? '-'}
          </span>
        </div>
      </div>

      <button
        type="button"
        className="view-team-btn"
        onClick={onClick}
      >
        View details
      </button>
    </div>
  );
};

export default TeamCard;