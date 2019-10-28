package org.hyperskill.webquizengine.repository;

import org.hyperskill.webquizengine.model.Quiz;
import org.springframework.data.repository.CrudRepository;

public interface QuizRepository extends CrudRepository<Quiz, Long> { }
