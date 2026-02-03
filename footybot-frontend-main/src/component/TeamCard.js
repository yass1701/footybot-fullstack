import React from 'react';
import './TeamCard.css';

// Clean, compact team card for quick scanning
const TeamCard = ({ team, onClick }) => {
  const coachName = team?.coach?.name || '-';

  return (
    <div className="team-card" onClick={onClick}>
      <img
        className="team-logo"
        src={team?.crest || ''}
        alt={team?.name || 'team'}
      />

      <h3 className="team-name">
        {team?.name || '-'}
        <span className="league-badge">Premier League</span>
      </h3>

      <div className="team-details">
        <p>🏟 {team?.venue ?? '-'}</p>
        <p>👤 {coachName}</p>
        <p>📅 Est. {team?.founded ?? '-'}</p>
        <p>🎨 Colors: {team?.clubColors ?? '-'}</p>
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