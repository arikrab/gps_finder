package com.example.arik.gps_finder;

import android.widget.TextView;

public class RegisterHelper extends Thread {

    private TextView textChenger;
    private RegisterThread registerThread;

    public RegisterHelper(TextView textChenger ,RegisterThread registerThread) {
        this.textChenger = textChenger;
        this.registerThread = registerThread;
    }

    @Override
    public void run() {
        boolean register = registerThread.IsRegistered();
        if (register){
        textChenger.setText("user created you can log in now");
        }else{
            textChenger.setText("user name is taken please try to create other username");
        }
    }
}
