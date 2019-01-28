package com.projet3.polypaint;

import java.util.ArrayList;

public class Conversation {

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

}
