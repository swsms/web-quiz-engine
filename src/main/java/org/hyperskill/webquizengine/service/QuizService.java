package org.hyperskill.webquizengine.service;

import org.hyperskill.webquizengine.exception.QuizNotFoundException;
import org.hyperskill.webquizengine.modelng.Quiz;
import org.hyperskill.webquizengine.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.hyperskill.webquizengine.util.Utils.getCorrectOptionsIndexes;

@Service
public class QuizService {
    private final QuizRepository repository;

    @Autowired
    public QuizService(QuizRepository repository) {
        this.repository = repository;
    }

    public boolean solve(long quizId, Set<Integer> answer) {
        var quiz = findById(quizId);
        var indexes = getCorrectOptionsIndexes(quiz.getOptions());
        return Objects.equals(answer, indexes);
    }

    public Long add(Quiz quiz) {
        return repository.save(quiz).getId();
    }

    public Quiz findById(long id) {
        var optionalQuiz = repository.findById(id);
        return optionalQuiz.orElseThrow(QuizNotFoundException::new);
    }

    public List<Quiz> findAllSortedById() {
        var quizzes = new ArrayList<Quiz>();
        repository.findAll().forEach(quizzes::add);
        return quizzes;
    }
}
