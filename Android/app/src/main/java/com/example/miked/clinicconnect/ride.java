package com.example.miked.clinicconnect;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.usebutton.sdk.ButtonContext;
import com.usebutton.sdk.ButtonDropin;
import com.usebutton.sdk.context.Location;
import com.usebutton.sdk.util.LocationProvider;

/**
 * Created by MikeD on 1/21/2017.
 */

public class ride extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride);

        Bundle extras = getIntent().getExtras();

        Button home_button = (Button) findViewById(R.id.home);
        Button maps = (Button) findViewById(R.id.maps);
        home_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });



        final Double lat = extras.getDouble("lat");
        final Double longitute = extras.getDouble("long");

        maps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String url = "http://maps.google.com/?q=" + String.valueOf(lat) + "," + String.valueOf(longitute);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);



            }
        });


        if (BuildConfig.DEBUG) {
            com.usebutton.sdk.Button.enableDebugLogging();
        }
        com.usebutton.sdk.Button.getButton(this).start();

        // Let's register our user ID so we can correlate our users with Button's data
        com.usebutton.sdk.Button.getButton(this).setUserIdentifier("niky.arora@menloschool.org");

        // Get the Button View
        ButtonDropin buttonDropin = (ButtonDropin) findViewById(R.id.main_dropin);



        // Create a PlacementContext for the location you want a ride to.
        final Location officeLocation = new Location("Clinic", lat, longitute);
        final ButtonContext context = ButtonContext.withSubjectLocation(officeLocation);

        //noinspection ResourceType
        final android.location.Location bestLocation = new LocationProvider(this).getBestLocation();
        if (bestLocation != null) {
            context.setUserLocation(new Location(bestLocation));
        }
        else {
            // comment out to use your current location from above
            context.setUserLocation(new Location(39.9502479,-75.1932046));
        }

        // Prepare the Button for display with our context
        buttonDropin.prepareForDisplay(context, new ButtonDropin.Listener() {
            @Override
            public void onPrepared(final boolean willDisplay) {
                // Toggle visibility of UI items here if necessary

            }

            @Override
            public void onClick(final ButtonDropin buttonDropin) {}
        });




    }
}
