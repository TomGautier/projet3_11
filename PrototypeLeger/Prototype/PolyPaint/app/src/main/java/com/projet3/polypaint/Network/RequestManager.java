package com.projet3.polypaint.Network;

import com.projet3.polypaint.Chat.Conversation;
import com.projet3.polypaint.UserList.User;
import com.projet3.polypaint.UserLogin.UserInformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.projet3.polypaint.USER.UserJsonPostTask;

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
                FetchManager.currentInstance = new FetchManager(user);
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

        return (response_ == null || response_.isEmpty()) ? false : true;
    }

    public ArrayList<Conversation> fetchUserConversations() {
        url = formatUrl(Request.Conversations,null);
        UserGetTask task = new UserGetTask();
        task.execute(url);
        try{
            ArrayList<Conversation> userConversations = configureFetchConversationsResponse(task.get(TIMEOUT_DELAY, TimeUnit.SECONDS));
            if (!userConversations.isEmpty())
                FetchManager.currentInstance.setUserConversations(userConversations);
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
                 String name;
                 try {
                     jsonObject = jsons.getJSONObject(i);
                     name = jsonObject.getString("name");
                     if (!name.isEmpty()){
                         conversations.add(new Conversation(name, new ArrayList()));
                         SocketManager.currentInstance.joinConversation(name);
                     }
                 } catch (JSONException e) {
                     e.printStackTrace();
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
    private boolean configureAddConversationResponse(String response){
        return response != null && !response.contains("409");
    }

    public ArrayList<JSONObject> fetchPublicGallery() {
        return fetchGalleryContent(Request.ImagesCommon);
    }

    public ArrayList<JSONObject> fetchPrivateGallery() {
        return fetchGalleryContent(Request.Images);
    }

    private ArrayList<JSONObject> fetchGalleryContent(String request) {
        url = formatUrl(request,null);
        UserGetTask task = new UserGetTask();
        task.execute(url);
        try{
            return configureFetchGalleryResponse(task.get(TIMEOUT_DELAY, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<User> fetchUsers(){
        url = formatUrl(Request.Users_Fetch,null);
        UserGetTask getTask = new UserGetTask();
        getTask.execute(url);
        try{
            ArrayList<User> users = configureFetchUsersResponse(getTask.get(TIMEOUT_DELAY,TimeUnit.SECONDS));
            if (users != null){
                FetchManager.currentInstance.setUsers(users);
            }
            return users;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<JSONObject> configureFetchGalleryResponse(JSONArray jsons){
        ArrayList<JSONObject> images = new ArrayList<>();
        if (jsons == null || jsons.length() == 0) {
            return images;
        }
        else{
            JSONObject image;
            for (int i = 0; i < jsons.length(); i ++){
                try {
                    image = jsons.getJSONObject(i);
                    if (image != null)
                        images.add(image);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return images;
        }
    }

    public void postImage(JSONObject image) {
        url = formatUrl(Request.Images,null);
        UserJsonPostTask task = new UserJsonPostTask();
        task.setData(image);
        task.execute(url);
        try {
            boolean response = task.get(TIMEOUT_DELAY, TimeUnit.SECONDS) != null;

            if (response)
                System.out.println("Successfully added image");
            else
                System.out.println("Couldn't successfully add image");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<User> configureFetchUsersResponse(JSONArray jsons) {
        ArrayList<User> users = new ArrayList<>();
        if (jsons == null || jsons.length() == 0)
            return users;
        else {
            try {
                for (int i = 0; i < jsons.length(); i++) {
                    JSONObject jsonObject = jsons.getJSONObject(i);
                    String name = jsonObject.getString("username");
                    boolean connected = jsonObject.getBoolean("connected");
                    if (!user.getUsername().equals(name))
                        users.add(new User(name, connected));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return users;
        }
    }

    private String formatUrl(String request, String information) {
        String formatUrl;

        if (request.equals(Request.Connection))
            formatUrl = "http://" + ip + PORT + request + user.getUsername() + "/"
                    + user.getPassword();
        else if (information == null)
            formatUrl = "http://" + ip + PORT + request + sessionID + "/" + user.getUsername();
        else
            formatUrl = "http://" + ip + PORT + request + sessionID + "/" + user.getUsername() + '/' + information;

        return formatUrl;
    }

}

final class Request {

    public static final String Connection = "/connection/login/";
    public static final String Sign_Up = "/connection/signup/";
    public static final String Conversations = "/api/chat/";
    public static final String Images = "/api/images/";
    public static final String ImagesCommon = "/api/images/common/";
    public static final String Users_Fetch ="/api/user/";


}
