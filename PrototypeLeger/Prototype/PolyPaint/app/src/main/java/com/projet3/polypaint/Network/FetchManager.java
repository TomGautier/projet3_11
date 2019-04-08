package com.projet3.polypaint.Network;

import com.projet3.polypaint.Chat.Conversation;
import com.projet3.polypaint.UserList.User;
import com.projet3.polypaint.UserLogin.UserInformation;

import java.util.ArrayList;

public class FetchManager {

    public static FetchManager currentInstance;
    private UserInformation user;
    private ArrayList<Conversation> userConversations;
    private ArrayList<User> users;

    public FetchManager(UserInformation user_){
        user = user_;
        userConversations = new ArrayList<>();
        users = new ArrayList<>();
    }

    public void setUserConversations(ArrayList<Conversation> conversations){
        userConversations = conversations;
    }
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
    public final ArrayList<String> getUsersNames(){
        ArrayList<String> ret = new ArrayList<>();
        for (User user : users){
            ret.add(user.getUsername());
        }
        return ret;
    }
    public final ArrayList<User> getUsers(){
        return users;
    }
    public boolean changeConnectedState(String username, boolean state){
        for (User user : users){
            if (user.getUsername().equals(username)){
                user.changeConnectionState(state);
                return true;
            }
        }
        return false;
    }

    public final ArrayList<String> getUserConversationsNames(){
        ArrayList<String> ret = new ArrayList<>();
        for (Conversation convo : userConversations){
            ret.add(convo.getName());
        }
        return ret;
    }
    public final Conversation getUserConversationAt(int index) {
        return userConversations.get(index);
    }
    public ArrayList<Conversation> getUserConversations(){
        return userConversations;
    }
    public Conversation getUserConversationByName(String conversationName){
        for (int i = 0; i < userConversations.size(); i++){
            if(userConversations.get(i).getName().equals(conversationName))
                return userConversations.get(i);
        }
        return null;
    }

    public void addUserConversation(String name){
        userConversations.add(new Conversation(name));
    }
    public void removeUserConversation(String name){
        for (Conversation convo : userConversations){
            if (convo.getName().equals(name)) {
                userConversations.remove(convo);
                return;
            }
        }
    }
    public String getUserUsername() {
        return user.getUsername();
    }

}
