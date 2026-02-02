import React, { useState, useEffect } from 'react';
import './QuizPage.css';

const QuizPage = () => {
    const [quizzes, setQuizzes] = useState([]);
    const [selectedQuiz, setSelectedQuiz] = useState(null);
    const [currentQuestion, setCurrentQuestion] = useState(0);
    const [questions, setQuestions] = useState([]);
    const [selectedAnswer, setSelectedAnswer] = useState('');
    const [quizAttempt, setQuizAttempt] = useState(null);
    const [timeLeft, setTimeLeft] = useState(0);
    const [questionStartTime, setQuestionStartTime] = useState(Date.now());
    const [quizCompleted, setQuizCompleted] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchQuizzes();
    }, []);

    useEffect(() => {
        if (quizAttempt && timeLeft > 0) {
            const timer = setInterval(() => {
                setTimeLeft(prev => {
                    if (prev <= 1) {
                        handleTimeUp();
                        return 0;
                    }
                    return prev - 1;
                });
            }, 1000);
            return () => clearInterval(timer);
        }
    // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [quizAttempt, timeLeft]);

    const fetchQuizzes = async () => {
        try {
            console.log('Fetching quizzes from API...');
            const response = await fetch('http://localhost:8080/api/quiz/quizzes');
            if (response.ok) {
                const data = await response.json();
                console.log('Quiz data received:', data);
                setQuizzes(data);
            } else {
                setError('Failed to load quizzes');
            }
        } catch (error) {
            console.error('Error fetching quizzes:', error);
            setError('Failed to load quizzes');
        }
    };

    const startQuiz = async (quiz) => {
        setLoading(true);
        setError('');
        try {
            // Get username from localStorage or use a default
            const username = localStorage.getItem('username') || 'Guest User';
            
        const response = await fetch(`http://localhost:8080/api/quiz/start/${quiz.id}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username: username })
        });

            if (response.ok) {
                const attempt = await response.json();
                setQuizAttempt(attempt);
                setSelectedQuiz(quiz);
                setTimeLeft(quiz.timeLimit * 60); // Convert minutes to seconds
                await fetchQuizQuestions(quiz.id);
            } else {
                const errorData = await response.json();
                setError(errorData.error || 'Failed to start quiz');
            }
        } catch (error) {
            console.error('Error starting quiz:', error);
            setError('Failed to start quiz');
        } finally {
            setLoading(false);
        }
    };

    const fetchQuizQuestions = async (quizId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/quiz/quizzes/${quizId}/questions`);
            if (response.ok) {
                const data = await response.json();
                setQuestions(data);
                setCurrentQuestion(0);
                setQuestionStartTime(Date.now());
            } else {
                setError('Failed to load questions');
            }
        } catch (error) {
            console.error('Error fetching questions:', error);
            setError('Failed to load questions');
        }
    };

    const submitAnswer = async () => {
        if (!selectedAnswer) {
            setError('Please select an answer');
            return;
        }

        const timeSpent = Math.floor((Date.now() - questionStartTime) / 1000);
        
        try {
            const response = await fetch('http://localhost:8080/api/quiz/submit-answer', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                },
                body: JSON.stringify({
                    attemptId: quizAttempt.id,
                    questionId: questions[currentQuestion].id,
                    selectedAnswer: selectedAnswer,
                    timeSpent: timeSpent
                })
            });

            if (response.ok) {
                const updatedAttempt = await response.json();
                setQuizAttempt(updatedAttempt);
                
                if (currentQuestion < questions.length - 1) {
                    setCurrentQuestion(prev => prev + 1);
                    setSelectedAnswer('');
                    setQuestionStartTime(Date.now());
                } else {
                    await completeQuiz();
                }
            } else {
                const errorData = await response.json();
                setError(errorData.error || 'Failed to submit answer');
            }
        } catch (error) {
            console.error('Error submitting answer:', error);
            setError('Failed to submit answer');
        }
    };

    const completeQuiz = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/quiz/complete/${quizAttempt.id}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                }
            });

            if (response.ok) {
                const completedAttempt = await response.json();
                setQuizAttempt(completedAttempt);
                setQuizCompleted(true);
            }
        } catch (error) {
            console.error('Error completing quiz:', error);
            setError('Failed to complete quiz');
        }
    };

    const handleTimeUp = () => {
        if (currentQuestion < questions.length - 1) {
            // Auto-submit current answer and move to next question
            submitAnswer();
        } else {
            // Complete the quiz
            completeQuiz();
        }
    };

    const resetQuiz = () => {
        setSelectedQuiz(null);
        setCurrentQuestion(0);
        setQuestions([]);
        setSelectedAnswer('');
        setQuizAttempt(null);
        setTimeLeft(0);
        setQuizCompleted(false);
        setError('');
    };

    const formatTime = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    if (quizCompleted && quizAttempt) {
        return (
            <div className="quiz-completed">
                <h2>Quiz Completed!</h2>
                <div className="quiz-results">
                    <div className="result-item">
                        <span className="result-label">Score:</span>
                        <span className="result-value">{quizAttempt.score} points</span>
                    </div>
                    <div className="result-item">
                        <span className="result-label">Correct Answers:</span>
                        <span className="result-value">{quizAttempt.correctAnswers}/{quizAttempt.totalQuestions}</span>
                    </div>
                    <div className="result-item">
                        <span className="result-label">Percentage:</span>
                        <span className="result-value">{Math.round((quizAttempt.correctAnswers / quizAttempt.totalQuestions) * 100)}%</span>
                    </div>
                    <div className="result-item">
                        <span className="result-label">Time Spent:</span>
                        <span className="result-value">{formatTime(quizAttempt.timeSpent)}</span>
                    </div>
                </div>
                <button onClick={resetQuiz} className="btn-primary">Take Another Quiz</button>
            </div>
        );
    }

    if (quizAttempt && questions.length > 0) {
        const question = questions[currentQuestion];
        return (
            <div className="quiz-container">
                <div className="quiz-header">
                    <h2>{selectedQuiz.title}</h2>
                    <div className="quiz-info">
                        <span>Question {currentQuestion + 1} of {questions.length}</span>
                        <span className="time-left">Time: {formatTime(timeLeft)}</span>
                    </div>
                </div>

                <div className="question-container">
                    <h3 className="question-text">{question.question}</h3>
                    <div className="options">
                        {['A', 'B', 'C', 'D'].map(option => (
                            <label key={option} className={`option ${selectedAnswer === option ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    name="answer"
                                    value={option}
                                    checked={selectedAnswer === option}
                                    onChange={(e) => setSelectedAnswer(e.target.value)}
                                />
                                <span className="option-letter">{option}.</span>
                                <span className="option-text">{question[`option${option}`]}</span>
                            </label>
                        ))}
                    </div>
                </div>

                <div className="quiz-actions">
                    <button 
                        onClick={submitAnswer} 
                        className="btn-primary"
                        disabled={!selectedAnswer}
                    >
                        {currentQuestion < questions.length - 1 ? 'Next Question' : 'Complete Quiz'}
                    </button>
                </div>

                {error && <div className="error-message">{error}</div>}
            </div>
        );
    }

    return (
        <div className="quiz-page">
            <div className="quiz-header-section">
                <div>
                    <h1>Football Quiz</h1>
                    <p>Test your football knowledge with our interactive quizzes!</p>
                </div>
                <button 
                    onClick={fetchQuizzes} 
                    className="btn-secondary refresh-btn"
                    disabled={loading}
                >
                    {loading ? '⏳ Loading...' : '🔄 Refresh Quizzes'}
                </button>
            </div>
            
            {error && <div className="error-message">{error}</div>}
            
            <div className="quizzes-grid">
                {quizzes.map(quiz => (
                    <div key={quiz.id} className="quiz-card">
                        <h3>{quiz.title}</h3>
                        <p className="quiz-description">{quiz.description}</p>
                        <div className="quiz-details">
                            <span className="quiz-category">{quiz.category}</span>
                            <span className="quiz-questions">{quiz.totalQuestions} questions</span>
                            <span className="quiz-time">{quiz.timeLimit} minutes</span>
                            <span className="quiz-points">{quiz.pointsPerQuestion} pts per question</span>
                        </div>
                        <div className="quiz-debug" style={{fontSize: '12px', color: '#666', marginTop: '5px'}}>
                            Quiz ID: {quiz.id} | Total Questions: {quiz.totalQuestions}
                        </div>
                        <button 
                            onClick={() => startQuiz(quiz)} 
                            className="btn-primary"
                            disabled={loading}
                        >
                            {loading ? 'Starting...' : 'Start Quiz'}
                        </button>
                    </div>
                ))}
            </div>

            {quizzes.length === 0 && !loading && (
                <div className="no-quizzes">
                    <p>No quizzes available at the moment.</p>
                    <button 
                        onClick={async () => {
                            try {
                                const response = await fetch('http://localhost:8080/api/quiz/initialize', { 
                                    method: 'POST'
                                });
                                if (response.ok) {
                                    // Refresh the quizzes list after initialization
                                    fetchQuizzes();
                                } else {
                                    setError('Failed to initialize sample quizzes');
                                }
                            } catch (error) {
                                console.error('Error initializing quizzes:', error);
                                setError('Failed to initialize sample quizzes');
                            }
                        }}
                        className="btn-secondary"
                    >
                        Initialize Sample Quizzes
                    </button>
                </div>
            )}
        </div>
    );
};

export default QuizPage;
