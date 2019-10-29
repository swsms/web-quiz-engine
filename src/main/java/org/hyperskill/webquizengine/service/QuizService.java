package org.hyperskill.webquizengine.service;

import org.hyperskill.webquizengine.dto.QuizDto;
import org.hyperskill.webquizengine.exception.QuizNotFoundException;
import org.hyperskill.webquizengine.exception.UserNotFoundException;
import org.hyperskill.webquizengine.model.Quiz;
import org.hyperskill.webquizengine.repository.QuizRepository;
import org.hyperskill.webquizengine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.hyperskill.webquizengine.util.Utils.convertQuizDtoToEntity;
import static org.hyperskill.webquizengine.util.Utils.getCorrectOptionsIndexes;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    public boolean solve(long quizId, Set<Integer> answer) {
        var quiz = findById(quizId);
        var indexes = getCorrectOptionsIndexes(quiz.getOptions());
        return Objects.equals(answer, indexes);
    }

    public Long create(QuizDto quizDto, String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        var quiz = convertQuizDtoToEntity(quizDto);
        quiz.setCreatedBy(user);

        return quizRepository.save(quiz).getId();
    }

    public Quiz findById(long id) {
        var optionalQuiz = quizRepository.findById(id);
        return optionalQuiz.orElseThrow(QuizNotFoundException::new);
    }

    public List<Quiz> findAllSortedById() {
        var quizzes = new ArrayList<Quiz>();
        quizRepository.findAll().forEach(quizzes::add);
        return quizzes;
    }
}
