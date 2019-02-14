package com.example.arik.gps_finder;

import android.net.sip.SipSession;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class LoginThread extends Thread {


    private User user;
    private String BASE_URL = "http://10.0.2.2:8080/LogReg";
    private String doneTask;
    private boolean access = false;


    public LoginThread() {
        this.user = null;

    }

    public boolean isAccess() {
        return access;
    }

    public LoginThread(User user) {
        this.user = user;

    }

    public String getDoneTask() {
        return doneTask;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public void run() {
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
*/
        URL url;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {

            int actuallyRead;
            byte[] wordBuffer = new byte[64];
            String word = null;
            BASE_URL = ("http://10.0.2.2:8080/logreg?action=login" +
                    "&username=" + user.getUsername() +
                    "&password=" + user.getPassword());
            url = new URL(BASE_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(false);
            connection.setRequestMethod("GET");
            connection.connect();

            inputStream = connection.getInputStream();
            actuallyRead = inputStream.read(wordBuffer);
            if (actuallyRead != -1) {
                word = new String(wordBuffer, 0, actuallyRead);
            }

            if (word.equals("access granted")) {
                access = true;
                doneTask = "done";

            } else {
                doneTask = "done";
                access=false;
            }


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(connection!=null){
                connection.disconnect();
                doneTask="done";
            }
        }


    }
    public boolean isConnected(){
        while (doneTask!="done"){//ensure that the thread is done working
             }
             return access;
    }

}


