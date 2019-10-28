package org.hyperskill.webquizengine.utils;

import org.hyperskill.webquizengine.exception.InvalidAnswerOptions;
import org.hyperskill.webquizengine.model.Quiz;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UtilsTest {

    @Test
    public void testCheckAnswerOptions_whenCorrect() {
        var quiz = new Quiz();
        quiz.setOptions(List.of("a", "b", "c"));
        quiz.setAnswer(List.of(0));
        Utils.checkAnswerOptions(quiz);

        quiz.setAnswer(List.of(0, 1, 2));
        Utils.checkAnswerOptions(quiz);
    }

    @Test
    public void testCheckAnswerOptions_whenOptionIndexIsNegative() {
        var quiz = new Quiz();
        quiz.setOptions(List.of("a", "b", "c"));
        quiz.setAnswer(List.of(-1));
        assertThrows(InvalidAnswerOptions.class, () -> Utils.checkAnswerOptions(quiz));
    }

    @Test
    public void testCheckAnswerOptions_whenOptionIndexIsLarge() {
        var quiz = new Quiz();
        quiz.setOptions(List.of("a", "b", "c"));
        quiz.setAnswer(List.of(3));
        assertThrows(InvalidAnswerOptions.class, () -> Utils.checkAnswerOptions(quiz));
    }

    @Test
    public void testCheckAnswerOptions_whenOneIsIncorrect() {
        var quiz = new Quiz();
        quiz.setOptions(List.of("a", "b", "c", "d", "e"));
        quiz.setAnswer(List.of(0, 1, 2, 6, 3));
        assertThrows(InvalidAnswerOptions.class, () -> Utils.checkAnswerOptions(quiz));
    }

    @Test
    public void testCheckAnswerOptions_whenNoOptions() {
        var quiz = new Quiz();
        quiz.setOptions(List.of());
        quiz.setAnswer(List.of(0));
        assertThrows(InvalidAnswerOptions.class, () -> Utils.checkAnswerOptions(quiz));
    }
}
