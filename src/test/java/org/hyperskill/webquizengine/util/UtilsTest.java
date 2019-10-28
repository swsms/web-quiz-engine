package org.hyperskill.webquizengine.util;

import org.hyperskill.webquizengine.exception.InvalidAnswerOptions;
import org.hyperskill.webquizengine.dto.QuizDto;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.hyperskill.webquizengine.util.Utils.checkAnswerOptions;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UtilsTest {

    @Test
    public void testCheckAnswerOptions_whenOneOptionIsCorrect() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of("a"));
        quiz.setAnswer(Set.of(0));
        checkAnswerOptions(quiz);
    }

    @Test
    public void testCheckAnswerOptions_whenSeveralOptionsAreCorrect() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of("a", "b"));
        quiz.setAnswer(Set.of(0));
        checkAnswerOptions(quiz);

        quiz.setAnswer(Set.of(0, 1));
        checkAnswerOptions(quiz);
    }

    @Test
    public void testCheckAnswerOptions_whenOptionIndexIsNegative() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of("a", "b", "c"));
        quiz.setAnswer(Set.of(-1));
        assertThrows(InvalidAnswerOptions.class, () -> checkAnswerOptions(quiz));
    }

    @Test
    public void testCheckAnswerOptions_whenOptionIndexIsLarge() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of("a", "b", "c"));
        quiz.setAnswer(Set.of(3));
        assertThrows(InvalidAnswerOptions.class, () -> checkAnswerOptions(quiz));
    }

    @Test
    public void testCheckAnswerOptions_whenOneIsIncorrect() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of("a", "b", "c", "d", "e"));
        quiz.setAnswer(Set.of(0, 1, 2, 6, 3));
        assertThrows(InvalidAnswerOptions.class, () -> checkAnswerOptions(quiz));
    }

    @Test
    public void testCheckAnswerOptions_whenNoOptions() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of());
        quiz.setAnswer(Set.of(0));
        assertThrows(InvalidAnswerOptions.class, () -> checkAnswerOptions(quiz));
    }

    @Test
    public void testCheckAnswerOptions_whenNoOptionsAndAnswer() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of());
        quiz.setAnswer(Set.of());
        checkAnswerOptions(quiz);
    }
}
