package com.harrisonbacordo.dataland2018temp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Map<String, Integer> crashMap = new HashMap<>();
    String data = Data.DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        String[] dataVals = data.split("\n");
        for (int i = 0; i < dataVals.length; i++) {
            String[] temp = dataVals[i].split(",");
            System.out.println(temp[0]);
            crashMap.put(temp[0], Integer.parseInt(temp[1]));
        }
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(-41.249834, 174.810447, 1);
            String streetName = addresses.get(0).getAddressLine(0);
            streetName = streetName.split(",")[0].replaceAll("[0-9]*", "").toUpperCase().replace("RD", "ROAD").trim();
            Log.e("STREET NAME", streetName);
            if (crashMap.containsKey(streetName)) {
                Log.e("ALERT", "FOUND");
                notifications();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void notifications() {
        Log.e("WWWW", "WWWW");
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);


        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        builder.setLights(Color.BLUE, 500, 500);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        builder.setVibrate(pattern);
        builder.setStyle(new NotificationCompat.InboxStyle());
// Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }
}
