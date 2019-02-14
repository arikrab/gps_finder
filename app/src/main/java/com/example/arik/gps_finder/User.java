package com.example.arik.gps_finder;

public class User {

    private boolean accessToken=false;
    private String username;
    private String password;
    private String imei;

    public boolean isAccessToken() {
        return accessToken;
    }

    public void setAccessToken(boolean accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
