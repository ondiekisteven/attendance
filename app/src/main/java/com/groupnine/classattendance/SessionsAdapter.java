package com.groupnine.classattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.SessionItem> {
    private List<Session> mSessions;
    private Context mContext;
    private OnItemClickListener mListener;
    private DateFormat mDateFormat;

    public interface OnItemClickListener {
        void closeSession(Session session);
    }

    public SessionsAdapter(List<Session> sessions, Context context, OnItemClickListener listener) {
        mSessions = sessions;
        mContext = context;
        mListener = listener;
        mDateFormat = new SimpleDateFormat("dd MMM yyyy");
    }

    @NonNull
    @Override
    public SessionItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new SessionItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionItem holder, int position) {

        final Session session = mSessions.get(position);

        holder.item_sess_img_bg.setImageResource(R.drawable.bg_success);
        holder.item_sess_code.setText(session.getUnitCode());
        holder.item_sess_period.setText(session.getUnitTitle());
        holder.item_sess_unit_title.setText(session.getAttCount() + " students");
        holder.item_sess_date.setText(mDateFormat.format(session.getSessionDate()));
        if (session.getSessionStatus() > 0) {
            holder.item_sess_status.setTextColor(mContext.getResources().getColor(R.color.green_A400));
            holder.item_sess_status.setText("active");
        } else {
            holder.item_sess_status.setTextColor(mContext.getResources().getColor(R.color.red_A400));
            holder.item_sess_status.setText("closed");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.closeSession(session);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSessions.size();
    }

    public class SessionItem extends RecyclerView.ViewHolder {
        TextView item_sess_code;
        TextView item_sess_period;
        TextView item_sess_unit_title;
        TextView item_sess_date;
        ImageView item_sess_img_bg;
//        ImageView item_sess_img_sign;
        TextView item_sess_status;
        View card;

        public SessionItem(@NonNull View itemView) {
            super(itemView);
            item_sess_code = itemView.findViewById(R.id.item_sess_code);
            item_sess_period = itemView.findViewById(R.id.item_sess_period);
            item_sess_unit_title = itemView.findViewById(R.id.item_sess_unit_title);
            item_sess_date = itemView.findViewById(R.id.item_sess_date);
            item_sess_img_bg = itemView.findViewById(R.id.item_sess_img_bg);
//            item_sess_img_sign = itemView.findViewById(R.id.item_sess_img_sign);
            item_sess_status = itemView.findViewById(R.id.item_sess_status);
            card = itemView;
        }
    }

    public void updateList(List<Session> newList) {
        mSessions = new ArrayList<>();
        mSessions = newList;
        notifyDataSetChanged();
    }
}
