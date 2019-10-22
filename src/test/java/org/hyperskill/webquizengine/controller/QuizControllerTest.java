package org.hyperskill.webquizengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperskill.webquizengine.model.Quiz;
import org.hyperskill.webquizengine.model.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizController.class)
public class QuizControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testGetFakeQuiz() throws Exception {
        var mvcResult = mvc.perform(get("/quizzes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var body = mvcResult.getResponse().getContentAsString();
        var quiz = mapper.readValue(body, Quiz.class);

        assertNotNull(quiz.getTitle());
        assertNotNull(quiz.getText());
        assertNotNull(quiz.getOptions());
        assertTrue(quiz.getOptions().size() >= 4);
    }

    @Test
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
