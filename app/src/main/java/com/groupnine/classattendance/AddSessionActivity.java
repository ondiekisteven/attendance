package com.groupnine.classattendance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSessionActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddSessionActivity";

    private EditText add_sess_start_edtxt;
    private EditText add_sess_end_edtxt;
    private EditText add_sess_code_edtxt;
    private EditText add_sess_venue_edtxt;
    private Button add_sess_submit_btn;
    private ProgressBar add_sess_progress_bar;

    private String start_date = null;
    private String end_date = null;
    private String unit_picked = null;
    private String venue_picked = null;

    private List<Unit> units = null;
    private List<Session> active_sessions = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        add_sess_start_edtxt = findViewById(R.id.add_sess_start_edtxt);
        add_sess_end_edtxt = findViewById(R.id.add_sess_end_edtxt);
        add_sess_code_edtxt = findViewById(R.id.add_sess_code_edtxt);
        add_sess_venue_edtxt = findViewById(R.id.add_sess_venue_edtxt);
        add_sess_submit_btn = findViewById(R.id.add_sess_submit_btn);
        add_sess_progress_bar = findViewById(R.id.add_sess_progress_bar);

        add_sess_submit_btn.setOnClickListener(this);
        add_sess_start_edtxt.setOnClickListener(this);
        add_sess_end_edtxt.setOnClickListener(this);
        add_sess_code_edtxt.setOnClickListener(this);
        add_sess_venue_edtxt.setOnClickListener(this);

        getUnitsForLec();
    }

    private void getUnitsForLec() {
        User user = SharedPrefs.getInstance(this).getUser();
        int user_id = user.getUserId();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Unit>> call = apiInterface.getUnitsByLec(user_id);
        call.enqueue(new Callback<List<Unit>>() {
            @Override
            public void onResponse(Call<List<Unit>> call, Response<List<Unit>> response) {
                units = response.body();
                for (Unit unit : units) {
                    Log.d(TAG, "onResponse: Unit Code: " + unit.getUnitCode());
                }
                if (units.isEmpty()) {
                    showErrorDialog("You have no Units to teach this class. Please Go home.");
                }
            }

            @Override
            public void onFailure(Call<List<Unit>> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed! " + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_sess_start_edtxt:
                showStartTimeDialog(v);
                break;
            case R.id.add_sess_end_edtxt:
                showEndTimeDialog(v);
                break;
            case R.id.add_sess_code_edtxt:
                showUnitDialog(v);
                break;
            case R.id.add_sess_venue_edtxt:
                showVenueDialog(v);
                break;
            case R.id.add_sess_submit_btn:
                showProgressBar();
                saveSession();
                break;
        }
    }

    private void showStartTimeDialog(final View v) {
        final String[] array = new String[] {
                "07 A.M", "09 A.M", "11 A.M", "01 P.M", "03 P.M", "05 P.M", "07 P.M"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Start Time");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                start_date = array[i];
                ((EditText) v).setText(start_date);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void showEndTimeDialog(final View v) {
        final String[] array = new String[] {
                "07 A.M", "09 A.M", "11 A.M", "01 P.M", "03 P.M", "05 P.M", "07 P.M"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("End Time");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                end_date = array[i];
                ((EditText) v).setText(end_date);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void showUnitDialog(final View v) {
        final List<String> codes = new ArrayList<>();
        if (!units.isEmpty()) {
            for (Unit unit : units) {
                codes.add(unit.getUnitCode());
            }

        } else {
            showErrorDialog("No Units Found! ");
            return;
        }

        for (int i=0; i<codes.size(); i++) {
            Log.d(TAG, "showUnitDialog: UNIT CODE " + codes.get(i));
        }

        final String[] array = codes.toArray(new String[codes.size()]);
        if (codes.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Course Code");
            builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    unit_picked = array[i];
                    ((EditText) v).setText(unit_picked);
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        } else {
            showErrorDialog("No Units Found!");
        }

    }

    private void showVenueDialog(final View v) {
        final String[] array = new String[] {
                "LAB 1", "LAB 2", "LAB 3", "LAB 4"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Venue");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                venue_picked = array[i];
                ((EditText) v).setText(venue_picked);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void saveSession() {

        String sess_code = add_sess_code_edtxt.getText().toString();
        String sess_venue = add_sess_venue_edtxt.getText().toString();

        findActiveSessions();

        if (sess_code.isEmpty() || sess_venue.isEmpty() || (start_date == null) || (end_date == null)) {
            hideProgressBar();
            showErrorDialog("Some Fields are empty!");
        } else {
            User user = SharedPrefs.getInstance(this).getUser();
            int user_id = user.getUserId();
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<Session> call = apiInterface.createSession(start_date, end_date, sess_code, sess_venue, 1, user_id);
            call.enqueue(new Callback<Session>() {
                @Override
                public void onResponse(Call<Session> call, Response<Session> response) {
                    Session session = response.body();
                    if (session.isFeedbackError()) {
                        hideProgressBar();
                        Log.e(TAG, "onResponse: Failed! " + session.getFeedbackMessage());
                        showErrorDialog(session.getFeedbackMessage());
                    } else {
                        hideProgressBar();
                        Log.d(TAG, "onResponse: Successful! " + session.getFeedbackMessage());
                        showSuccessDialog(session.getFeedbackMessage());
                    }
                }

                @Override
                public void onFailure(Call<Session> call, Throwable t) {
                    hideProgressBar();
                    showErrorDialog(t.getMessage());
                    Log.e(TAG, "onFailure: Failed !" + t.getMessage());
                }
            });
        }


    }

    private void findActiveSessions() {
        User user = SharedPrefs.getInstance(this).getUser();
        int user_id = user.getUserId();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Session>> call = apiInterface.findActiveSessionsForLec(user_id);
        call.enqueue(new Callback<List<Session>>() {
            @Override
            public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
                Log.d(TAG, "onResponse: Success fetching sessions");
                active_sessions = response.body();

            }

            @Override
            public void onFailure(Call<List<Session>> call, Throwable t) {
                Log.d(TAG, "onFailure: Error fetching active sessions: " + t.getMessage());
            }
        });
    }

    private void showProgressBar() {
        add_sess_submit_btn.setVisibility(View.GONE);
        add_sess_progress_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        add_sess_submit_btn.setVisibility(View.VISIBLE);
        add_sess_progress_bar.setVisibility(View.GONE);
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
