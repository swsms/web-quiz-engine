package org.hyperskill.webquizengine.utils;

import org.hyperskill.webquizengine.exception.InvalidAnswerOptions;
import org.hyperskill.webquizengine.model.Quiz;

public final class Utils {

    private Utils() { }

    public static void checkAnswerOptions(Quiz quiz) {
        int numberOfOptionsInQuiz = quiz.getOptions().size();
        quiz.getAnswer().forEach(answerOptionIdx -> {
            if (answerOptionIdx < 0 || answerOptionIdx >= numberOfOptionsInQuiz) {
                throw new InvalidAnswerOptions();
            }
        });
    }
}
