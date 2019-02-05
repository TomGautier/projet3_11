package com.projet3.polypaint;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInformation implements Parcelable {
    String username;
    String password;
    String ipAddress;

    public UserInformation(String username_, String password_, String ipAddress_){
        username = username_;
        password = password_;
        ipAddress = ipAddress_;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(ipAddress);
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
