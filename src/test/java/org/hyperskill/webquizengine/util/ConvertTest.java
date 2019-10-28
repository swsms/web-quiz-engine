package org.hyperskill.webquizengine.util;

import org.hyperskill.webquizengine.dto.QuizDto;
import org.hyperskill.webquizengine.modelng.Quiz;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.hyperskill.webquizengine.modelng.Option.newOption;
import static org.hyperskill.webquizengine.util.Utils.convertQuizDtoToEntity;
import static org.hyperskill.webquizengine.util.Utils.convertQuizEntityToDtoWithoutAnswer;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConvertTest {

    @Test
    public void testConvertQuizEntityToDto() {
        var quiz = new Quiz();
        quiz.setId(10L);
        quiz.setTitle("Test title");
        quiz.setText("Test text");
        quiz.setOptions(List.of(
                newOption("a", true),
                newOption("b", false),
                newOption("c", true),
                newOption("d", false)
        ));

        var quizDto = convertQuizEntityToDtoWithoutAnswer(quiz);

        assertEquals(quiz.getId(), quizDto.getId());
        assertEquals(quiz.getTitle(), quizDto.getTitle());
        assertEquals(quiz.getText(), quizDto.getText());
        assertEquals(quiz.getOptions().size(), quizDto.getOptions().size());
        for (int i = 0; i < quizDto.getOptions().size(); i++) {
            assertEquals(quizDto.getOptions().get(i), quiz.getOptions().get(i).getText());
        }
    }

    @Test
    public void testConvertQuizDtoToEntity() {
        var quizDto = new QuizDto();
        quizDto.setTitle("Test title");
        quizDto.setText("Test text");
        quizDto.setOptions(List.of("a", "b", "c", "d", "e"));
        quizDto.setAnswer(Set.of(0, 1, 4));

        var quiz = convertQuizDtoToEntity(quizDto);

        assertEquals(quizDto.getTitle(), quiz.getTitle());
        assertEquals(quizDto.getText(), quiz.getText());
        assertEquals(quizDto.getOptions().size(), quiz.getOptions().size());

        for (int i = 0; i < quiz.getOptions().size(); i++) {
            var option = quiz.getOptions().get(i);
            var contains = quizDto.getAnswer().contains(i);
            assertEquals(contains, option.getCorrect());
            assertEquals(quizDto.getOptions().get(i), option.getText());
        }
    }
}
