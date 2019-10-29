package org.hyperskill.webquizengine.repository;

import org.hyperskill.webquizengine.model.Quiz;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface QuizRepository extends PagingAndSortingRepository<Quiz, Long> { }
