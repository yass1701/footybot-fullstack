package com.footybot.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.footybot.model.Quiz;
import com.footybot.model.QuizAnswer;
import com.footybot.model.QuizAttempt;
import com.footybot.model.QuizQuestion;
import com.footybot.model.User;
import com.footybot.repository.QuizAnswerRepository;
import com.footybot.repository.QuizAttemptRepository;
import com.footybot.repository.QuizQuestionRepository;
import com.footybot.repository.QuizRepository;
import com.footybot.repository.UserRepository;

@Service
@Transactional
public class QuizService {
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    @Autowired
    private QuizAnswerRepository quizAnswerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Quiz Management
    public List<Quiz> getAllActiveQuizzes() {
        return quizRepository.findByIsActiveTrue();
    }
    
    public List<Quiz> getQuizzesByCategory(String category) {
        return quizRepository.findByCategoryAndIsActiveTrue(category);
    }
    
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }
    
    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }
    
    public Quiz updateQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }
    
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }
    
    // Question Management
    public List<QuizQuestion> getQuestionsByQuizId(Long quizId) {
        return quizQuestionRepository.findByQuizId(quizId);
    }
    
    public QuizQuestion createQuestion(QuizQuestion question) {
        return quizQuestionRepository.save(question);
    }
    
    public QuizQuestion updateQuestion(QuizQuestion question) {
        return quizQuestionRepository.save(question);
    }
    
    public void deleteQuestion(Long id) {
        quizQuestionRepository.deleteById(id);
    }
    
    // Quiz Attempt Management
    public QuizAttempt startQuiz(Long userId, Long quizId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));
        
        if (!quiz.isActive()) {
            throw new RuntimeException("Quiz is not active");
        }
        
        // For demo purposes, we'll allow multiple quiz attempts
        // In a real application, you might want to limit this or handle it differently
        
        QuizAttempt attempt = new QuizAttempt(user, quiz);
        return quizAttemptRepository.save(attempt);
    }
    
    public QuizAttempt submitAnswer(Long attemptId, Long questionId, String selectedAnswer, int timeSpent) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
            .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));
        
        QuizQuestion question = quizQuestionRepository.findById(questionId)
            .orElseThrow(() -> new RuntimeException("Question not found"));
        
        if (attempt.isCompleted()) {
            throw new RuntimeException("Quiz attempt is already completed");
        }
        
        // Check if answer already exists
        QuizAnswer existingAnswer = quizAnswerRepository.findByQuizAttemptAndQuestion(attempt, question);
        if (existingAnswer != null) {
            existingAnswer.setSelectedAnswer(selectedAnswer);
            existingAnswer.setTimeSpent(timeSpent);
            quizAnswerRepository.save(existingAnswer);
            return attempt;
        }
        
        QuizAnswer answer = new QuizAnswer(attempt, question, selectedAnswer, timeSpent);
        quizAnswerRepository.save(answer);
        
        return attempt;
    }
    
    public QuizAttempt completeQuiz(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
            .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));
        
        if (attempt.isCompleted()) {
            throw new RuntimeException("Quiz attempt is already completed");
        }
        
        // Calculate final score
        List<QuizAnswer> answers = quizAnswerRepository.findByQuizAttempt(attempt);
        int correctAnswers = 0;
        int totalTimeSpent = 0;
        
        for (QuizAnswer answer : answers) {
            if (answer.isCorrect()) {
                correctAnswers++;
            }
            totalTimeSpent += answer.getTimeSpent();
        }
        
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setWrongAnswers(attempt.getTotalQuestions() - correctAnswers);
        attempt.setTimeSpent(totalTimeSpent);
        attempt.calculateScore();
        attempt.setCompleted(true);
        
        return quizAttemptRepository.save(attempt);
    }
    
    public List<QuizAttempt> getUserAttempts(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return quizAttemptRepository.findByUser(user);
    }
    
    public List<QuizAttempt> getTopScoresForQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new RuntimeException("Quiz not found"));
        return quizAttemptRepository.findTopScoresByQuiz(quiz);
    }
    
    public List<QuizAttempt> getLeaderboard() {
        // Get all completed attempts
        List<QuizAttempt> allAttempts = quizAttemptRepository.findAll().stream()
            .filter(QuizAttempt::isCompleted)
            .toList();
        
        // Group by user and quiz, keep only the best score for each combination
        Map<String, QuizAttempt> bestAttempts = new HashMap<>();
        
        for (QuizAttempt attempt : allAttempts) {
            String key = attempt.getUser().getUsername() + "_" + attempt.getQuiz().getId();
            
            if (!bestAttempts.containsKey(key) || 
                attempt.getScore() > bestAttempts.get(key).getScore() ||
                (attempt.getScore() == bestAttempts.get(key).getScore() && 
                 attempt.getTimeSpent() < bestAttempts.get(key).getTimeSpent())) {
                bestAttempts.put(key, attempt);
            }
        }
        
        // Sort by score (descending), then by time (ascending)
        return bestAttempts.values().stream()
            .sorted((a, b) -> {
                int scoreCompare = Integer.compare(b.getScore(), a.getScore());
                if (scoreCompare == 0) {
                    return Integer.compare(a.getTimeSpent(), b.getTimeSpent());
                }
                return scoreCompare;
            })
            .limit(50) // Top 50
            .toList();
    }
    
    // Initialize sample quiz data
    public void initializeSampleQuizzes() {
        // Clear existing quiz data to ensure fresh initialization
        quizRepository.deleteAll();
        quizQuestionRepository.deleteAll();
        quizAttemptRepository.deleteAll();
        quizAnswerRepository.deleteAll();
            // Create Premier League Quiz
            Quiz premierLeagueQuiz = new Quiz(
                "Premier League Knowledge",
                "Test your knowledge of the English Premier League",
                "Premier League",
                10, // 10 minutes
                15,  // 15 questions
                10  // 10 points per question
            );
            premierLeagueQuiz = quizRepository.save(premierLeagueQuiz);
            
            // Add questions
            createSampleQuestions(premierLeagueQuiz);
            
            // Create General Football Quiz
            Quiz generalQuiz = new Quiz(
                "General Football Trivia",
                "General knowledge about football around the world",
                "General",
                15, // 15 minutes
                8,  // 8 questions
                5   // 5 points per question
            );
            generalQuiz = quizRepository.save(generalQuiz);
            
            // Add questions
            createGeneralSampleQuestions(generalQuiz);
    }
    
    private void createSampleQuestions(Quiz quiz) {
        // Question 1
        QuizQuestion q1 = new QuizQuestion(
            quiz,
            "Which team has won the most Premier League titles?",
            "Manchester United",
            "Chelsea",
            "Arsenal",
            "Liverpool",
            "A",
            "Manchester United has won 13 Premier League titles since its inception in 1992.",
            10,
            1
        );
        quizQuestionRepository.save(q1);
        
        // Question 2
        QuizQuestion q2 = new QuizQuestion(
            quiz,
            "Who is the all-time top scorer in Premier League history?",
            "Wayne Rooney",
            "Alan Shearer",
            "Thierry Henry",
            "Sergio Aguero",
            "B",
            "Alan Shearer scored 260 goals in the Premier League.",
            10,
            2
        );
        quizQuestionRepository.save(q2);
        
        // Question 3
        QuizQuestion q3 = new QuizQuestion(
            quiz,
            "Which stadium is known as 'The Theatre of Dreams'?",
            "Anfield",
            "Old Trafford",
            "Stamford Bridge",
            "Emirates Stadium",
            "B",
            "Old Trafford, home of Manchester United, is known as 'The Theatre of Dreams'.",
            10,
            1
        );
        quizQuestionRepository.save(q3);
        
        // Question 4
        QuizQuestion q4 = new QuizQuestion(
            quiz,
            "In which year was the Premier League founded?",
            "1990",
            "1991",
            "1992",
            "1993",
            "C",
            "The Premier League was founded in 1992, breaking away from the Football League.",
            10,
            1
        );
        quizQuestionRepository.save(q4);
        
        // Question 5
        QuizQuestion q5 = new QuizQuestion(
            quiz,
            "Which player has made the most Premier League appearances?",
            "Ryan Giggs",
            "Gareth Barry",
            "Frank Lampard",
            "Steven Gerrard",
            "B",
            "Gareth Barry holds the record with 653 Premier League appearances.",
            10,
            3
        );
        quizQuestionRepository.save(q5);
        
        // Question 6
        QuizQuestion q6 = new QuizQuestion(
            quiz,
            "Which team is known as 'The Gunners'?",
            "Chelsea",
            "Arsenal",
            "Tottenham",
            "West Ham",
            "B",
            "Arsenal is known as 'The Gunners' due to their historical connection to the Royal Arsenal.",
            10,
            1
        );
        quizQuestionRepository.save(q6);
        
        // Question 7
        QuizQuestion q7 = new QuizQuestion(
            quiz,
            "Who scored the fastest hat-trick in Premier League history?",
            "Sadio Mane",
            "Mohamed Salah",
            "Harry Kane",
            "Jamie Vardy",
            "A",
            "Sadio Mane scored a hat-trick in 2 minutes and 56 seconds against Aston Villa in 2015.",
            10,
            3
        );
        quizQuestionRepository.save(q7);
        
        // Question 8
        QuizQuestion q8 = new QuizQuestion(
            quiz,
            "Which manager has won the most Premier League titles?",
            "Arsene Wenger",
            "Jose Mourinho",
            "Sir Alex Ferguson",
            "Pep Guardiola",
            "C",
            "Sir Alex Ferguson won 13 Premier League titles with Manchester United.",
            10,
            2
        );
        quizQuestionRepository.save(q8);
        
        // Question 9
        QuizQuestion q9 = new QuizQuestion(
            quiz,
            "What is the maximum number of points a team can earn in a Premier League season?",
            "108",
            "114",
            "120",
            "126",
            "B",
            "A team can earn a maximum of 114 points (38 games × 3 points per win).",
            10,
            2
        );
        quizQuestionRepository.save(q9);
        
        // Question 10
        QuizQuestion q10 = new QuizQuestion(
            quiz,
            "Which player has the most Premier League assists?",
            "Ryan Giggs",
            "Kevin De Bruyne",
            "Cesc Fabregas",
            "Frank Lampard",
            "A",
            "Ryan Giggs holds the record with 162 Premier League assists.",
            10,
            3
        );
        quizQuestionRepository.save(q10);
        
        // Question 11
        QuizQuestion q11 = new QuizQuestion(
            quiz,
            "Which team has never been relegated from the Premier League?",
            "Arsenal",
            "Manchester United",
            "Liverpool",
            "Chelsea",
            "A",
            "Arsenal is one of the few teams that has never been relegated from the Premier League.",
            10,
            2
        );
        quizQuestionRepository.save(q11);
        
        // Question 12
        QuizQuestion q12 = new QuizQuestion(
            quiz,
            "What is the name of the Premier League trophy?",
            "Premier League Cup",
            "Premier League Shield",
            "Premier League Trophy",
            "Premier League Crown",
            "C",
            "The official name is the Premier League Trophy.",
            10,
            1
        );
        quizQuestionRepository.save(q12);
        
        // Question 13
        QuizQuestion q13 = new QuizQuestion(
            quiz,
            "Which player has scored the most goals in a single Premier League season?",
            "Mohamed Salah",
            "Alan Shearer",
            "Thierry Henry",
            "Cristiano Ronaldo",
            "A",
            "Mohamed Salah scored 32 goals in the 2017-18 season.",
            10,
            3
        );
        quizQuestionRepository.save(q13);
        
        // Question 14
        QuizQuestion q14 = new QuizQuestion(
            quiz,
            "Which stadium has the largest capacity in the Premier League?",
            "Old Trafford",
            "Emirates Stadium",
            "Tottenham Hotspur Stadium",
            "London Stadium",
            "A",
            "Old Trafford has a capacity of 74,310, making it the largest in the Premier League.",
            10,
            2
        );
        quizQuestionRepository.save(q14);
        
        // Question 15
        QuizQuestion q15 = new QuizQuestion(
            quiz,
            "Which team won the first ever Premier League title?",
            "Manchester United",
            "Arsenal",
            "Blackburn Rovers",
            "Chelsea",
            "A",
            "Manchester United won the inaugural Premier League title in 1992-93.",
            10,
            2
        );
        quizQuestionRepository.save(q15);
    }
    
    private void createGeneralSampleQuestions(Quiz quiz) {
        // Question 1
        QuizQuestion q1 = new QuizQuestion(
            quiz,
            "How many players are on a football team on the field at one time?",
            "10",
            "11",
            "12",
            "9",
            "B",
            "Each team has 11 players on the field at one time.",
            5,
            1
        );
        quizQuestionRepository.save(q1);
        
        // Question 2
        QuizQuestion q2 = new QuizQuestion(
            quiz,
            "What is the maximum number of substitutions allowed in a match?",
            "3",
            "5",
            "7",
            "Unlimited",
            "B",
            "Teams are allowed up to 5 substitutions in most competitions.",
            5,
            1
        );
        quizQuestionRepository.save(q2);
        
        // Question 3
        QuizQuestion q3 = new QuizQuestion(
            quiz,
            "Which country has won the most FIFA World Cups?",
            "Germany",
            "Brazil",
            "Argentina",
            "Italy",
            "B",
            "Brazil has won 5 FIFA World Cups (1958, 1962, 1970, 1994, 2002).",
            5,
            2
        );
        quizQuestionRepository.save(q3);
        
        // Question 4
        QuizQuestion q4 = new QuizQuestion(
            quiz,
            "What is the length of a standard football field?",
            "90-100 meters",
            "100-110 meters",
            "110-120 meters",
            "120-130 meters",
            "B",
            "A standard football field is 100-110 meters long.",
            5,
            2
        );
        quizQuestionRepository.save(q4);
        
        // Question 5
        QuizQuestion q5 = new QuizQuestion(
            quiz,
            "Which tournament is known as 'The Beautiful Game'?",
            "Champions League",
            "World Cup",
            "Euros",
            "Copa America",
            "B",
            "The FIFA World Cup is often referred to as 'The Beautiful Game'.",
            5,
            1
        );
        quizQuestionRepository.save(q5);
        
        // Question 6
        QuizQuestion q6 = new QuizQuestion(
            quiz,
            "What does VAR stand for?",
            "Video Assistant Referee",
            "Virtual Assistant Referee",
            "Video Analysis Review",
            "Virtual Analysis Review",
            "A",
            "VAR stands for Video Assistant Referee.",
            5,
            1
        );
        quizQuestionRepository.save(q6);
        
        // Question 7
        QuizQuestion q7 = new QuizQuestion(
            quiz,
            "Which player is known as 'The Special One'?",
            "Pep Guardiola",
            "Jose Mourinho",
            "Jurgen Klopp",
            "Carlo Ancelotti",
            "B",
            "Jose Mourinho is famously known as 'The Special One'.",
            5,
            2
        );
        quizQuestionRepository.save(q7);
        
        // Question 8
        QuizQuestion q8 = new QuizQuestion(
            quiz,
            "What is the minimum number of players needed to start a match?",
            "7",
            "8",
            "9",
            "10",
            "A",
            "A team needs at least 7 players to start a match.",
            5,
            3
        );
        quizQuestionRepository.save(q8);
    }
}
