package com.projet3.polypaint.Chat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Conversation implements Parcelable  {

    private ArrayList<String> history;
    private String name;
    //public ConversationTask conversationTask;


    public Conversation(String name_) {
        history = new ArrayList<>();
        name = name_;
      //  conversationTask = new ConversationTask(this);

    }
    public Conversation(String name_, ArrayList history_) {
        history = history_;
        name = name_;
        //  conversationTask = new ConversationTask(this);
    }


    public String getName()  {
        return name;
    }
    public void setName(String newName) {
        name = newName;
    }
    public String getHistoryAt(int index) {
        return history.get(index);
    }
    public int getHistorySize() {
        return history.size();
    }
    public void addToHistory(String message) {
        history.add(message);
    }
    public ArrayList<String> getHistory(){
        return history;
    }
    public void setHistory(ArrayList<String> history){
        this.history = history;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeList(history);
    }
    public static final Parcelable.Creator<Conversation> CREATOR
            = new Parcelable.Creator<Conversation>() {
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in.readString(),in.readArrayList(Conversation.class.getClassLoader()));
        }

        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };
}
