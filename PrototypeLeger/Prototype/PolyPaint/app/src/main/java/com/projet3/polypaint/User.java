package com.projet3.polypaint;

public class User {

    private boolean isConnected;
    private String username;
    public User(String username_, boolean isConnected_){
        username = username_;
        isConnected = isConnected_;
    }
    public final String getUsername(){
        return username;
    }
    public final boolean isConnected(){
        return isConnected;
    }
    public void changeConnectionState(boolean state){
        isConnected = state;
    }
}
