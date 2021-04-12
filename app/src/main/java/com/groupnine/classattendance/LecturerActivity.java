package com.groupnine.classattendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LecturerActivity extends AppCompatActivity implements LecturerActionsAdapter.OnActionClickedListener {
    private static final String TAG = "LecturerActivity";

    private RecyclerView recyclerView;
    private LecturerActionsAdapter mAdapter;
    private TextView lec_panel_header;
    private User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lec_panel_header = findViewById(R.id.lec_panel_header);
        current_user = SharedPrefs.getInstance(this).getUser();
        if (current_user == null) {
            finish();
        } else {
            lec_panel_header.setText("Welcome to Admin Panel " + current_user.getUserFirstName() + ", " +
                    "You can perfom the operations below");
        }

        init_component();

    }

    private void init_component() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        List<LecAction> actions = getActions();

        //set data and list adapter
        mAdapter = new LecturerActionsAdapter(this, actions, this);
        recyclerView.setAdapter(mAdapter);

    }

    private List<LecAction> getActions() {
        List<LecAction> items = new ArrayList<>();
        items.add(new LecAction(R.drawable.ic_event, "Add Session"));
        items.add(new LecAction(R.drawable.ic_event, "Active Sessions"));
        items.add(new LecAction(R.drawable.ic_event, "Closed Sessions"));
        items.add(new LecAction(R.drawable.ic_event, "All Session"));

        return items;
    }

    @Override
    public void onActionClick(int action) {
        Log.d(TAG, "onActionClick: CLICKED!!");
        List<LecAction> items = getActions();
        Intent intent = null;
        switch (action) {
            case 0:
                //add new session
                Log.d(TAG, "onActionClick: action: " + action + "CLICKED");
                intent = new Intent(this, AddSessionActivity.class);
                break;
            case 1:
                //view active sessions
                intent = new Intent(this, DisplaySessionsActivity.class);
                intent.putExtra(DisplaySessionsActivity.SESSIONS_TYPE, DisplaySessionsActivity.SESSIONS_ACTIVE);
                break;
            case 2:
                //view closed sessions
                intent = new Intent(this, DisplaySessionsActivity.class);
                intent.putExtra(DisplaySessionsActivity.SESSIONS_TYPE, DisplaySessionsActivity.SESSIONS_CLOSED);
                break;
            case 3:
                //view all sessions
                intent = new Intent(this, DisplaySessionsActivity.class);
                intent.putExtra(DisplaySessionsActivity.SESSIONS_TYPE, DisplaySessionsActivity.SESSIONS_ALL);
                break;
        }
        startActivity(intent);
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
