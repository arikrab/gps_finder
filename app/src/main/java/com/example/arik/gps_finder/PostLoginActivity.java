package com.example.arik.gps_finder;

import android.Manifest;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class PostLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        boolean activeService;
        Button startButton=(Button) findViewById(R.id.btnStart);
        Button stopButton=(Button)  findViewById(R.id.btnStop);
        TextView serverText=(TextView)findViewById(R.id.textService);
        TextView p2pText=(TextView)findViewById(R.id.textP2P);
        TextView premissionStatusText=(TextView) findViewById(R.id.textPermissionStatus);

        //ask fine location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            premissionStatusText.setText(new String("the app is did not get FineLoctaion permissions"));
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            }else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},1);


            }
        } else {
            // Permission has already been granted
            premissionStatusText.setText(new String("the app is did get FineLoctaion permissions"));
        }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                premissionStatusText.setText("permission granted");
            }


        //fist check if the service is already running and enable or disable buttons by it
        if(isServiceRunning()){
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        }else{
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }


    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(Gps_finerSearchConnectionService.class.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void onCliclStartService(){


    }
    private void onClickStopService(){


    }

}

