package com.groupnine.classattendance;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Session {

    @SerializedName("session_id") private int sessionId;
    @SerializedName("session_start") private String sessionStart;
    @SerializedName("session_end") private String sessionEnd;
    @SerializedName("unit_code") private String unitCode;
    @SerializedName("unit_title") private String unitTitle;
    @SerializedName("venue_name") private String venueName;
    @SerializedName("session_status") private int sessionStatus;
    @SerializedName("user_id") private int userId;
    @SerializedName("att_count") private int attCount;
    @SerializedName("session_date") private Date sessionDate;
    @SerializedName("feedback_error") private boolean feedbackError;
    @SerializedName("feedback_message") private String feedbackMessage;

    public int getSessionId() {
        return sessionId;
    }

    public String getSessionStart() {
        return sessionStart;
    }

    public String getSessionEnd() {
        return sessionEnd;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public String getUnitTitle() {
        return unitTitle;
    }

    public String getVenueName() {
        return venueName;
    }

    public int getSessionStatus() {
        return sessionStatus;
    }

    public int getUserId() {
        return userId;
    }

    public int getAttCount() {
        return attCount;
    }

    public Date getSessionDate() {
        return sessionDate;
    }

    public boolean isFeedbackError() {
        return feedbackError;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }
}
