import React from 'react';
import './TeamCard.css';

// 1. Add "onClick" back to the props
const TeamCard = ({ team, onClick }) => {
  const coachName = team?.coach?.name || "-";

  return (
    // 2. Add the onClick handler to the main div
    <div className="team-card" onClick={onClick} style={{ cursor: 'pointer' }}>
      <img
        className="team-logo"
        src={team?.crest || ""}
        alt={team?.name || "team"}
      />
      
      <h3 className="team-name">{team?.name || "-"}</h3>
      
      <div className="team-details">
        <p><strong>Founded:</strong> {team?.founded ?? "-"}</p>
        <p><strong>Venue:</strong> {team?.venue ?? "-"}</p>
        <p><strong>Colors:</strong> {team?.clubColors ?? "-"}</p>
        <p><strong>Coach:</strong> {coachName}</p>
      </div>
    </div>
  );
};

export default TeamCard;