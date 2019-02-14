package com.example.arik.gps_finder;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Connection;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;

public class gps_finder extends AppCompatActivity {

    static User user;
    static TextView textUsername;
    static TextView textPassword;
    Button buttonLogin;
    Button buttonRegister;
    TextView textIsRegisterd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_finder);
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String imei;
        textUsername = (TextView) findViewById(R.id.textUsername);
        textPassword = (TextView) findViewById(R.id.textPassword);
        buttonLogin = (Button) findViewById(R.id.btnLogin);
        buttonRegister = (Button) findViewById(R.id.btnRegister);
        textIsRegisterd = (TextView) findViewById(R.id.textIsRegistered);


        try {
            imei = tm.getDeviceId();
        } catch (SecurityException e) {
            e.printStackTrace();
            return;
        }

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        user.setAccessToken(false);

        if (sharedPreferences.contains("username") == true) {
            user.setUsername(sharedPreferences.getString("username", ""));
            user.setPassword(sharedPreferences.getString("password", ""));
            if (imei != null) {
                user.setImei(imei);
            }

            checkLogin(user);
            if (user.isAccessToken()) {
                Intent intent = new Intent(this, PostLoginActivity.class);
                startActivity(intent);
            }

        }

    }

    public void loginOnclick(View view) {
        Log.d("Debug-------:", "button clicked");
        User user = new User();
        user.setUsername(textUsername.getText().toString());
        user.setPassword(textPassword.getText().toString());
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String imei;
       /* try {
            imei = tm.getDeviceId();
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d("premition:" ,"    denied");
            return;
        }*/
        //////////////////////////////////////////////////////////////////////////////////////1
        if (checkLogin(user)) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", user.getUsername());
            editor.putString("password", user.getPassword());
            //  editor.putString("imei", imei);
        }
        if (user.isAccessToken()) {
            Intent intent = new Intent(this, PostLoginActivity.class);
            intent.putExtra("user", user.getUsername());
            intent.putExtra("password", user.getPassword());
            //intent.putExtra("imei", imei);
            startActivity(intent);
        }
    }

    public void registerOnClick(View view){
        boolean registered;
        Log.d("Debug-------:", "button clicked");
        User user = new User();
        user.setUsername(textUsername.getText().toString());
        user.setPassword(textPassword.getText().toString());
        RegisterThread registerThread = new RegisterThread(user);
        registerThread.start();
        RegisterHelper registerHelper = new RegisterHelper(textIsRegisterd,registerThread);
        registerHelper.start();



    }


    public Boolean checkLogin(final User user) {

        String urlString = new String("http://10.0.2.2:8080/logreg?action=" +
                "login&username=" + user.getUsername() +
                "&password=" + user.getPassword());


        LoginThread loginThread=new LoginThread(user);
        loginThread.start();
        user.setAccessToken(loginThread.isConnected());

        return user.isAccessToken();
    }


}
