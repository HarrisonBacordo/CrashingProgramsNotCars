package com.harrisonbacordo.dataland2018temp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_REQUEST_INT = 177;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Map<String, Integer> crashMap = new HashMap<>();
    private String data = Data.DATA;
    private boolean initialNotificationSent = false;
    private String previousStreetName = null;
    private static final int SPEED_THRESHOLD = 15;
    HashMap<Double, Double> longLatMap = new HashMap<>();
    String longLats = Data.LONGLATS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        String[] longLatVals = longLats.split("\n");
        for (int q = 0; q < longLatVals.length; q++) {
            String[] temp = longLatVals[q].split(",");
            longLatMap.put(Double.parseDouble(temp[0]), Double.parseDouble(temp[1]));
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        createNotificationChannel();

        String[] dataVals = data.split("\n");
        for (int i = 0; i < dataVals.length; i++) {
            String[] temp = dataVals[i].split(",");
            crashMap.put(temp[0], Integer.parseInt(temp[1]));
        }
//        Ensure permissions are granted. Otherwise, request them.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            String[] permissions = new String[2];
            permissions[0] = Manifest.permission.ACCESS_COARSE_LOCATION;
            permissions[1] = Manifest.permission.ACCESS_FINE_LOCATION;
            ActivityCompat.requestPermissions(this, permissions, 5);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_INT);
            }
            return;
        }else{
            mMap.setMyLocationEnabled(true);

        }
        for(Double p : longLatMap.keySet()){
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(longLatMap.get(p),p))
                    .radius(15)
                    .strokeWidth(4)
                    .strokeColor(Color.argb(250, 231, 76, 60))
                    .fillColor(Color.argb(100, 231, 76, 60));
            mMap.addCircle(circleOptions);

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(longLatMap.get((Double)longLatMap.keySet().toArray()[0]), (Double)longLatMap.keySet().toArray()[0])));
    }

    /**
     * Gets constant location updates from the geo
     *
     * @throws IOException
     */
    private void startLocationUpdates() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        Another check required to avoid errors
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        while (true) {
            try {
//                get current location
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                int speed = (int) ((location.getSpeed() * 3600) / 1000);
                speed = SPEED_THRESHOLD;
                Log.e("SPEED", String.valueOf(speed));
                if (speed < 15) {
                    continue;
                } else if (!initialNotificationSent) {
                    this.initialNotificationSent = true;
                    showNotification("Safe Driving", "Stat Here");
                }
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
//                Get street name from location
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String currentStreetName = addresses.get(0).getAddressLine(0);
//                Skip checks if current street hasn't changed from previous
                if (currentStreetName.equals(this.previousStreetName)) {
                    return;
                } else {
//                    Parse street name to remove numbers
                    this.previousStreetName = currentStreetName;
                    currentStreetName = currentStreetName.split(",")[0].replaceAll("[0-9]*", "").toUpperCase().replace("RD", "ROAD").trim();
                    Log.e("STREET NAME", currentStreetName);
//                    Check if dangerous road, if so, notify
                    if (crashMap.containsKey(currentStreetName)) {
                        Log.e("ALERT", "FOUND");
                        showNotification("DANGEROUS ROAD", currentStreetName + " is a dangerous road. Be careful!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        startLocationUpdates();
    }


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
