package com.groupnine.classattendance;

import android.app.Dialog;
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

public class AttendanceActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "AttendanceActivity";
    public static final String SESSION_ID = "session_id";
    public static final String SESSION_NAME = "session_name";

    private RecyclerView mRecyclerView;
    private AttendanceAdapter mAdapter;
    private List<User> mUsers = null;

    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            String name = args.getString(SESSION_NAME);
            getSupportActionBar().setTitle(name + " Attendance");
        } else {
            finish();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRefreshLayout = findViewById(R.id.swipeContainer);
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = findViewById(R.id.rc_attendance);

        getStudentAttendants();
    }

    private void getStudentAttendants() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<User>> call = null;
        Bundle args = getIntent().getExtras();
        if (args != null) {
            int id = args.getInt(SESSION_ID);
            call = apiInterface.getStudentAttendance(id);
        } else {
            finish();
        }

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d(TAG, "onResponse: Success!");
                mUsers = response.body();
                mAdapter = new AttendanceAdapter( AttendanceActivity.this, mUsers);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(AttendanceActivity.this));
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed! " + t.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        updateList();
    }

    private void updateList() {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<User>> call = null;

        Bundle args = getIntent().getExtras();
        if (args != null) {
            int id = args.getInt(SESSION_ID);
            call = apiInterface.getStudentAttendance(id);
        } else {
            finish();
        }

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d(TAG, "onResponse: Success!");
                List<User> newList = response.body();
                mAdapter.updateList(newList);
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed! " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_close_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_close_session:
                int session_id = -1;
                Bundle args = getIntent().getExtras();
                if (args != null) {
                    session_id = args.getInt(SESSION_ID);
                } else {
                    finish();
                }
                if (session_id != -1) {
                    closeSessionOnDb(session_id);
                } else {
                    finish();
                }

                break;
        }

        return true;
    }

    private void closeSessionOnDb(int session) {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Session> call = apiInterface.closeSession(session);
        call.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                Log.d(TAG, "onResponse: Success!");
                Session session1 = response.body();
                if (session1.isFeedbackError()) {
                    //do nothing
                    Log.d(TAG, "onResponse: Error updating");
                } else {
                    //success
                    showSuccessDialog("Session Closed Successfully!");
                    updateList();

                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed!" + t.getMessage());
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
}
