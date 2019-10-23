package org.hyperskill.webquizengine.testutils;

import org.hyperskill.webquizengine.model.Quiz;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public final class TestUtils {
    private TestUtils() { }

    public static Quiz createJavaLogoQuizWithoutId() {
        return createJavaLogoQuizWithId(null);
    }

    public static Quiz createJavaLogoQuizWithId(Long id) {
        var quiz = new Quiz();
        quiz.setId(id);
        quiz.setTitle("The Java Logo");
        quiz.setText("What is depicted on the Java logo?");
        quiz.setOptions(List.of("Robot","Tea leaf","Cup of coffee","Bug"));
        quiz.setAnswer(2);
        return quiz;
    }

    public static ResultActions expectQuizJsonIsValid(ResultActions actions, Quiz quiz) throws Exception {
        return actions
                .andExpect(jsonPath("$.id").value(quiz.getId()))
                .andExpect(jsonPath("$.title").value(quiz.getTitle()))
                .andExpect(jsonPath("$.text").value(quiz.getText()))
                .andExpect(jsonPath("$.options").isArray())
                .andExpect(jsonPath("$.options", hasSize(4)))
                .andExpect(jsonPath("$.options", hasItem("Robot")))
                .andExpect(jsonPath("$.options", hasItem("Tea leaf")))
                .andExpect(jsonPath("$.options", hasItem("Cup of coffee")))
                .andExpect(jsonPath("$.options", hasItem("Bug")));
    }

    public static List<Quiz> createTestQuizzes(int n) {
        return Stream.generate(Quiz::new)
                .limit(10)
                .collect(Collectors.toList());
    }

}
