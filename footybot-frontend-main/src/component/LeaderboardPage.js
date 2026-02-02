import React, { useState, useEffect } from 'react';
import './LeaderboardPage.css';

const LeaderboardPage = () => {
    const [leaderboard, setLeaderboard] = useState([]);
    const [userAttempts, setUserAttempts] = useState([]);
    const [quizzes, setQuizzes] = useState([]);
    const [selectedQuiz, setSelectedQuiz] = useState('all');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    useEffect(() => {
        if (selectedQuiz === 'all') {
            fetchGlobalLeaderboard();
        } else {
            fetchQuizLeaderboard(selectedQuiz);
        }
    }, [selectedQuiz]);

    const fetchData = async () => {
        setLoading(true);
        try {
            await Promise.all([
                fetchQuizzes(),
                fetchUserAttempts(),
                fetchGlobalLeaderboard()
            ]);
        } catch (error) {
            console.error('Error fetching data:', error);
            setError('Failed to load leaderboard data');
        } finally {
            setLoading(false);
        }
    };

    const fetchQuizzes = async () => {
        try { 
            const response = await fetch('http://localhost:8080/api/quiz/quizzes');
            if (response.ok) {
                const data = await response.json();
                setQuizzes(data);
            }
        } catch (error) {
            console.error('Error fetching quizzes:', error);
        }
    };

    const fetchGlobalLeaderboard = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/quiz/leaderboard');
            if (response.ok) {
                const data = await response.json();
                setLeaderboard(data);
            }
        } catch (error) {
            console.error('Error fetching global leaderboard:', error);
        }
    };

    const fetchQuizLeaderboard = async (quizId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/quiz/quizzes/${quizId}/leaderboard`);
            if (response.ok) {
                const data = await response.json();
                setLeaderboard(data);
            }
        } catch (error) {
            console.error('Error fetching quiz leaderboard:', error);
        }
    };

    const fetchUserAttempts = async () => {
        try {
            // Get username from localStorage or use default
            const username = localStorage.getItem('username') || 'Guest User';
            
        const response = await fetch(`http://localhost:8080/api/quiz/my-attempts?username=${encodeURIComponent(username)}`, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
            if (response.ok) {
                const data = await response.json();
                setUserAttempts(data);
            }
        } catch (error) {
            console.error('Error fetching user attempts:', error);
        }
    };

    const formatTime = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    const getRankIcon = (index) => {
        switch (index) {
            case 0:
                return '🥇';
            case 1:
                return '🥈';
            case 2:
                return '🥉';
            default:
                return `#${index + 1}`;
        }
    };

    const getScoreColor = (percentage) => {
        if (percentage >= 80) return '#27ae60'; // Green
        if (percentage >= 60) return '#f39c12'; // Orange
        return '#e74c3c'; // Red
    };

    if (loading) {
        return (
            <div className="leaderboard-page">
                <div className="loading">
                    <div className="spinner"></div>
                    <p>Loading leaderboard...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="leaderboard-page">
            <h1>Leaderboard</h1>
            <p>See how you rank against other football fans!</p>

            {error && <div className="error-message">{error}</div>}

            <div className="leaderboard-controls">
                <div className="quiz-selector">
                    <label htmlFor="quiz-select">Select Quiz:</label>
                    <select 
                        id="quiz-select"
                        value={selectedQuiz} 
                        onChange={(e) => setSelectedQuiz(e.target.value)}
                        className="quiz-select"
                    >
                        <option value="all">All Quizzes</option>
                        {quizzes.map(quiz => (
                            <option key={quiz.id} value={quiz.id}>
                                {quiz.title}
                            </option>
                        ))}
                    </select>
                </div>
            </div>

            <div className="leaderboard-content">
                <div className="leaderboard-main">
                    <h2>
                        {selectedQuiz === 'all' 
                            ? 'Global Leaderboard' 
                            : quizzes.find(q => q.id.toString() === selectedQuiz)?.title + ' Leaderboard'
                        }
                    </h2>
                    
                    {leaderboard.length === 0 ? (
                        <div className="no-data">
                            <p>No quiz attempts found for this selection.</p>
                        </div>
                    ) : (
                        <div className="leaderboard-table">
                            <div className="table-header">
                                <div className="rank-col">Rank</div>
                                <div className="player-col">Player</div>
                                <div className="quiz-col">Quiz</div>
                                <div className="score-col">Score</div>
                                <div className="percentage-col">Accuracy</div>
                                <div className="time-col">Time</div>
                            </div>
                            
                            {leaderboard.map((attempt, index) => {
                                const percentage = Math.round((attempt.correctAnswers / attempt.totalQuestions) * 100);
                                return (
                                    <div key={attempt.id} className="table-row">
                                        <div className="rank-col">
                                            <span className="rank-icon">{getRankIcon(index)}</span>
                                        </div>
                                        <div className="player-col">
                                            <span className="player-name">{attempt.username}</span>
                                        </div>
                                        <div className="quiz-col">
                                            <span className="quiz-name">{attempt.quizTitle}</span>
                                        </div>
                                        <div className="score-col">
                                            <span className="score">{attempt.score}</span>
                                        </div>
                                        <div className="percentage-col">
                                            <span 
                                                className="percentage"
                                                style={{ color: getScoreColor(percentage) }}
                                            >
                                                {percentage}%
                                            </span>
                                        </div>
                                        <div className="time-col">
                                            <span className="time">{formatTime(attempt.timeSpent)}</span>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>

                <div className="user-stats">
                    <h3>Your Performance</h3>
                    {userAttempts.length === 0 ? (
                        <div className="no-attempts">
                            <p>You haven't taken any quizzes yet.</p>
                            <p>Start taking quizzes to see your stats here!</p>
                        </div>
                    ) : (
                        <div className="user-stats-content">
                            <div className="stat-card">
                                <div className="stat-number">{userAttempts.length}</div>
                                <div className="stat-label">Quizzes Taken</div>
                            </div>
                            
                            <div className="stat-card">
                                <div className="stat-number">
                                    {Math.round(userAttempts.reduce((sum, attempt) => sum + (attempt.correctAnswers / attempt.totalQuestions * 100), 0) / userAttempts.length)}%
                                </div>
                                <div className="stat-label">Average Accuracy</div>
                            </div>
                            
                            <div className="stat-card">
                                <div className="stat-number">
                                    {Math.max(...userAttempts.map(attempt => attempt.score))}
                                </div>
                                <div className="stat-label">Best Score</div>
                            </div>
                            
                            <div className="recent-attempts">
                                <h4>Recent Attempts</h4>
                                {userAttempts.slice(0, 5).map(attempt => {
                                    const percentage = Math.round((attempt.correctAnswers / attempt.totalQuestions) * 100);
                                    return (
                                        <div key={attempt.id} className="attempt-item">
                                            <div className="attempt-quiz">{attempt.quizTitle}</div>
                                            <div className="attempt-details">
                                                <span className="attempt-score">{attempt.score} pts</span>
                                                <span 
                                                    className="attempt-percentage"
                                                    style={{ color: getScoreColor(percentage) }}
                                                >
                                                    {percentage}%
                                                </span>
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default LeaderboardPage;
