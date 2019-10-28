package org.hyperskill.webquizengine.service;

import org.hyperskill.webquizengine.exception.QuizNotFoundException;
import org.hyperskill.webquizengine.dto.QuizDto;
import org.hyperskill.webquizengine.model.Quiz;
import org.hyperskill.webquizengine.repository.QuizRepository;
import org.hyperskill.webquizengine.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hyperskill.webquizengine.model.Option.newOption;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class QuizServiceTest {
    private static final int NUMBER_OF_QUIZZES = 10;
    private QuizService service;

    @MockBean
    private QuizRepository mockRepository;

    @BeforeEach
    void init() {
        service = new QuizService(mockRepository);
    }

    @Test
    public void testAddQuiz() {
        var quiz = new Quiz();
        quiz.setId(5L);

        when(mockRepository.save(any())).thenReturn(quiz);
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(quiz));

        assertEquals(quiz.getId(), service.add(new Quiz()));
    }

    @Test
    public void testFindById_whenExist() {
        var quiz = new Quiz();
        quiz.setId(10L);

        when(mockRepository.save(any())).thenReturn(quiz);
        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(quiz));

        service.add(new Quiz());
        var foundQuiz = service.findById(quiz.getId());

        assertEquals(quiz.getId(), foundQuiz.getId());
    }

    @Test
    public void testFindById_whenNonExist() {
        var quiz = new Quiz();
        quiz.setId(15L);

        when(mockRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(QuizNotFoundException.class, () -> service.findById(quiz.getId()));
    }

    @Test
    public void testSolve_whenSingleCorrectOption() {
        var quiz = new Quiz();
        quiz.setOptions(List.of(
                newOption("a", true),
                newOption("b", false),
                newOption("c", false)
        ));

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(quiz));

        assertTrue(service.solve(1L, Set.of(0)));
    }

    @Test
    public void testSolve_whenSeveralCorrectOptions() {
        var quiz = new Quiz();
        quiz.setOptions(List.of(
                newOption("a", true),
                newOption("b", false),
                newOption("c", false),
                newOption("d", true),
                newOption("e", true)
        ));

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(quiz));

        assertTrue(service.solve(1L, Set.of(0, 3, 4)));
    }


    @Test
    public void testSolve_whenSingleIncorrectOption() {
        var quiz = new Quiz();
        quiz.setOptions(List.of(
                newOption("a", true),
                newOption("b", false),
                newOption("c", false)
        ));

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(quiz));

        assertFalse(service.solve(1L, Set.of(2)));
    }

    @Test
    public void testSolve_whenNotAllCorrectOptions() {
        var quiz = new Quiz();
        quiz.setOptions(List.of(
                newOption("a", true),
                newOption("b", false),
                newOption("c", false),
                newOption("d", true),
                newOption("e", true)
        ));

        when(mockRepository.findById(anyLong())).thenReturn(Optional.of(quiz));

        assertFalse(service.solve(1L, Set.of(0, 3, 2)));
    }

    @Test
    public void testSolve_whenQuizNotFound() {
        when(mockRepository.findById(anyLong())).thenThrow(QuizNotFoundException.class);
        assertThrows(QuizNotFoundException.class, () -> service.solve(2, Set.of(1)));
    }
}
