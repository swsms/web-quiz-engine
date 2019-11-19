package org.hyperskill.webquizengine.controller;

import org.hyperskill.webquizengine.model.Quiz;
import org.hyperskill.webquizengine.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/quiz")
public class QuizController {
    private final Logger logger = LoggerFactory.getLogger(QuizController.class);
    private final static Quiz quiz;

    static {
        quiz = new Quiz();
        quiz.setTitle("The Java Logo");
        quiz.setText("What is depicted on the Java logo?");
        quiz.setOptions(List.of("Robot", "Tea leaf", "Cup of coffee", "Bug"));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Quiz getFakeQuiz() {
        logger.info("Someone got the quiz");
        return quiz;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Result solveQuiz(@RequestParam int answer) {
        return answer == 2 ? Result.success() : Result.failure();
    }
}
