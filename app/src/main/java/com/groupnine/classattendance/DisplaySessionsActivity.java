package com.groupnine.classattendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplaySessionsActivity extends AppCompatActivity implements SessionsAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "DisplaySessionsActivity";
    public static final String SESSIONS_TYPE = "type";
    public static final String SESSIONS_ALL = "all";
    public static final String SESSIONS_CLOSED = "closed";
    public static final String SESSIONS_ACTIVE = "active";

    private FloatingActionButton fab;
    private RecyclerView mRecyclerView;
    private SessionsAdapter mAdapter;
    private List<Session> mSessions = null;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;

    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sessions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRefreshLayout = findViewById(R.id.swipeContainer);
        mRefreshLayout.setOnRefreshListener(this);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplaySessionsActivity.this, AddSessionActivity.class));
                finish();
            }
        });
        mRecyclerView = findViewById(R.id.rc_sessions);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        readSessions();


    }

    private void readSessions() {

        User user = SharedPrefs.getInstance(this).getUser();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<Session>> call = null;
        Bundle args = getIntent().getExtras();
        if (args != null) {
            String category = args.getString(SESSIONS_TYPE);
            switch (category) {
                case SESSIONS_ALL:
                    call = apiInterface.findAllSessionsByLec(user.getUserId());
                    break;
                case SESSIONS_ACTIVE:
                    call = apiInterface.findActiveSessionsForLec(user.getUserId());
                    break;
                case SESSIONS_CLOSED:
                    call = apiInterface.findAllClosedSessionsByLec(user.getUserId());
                    break;
            }
        }

        call.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                Log.d(TAG, "onResponse: Success!");
                mSessions = response.body();
                mAdapter = new SessionsAdapter(mSessions, DisplaySessionsActivity.this, DisplaySessionsActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(DisplaySessionsActivity.this));
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed! " + t.getMessage());
            }
        });
    }

    @Override
    public void closeSession(Session session) {
//        showBottomSheetDialog(session);
        showAttendance(session);
    }

    private void showAttendance(Session session) {
        Intent intent = new Intent(this, AttendanceActivity.class);
        intent.putExtra(AttendanceActivity.SESSION_ID, session.getSessionId());
        intent.putExtra(AttendanceActivity.SESSION_NAME, session.getUnitCode());
        startActivity(intent);
    }

    private void showBottomSheetDialog(final Session session) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_basic, null);
        ((TextView) view.findViewById(R.id.btm_sheet_sess_code)).setText(session.getUnitCode());
        ((TextView) view.findViewById(R.id.btm_sheet_sess_title)).setText(session.getUnitTitle());
        (view.findViewById(R.id.btm_sheet_cancel_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });

        Button closeSessBtn = view.findViewById(R.id.btm_sheet_close_sess_btn);

        closeSessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSessionOnDb(session);
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    private void closeSessionOnDb(Session session) {
        User user = SharedPrefs.getInstance(this).getUser();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Session> call = apiInterface.closeSession(session.getSessionId());
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
                    updateAdapterList();
                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed!" + t.getMessage());
            }
        });

    }

    private void updateAdapterList() {
        User user = SharedPrefs.getInstance(this).getUser();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Session>> call = null;

        Bundle args = getIntent().getExtras();
        if (args != null) {
            String category = args.getString(SESSIONS_TYPE);
            switch (category) {
                case SESSIONS_ALL:
                    call = apiInterface.findAllSessionsByLec(user.getUserId());
                    break;
                case SESSIONS_ACTIVE:
                    call = apiInterface.findActiveSessionsForLec(user.getUserId());
                    break;
                case SESSIONS_CLOSED:
                    call = apiInterface.findAllClosedSessionsByLec(user.getUserId());
                    break;
            }
        }

        call.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                Log.d(TAG, "onResponse: Success!");
                List<Session> newList = response.body();
                mAdapter.updateList(newList);
                mBottomSheetDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed! " + t.getMessage());
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

        Call<List<Session>> call = null;
        Bundle args = getIntent().getExtras();
        if (args != null) {
            String category = args.getString(SESSIONS_TYPE);
            switch (category) {
                case SESSIONS_ALL:
                    call = apiInterface.findAllSessionsByLec(user.getUserId());
                    break;
                case SESSIONS_ACTIVE:
                    call = apiInterface.findActiveSessionsForLec(user.getUserId());
                    break;
                case SESSIONS_CLOSED:
                    call = apiInterface.findAllClosedSessionsByLec(user.getUserId());
                    break;
            }
        }

        call.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                Log.d(TAG, "onResponse: Success!");
                final List<Session> newList = response.body();

                mAdapter.updateList(newList);
                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                Log.d(TAG, "onFailure: Failed! " + t.getMessage());
            }
        });
    }
}
