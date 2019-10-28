package org.hyperskill.webquizengine.service;

import org.hyperskill.webquizengine.exception.QuizNotFoundException;
import org.hyperskill.webquizengine.model.Quiz;
import org.hyperskill.webquizengine.model.Result;
import org.hyperskill.webquizengine.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class QuizService {
    private final ConcurrentMap<Long, Quiz> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    public Result solve(long quizId, List<Integer> answer) {
        var quiz = storage.get(quizId);
        if (quiz == null) {
            throw new QuizNotFoundException();
        }
        return Objects.equals(quiz.getAnswer().get(0), answer.get(0)) ?
                Result.success() : Result.failure();
    }

    public Quiz add(Quiz quiz) {
        Utils.checkAnswerOptions(quiz);
        var id = idGenerator.incrementAndGet();
        quiz.setId(id);
        storage.putIfAbsent(id, quiz);
        return quiz;
    }

    public Quiz findById(long id) {
        var quiz = storage.get(id);
        if (quiz == null) {
            throw new QuizNotFoundException();
        }
        return quiz;
    }

    public List<Quiz> findAllSortedById() {
        var quizzes = new ArrayList<>(storage.values());
        quizzes.sort(Comparator.comparingLong(Quiz::getId));
        return quizzes;
    }

}
