package org.hyperskill.webquizengine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Incorrect answer options")
public class InvalidAnswerOptions extends RuntimeException { }
