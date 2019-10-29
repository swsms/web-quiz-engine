package org.hyperskill.webquizengine.service;

import org.hyperskill.webquizengine.dto.QuizDto;
import org.hyperskill.webquizengine.exception.NotPermittedException;
import org.hyperskill.webquizengine.exception.QuizNotFoundException;
import org.hyperskill.webquizengine.exception.UserNotFoundException;
import org.hyperskill.webquizengine.model.Completion;
import org.hyperskill.webquizengine.model.Quiz;
import org.hyperskill.webquizengine.repository.CompletionRepository;
import org.hyperskill.webquizengine.repository.QuizRepository;
import org.hyperskill.webquizengine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

import static org.hyperskill.webquizengine.model.Completion.createCompletion;
import static org.hyperskill.webquizengine.util.Utils.convertQuizDtoToEntity;
import static org.hyperskill.webquizengine.util.Utils.getCorrectOptionsIndexes;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final CompletionRepository completionRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository,
                       UserRepository userRepository,
                       CompletionRepository completionRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.completionRepository = completionRepository;
    }

    public boolean solve(long quizId, Set<Integer> answer, String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        var quiz = findById(quizId);
        var indexes = getCorrectOptionsIndexes(quiz.getOptions());

        boolean correct = Objects.equals(answer, indexes);
        if (correct) {
            completionRepository.save(createCompletion(user, quiz));
        }

        return correct;
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

    public void delete(long quizId, String username) {
        var quiz = findById(quizId);
        var user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        if (Objects.equals(quiz.getCreatedBy().getId(), user.getId())) {
            quizRepository.delete(quiz);
        } else {
            throw new NotPermittedException();
        }
    }

    public Page<Quiz> findAllAsPage(Pageable pageable) {
        return quizRepository.findAll(pageable);
    }

    public Page<Completion> findAllCompletedQuizzesAsPage(String username, Pageable pageable) {
        return completionRepository.findAllByUserOrderByCompletedAtDesc(username, pageable);
    }
}
