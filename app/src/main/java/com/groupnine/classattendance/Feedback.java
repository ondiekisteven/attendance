package com.groupnine.classattendance;

import com.google.gson.annotations.SerializedName;

public class Feedback {
    @SerializedName("feedback_error") private boolean feedbackError;
    @SerializedName("feedback_message") private String feedbackMessage;

    public boolean isFeedbackError() {
        return feedbackError;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }
}
