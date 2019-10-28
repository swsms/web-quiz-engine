package org.hyperskill.webquizengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.hyperskill.webquizengine.exception.InvalidAnswerOptions;
import org.hyperskill.webquizengine.exception.QuizNotFoundException;
import org.hyperskill.webquizengine.model.Result;
import org.hyperskill.webquizengine.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hyperskill.webquizengine.testutils.TestUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizController.class)
public class QuizControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private QuizService service;

    @Test
    public void testCreateQuiz() throws Exception {
        var quizWithId = createJavaLogoQuizWithId(1L);
        var quizWithoutId = createJavaLogoQuizWithoutId();

        when(service.add(any())).thenReturn(quizWithId);

        expectQuizJsonIsValid(mvc.perform(post("/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(quizWithoutId)))
                .andExpect(status().isOk()), quizWithId);
    }

    @Test
    public void testGetQuiz_whenExists() throws Exception {
        var quiz = createJavaLogoQuizWithId(1L);

        when(service.findById(anyLong())).thenReturn(quiz);

        expectQuizJsonIsValid(mvc.perform(get(String.format("/quizzes/%s", quiz.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").doesNotExist()), quiz);
    }

    @Test
    public void testGetQuiz_whenQuizNotFound() throws Exception {
        when(service.findById(anyLong())).thenThrow(QuizNotFoundException.class);

        mvc.perform(get(String.format("/quizzes/%d", 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetQuizList_whenManyQuizzes() throws Exception {
        var quizzes = createTestQuizzes(10);

        when(service.findAllSortedById()).thenReturn(quizzes);

        mvc.perform(get("/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(quizzes.size())));
    }

    @Test
    public void testGetQuizList_whenNoQuizzes() throws Exception {
        when(service.findAllSortedById()).thenReturn(Lists.emptyList());

        mvc.perform(get("/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testSolveQuiz_whenCorrectAnswer() throws Exception {
        when(service.solve(anyLong(), anySet())).thenReturn(Result.success());

        mvc.perform(post(String.format("/quizzes/%d/solve", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Set.of(0, 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testSolveQuiz_whenIncorrectAnswer() throws Exception {
        when(service.solve(anyLong(), anySet())).thenReturn(Result.failure());

        mvc.perform(post(String.format("/quizzes/%d/solve", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Set.of(0, 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testSolveQuiz_whenNoAnswer() throws Exception {
        mvc.perform(post(String.format("/quizzes/%d/solve", 1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSolveQuiz_whenInvalidAnswerOptions() throws Exception {
        when(service.solve(anyLong(), anySet())).thenThrow(InvalidAnswerOptions.class);

        mvc.perform(post(String.format("/quizzes/%d/solve", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Set.of(0, 1))))
                .andExpect(status().isBadRequest());
    }
}
