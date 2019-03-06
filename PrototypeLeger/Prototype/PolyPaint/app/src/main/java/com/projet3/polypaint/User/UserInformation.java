package com.projet3.polypaint.User;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInformation implements Parcelable {
    private String username;
    private String password;

    public UserInformation(String username_, String password_){
        username = username_;
        password = password_;
    }

    public String getUsername() {return username;}
    public String getPassword() {return password;}

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(password);
    }
    public static final Parcelable.Creator<UserInformation> CREATOR
            = new Parcelable.Creator<UserInformation>() {
        public UserInformation createFromParcel(Parcel in) {
            return new UserInformation(in.readString(),in.readString());
        }

        public UserInformation[] newArray(int size) {
            return new UserInformation[size];
        }
    };
}
