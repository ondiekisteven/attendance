package com.groupnine.classattendance;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    public static final String SHARED_PREFS = "shares_pref";
    public static final String USER_ID = "id";
    public static final String USER_F_NAME = "f_name";
    public static final String USER_L_NAME = "l_name";
    public static final String USER_NAME = "u_name";
    public static final String USER_ROLE = "u_role";
    public static final String USER_PASSWORD = "u_pwrd";

    private SharedPreferences instance;

    public SharedPrefs(Context context) {

        instance = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

    }

    public static SharedPrefs getInstance(Context context) {

        return new SharedPrefs(context);
    }

    public void saveUser(User user) {
        instance.edit().putInt(USER_ID, user.getUserId()).apply();
        instance.edit().putString(USER_F_NAME, user.getUserFirstName()).apply();
        instance.edit().putString(USER_L_NAME, user.getUserLastName()).apply();
        instance.edit().putString(USER_NAME, user.getUserName()).apply();
        instance.edit().putInt(USER_ROLE, user.getUserRole()).apply();
        instance.edit().putString(USER_PASSWORD, user.getUserPassword()).apply();

    }

    public User getUser() {
        return new User(
                instance.getInt(USER_ID, -1),
                instance.getString(USER_F_NAME, null),
                instance.getString(USER_L_NAME, null),
                instance.getString(USER_NAME, null),
                instance.getInt(USER_ROLE, -1),
                instance.getString(USER_PASSWORD, null)
        );
    }

    public void deleteUser() {

        instance.edit().remove(USER_ID).apply();
        instance.edit().remove(USER_F_NAME).apply();
        instance.edit().remove(USER_L_NAME).apply();
        instance.edit().remove(USER_NAME).apply();
        instance.edit().remove(USER_ROLE).apply();
        instance.edit().remove(USER_PASSWORD).apply();

    }



}
