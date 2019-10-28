package org.hyperskill.webquizengine.controller;

import org.hyperskill.webquizengine.model.User;
import org.hyperskill.webquizengine.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final BCryptPasswordEncoder encoder;
    private final UserRepository repository;

    @Autowired
    public AuthController(BCryptPasswordEncoder encoder, UserRepository repository) {
        this.encoder = encoder;
        this.repository = repository;
    }

    @PostMapping(path = "/register", consumes = APPLICATION_JSON_VALUE)
    public void register(@RequestBody User user) {
        logger.info("A new user '{}' wants to register", user.getUsername());
        var encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        repository.save(user);
    }
}
