package com.example.student.teacher.nfc.services;

/**
 * Created by opulent on 24/11/16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.student.teacher.nfc.HomeActivity;
import com.example.student.teacher.nfc.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d("########", "remoteMessage: " + remoteMessage);
        Log.d("########", "remoteMessage data : " + remoteMessage.getData());   //e data : {message={"notification":{"body":"Hi","title":"App"}}}
String message=remoteMessage.getData().toString();
        message=message.substring(message.lastIndexOf("{"),message.indexOf("}")+1);

        Log.d("########", "message : " + message);   //e data : {message={"notification":{"body":"Hi","title":"App"}}}

        /*

        Log.d("########", "From: " + remoteMessage.getFrom());
        Log.d("#######", "Notification Message : " + remoteMessage.getNotification());
        Log.d("#######", "Notification Message body: " + remoteMessage.getNotification().getBody());
        Log.d("#######", "Notification Message title: " + remoteMessage.getNotification().getTitle());  //data : {message={"notification":{"body":"Hi","title":"App"}}}
*/

        //Calling method to generate notification
        sendNotification(message);
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        String message,name,flag,date;



        try {
            JSONObject jsonObject=new JSONObject(messageBody);

         //   {message={"body":"hshhdjjf","title":"aaa aaa","flag":"0"}}

            flag=jsonObject.getString("flag");
if(flag.equals("0")) {

    message = jsonObject.getString("body");
    name = jsonObject.getString("title");
    date = jsonObject.getString("date");
    notification1(message,name,date);

}


        } catch (JSONException e) {
            e.printStackTrace();
        }

}

public void notification1(String message,String name,String date){


    Intent intent = new Intent(this, HomeActivity.class);
    intent.putExtra("message",message);
    intent.putExtra("name",name);
    intent.putExtra("date",date);

    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT);

    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("NFC")
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

    NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(0, notificationBuilder.build());

}



}