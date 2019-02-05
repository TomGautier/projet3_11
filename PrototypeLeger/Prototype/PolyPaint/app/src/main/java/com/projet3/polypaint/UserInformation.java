package com.projet3.polypaint;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInformation implements Parcelable {
    String username;
    String password;
    String serverAddress;

    public UserInformation(String username_, String password_, String serverAddress_){
        username = username_;
        password = password_;
        serverAddress = serverAddress_;
    }

    public String getServerAddress() {return serverAddress;}
    public String getUsername() {return username;}
    public String getPassword() {return password;}

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(serverAddress);
    }
    public static final Parcelable.Creator<UserInformation> CREATOR
            = new Parcelable.Creator<UserInformation>() {
        public UserInformation createFromParcel(Parcel in) {
            //return new Conversation(in);
            return new UserInformation(in.readString(),in.readString(),in.readString());

        }

        public UserInformation[] newArray(int size) {
            return new UserInformation[size];
        }
    };
}
