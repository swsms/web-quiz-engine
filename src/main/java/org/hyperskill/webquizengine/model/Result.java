package org.hyperskill.webquizengine.model;

public class Result {
    private boolean success;
    private String feedback;

    public Result(boolean success, String feedback) {
        this.success = success;
        this.feedback = feedback;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public static Result success() {
        var feedback = "Congratulations, you're right!";
        return new Result(true, feedback);
    }

    public static Result failure() {
        var feedback = "Wrong answer! Please, try again.";
        return new Result(false, feedback);
    }
}

