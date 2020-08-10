package com.example.student.teacher.nfc.javaclass;

import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.student.teacher.nfc.R;

import java.util.List;

/**
 * Created by opulent on 5/1/17.
 */


public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder> {

    private List<Attendance> attendanceList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, attendance;

        public MyViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            attendance = (TextView) view.findViewById(R.id.attendance);
        }
    }


    public AttendanceAdapter(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Attendance attendance = attendanceList.get(position);
        holder.date.setText(attendance.getDate());
        holder.attendance.setText(attendance.getAttendance());
    }

    @Override
    public int getItemCount() {
        return attendanceList.size();
    }
}