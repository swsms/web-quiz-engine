package org.hyperskill.webquizengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperskill.webquizengine.model.Quiz;
import org.hyperskill.webquizengine.model.Result;
import org.hyperskill.webquizengine.service.QuizService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizController.class)
public class QuizControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private QuizService service;

    private Quiz createTestQuizWithoutId() {
        return createTestQuizWithId(null);
    }

    private Quiz createTestQuizWithId(Long id) {
        var quiz = new Quiz();
        quiz.setId(id);
        quiz.setTitle("The Java Logo");
        quiz.setText("What is depicted on the Java logo?");
        quiz.setOptions(List.of("Robot","Tea leaf","Cup of coffee","Bug"));
        quiz.setAnswer(2);
        return quiz;
    }

    @Test
    public void testCreateQuiz() throws Exception {
        var quizWithId = createTestQuizWithId(1L);
        var quizWithoutId = createTestQuizWithoutId();

        when(service.add(any())).thenReturn(quizWithId);

        mvc.perform(post("/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(quizWithoutId)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(quizWithId)))
                .andReturn();
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
