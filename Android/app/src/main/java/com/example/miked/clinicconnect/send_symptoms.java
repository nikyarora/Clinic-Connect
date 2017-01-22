package com.example.miked.clinicconnect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

/**
 * Created by MikeD on 1/21/2017.
 */

public class send_symptoms extends Activity {


    public EditText symptoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_symptoms);

        Bundle extras = getIntent().getExtras();

         symptoms = (EditText) findViewById(R.id.symptoms);

        ImageButton submit = (ImageButton) findViewById(R.id.submit_symptoms);
        TextView name = (TextView) (findViewById(R.id.name));
        final String phone_number = extras.getString("phone").replace("-","");
        name.setText(extras.getString("name"));

        final Double lat = extras.getDouble("lat");
        final Double longitute = extras.getDouble("long");

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendHippaText(phone_number);

                Intent i = new Intent(getApplicationContext(), ride.class);
                i.putExtra("lat", lat);
                i.putExtra("long", longitute);
                startActivity(i);
            }
        });



    }

    public void sendHippaText(String phone_number) {

        String original_message = symptoms.getText().toString();
        String encryptedMsg = "";
        try {
            encryptedMsg = AESCrypt.encrypt("clinic", original_message);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        SmsManager smsManager = SmsManager.getDefault();
//commented out for testing purposes
       //smsManager.sendTextMessage("6509194010", null, encryptedMsg, null, null);

    }
}
