package com.projet3.polypaint;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Conversation implements Parcelable  {

    private ArrayList<String> history;
    private String name;
    public Conversation(String name_) {
        history = new ArrayList<>();
        name = name_;
    }

    public String GetName()  {
        return name;
    }
    public void SetName(String newName) {
        name = newName;
    }
    public String GetHistoryAt(int index) {
        return history.get(index);
    }
    public int GetHistorySize() {
        return history.size();
    }
    public void AddToHistory(String message) {
        history.add(message);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(history);
    }
    public static final Parcelable.Creator<Conversation> CREATOR
            = new Parcelable.Creator<Conversation>() {
        public Conversation createFromParcel(Parcel in) {
            //return new Conversation(in);
            return null;
        }

        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };
}
