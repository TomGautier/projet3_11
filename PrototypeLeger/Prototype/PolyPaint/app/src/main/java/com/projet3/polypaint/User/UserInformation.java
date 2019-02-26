package com.projet3.polypaint.User;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInformation  {
    private String username;
    private String password;

    public UserInformation(String username_, String password_){
        username = username_;
        password = password_;
    }

    public String getUsername() {return username;}
    public String getPassword() {return password;}


}
