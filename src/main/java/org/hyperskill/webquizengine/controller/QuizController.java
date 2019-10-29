package org.hyperskill.webquizengine.controller;

import org.hyperskill.webquizengine.dto.QuizDto;
import org.hyperskill.webquizengine.dto.ResultDto;
import org.hyperskill.webquizengine.service.QuizService;
import org.hyperskill.webquizengine.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hyperskill.webquizengine.util.Utils.checkAnswerOptions;
import static org.hyperskill.webquizengine.util.Utils.convertQuizEntityToDtoWithoutAnswer;
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
    public ResultDto solveQuiz(@PathVariable long id,
                               @RequestBody Set<Integer> answer) {
        logger.info("Solving a quiz {} with answer {}", id, answer);
        return service.solve(id, answer) ? ResultDto.success() : ResultDto.failure();
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public QuizDto createQuiz(@Valid @RequestBody QuizDto quizDto,
                              @Autowired Principal principal) {
        logger.info("User {} wants to create a quiz {}", principal.getName(), quizDto);
        checkAnswerOptions(quizDto);
        var id = service.create(quizDto, principal.getName());
        quizDto.setId(id);
        return quizDto;
    }

    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public QuizDto getQuiz(@PathVariable long id) {
        return convertQuizEntityToDtoWithoutAnswer(service.findById(id));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<QuizDto> getQuizList() {
        return service.findAllSortedById().stream()
                .map(Utils::convertQuizEntityToDtoWithoutAnswer)
                .collect(Collectors.toList());
    }

    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteQuiz(@PathVariable long id,
                           @Autowired Principal principal) {
        logger.info("User {} wants to delete a quiz with id {}", principal.getName(), id);
        service.delete(id, principal.getName());
    }
}
