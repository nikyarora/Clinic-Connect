package com.example.miked.clinicconnect;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.round;


class Clinic {
    public double lat_clinic;
    public double long_clinic;
    public int population;
    public String phone_number;
    public String name_clinic;
    public float distance;
    public double time_to_arrive;
    public String hours;
}



public class MainActivity extends AppCompatActivity {
    public double user_long;
    public double user_lat;
    public Spinner dropdown;

    public ArrayList<Clinic> clinics = new ArrayList<Clinic>();
    public ListView list;
    public List<String> List_file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dropdown = (Spinner) findViewById(R.id.dropdown);


        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        list = (ListView) findViewById(R.id.clinics_list_view);
        List_file = new ArrayList<String>();

        //start location tracker
        Intent intent = new Intent(this, location_tracker.class);

        startService(intent);

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
             user_long = location.getLongitude();
             user_lat = location.getLatitude();
        }
        else {
            user_lat = 39.952300;
            user_long = -75.190975;
        }


//json
        try {
            JSONArray array = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < array.length(); i++) {

                JSONObject row = array.getJSONObject(i);
                double lat = row.getDouble("lat");
                double longitutde = row.getDouble("long");

                final String phone_number = row.getString("number");
                String name = row.getString("name");
                String hours = row.getString("hours");

                int population = 0;

                //distance
                Location loc1 = new Location("");
                loc1.setLatitude(user_lat);
                loc1.setLongitude(user_long );

                Location loc2 = new Location("");
                loc2.setLatitude(lat);
                loc2.setLongitude(longitutde);

                float distanceInMeters = loc1.distanceTo(loc2);

                //gives time assuming 30mh
                //46 km/h is 500 meters per minute
                int time_to_arrive = (int) round(distanceInMeters / 767.00);

                //calculate wait time
                JSONArray json_population = new JSONArray(getPeopleCount());


                for (int j = 0; j < json_population.length(); j++) {
                    JSONObject row_second = json_population.getJSONObject(j);

                    if (lat == row_second.getDouble("lat") && longitutde == row_second.getDouble("long")) {
                        population = row_second.getInt("population");
                    }
                    else {
                        population = 0;
                    }

                }
                //finalize object and store in array
                Clinic temp_clinic = new Clinic();
                temp_clinic.lat_clinic = lat;
                temp_clinic.long_clinic = longitutde;
                temp_clinic.phone_number = phone_number;
                temp_clinic.population = population;
                temp_clinic.name_clinic = name;
                temp_clinic.distance = distanceInMeters;
                temp_clinic.time_to_arrive = time_to_arrive;
                temp_clinic.hours = hours;


                clinics.add(temp_clinic);

                //sort
                sort_array();
                for (int k = 0; k < clinics.size(); k++) {

                    String description = clinics.get(k).name_clinic + ": " + clinics.get(k).population + " people\n" + clinics.get(k).time_to_arrive + " minutes away\n Phone Number: " + clinics.get(k).phone_number + "\n Hours: " + clinics.get(k).hours;
                    List_file.add(description);
                }

                list.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, List_file));


                //click
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        Object o = list.getItemAtPosition(position);
                        String ostring = o.toString();
                        String intent_number = ostring.substring(Math.max(ostring.length() - 12, 0));

                        String clinic_name_intent = ostring.substring(0, Math.min(ostring.length(), ostring.indexOf(":")));

                        Intent i = new Intent(getApplicationContext(), send_symptoms.class);
                        i.putExtra("phone", intent_number);
                        i.putExtra("name", clinic_name_intent);
                        i.putExtra("lat", clinics.get(position).lat_clinic);
                        i.putExtra("long", clinics.get(position).long_clinic);
                        startActivity(i);
                        //now we have the year so let's change the  view

                    }
                });


            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String specialization = parent.getItemAtPosition(position).toString();
                clinics = new ArrayList<Clinic>();
                List_file = new ArrayList<String>();

                try {
                    JSONArray array = new JSONArray(loadJSONFromAsset());
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject row = array.getJSONObject(i);


                        //only display specilization
                        if (row.getString("specialization").contains(specialization) || specialization == "All") {
                            double lat = row.getDouble("lat");
                            double longitutde = row.getDouble("long");

                            final String phone_number = row.getString("number");
                            String name = row.getString("name");
                            String hours = row.getString("hours");

                            int population = 0;

                            //distance
                            Location loc1 = new Location("");
                            loc1.setLatitude(user_lat);
                            loc1.setLongitude(user_long);

                            Location loc2 = new Location("");
                            loc2.setLatitude(lat);
                            loc2.setLongitude(longitutde);

                            float distanceInMeters = loc1.distanceTo(loc2);

                            //gives time assuming 30mh
                            //46 km/h is 500 meters per minute
                            int time_to_arrive = (int) round(distanceInMeters / 767.00);

                            //calculate wait time
                            JSONArray json_population = new JSONArray(getPeopleCount());


                            for (int j = 0; j < json_population.length(); j++) {
                                JSONObject row_second = json_population.getJSONObject(j);

                                if (lat == row_second.getDouble("lat") && longitutde == row_second.getDouble("long")) {
                                    population = row_second.getInt("population");
                                } else {
                                    population = 0;
                                }

                            }
                            //finalize object and store in array
                            Clinic temp_clinic = new Clinic();
                            temp_clinic.lat_clinic = lat;
                            temp_clinic.long_clinic = longitutde;
                            temp_clinic.phone_number = phone_number;
                            temp_clinic.population = population;
                            temp_clinic.name_clinic = name;
                            temp_clinic.distance = distanceInMeters;
                            temp_clinic.time_to_arrive = time_to_arrive;
                            temp_clinic.hours = hours;


                            clinics.add(temp_clinic);

                            //sort
                            sort_array();
                            for (int k = 0; k < clinics.size(); k++) {

                                String description = clinics.get(k).name_clinic + ": " + clinics.get(k).population + " people\n" + clinics.get(k).time_to_arrive + " minutes away\n Phone Number: " + clinics.get(k).phone_number + "\n Hours: " + clinics.get(k).hours;
                                List_file.add(description);
                            }

                            list.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, List_file));


                            //click
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                                    Object o = list.getItemAtPosition(position);
                                    String ostring = o.toString();
                                    String intent_number = ostring.substring(Math.max(ostring.length() - 12, 0));

                                    String clinic_name_intent = ostring.substring(0, Math.min(ostring.length(), ostring.indexOf(":")));

                                    Intent i = new Intent(getApplicationContext(), send_symptoms.class);
                                    i.putExtra("phone", intent_number);
                                    i.putExtra("name", clinic_name_intent);
                                    i.putExtra("lat", clinics.get(position).lat_clinic);
                                    i.putExtra("long", clinics.get(position).long_clinic);
                                    startActivity(i);
                                    //now we have the year so let's change the  view

                                }
                            });

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });

    }





    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("locations.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String getPeopleCount() throws IOException {



        HttpResponse response = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("https://clinicconnect.herokuapp.com/"));
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

        String json_response = EntityUtils.toString(response.getEntity());


        return json_response;
    }

    public void sort_array() {

            int i;
            boolean flag = true;

            while (flag)
            {
                flag= false;
                for(i=0;i < clinics.size() - 1;i++)
                {
                    if (clinics.get(i).population < clinics.get(i+1).population)
                    {
                        Clinic temp = clinics.get(i);
                        clinics.set(i, clinics.get(i+1));
                        clinics.set(i+1, temp);
                        flag = true;
                    }
                }
            }

        int j;
         flag = true;

        while (flag)
        {
            flag= false;
            for(j=0;j < clinics.size() - 1;j++)
            {
                if (clinics.get(j).population < clinics.get(j+1).population)
                {
                    Clinic temp = clinics.get(j);
                    clinics.set(j, clinics.get(j+1));
                    clinics.set(j+1, temp);
                    flag = true;
                }
            }
        }



    }

    }








