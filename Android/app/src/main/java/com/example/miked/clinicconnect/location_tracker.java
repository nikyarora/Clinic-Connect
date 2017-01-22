package com.example.miked.clinicconnect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by MikeD on 1/21/2017.
 */

public class location_tracker extends Service {

    public static PowerManager mgr;

    public LocationManager lm;
    public Location location;

    public double prev_lat = 0;
    public double prev_long = 0;

    @Override
    public void onCreate()
    {
        super.onCreate();






    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId)
    {
        //initialize the thing that will keep the service running
        mgr = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        //turn on the wakelock
        PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();

        final Handler h = new Handler();
        final int delay = 300000; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
                //do something
                record_location();
                h.postDelayed(this, delay);
            }
        }, delay);

        return  START_STICKY;
    }

    public void record_location() {
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double user_long = location.getLongitude();
        double user_lat = location.getLatitude();

        if (prev_lat == 0 && prev_long == 0) {

            HttpResponse response = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI("https://clinicconnect.herokuapp.com/add_data/" + Double.toString(user_lat) + "/" + Double.toString(user_long)));
                response = client.execute(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            prev_lat = user_lat;
            prev_long = user_long;
        }

        else {

            HttpResponse response = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI("https://clinicconnect.herokuapp.com/add_data/" + Double.toString(user_lat) + "/" + Double.toString(user_long)));
                response = client.execute(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI("https://clinicconnect.herokuapp.com/decrease_data/" + Double.toString(user_lat) + "/" + Double.toString(user_long)));
                response = client.execute(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            prev_lat = user_lat;
            prev_long = user_long;
        }



    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
