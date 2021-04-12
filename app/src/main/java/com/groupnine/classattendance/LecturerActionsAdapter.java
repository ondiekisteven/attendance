package com.groupnine.classattendance;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LecturerActionsAdapter extends RecyclerView.Adapter<LecturerActionsAdapter.ActionViewholder> {
    private static final String TAG = "LecturerActionsAdapter";

    private Context mContext;
    private List<LecAction> mActions;
    private OnActionClickedListener mListener;

    public interface OnActionClickedListener {
        void onActionClick(int action);
    }

    public LecturerActionsAdapter(Context context, List<LecAction> actions, OnActionClickedListener listener) {
        mContext = context;
        mActions = actions;
        mListener = listener;
    }

    @NonNull
    @Override
    public ActionViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_admin_action, parent, false);
        return new ActionViewholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ActionViewholder holder, final int position) {
        LecAction action = mActions.get(position);

        holder.itm_adm_act_image.setImageResource(action.getImage());
        holder.itm_adm_act_title.setText(action.getTitle());

        Log.d(TAG, "onBindViewHolder: item number: " + position);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: CARD NUMBER " + position);
                mListener.onActionClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mActions.size();
    }

    class ActionViewholder extends RecyclerView.ViewHolder {

        ImageView itm_adm_act_image;
        TextView itm_adm_act_title;
        View item;

        ActionViewholder(@NonNull View itemView) {
            super(itemView);
            itm_adm_act_image = itemView.findViewById(R.id.itm_adm_act_image);
            itm_adm_act_title = itemView.findViewById(R.id.itm_adm_act_title);
            item = itemView.findViewById(R.id.itm_adm);

        }
    }
}
