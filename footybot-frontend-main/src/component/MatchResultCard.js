import React from 'react';
import './MatchResultCard.css';

const MatchResultCard = ({ match, teams }) => {
    // Handle both Match objects (from H2H) and MatchWithH2HDTO objects (from other endpoints)
    const matchData = match?.match || match;

    // Safety check - if no match data, return null
    if (!matchData || !matchData.homeTeam || !matchData.awayTeam) {
        return null;
    }

    const homeCrest = teams?.find(t => t.name === matchData.homeTeam)?.crest || '';
    const awayCrest = teams?.find(t => t.name === matchData.awayTeam)?.crest || '';

    // Determine match outcome for dynamic styling
    const getMatchOutcome = () => {
        if (matchData.homeGoals == null || matchData.awayGoals == null) return 'draw';
        if (matchData.homeGoals > matchData.awayGoals) return 'home-win';
        if (matchData.awayGoals > matchData.homeGoals) return 'away-win';
        return 'draw';
    };

    // Format team names to be more readable
    const formatTeamName = (teamName) => {
        return teamName
            .replace(/ FC$/, '')
            .replace(/ AFC$/, '')
            .replace(/ United$/, ' Utd')
            .replace(/ City$/, '')
            .replace(/ & /g, ' & ');
    };

    // Get match date for display
    const formatMatchDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', { 
            month: 'short', 
            day: 'numeric',
            year: 'numeric'
        });
    };

    return (
        <div className={`match-card ${getMatchOutcome()}`}>
            <div className="match-header">
                <span>Matchday {matchData.matchday}</span>
                {matchData.matchDate && (
                    <span className="match-date">{formatMatchDate(matchData.matchDate)}</span>
                )}
            </div>
            <div className="match-content">
                <div className="team home-team">
                    <div className="team-logo-container">
                        <img 
                            src={homeCrest} 
                            alt={matchData.homeTeam}
                            onError={(e) => {
                                e.target.style.display = 'none';
                                e.target.nextSibling.style.display = 'block';
                            }}
                        />
                        <div className="team-logo-fallback" style={{display: 'none'}}>
                            {matchData.homeTeam.charAt(0)}
                        </div>
                    </div>
                    <span className="team-name">{formatTeamName(matchData.homeTeam)}</span>
                </div>
                
                <div className="score-box">
                    <span className="score">
                        {matchData.homeGoals ?? '?'} - {matchData.awayGoals ?? '?'}
                    </span>
                    <div className="status">FT</div>
                    {matchData.homePossession && matchData.awayPossession && (
                        <div className="possession">
                            <span className="possession-home">{matchData.homePossession}%</span>
                            <span className="possession-separator">-</span>
                            <span className="possession-away">{matchData.awayPossession}%</span>
                        </div>
                    )}
                </div>
                
                <div className="team away-team">
                    <div className="team-logo-container">
                        <img 
                            src={awayCrest} 
                            alt={matchData.awayTeam}
                            onError={(e) => {
                                e.target.style.display = 'none';
                                e.target.nextSibling.style.display = 'block';
                            }}
                        />
                        <div className="team-logo-fallback" style={{display: 'none'}}>
                            {matchData.awayTeam.charAt(0)}
                        </div>
                    </div>
                    <span className="team-name">{formatTeamName(matchData.awayTeam)}</span>
                </div>
            </div>
        </div>
    );
};

export default MatchResultCard;