package com.projet3.polypaint.User;

import org.json.JSONObject;

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
    private ArrayList<JSONObject> userConversations;


    public static UserManager currentInstance;

    public UserManager(String ip_) {
        ip = ip_;
    }

    public Boolean requestLogin(UserInformation userInformation_) {
        url = formatUrl(userInformation_, Request.Connection);
        UserLoginTask loginTask = new UserLoginTask();
        loginTask.execute(url);
        try{
            return configureLoginResponse(loginTask.get(TIMEOUT_DELAY, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void fetchUserConversations(UserInformation userInformation_) {
        url = formatUrl(userInformation_, Request.Fetch_Conversations);
        new UserFetchConversationsTask().execute(url);
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

    public void setUserConversations(ArrayList<JSONObject> conversations) {
        userConversations = conversations;
    }
    public final ArrayList<JSONObject> getUserconversation() {
        return userConversations;
    }

}

final class Request {

    public static final String Connection = "/connection/login/";
    public static final String Sign_Up = "/connection/signup/";
    public static final String Fetch_Conversations = "/api/chat/";

}
