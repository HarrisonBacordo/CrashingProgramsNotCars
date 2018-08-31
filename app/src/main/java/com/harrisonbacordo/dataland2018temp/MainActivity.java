package com.harrisonbacordo.dataland2018temp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Map<String, Integer> crashMap = new HashMap<>();
    String data = Data.DATA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();


//        Stats

        String[] dataVals = data.split("\n");
        for (int i = 0; i < dataVals.length; i++) {
            String[] temp = dataVals[i].split(",");
            crashMap.put(temp[0], Integer.parseInt(temp[1]));
        }
        try {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = new String[2];
                permissions[0] = Manifest.permission.ACCESS_COARSE_LOCATION;
                permissions[1] = Manifest.permission.ACCESS_FINE_LOCATION;
                ActivityCompat.requestPermissions(this, permissions, 5);
            }
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String streetName = addresses.get(0).getAddressLine(0);
            streetName = streetName.split(",")[0].replaceAll("[0-9]*", "").toUpperCase().replace("RD", "ROAD").trim();
            Log.e("STREET NAME", streetName);
            if (crashMap.containsKey(streetName)) {
                Log.e("ALERT", "FOUND");
                showNotification("DANGEROUS ROAD", streetName + " is a dangerous road. Be careful!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

    }

//    void dailyNotification() {
//        int num = new Random().nextInt(Data.STATS.length);
//        String stat = Data.STATS[num];
//
//
//    }

    void showNotification(String title, String content) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 100000);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(78, mBuilder.build());


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
