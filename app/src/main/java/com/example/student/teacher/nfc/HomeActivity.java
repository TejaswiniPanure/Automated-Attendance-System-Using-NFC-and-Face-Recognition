package com.example.student.teacher.nfc;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnFragmentInteractionListener {
        boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
        if(sp.getString("user","student").equals("teacher")){
            navigationView.inflateMenu(R.menu.activity_home_teacher_drawer);

        }else if(sp.getString("user","student").equals("student")){
            navigationView.inflateMenu(R.menu.activity_home_student_drawer);
        }else{
            navigationView.inflateMenu(R.menu.activity_home_parent_drawer);

        }
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(flag){
                super.onBackPressed();
                Intent i=new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
            flag=true;
            Toast.makeText(this, "Press twice to exit", Toast.LENGTH_LONG).show();        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fr;
        fr=new ComingSoonFragment();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            fr=new HomeFragment();
        } else if (id == R.id.nav_marks) {
            fr=new MarksFragment();

        } else if (id == R.id.nav_add_marks) {
            fr=new AddMarksFragment();

        } else if (id == R.id.nav_attendance) {
            fr=new AttendanceDateFragment();

        } else if (id == R.id.nav_feedback) {
            fr=new SendFeedbackFragment();

        } else if (id == R.id.nav_uploaded_files) {
            fr=new UploadedFileFragment();

        }else if (id == R.id.nav_notification) {
            fr=new TeacherNotificationFragment();

        } else if (id == R.id.nav_get_notification) {
            fr=new GetAllNotificationFragment();

        } else if (id == R.id.nav_upload_files) {
            fr=new UploadFileFragment();

        }else if (id == R.id.nav_logout) {
            Intent i =new Intent(HomeActivity.this,Activity_Login.class);
            startActivity(i);
            //show message
            SharedPreferences pref = getApplicationContext().getSharedPreferences("isLogin", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();

            //        {"phone":"9495546646","adminapproval":"Approved","post":"Professor","last":"a","status":"success","department":"Computer","password":"a","message":"Teacher Login Successful","email":"a@gmail.com","permanent":"a","gender":"Male","local":"a","user":"Teacher","first":"a"}


            SharedPreferences sp = getApplicationContext().getSharedPreferences("userInfo", MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
          edit.clear();
            edit.apply();
            finish();



        }else if (id == R.id.nav_feedback) {
            fr=new ComingSoonFragment();

        }






        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_place, fr);

        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
