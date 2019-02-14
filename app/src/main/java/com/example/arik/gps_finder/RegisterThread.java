package com.example.arik.gps_finder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class RegisterThread extends Thread {

    private User user;
    private String BASE_URL = "http://10.0.2.2:8080/LogReg";
    private String doneTask;
    private String word;

    public RegisterThread(User user) {
        this.user = user;
    }

    @Override
    public void run() {

        URL url;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            int actuallyRead;
            byte[] wordBuffer = new byte[64];
            word=null;
            BASE_URL = ("http://10.0.2.2:8080/logreg?action=register" +
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
            if (word.equals(new String("user already taken"))){

                doneTask="done";
            }
            else if (word.equals(new String("user created"))){

                doneTask="done";
            }else{

                doneTask="done";
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (connection!=null){
                connection.disconnect();
            }


            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            doneTask="done";
        }

    }
    public boolean IsRegistered(){
        int i=0;
        while (doneTask!="done"){
            if(i==1500){
                return false;
            }
                i++;


        }

        if (word.equals(new String("user already taken"))){
            return false;

        }
        else if (word.equals(new String("user created"))) {
            return true;

        }

        return false;
    }

    public User getUser() {
        return user;
    }
}