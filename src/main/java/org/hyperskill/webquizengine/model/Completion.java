package org.hyperskill.webquizengine.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "completion", indexes = {
        @Index(name = "IDX_COMPLETED_AT", columnList = "completedAt")
})
public class Completion {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime completedAt = LocalDateTime.now();

    public static Completion createCompletion(User user, Quiz quiz) {
        var completion = new Completion();
        completion.setUser(user);
        completion.setQuiz(quiz);
        return completion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public String toString() {
        return "Completion{" +
                "id=" + id +
                ", user=" + user +
                ", quiz=" + quiz +
                ", completedAt=" + completedAt +
                '}';
    }
}
