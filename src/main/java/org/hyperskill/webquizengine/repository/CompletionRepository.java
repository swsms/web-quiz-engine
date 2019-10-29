package org.hyperskill.webquizengine.repository;

import org.hyperskill.webquizengine.model.Completion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletionRepository extends CrudRepository<Completion, Long> {

    @Query("SELECT c FROM Completion c where c.user.username = :username order by c.completedAt desc")
    Page<Completion> findAllByUserOrderByCompletedAtDesc(String username, Pageable pageable);
}
