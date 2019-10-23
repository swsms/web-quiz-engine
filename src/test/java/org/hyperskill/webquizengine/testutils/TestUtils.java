package org.hyperskill.webquizengine.testutils;

import org.hyperskill.webquizengine.model.Quiz;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TestUtils {
    private TestUtils() { }

    public static Quiz createTestQuizWithoutId() {
        return createTestQuizWithId(null);
    }

    public static Quiz createTestQuizWithId(Long id) {
        var quiz = new Quiz();
        quiz.setId(id);
        quiz.setTitle("The Java Logo");
        quiz.setText("What is depicted on the Java logo?");
        quiz.setOptions(List.of("Robot","Tea leaf","Cup of coffee","Bug"));
        quiz.setAnswer(2);
        return quiz;
    }

    public static List<Quiz> generateTestQuizzes() {
        return Stream.generate(Quiz::new)
                .limit(10)
                .collect(Collectors.toList());
    }

}
