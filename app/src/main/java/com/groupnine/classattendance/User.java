package com.groupnine.classattendance;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id") private int userId;
    @SerializedName("user_first_name") private String userFirstName;
    @SerializedName("user_last_name") private String userLastName;
    @SerializedName("user_name") private String userName;
    @SerializedName("role_id") private int userRole;
    @SerializedName("user_password") private String userPassword;
    @SerializedName("feedback_error") private boolean feedbackError;
    @SerializedName("feedback_message") private String feedbackMessage;

    public User(int userId, String userFirstName, String userLastName, String userName, int userRole, String userPassword) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userName = userName;
        this.userRole = userRole;
        this.userPassword = userPassword;
    }

    public User(String userFirstName, String userLastName, String userName, int userRole, String userPassword) {
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userName = userName;
        this.userRole = userRole;
        this.userPassword = userPassword;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getUserName() {
        return userName;
    }

    public int getUserRole() {
        return userRole;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public boolean isFeedbackError() {
        return feedbackError;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }
}
