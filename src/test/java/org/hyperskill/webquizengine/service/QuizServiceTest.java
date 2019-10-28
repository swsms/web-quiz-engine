package org.hyperskill.webquizengine.service;

import org.hyperskill.webquizengine.exception.QuizNotFoundException;
import org.hyperskill.webquizengine.dto.QuizDto;
import org.hyperskill.webquizengine.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class QuizServiceTest {
    /*
    private static final int NUMBER_OF_QUIZZES = 10;
    private QuizService service;

    @BeforeEach
    void init() {
        service = new QuizService(repository);
    }

    @Test
    public void testAddQuiz() {
        var quizzes = TestUtils.createTestQuizzes(NUMBER_OF_QUIZZES);
        var index = 1;
        for (var q : quizzes) {
            var quiz = service.add(q);
            assertNotNull(quiz);
            assertEquals(index, (long) quiz.getId());
            index++;
        }
    }

    @Test
    public void testFindById_whenExist() {
        TestUtils.createTestQuizzes(NUMBER_OF_QUIZZES).stream()
                .map(q -> service.add(q))
                .forEach((q) -> {
                    var quiz = service.findById(q.getId());
                    assertNotNull(quiz);
                    assertEquals(q.getId(), quiz.getId());
        });
    }

    @Test
    public void testFindById_whenNonExist() {
        var idList = List.of(0, 1, 2, 3);
        idList.forEach((id) -> assertThrows(QuizNotFoundException.class, () -> service.findById(id)));
    }

    @Test
    public void testSolve_whenSingleCorrectOption() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of("a", "b", "c"));
        quiz.setAnswer(Set.of(0));
        var id = service.add(quiz).getId();
        var result = service.solve(id, Set.of(0));
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSolve_whenSeveralCorrectOptions() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of("a", "b", "c"));
        quiz.setAnswer(Set.of(0, 1, 2));
        var id = service.add(quiz).getId();
        var result = service.solve(id, Set.of(0, 1, 2));
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSolve_whenDuplicateCorrectOptions() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of("a", "b", "c"));
        quiz.setAnswer(Set.of(0, 1));
        var id = service.add(quiz).getId();
        var result = service.solve(id, Set.of(0, 1));
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSolve_whenSingleIncorrectOption() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of("a", "b", "c"));
        quiz.setAnswer(Set.of(2));
        var id = service.add(quiz).getId();
        var result = service.solve(id, Set.of(1));
        assertFalse(result.isSuccess());
    }

    @Test
    public void testSolve_whenNotAllCorrectOptions() {
        var quiz = new QuizDto();
        quiz.setOptions(List.of("a", "b", "c"));
        quiz.setAnswer(Set.of(0, 1, 2));
        var id = service.add(quiz).getId();
        var result = service.solve(id, Set.of(0, 1));
        assertFalse(result.isSuccess());
    }

    @Test
    public void testSolve_whenQuizNotFound() {
        assertThrows(QuizNotFoundException.class, () -> service.solve(2, Set.of(1)));
    }*/
}
