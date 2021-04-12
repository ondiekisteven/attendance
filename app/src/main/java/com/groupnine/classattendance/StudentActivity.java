package com.groupnine.classattendance;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentActivity extends AppCompatActivity implements ActiveSessionsAdapter.OnConnectSessionListener,
        SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "StudentActivity";

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<Session> mSessions;
    private ActiveSessionsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRefreshLayout = findViewById(R.id.swipeContainer);
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = findViewById(R.id.rc_student);

        readActiveSessions();

    }

    private void readActiveSessions() {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Session>> call = apiInterface.findAllActiveSessions();
        call.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                Log.d(TAG, "onResponse: Success!");
                mSessions = response.body();
                mAdapter = new ActiveSessionsAdapter(StudentActivity.this, mSessions, StudentActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(StudentActivity.this));
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed! " + t.getMessage());
            }
        });
    }

    @Override
    public void onConnectSession(final Session session) {
        User currentUser = SharedPrefs.getInstance(this).getUser();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Session> call = apiInterface.registerAttendance(session.getSessionId(), currentUser.getUserId());
        call.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                Log.d(TAG, "onResponse: Attendance marked succesfully");
                Session session1 = response.body();
                if (session1.isFeedbackError()) {
                    // error occurred
                    showErrorDialog(session1.getFeedbackMessage());

                } else {
                    // no error
                    showSuccessDialog(session1.getFeedbackMessage());
                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to mark attendance");
            }
        });

    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    private void refreshList() {
        User user = SharedPrefs.getInstance(this).getUser();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Session>> call = apiInterface.findAllActiveSessions();
        call.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                Log.d(TAG, "onResponse: Success!");
                final List<Session> newList = response.body();
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.updateList(newList);
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 3000);

            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed! " + t.getMessage());
            }
        });
    }

    private void showErrorDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        ((TextView) dialog.findViewById(R.id.content)).setText(message);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showSuccessDialog(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_info);
        ((TextView) dialog.findViewById(R.id.content)).setText(message);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                logoutUser();
                break;
        }

        return true;
    }

    private void logoutUser() {
        SharedPrefs.getInstance(this).deleteUser();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
