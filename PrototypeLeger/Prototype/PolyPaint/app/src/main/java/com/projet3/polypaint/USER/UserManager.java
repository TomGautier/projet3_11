package com.projet3.polypaint.USER;

import com.projet3.polypaint.Chat.Conversation;

import java.util.ArrayList;

public class UserManager {

    public static UserManager currentInstance;
    private UserInformation user;
    private ArrayList<Conversation> userConversations;

    public UserManager(UserInformation user_){
        user = user_;
        userConversations = new ArrayList<>();
    }

    public void setUserConversations(ArrayList<Conversation> conversations){
        userConversations = conversations;
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
