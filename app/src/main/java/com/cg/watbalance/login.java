package com.cg.watbalance;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cg.watbalance.preferences.Connection;
import com.cg.watbalance.preferences.ConnectionDetails;
import com.cg.watbalance.preferences.Encryption;
import com.cg.watbalance.service.Service;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Calendar;

public class login extends AppCompatActivity {

    EditText IDNum;
    EditText pinNum;
    Connection myConn;
    ConnectionDetails myConnDet;
    Button mySaveButton;
    TextView forgotPIN;
    Encryption myEncryption;
    SharedPreferences myPreferences;
    SharedPreferences.Editor myPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myEncryption = new Encryption(getApplicationContext());

        //Variable Declaration
        IDNum = (EditText) findViewById(R.id.IDNum);
        pinNum = (EditText) findViewById(R.id.pinNum);
        mySaveButton = (Button) findViewById(R.id.button);
        forgotPIN = (TextView) findViewById(R.id.forgotPIN);
        myPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (myPreferences.getString("PinNum", "0000").length() > 6) {
            startRepeat();

            //Go to Launch Screen
            Intent myIntent = new Intent(getApplicationContext(), balanceScreen.class);
            startActivity(myIntent);
            finish();
        }

        mySaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myConnDet = new ConnectionDetails(IDNum.getText().toString(), pinNum.getText().toString());
                myConn = new Connection(myConnDet, getApplicationContext()) {

                    @Override
                    public void onResponseReceive(Document myDoc) {
                        Element myNameTag = myDoc.getElementById("oneweb_account_name");
                        String TempFirstName = myNameTag.text().split(",")[1];
                        String TempLastName = myNameTag.text().split(",")[0];
                        String FirstName = WordUtils.capitalizeFully(TempFirstName.substring(0, TempFirstName.length() - 1));
                        String LastName = WordUtils.capitalizeFully(TempLastName);
                        myPrefEditor = myPreferences.edit();
                        myPrefEditor.putString("Name", FirstName + " " + LastName);
                        myPrefEditor.apply();
                    }

                    @Override
                    public void onComplete() {
                        Log.d("LOGIN", "SUCCESS");
                        String encryptedPIN = myEncryption.encryptPIN(pinNum.getText().toString());

                        myPrefEditor = myPreferences.edit();
                        myPrefEditor.putString("IDNum", IDNum.getText().toString());
                        myPrefEditor.putString("PinNum", encryptedPIN);
                        myPrefEditor.apply();

                        startRepeat();

                        Intent myIntent = new Intent(login.this, balanceScreen.class);
                        startActivity(myIntent);
                        finish();
                    }

                    @Override
                    public void beforeConnect() {

                    }

                    @Override
                    public void onConnectionError() {
                        Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onIncorrectLogin() {
                        Toast.makeText(getApplicationContext(), "Incorrect Login Information", Toast.LENGTH_LONG).show();
                    }
                };
                myConn.getData();
            }
        });

        forgotPIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String resetURL = "https://account.watcard.uwaterloo.ca/pinreset.asp";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(resetURL));
                startActivity(i);
            }
        });
    }

    public void startRepeat() {
        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent newPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, Service.class), 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, newPendingIntent);
    }

}
