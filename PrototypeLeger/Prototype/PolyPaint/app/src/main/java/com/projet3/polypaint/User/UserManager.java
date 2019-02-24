package com.projet3.polypaint.User;

import android.util.JsonReader;

import com.projet3.polypaint.Chat.Conversation;
import com.projet3.polypaint.Chat.SocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UserManager {
    private final int TIMEOUT_DELAY = 5;
    private final String PORT =":3000";

    private String url;
    private String ip;
    private String sessionID;
    private ArrayList<Conversation> userConversations;
    private UserInformation user;


    public static UserManager currentInstance;

    public UserManager(String ip_) {
        ip = ip_;
        userConversations = new ArrayList<>();
        sessionID = null;
        user = null;
    }

    public boolean requestLogin(UserInformation userInformation_) {
        url = formatUrl(userInformation_, Request.Connection);
        UserLoginTask loginTask = new UserLoginTask();
        loginTask.execute(url);
        boolean response = false;
        try{
            response = configureLoginResponse(loginTask.get(TIMEOUT_DELAY, TimeUnit.SECONDS));
            if (response)
                user = userInformation_;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return response;
    }
    public ArrayList<Conversation> fetchUserConversations(UserInformation userInformation_) {
        url = formatUrl(userInformation_, Request.Fetch_Conversations);
        UserFetchConversationsTask task = new UserFetchConversationsTask();
        task.execute(url);
        try{
            userConversations = configureFetchConversationsResponse(task.get(TIMEOUT_DELAY, TimeUnit.SECONDS));
            return userConversations;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
    private ArrayList<Conversation> configureFetchConversationsResponse(JSONArray jsons){
        ArrayList<Conversation> conversations = new ArrayList<>();
        if (jsons == null || jsons.length() == 0)
            return conversations;
        else{
             for (int i = 0; i < jsons.length(); i ++){
                 JSONObject jsonObject;
                 String name = "";
                 try {
                     jsonObject = jsons.getJSONObject(i);
                     name = jsonObject.getString("name");
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
                 if (!name.isEmpty()){
                     conversations.add(new Conversation(name, new ArrayList()));
                     SocketManager.currentInstance.joinConversation(sessionID, name);
                 }
             }
             return conversations;
        }


    }

    private boolean configureLoginResponse(String response_){
        sessionID = response_;
        return response_.isEmpty() ? false : true;
    }

    private String formatUrl(UserInformation userInformation_, String request){
        String formatUrl = null;
        switch (request){
            case Request.Connection:
                formatUrl = "http://" + ip + PORT + request + userInformation_.getUsername()+ "/"
                    + userInformation_.getPassword();
                break;

            case Request.Fetch_Conversations:
                formatUrl = "http://" + ip + PORT + request + sessionID + "/" + userInformation_.getUsername();
                break;
        }
        return formatUrl;
    }

    /*public void setUserConversations(ArrayList<JSONObject> conversations) {
        //userConversations = conversations;
        for(JSONObject convo : conversations){

            Conversation conversation = new Conversation()
        }
    }*/
    /*public final Conversation getUserconversation() {
        return userConversations;
    }*/
    public final String getUserUsername(){
        return user.getUsername();
    }

}

final class Request {

    public static final String Connection = "/connection/login/";
    public static final String Sign_Up = "/connection/signup/";
    public static final String Fetch_Conversations = "/api/chat/";

}
