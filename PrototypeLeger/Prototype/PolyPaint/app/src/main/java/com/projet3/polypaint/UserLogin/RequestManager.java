package com.projet3.polypaint.UserLogin;

import com.projet3.polypaint.Chat.Conversation;
import com.projet3.polypaint.SocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestManager {
    private final int TIMEOUT_DELAY = 5;
    private final String PORT =":3000";

    private String url;
    private String ip;
    private String sessionID;
    //private ArrayList<Conversation> userConversations;
    private UserInformation user;


    public static RequestManager currentInstance;

    public RequestManager(String ip_) {
        ip = ip_;
        //userConversations = new ArrayList<>();
        sessionID = null;
        user = null;
    }

    public boolean requestLogin(UserInformation userInformation_) {
        user = userInformation_;
        url = formatUrl(Request.Connection, null);
        UserPostTask loginTask = new UserPostTask();
        loginTask.execute(url);
        boolean response = false;
        try{
            response = configureLoginResponse(loginTask.get(TIMEOUT_DELAY, TimeUnit.SECONDS));
            if (response){
                UserManager.currentInstance = new UserManager(user);
                SocketManager.currentInstance = new SocketManager(ip, sessionID);
            }
            else
                user = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return response;
    }
    private boolean configureLoginResponse(String response_){
        sessionID = response_;
        return response_.isEmpty() ? false : true;
    }

    public ArrayList<Conversation> fetchUserConversations(UserInformation userInformation_) {
        url = formatUrl(Request.Conversations,null);
        UserGetTask task = new UserGetTask();
        task.execute(url);
        try{
            ArrayList<Conversation> userConversations = configureFetchConversationsResponse(task.get(TIMEOUT_DELAY, TimeUnit.SECONDS));
            if (!userConversations.isEmpty())
                UserManager.currentInstance.setUserConversations(userConversations);
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
                     SocketManager.currentInstance.joinConversation(name);
                 }
             }
             return conversations;
        }
    }
    public boolean addUserConversation(String name){
        url = formatUrl(Request.Conversations, name);
        UserPostTask task = new UserPostTask();
        task.execute(url);
        try{
            boolean response = configureAddConversationResponse(task.get(TIMEOUT_DELAY, TimeUnit.SECONDS));
            if (response)
                SocketManager.currentInstance.joinConversation(name);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean configureAddConversationResponse(String response){
        boolean ret = response != null && response.contains("409") ? false: true;
        return ret;
    }

    private String formatUrl(String request, String information){
        String formatUrl = null;
        switch (request){
            case Request.Connection:
                formatUrl = "http://" + ip + PORT + request + user.getUsername()+ "/"
                    + user.getPassword();
                break;
            case Request.Conversations:
                if (information == null)
                    formatUrl = "http://" + ip + PORT + request + sessionID + "/" + user.getUsername();
                else
                    formatUrl = "http://" + ip + PORT + request + sessionID + "/" + user.getUsername() + '/' + information;
                    break;
        }
        return formatUrl;
    }
   // public final String getSessionId(){
  //      return sessionID;
    //}

}

final class Request {

    public static final String Connection = "/connection/login/";
    public static final String Sign_Up = "/connection/signup/";
    public static final String Conversations = "/api/chat/";


}
