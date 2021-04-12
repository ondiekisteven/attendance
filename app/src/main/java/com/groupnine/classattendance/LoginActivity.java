package com.groupnine.classattendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private Button lgn_btn;
    private ProgressBar lgn_progress_bar;
    private TextInputEditText lgn_username;
    private TextInputEditText lgn_password;
    private TextView lgn_reg_acc;
    List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        users = new ArrayList<>();

        lgn_btn = findViewById(R.id.lgn_btn);
        lgn_progress_bar = findViewById(R.id.lgn_progress_bar);
        lgn_username = findViewById(R.id.lgn_username);
        lgn_password = findViewById(R.id.lgn_password);
        lgn_reg_acc = findViewById(R.id.lgn_reg_acc);

        lgn_reg_acc.setOnClickListener(this);
        lgn_btn.setOnClickListener(this);

        getAllSessions();

    }

    private void getAllSessions() {
        User user = SharedPrefs.getInstance(this).getUser();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Session>> call = apiInterface.findAllSessionsByLec(1);
        call.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                Log.d(TAG, "onResponse: YAAAY !!! We are receiving response");

                List<Session> sessions = response.body();
                for (Session session: sessions) {
                    Log.d(TAG, "onResponse: session DATE: " + session.getSessionDate());
                }

            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to load list: " + t.getMessage());
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.lgn_btn:
                Log.d(TAG, "onClick: Login button clicked");
                showProgressBar();
                login();
                break;

            case R.id.lgn_reg_acc:
                Log.d(TAG, "onClick: Reg TextView clicked");
                break;

            default:
                //do nothing
        }
    }

    private void showProgressBar() {
        lgn_btn.setVisibility(View.GONE);
        lgn_progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        lgn_btn.setVisibility(View.VISIBLE);
        lgn_progress_bar.setVisibility(View.GONE);
    }

    private void login() {
        final String username = lgn_username.getText().toString().trim();
        String password = lgn_password.getText().toString().trim();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<User> call = apiInterface.login(username, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "onResponse: YAAAY !!! We are receiving a user");

                User user = response.body();
                if (user.isFeedbackError()) {
                    hideProgressBar();
                    Log.e(TAG, "onResponse: " + user.getFeedbackMessage().toUpperCase());
                } else  {
                    /*Log.d(TAG, "onResponse: \n user_id: " + user.getUserId() + "\n" +
                                                   "user_first_name: " + user.getUserFirstName() + "\n" +
                                                   "user_last_name: " + user.getUserLastName() + "\n" +
                                                   "user_name: " + user.getUserName() + "\n" +
                                                   "user_password: " + user.getUserPassword());*/
                    SharedPrefs.getInstance(LoginActivity.this).saveUser(user);
                    Intent intent = null;
                    switch (user.getUserRole()) {
                        case 1:
                            intent = new Intent(LoginActivity.this, LecturerActivity.class);
                            break;
                        case 2:
                            //do nothing
                            intent = new Intent(LoginActivity.this, StudentActivity.class);
                            break;
                    }
                    if (intent != null) {
                        startActivity(intent);
                        finish();
                    } else {
                        //do nothing
                    }
                    hideProgressBar();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                hideProgressBar();
                Log.e(TAG, "onFailure: FAILED!" + t.getLocalizedMessage());
            }
        });
    }

    public void getUsers() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<User>> call = apiInterface.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d(TAG, "onResponse: YAAAY !!! We are receiving response");
                users = response.body();
                for (int i=0; i<users.size(); i++) {
                    Log.d(TAG, "onClick: user: " + users.get(i).getUserId() + " username: " + users.get(i).getUserName());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e(TAG, "onFailure: FAILED!" + t.getLocalizedMessage());
            }
        });
    }
}
