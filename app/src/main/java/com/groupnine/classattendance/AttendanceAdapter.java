package com.groupnine.classattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    public AttendanceAdapter(Context context, List<User> users) {
        mContext = context;
        mUsers = users;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_students, parent, false);
        return new AttendanceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        User user = mUsers.get(position);

        holder.student_name.setText(user.getUserFirstName() + " " + user.getUserLastName());
        holder.student_reg_number.setText(user.getUserName());
        displayImage(holder);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder {

        ImageView student_image;
        TextView student_name, image_letter;
        TextView student_reg_number;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            student_image = itemView.findViewById(R.id.student_image);
            student_name = itemView.findViewById(R.id.student_name);
            image_letter = itemView.findViewById(R.id.image_letter);
            student_reg_number = itemView.findViewById(R.id.student_reg_number);
        }
    }

    private void displayImage(AttendanceViewHolder holder) {

        Tools.displayImageRound(mContext, holder.student_image, R.drawable.photo_male_7);
        holder.student_image.setColorFilter(null);
        holder.image_letter.setVisibility(View.GONE);

    }

    public void updateList(List<User> newList) {
        mUsers = new ArrayList<>();
        mUsers = newList;
        notifyDataSetChanged();
    }
}
