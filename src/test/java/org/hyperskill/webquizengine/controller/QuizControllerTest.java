package org.hyperskill.webquizengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperskill.webquizengine.exception.InvalidAnswerOptions;
import org.hyperskill.webquizengine.exception.QuizNotFoundException;
import org.hyperskill.webquizengine.service.QuizService;
import org.hyperskill.webquizengine.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hyperskill.webquizengine.testutils.TestUtils.*;
import static org.hyperskill.webquizengine.util.Utils.convertQuizDtoToEntity;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(QuizController.class)
public class QuizControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private QuizService quizService;

    @MockBean
    private UserService service;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testCreateQuiz_whenFourOptionsAndAnswerExist() throws Exception {
        var quizWithId = createJavaLogoQuizWithId(1L);
        var quizWithoutId = createJavaLogoQuizWithoutId();

        when(quizService.create(any(), anyString())).thenReturn(quizWithId.getId());

        expectQuizJsonIsValid(mvc.perform(post("/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(quizWithoutId)))
                .andExpect(status().isOk()), quizWithId);
    }

    @Test
    public void testCreateQuiz_whenUnauthorized() throws Exception {
        var quizWithId = createJavaLogoQuizWithId(1L);
        var quizWithoutId = createJavaLogoQuizWithoutId();

        when(quizService.create(any(), anyString())).thenReturn(quizWithId.getId());

        mvc.perform(post("/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(quizWithoutId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testCreateQuiz_whenNoAnswer() throws Exception {
        var quizWithId = createJavaLogoQuizWithId(1L);
        var quizWithoutId = createJavaLogoQuizWithoutId();

        quizWithoutId.setAnswer(Collections.emptySet());

        when(quizService.create(any(), anyString())).thenReturn(quizWithId.getId());

        expectQuizJsonIsValid(mvc.perform(post("/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(quizWithoutId)))
                .andExpect(status().isOk()), quizWithId);
    }


    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testCreateQuiz_whenNoOptions() throws Exception {
        var quizWithId = createJavaLogoQuizWithId(1L);
        var quizWithoutId = createJavaLogoQuizWithoutId();

        quizWithoutId.setOptions(Collections.emptyList());

        when(quizService.create(any(), anyString())).thenReturn(quizWithId.getId());

        mvc.perform(post("/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(quizWithoutId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testGetQuiz_whenExists() throws Exception {
        var quiz = createJavaLogoQuizWithId(1L);

        when(quizService.findById(anyLong())).thenReturn(convertQuizDtoToEntity(quiz));

        expectQuizJsonIsValid(mvc.perform(get(String.format("/quizzes/%s", quiz.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").doesNotExist()), quiz);
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testGetQuiz_whenQuizNotFound() throws Exception {
        when(quizService.findById(anyLong())).thenThrow(QuizNotFoundException.class);

        mvc.perform(get(String.format("/quizzes/%d", 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetQuiz_whenUnauthorized() throws Exception {
        mvc.perform(get(String.format("/quizzes/%d", 1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    void testGetQuizList_evaluatesPageableParameter() throws Exception {
        var quizzes = createTestQuizzes(15);

        when(quizService.findAllAsPage(any())).thenReturn(new PageImpl<>(quizzes));

        mvc.perform(get("/quizzes")
                .param("page", "5")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testGetQuizList_whenManyQuizzes() throws Exception {
        var quizzes = createTestQuizzes(15);

        when(quizService.findAllAsPage(any())).thenReturn(new PageImpl<>(quizzes));

        mvc.perform(get("/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(quizzes.size())));
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testGetQuizList_whenNoQuizzes() throws Exception {
        when(quizService.findAllAsPage(any())).thenReturn(new PageImpl<>(List.of()));

        mvc.perform(get("/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testSolveQuiz_whenCorrectAnswer() throws Exception {
        when(quizService.solve(anyLong(), anySet(), anyString())).thenReturn(true);

        mvc.perform(post(String.format("/quizzes/%d/solve", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Set.of(0, 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testSolveQuiz_whenIncorrectAnswer() throws Exception {
        when(quizService.solve(anyLong(), anySet(), anyString())).thenReturn(false);

        mvc.perform(post(String.format("/quizzes/%d/solve", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Set.of(0, 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testSolveQuiz_whenNoAnswer() throws Exception {
        mvc.perform(post(String.format("/quizzes/%d/solve", 1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testSolveQuiz_whenInvalidAnswerOptions() throws Exception {
        when(quizService.solve(anyLong(), anySet(), anyString()))
                .thenThrow(InvalidAnswerOptions.class);

        mvc.perform(post(String.format("/quizzes/%d/solve", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Set.of(0, 1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSolveQuiz_whenUnauthorized() throws Exception {
        mvc.perform(post(String.format("/quizzes/%d/solve", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Set.of(0, 1))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME, password = DEFAULT_PASSWORD)
    public void testDeleteQuiz_whenSuccessful() throws Exception {
        mvc.perform(delete(String.format("/quizzes/%d", 1)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testDeleteQuiz_whenUnauthorized() throws Exception {
        mvc.perform(delete(String.format("/quizzes/%d", 1)))
                .andExpect(status().isUnauthorized());
    }
}
