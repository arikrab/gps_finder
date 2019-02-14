package com.example.arik.gps_finder;

import android.Manifest;
import android.app.Activity;
import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;


public class Gps_finerSearchConnectionService extends Service {
    private static final byte checkConnection = 100;
    private static final byte foundConnection = 101;
    private static boolean foundSearcher = false;
    private static String p2pIpConnection = null;

    private static LocationManager locationManager;
    private static LocationListener locationListener;

    private Context serviceContext=this;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onDestroy() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        HandlerThread SearchUserhandlerThread = new HandlerThread("searchingUser", Thread.NORM_PRIORITY);
        SearchUserhandlerThread.start();
        Looper looper = SearchUserhandlerThread.getLooper();
        Handler handler = new Handler(looper);

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (foundSearcher == false) {
                    URL url = null;
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    HttpURLConnection connection = null;

                    try {
                        url = new URL("http://10.0.2.2:8080/Searching");
                        byte[] stringBuffer;
                        byte[] byteBuffer = new byte[4];
                        int actuallyRead;
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setUseCaches(false);
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.connect();

                        inputStream = connection.getInputStream();
                        outputStream = connection.getOutputStream();

                        outputStream.write(checkConnection);
                        actuallyRead = inputStream.read(byteBuffer);
                        if (actuallyRead != 4) {
                            connection.disconnect();
                            Log.d("input", "didnt got the whole stream ,Disconnecting!");
                        }
                        if (ByteBuffer.wrap(byteBuffer).getInt() == foundConnection) {
                            foundSearcher = true;
                            //getting IP String size
                            actuallyRead = inputStream.read(byteBuffer);
                            if (actuallyRead != 4) {
                                connection.disconnect();
                                Log.d("input", "didnt got the whole stream ,Disconnecting!");
                            }
                            //getting actual IP string
                            stringBuffer = new byte[ByteBuffer.wrap(byteBuffer).getInt()];
                            actuallyRead = inputStream.read(stringBuffer);
                            if (actuallyRead != ByteBuffer.wrap(byteBuffer).getInt()) {
                                connection.disconnect();
                                Log.d("input", "didnt got the whole stream ,Disconnecting!");
                            } else {
                                p2pIpConnection = new String(stringBuffer);
                            }


                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        connection.disconnect();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        HandlerThread sendLocationThreadThread = new HandlerThread("searchingUser", Thread.NORM_PRIORITY);
        sendLocationThreadThread.start();
        Looper locationlooper = sendLocationThreadThread.getLooper();
        Handler handlerLocation = new Handler(locationlooper);
        handlerLocation.post(new Runnable() {
            @Override
            public void run() {
                if (foundSearcher) {
                    Socket socket = null;
                    OutputStream outputStream = null;

                    try {
                        socket = new Socket(p2pIpConnection, 3456);
                        outputStream = socket.getOutputStream();

                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        final OutputStream finalOutputStream = outputStream;
                        locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                byte[] stringBuffer;
                                byte[] byteBuffer=new byte[4];
                                String longitude;
                                String latitude;

                                latitude=Double.toString(location.getLatitude());
                                longitude=Double.toString(location.getLongitude());


                                try {
                                    //send latitude--
                                    //send string size
                                    ByteBuffer.wrap(byteBuffer).putInt(latitude.length());
                                    finalOutputStream.write(byteBuffer);
                                    //send words
                                    stringBuffer=new byte[latitude.length()];
                                    stringBuffer=latitude.getBytes();
                                    finalOutputStream.write(stringBuffer);


                                    //send longitude
                                    ByteBuffer.wrap(byteBuffer).putInt(longitude.length());
                                    finalOutputStream.write(byteBuffer);
                                    //send words
                                    stringBuffer=new byte[longitude.length()];
                                    stringBuffer=latitude.getBytes();
                                    finalOutputStream.write(stringBuffer);

                                }catch (IOException e){
                                    foundSearcher = false;
                                }finally {
                                    try {
                                        finalOutputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    Thread.sleep(5000);
                                }catch (InterruptedException e){

                                }
                            }

                            @Override
                            public void onStatusChanged(String s, int i, Bundle bundle) {

                            }

                            @Override
                            public void onProviderEnabled(String s) {

                            }

                            @Override
                            public void onProviderDisabled(String s) {

                            }
                        };

                        if (ContextCompat.checkSelfPermission(serviceContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) serviceContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                        } else {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        }
                    }
                     catch (IOException e) {
                        foundSearcher = false;
                    } finally {
                        try {
                            socket.close();
                            foundSearcher = false;
                        } catch (IOException e) {
                            foundSearcher = false;
                        }
                    }
                }
            }
        });

        return START_STICKY;

    }

    }




