package com.example.student.teacher.nfc.services;

/**
 * Created by opulent on 24/11/16.
 */
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d("######", "Refreshed token: " + refreshedToken);
        SharedPreferences sp=getSharedPreferences("fcmID",MODE_PRIVATE);
        SharedPreferences.Editor edit=sp.edit();
        edit.putString("fcmId",refreshedToken);
        edit.apply();


    }



}