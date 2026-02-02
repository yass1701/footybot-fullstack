// src/component/LastMatchComparison.js
import React from 'react';
import './LastMatchComparison.css';

const StatBar = ({ label, value1, value2 }) => {
    const total = value1 + value2;
    const percentage1 = total > 0 ? (value1 / total) * 100 : 50;
    
    return (
        <div className="stat-comparison">
            <div className="stat-info">
                <span>{value1}</span>
                <span className="label">{label}</span>
                <span>{value2}</span>
            </div>
            <div className="stat-bar-container">
                <div className="stat-bar team1" style={{ width: `${percentage1}%` }}></div>
            </div>
        </div>
    );
};

const LastMatchComparison = ({ match }) => {
    return (
        <div className="last-match-container">
            <h4>Last Match</h4>
            <div className="comparison-grid">
                <StatBar label="Possession %" value1={match.homePossession} value2={match.awayPossession} />
                <StatBar label="Total Shots" value1={match.homeShots} value2={match.awayShots} />
                <StatBar label="Fouls" value1={match.homeFouls} value2={match.awayFouls} />
            </div>
        </div>
    );
};

export default LastMatchComparison;