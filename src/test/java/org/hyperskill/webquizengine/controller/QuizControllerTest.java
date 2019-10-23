package org.hyperskill.webquizengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.hyperskill.webquizengine.exception.QuizNotFoundException;
import org.hyperskill.webquizengine.model.Result;
import org.hyperskill.webquizengine.service.QuizService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hyperskill.webquizengine.testutils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

        mvc.perform(post("/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(quizWithoutId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(quizWithId.getId()))
                .andExpect(jsonPath("$.title").value(quizWithId.getTitle()))
                .andExpect(jsonPath("$.text").value(quizWithId.getText()))
                .andExpect(jsonPath("$.options").isArray())
                .andExpect(jsonPath("$.options", hasSize(4)))
                .andExpect(jsonPath("$.options", hasItem("Robot")))
                .andExpect(jsonPath("$.options", hasItem("Tea leaf")))
                .andExpect(jsonPath("$.options", hasItem("Cup of coffee")))
                .andExpect(jsonPath("$.options", hasItem("Bug")));
    }

    @Test
    public void testGetQuiz_whenExists() throws Exception {
        var quiz = createJavaLogoQuizWithId(1L);

        when(service.findById(anyLong())).thenReturn(quiz);

        mvc.perform(get(String.format("/quizzes/%s", quiz.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(quiz.getId()))
                .andExpect(jsonPath("$.title").value(quiz.getTitle()))
                .andExpect(jsonPath("$.text").value(quiz.getText()))
                .andExpect(jsonPath("$.options").isArray())
                .andExpect(jsonPath("$.options", hasSize(4)))
                .andExpect(jsonPath("$.options", hasItem("Robot")))
                .andExpect(jsonPath("$.options", hasItem("Tea leaf")))
                .andExpect(jsonPath("$.options", hasItem("Cup of coffee")))
                .andExpect(jsonPath("$.options", hasItem("Bug")))
                .andExpect(jsonPath("$.answer").doesNotExist());
    }

    @Test
    public void testGetQuiz_whenNonExists() throws Exception {
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
    @Disabled
    public void testSolveQuiz_whenCorrectAnswer() throws Exception {
        var mvcResult = mvc.perform(post("/quizzes")
                .param("answer", "2"))
                .andExpect(status().isOk())
                .andReturn();

        var body = mvcResult.getResponse().getContentAsString();
        var quizResult = mapper.readValue(body, Result.class);

        assertTrue(quizResult.isSuccess());
    }

    @Test
    @Disabled
    public void testSolveQuiz_whenIncorrectAnswer() throws Exception {
        var mvcResult = mvc.perform(post("/quizzes")
                .param("answer", "1"))
                .andExpect(status().isOk())
                .andReturn();

        var body = mvcResult.getResponse().getContentAsString();
        var quizResult = mapper.readValue(body, Result.class);

        assertFalse(quizResult.isSuccess());
    }
}
