package com.groupnine.classattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ActiveSessionsAdapter extends RecyclerView.Adapter<ActiveSessionsAdapter.ActiveViewHolder> {
    private static final String TAG = "ActiveSessionsAdapter";

    private Context mContext;
    private List<Session> mSessions;
    private OnConnectSessionListener mListener;

    public interface OnConnectSessionListener {
        void onConnectSession(Session session);
    }

    public ActiveSessionsAdapter(Context context, List<Session> sessions, OnConnectSessionListener listener) {
        mContext = context;
        mSessions = sessions;
        mListener = listener;
    }

    @NonNull
    @Override
    public ActiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.active_sessions, parent, false);
        return new ActiveViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveViewHolder holder, int position) {
        final Session session = mSessions.get(position);

        holder.active_sess_code.setText(session.getUnitCode());
        holder.active_sess_title.setText(session.getUnitTitle());

        holder.active_sess_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onConnectSession(session);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSessions.size();
    }

    public class ActiveViewHolder extends RecyclerView.ViewHolder {

        TextView active_sess_code;
        TextView active_sess_title;
        Button active_sess_btn;

        public ActiveViewHolder(@NonNull View itemView) {
            super(itemView);
            active_sess_code = itemView.findViewById(R.id.active_sess_code);
            active_sess_title = itemView.findViewById(R.id.active_sess_title);
            active_sess_btn = itemView.findViewById(R.id.active_sess_btn);
        }
    }

    public void updateList(List<Session> newList) {
        mSessions = new ArrayList<>();
        mSessions = newList;
        notifyDataSetChanged();
    }
}
