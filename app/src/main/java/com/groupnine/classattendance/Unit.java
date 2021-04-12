package com.groupnine.classattendance;

import com.google.gson.annotations.SerializedName;

public class Unit {
    @SerializedName("unit_id") private int unitId;
    @SerializedName("unit_title") private String unitTitle;
    @SerializedName("unit_code") private String unitCode;
    @SerializedName("user_id") private int userId;
    @SerializedName("feedback_error") private boolean feedbackError;
    @SerializedName("feedback_message") private String feedbackMessage;

    public int getUnitId() {
        return unitId;
    }

    public String getUnitTitle() {
        return unitTitle;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isFeedbackError() {
        return feedbackError;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }
}
