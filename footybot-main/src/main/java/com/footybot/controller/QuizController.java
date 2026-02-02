package com.footybot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.footybot.dto.QuizAttemptDTO;
import com.footybot.dto.QuizDTO;
import com.footybot.dto.QuizQuestionDTO;
import com.footybot.model.Quiz;
import com.footybot.model.QuizAttempt;
import com.footybot.model.QuizQuestion;
import com.footybot.model.User;
import com.footybot.repository.UserRepository;
import com.footybot.service.QuizService;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizController {
    
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private UserRepository userRepository;
    
    // Get all active quizzes
    @GetMapping("/quizzes")
    public ResponseEntity<List<QuizDTO>> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllActiveQuizzes();
        List<QuizDTO> quizDTOs = quizzes.stream()
            .map(this::convertToQuizDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(quizDTOs);
    }
    
    // Get quizzes by category
    @GetMapping("/quizzes/category/{category}")
    public ResponseEntity<List<Quiz>> getQuizzesByCategory(@PathVariable String category) {
        List<Quiz> quizzes = quizService.getQuizzesByCategory(category);
        return ResponseEntity.ok(quizzes);
    }
    
    // Get quiz by ID
    @GetMapping("/quizzes/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        Optional<Quiz> quiz = quizService.getQuizById(id);
        return quiz.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    // Get questions for a quiz
    @GetMapping("/quizzes/{id}/questions")
    public ResponseEntity<List<QuizQuestionDTO>> getQuizQuestions(@PathVariable Long id) {
        List<QuizQuestion> questions = quizService.getQuestionsByQuizId(id);
        List<QuizQuestionDTO> questionDTOs = questions.stream()
            .map(this::convertToQuizQuestionDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(questionDTOs);
    }
    
    // Start a quiz
    @PostMapping("/start/{quizId}")
    public ResponseEntity<?> startQuiz(@PathVariable Long quizId, @RequestBody(required = false) Map<String, String> requestBody) {
        try {
            User user = null;
            String username = "Guest User";
            
            // Try to get username from request body
            if (requestBody != null && requestBody.containsKey("username")) {
                username = requestBody.get("username");
            }
            
            // Try to find existing user or create new one
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                // Create new user for this username
                user = new User();
                user.setUsername(username);
                user.setPassword("$2a$10$dummy"); // Dummy password for quiz users
                user.setRole("ROLE_USER");
                user = userRepository.save(user);
            }
            
            QuizAttempt attempt = quizService.startQuiz(user.getId(), quizId);
            return ResponseEntity.ok(attempt);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Submit an answer
    @PostMapping("/submit-answer")
    public ResponseEntity<?> submitAnswer(@RequestBody Map<String, Object> request) {
        try {
            Long attemptId = Long.valueOf(request.get("attemptId").toString());
            Long questionId = Long.valueOf(request.get("questionId").toString());
            String selectedAnswer = request.get("selectedAnswer").toString();
            int timeSpent = Integer.parseInt(request.get("timeSpent").toString());
            
            QuizAttempt attempt = quizService.submitAnswer(attemptId, questionId, selectedAnswer, timeSpent);
            return ResponseEntity.ok(attempt);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Complete quiz
    @PostMapping("/complete/{attemptId}")
    public ResponseEntity<?> completeQuiz(@PathVariable Long attemptId) {
        try {
            QuizAttempt attempt = quizService.completeQuiz(attemptId);
            return ResponseEntity.ok(attempt);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get user's quiz attempts
    @GetMapping("/my-attempts")
    public ResponseEntity<List<QuizAttemptDTO>> getUserAttempts(@RequestParam(required = false) String username) {
        try {
            User user = null;
            
            // Use provided username or default
            String targetUsername = (username != null) ? username : "Guest User";
            
            // Try to find the user
            Optional<User> foundUser = userRepository.findByUsername(targetUsername);
            if (foundUser.isPresent()) {
                user = foundUser.get();
            }
            
            if (user == null) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            List<QuizAttempt> attempts = quizService.getUserAttempts(user.getId());
            List<QuizAttemptDTO> attemptDTOs = attempts.stream()
                .map(this::convertToQuizAttemptDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(attemptDTOs);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
    
    // Get leaderboard
    @GetMapping("/leaderboard")
    public ResponseEntity<List<QuizAttemptDTO>> getLeaderboard() {
        List<QuizAttempt> leaderboard = quizService.getLeaderboard();
        List<QuizAttemptDTO> leaderboardDTOs = leaderboard.stream()
            .map(this::convertToQuizAttemptDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(leaderboardDTOs);
    }
    
    // Get top scores for a specific quiz
    @GetMapping("/quizzes/{id}/leaderboard")
    public ResponseEntity<List<QuizAttemptDTO>> getQuizLeaderboard(@PathVariable Long id) {
        List<QuizAttempt> leaderboard = quizService.getTopScoresForQuiz(id);
        List<QuizAttemptDTO> leaderboardDTOs = leaderboard.stream()
            .map(this::convertToQuizAttemptDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(leaderboardDTOs);
    }
    
    // Initialize sample data (for development)
    @PostMapping("/initialize")
    public ResponseEntity<?> initializeSampleData() {
        try {
            quizService.initializeSampleQuizzes();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sample quiz data initialized successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Helper methods to convert entities to DTOs
    private QuizDTO convertToQuizDTO(Quiz quiz) {
        return new QuizDTO(
            quiz.getId(),
            quiz.getTitle(),
            quiz.getDescription(),
            quiz.getCategory(),
            quiz.getTimeLimit(),
            quiz.getTotalQuestions(),
            quiz.getPointsPerQuestion(),
            quiz.isActive(),
            quiz.getCreatedAt(),
            quiz.getUpdatedAt()
        );
    }
    
    private QuizQuestionDTO convertToQuizQuestionDTO(QuizQuestion question) {
        return new QuizQuestionDTO(
            question.getId(),
            question.getQuestion(),
            question.getOptionA(),
            question.getOptionB(),
            question.getOptionC(),
            question.getOptionD(),
            question.getCorrectAnswer(),
            question.getExplanation(),
            question.getPoints(),
            question.getDifficulty(),
            question.getCreatedAt(),
            question.getUpdatedAt()
        );
    }
    
    private QuizAttemptDTO convertToQuizAttemptDTO(QuizAttempt attempt) {
        return new QuizAttemptDTO(
            attempt.getId(),
            attempt.getUser().getUsername(),
            attempt.getQuiz().getTitle(),
            attempt.getScore(),
            attempt.getTotalQuestions(),
            attempt.getCorrectAnswers(),
            attempt.getWrongAnswers(),
            attempt.getTimeSpent(),
            attempt.getStartedAt(),
            attempt.getCompletedAt(),
            attempt.isCompleted()
        );
    }
}
