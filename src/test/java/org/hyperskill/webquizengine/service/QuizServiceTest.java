package org.hyperskill.webquizengine.service;

import org.hyperskill.webquizengine.exception.NotPermittedException;
import org.hyperskill.webquizengine.exception.QuizNotFoundException;
import org.hyperskill.webquizengine.exception.UserNotFoundException;
import org.hyperskill.webquizengine.model.Quiz;
import org.hyperskill.webquizengine.model.User;
import org.hyperskill.webquizengine.repository.QuizRepository;
import org.hyperskill.webquizengine.repository.UserRepository;
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
import static org.hyperskill.webquizengine.testutils.TestUtils.*;
import static org.hyperskill.webquizengine.util.Utils.convertQuizEntityToDtoWithoutAnswer;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class QuizServiceTest {
    private QuizService service;

    @MockBean
    private QuizRepository quizRepository;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void init() {
        service = new QuizService(quizRepository, userRepository);
    }

    @Test
    public void testCreateQuiz() {
        var quiz = createQuizEntityWithId(1L);
        var user = createTestUserWithDefaultName();

        when(quizRepository.save(any())).thenReturn(quiz);
        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(quiz));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        var quizDto = convertQuizEntityToDtoWithoutAnswer(quiz);
        var id = service.create(quizDto, user.getUsername());

        assertEquals(quiz.getId(), id);
    }

    @Test
    public void testCreateQuiz_whenNoUserFound() {
        var quiz = createQuizEntityWithId(5L);
        var user = createTestUserWithDefaultName();

        when(quizRepository.save(any())).thenReturn(quiz);
        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(quiz));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        var quizDto = convertQuizEntityToDtoWithoutAnswer(quiz);

        assertThrows(UserNotFoundException.class,
                () -> service.create(quizDto, user.getUsername()));
    }

    @Test
    public void testFindById_whenExist() {
        var quiz = createQuizEntityWithId(10L);
        var user = createTestUserWithDefaultName();

        when(quizRepository.save(any())).thenReturn(quiz);
        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(quiz));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        service.create(convertQuizEntityToDtoWithoutAnswer(new Quiz()), user.getUsername());
        var foundQuiz = service.findById(quiz.getId());

        assertEquals(quiz.getId(), foundQuiz.getId());
    }

    @Test
    public void testFindById_whenNonExist() {
        var quiz = new Quiz();
        quiz.setId(15L);

        when(quizRepository.findById(anyLong())).thenReturn(Optional.empty());

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

        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(quiz));

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

        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(quiz));

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

        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(quiz));

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

        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(quiz));

        assertFalse(service.solve(1L, Set.of(0, 3, 2)));
    }

    @Test
    public void testSolve_whenQuizNotFound() {
        when(quizRepository.findById(anyLong())).thenThrow(QuizNotFoundException.class);
        assertThrows(QuizNotFoundException.class, () -> service.solve(2, Set.of(1)));
    }

    @Test
    public void testDelete_whenSuccess() {
        var quiz = createQuizEntityWithId(10L);
        var user = createTestUserWithDefaultName();
        quiz.setCreatedBy(user);

        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(quiz));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        service.delete(quiz.getId(), user.getUsername());
    }

    @Test
    public void testDelete_whenNotPermitted() {
        var quiz = createQuizEntityWithId(10L);
        var user = createTestUserWithDefaultName();
        quiz.setCreatedBy(user);

        var anotherUser = new User();
        anotherUser.setId(20L);
        anotherUser.setUsername("anotheruser@gmail.com");

        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(quiz));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(anotherUser));

        assertThrows(NotPermittedException.class,
                () -> service.delete(quiz.getId(), anotherUser.getUsername()));
    }

    @Test
    public void testDelete_whenQuizNotFound() {
        var user = createTestUserWithDefaultName();

        when(quizRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThrows(QuizNotFoundException.class,
                () -> service.delete(10L, user.getUsername()));
    }

    @Test
    public void testDelete_whenUserNotFound() {
        var quiz = createQuizEntityWithId(10L);

        when(quizRepository.findById(anyLong())).thenReturn(Optional.of(quiz));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.delete(quiz.getId(), DEFAULT_USERNAME));
    }
}