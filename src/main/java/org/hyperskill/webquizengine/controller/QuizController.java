package org.hyperskill.webquizengine.controller;

import org.hyperskill.webquizengine.model.Quiz;
import org.hyperskill.webquizengine.model.Result;
import org.hyperskill.webquizengine.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/quizzes")
public class QuizController {
    private final Logger logger = LoggerFactory.getLogger(QuizController.class);
    private final QuizService service;

    @Autowired
    public QuizController(QuizService service) {
        this.service = service;
    }

    @PostMapping(path = "/{id}/solve", produces = APPLICATION_JSON_VALUE)
    public Result solveQuiz(@PathVariable long id,
                            @RequestParam Set<Integer> answer) {
        return service.solve(id, answer);
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Quiz createQuiz(@Valid @RequestBody Quiz quiz) {
        logger.info("Creating a quiz: {}", quiz);
        return service.add(quiz);
    }

    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public Quiz getQuiz(@PathVariable long id) {
        return service.findById(id);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Quiz> getQuizList() {
        return service.findAllSortedById();
    }
}
