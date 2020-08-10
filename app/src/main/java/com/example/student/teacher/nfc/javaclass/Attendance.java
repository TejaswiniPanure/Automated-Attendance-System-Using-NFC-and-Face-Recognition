package com.example.student.teacher.nfc.javaclass;

/**
 * Created by opulent on 5/1/17.
 */

public class Attendance {
    String date, attendance;
    public Attendance(){}
    public Attendance(String date, String attendance) {
        this.date = date;
        this.attendance = attendance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}
