package org.hyperskill.webquizengine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperskill.webquizengine.dto.UserDto;
import org.hyperskill.webquizengine.exception.DuplicateEmailException;
import org.hyperskill.webquizengine.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hyperskill.webquizengine.testutils.TestUtils.DEFAULT_PASSWORD;
import static org.hyperskill.webquizengine.testutils.TestUtils.DEFAULT_USERNAME;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService service;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void testRegister_whenSuccessful() throws Exception {
        var userDto = new UserDto();
        userDto.setEmail(DEFAULT_USERNAME);
        userDto.setPassword(DEFAULT_PASSWORD);

        when(service.registerNewUser(anyString(), anyString())).thenReturn(10L);

        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testRegister_whenPasswordTooShort() throws Exception {
        var userDto = new UserDto();
        userDto.setEmail(DEFAULT_USERNAME);
        userDto.setPassword("123");

        when(service.registerNewUser(anyString(), anyString())).thenReturn(10L);

        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegister_whenUsernameIsNotEmail() throws Exception {
        var userDto = new UserDto();
        userDto.setEmail("abcde");
        userDto.setPassword(DEFAULT_PASSWORD);

        when(service.registerNewUser(anyString(), anyString())).thenReturn(10L);

        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegister_whenDuplicateEmail() throws Exception {
        var userDto = new UserDto();
        userDto.setEmail(DEFAULT_USERNAME);
        userDto.setPassword(DEFAULT_PASSWORD);

        when(service.registerNewUser(anyString(), anyString()))
                .thenThrow(DuplicateEmailException.class);

        mvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

}
