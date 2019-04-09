package com.projet3.polypaint.Network;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.projet3.polypaint.CanvasElement.Comment;
import com.projet3.polypaint.CanvasElement.ConnectionForm;
import com.projet3.polypaint.CanvasElement.GenericShape;
import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.CanvasElement.TextBox;
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLPhase;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.Chat.Conversation;
import com.projet3.polypaint.DrawingCollabSession.CollabShape;
import com.projet3.polypaint.UserList.User;
import com.projet3.polypaint.UserLogin.UserInformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
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
        url = formatUrl(Request.Connection, null,null);
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
    private ArrayList<Conversation> retrieveHistory(ArrayList<Conversation> olds, ArrayList<Conversation> news){
        for (int i = 0; i < news.size(); i++){
            for (int j = 0; j < olds.size(); j++){
                if (news.get(i).getName().equals(olds.get(j).getName())){
                    news.get(i).setHistory(olds.get(j).getHistory());
                    break;
                }
            }
        }
        return news;
    }
    public ArrayList<Conversation> fetchUserConversations() {
        ArrayList<Conversation> oldConversations = FetchManager.currentInstance.getUserConversations();
        url = formatUrl(Request.Conversations,null,null);
        UserGetTask task = new UserGetTask();
        task.execute(url);
        try{
            ArrayList<Conversation> userConversations = configureFetchConversationsResponse(task.get(TIMEOUT_DELAY, TimeUnit.SECONDS));
            ArrayList<Conversation> conversations = retrieveHistory(oldConversations,userConversations);
            if (!conversations.isEmpty())
                FetchManager.currentInstance.setUserConversations(conversations);
            return conversations;
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
        Conversation general = null;
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
                         Conversation convo = new Conversation(name, new ArrayList());
                         conversations.add(convo);
                         if (name.equals("General")){
                             general = convo;
                             SocketManager.currentInstance.joinConversation(name);
                         }
                        // else
                            // conversations.add(convo);
                     }
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }

             }
             //conversations.remove(general);
             conversations.set(0,general);
             return conversations;
        }
    }
    public boolean postSignUp(String username, String password){
        String url = formatUrl(Request.Sign_Up,username,password);
        boolean response = false;
        UserPostTask task = new UserPostTask();
        task.execute(url);
        try{
            response = configureSignUpResponse(task.get(TIMEOUT_DELAY,TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return response;

    }
    public boolean configureSignUpResponse(String response_){
        return (response_ == null || response_.isEmpty()) || response_.equals("400") ? false : true;
    }

    public boolean addUserConversation(String name){
        url = formatUrl(Request.Conversations, name,null);
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

    public ArrayList<User> fetchUsers(){
        url = formatUrl(Request.Users_Fetch,null,null);
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

    public ArrayList<JSONObject> fetchPublicGallery() {
        return fetchGalleryContent("public");
    }
    public ArrayList<JSONObject> fetchPrivateGallery() {
        return fetchGalleryContent("private");
    }
    private ArrayList<JSONObject> fetchGalleryContent(String request) {
        url = formatUrl(Request.Images, request,null);
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

    public String getImagePassword(String imageId) {
        url = formatUrl(Request.SingleImage, imageId,null);
        SingleObjectGetTask task = new SingleObjectGetTask();
        task.execute(url);
        try{
            return configureGetImagePassword(task.get(TIMEOUT_DELAY, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String configureGetImagePassword(JSONObject obj) {
        String password = "";

        try {
            //JSONObject obj = jsons.getJSONObject(0);

            if (obj.has("protection"))
                password = obj.getString("protection");
        } catch (JSONException e) { /*Do nothing*/ }

        return password;
    }

    public void postImage(String id, String visibility, String protection, String author) {
        url = formatUrl(Request.Images, null,null);
        UserJsonPostTask task = new UserJsonPostTask();

        JSONObject image = new JSONObject();
        try {
        image.put("imageId", id)
                .put("visibility", visibility)
                .put("protection", protection)
                .put("author", author);
        task.setData(image);
        task.execute(url);
        } catch (JSONException e) {e. printStackTrace(); }

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

    public void postThumbnail(Bitmap bitmap, String imageID) {
        url = formatUrl(Request.Thumbnail, imageID,null);
        UserJsonPostTask task = new UserJsonPostTask();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        byte[] bytes = os.toByteArray();
        String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);

        JSONObject body = new JSONObject();
        try {
            body.put("thumbnailTimestamp", (new Date()).getTime());
            body.put("thumbnail", encoded);
            task.setData(body);
            task.execute(url);
        } catch (JSONException e) { e.printStackTrace(); }

       /* try {
            //boolean response = task.get(TIMEOUT_DELAY, TimeUnit.SECONDS) != null;
           // task.get();

            if (response)
                System.out.println("Successfully posted thumbnail");
            else
                System.out.println("Couldn't successfully post thumbnail");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }*/
    }

    public ArrayList<CollabShape> getAllShapes(String imageId) {
        url = formatUrl(Request.Shapes, imageId,null);
        UserGetTask getTask = new UserGetTask();
        getTask.execute(url);

        try{
            ArrayList<CollabShape> shapes = configureGetAllShapesResponse(getTask.get(TIMEOUT_DELAY,TimeUnit.SECONDS));

            return shapes;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
    private ArrayList<CollabShape> configureGetAllShapesResponse(JSONArray jsons) {
        ArrayList<CollabShape> shapes = new ArrayList<>();
        if (jsons == null || jsons.length() == 0)
            return shapes;
        else {
            try {
                for (int i = 0; i < jsons.length(); i++){
                    shapes.add(new CollabShape(jsons.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return shapes;
        }
    }

    private String formatUrl(String request, String information, String information2) {
        String formatUrl;

        if (request.equals(Request.Connection))
            formatUrl = "http://" + ip + PORT + request + user.getUsername() + "/"
                    + user.getPassword();
        else if (request.equals(Request.Sign_Up))
            formatUrl = "http://" + ip + PORT + request + information + "/" + information2;
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
    public static final String SingleImage = "/api/images/single/";
    public static final String ImagesCommon = "/api/images/common/";
    public static final String Thumbnail = "/api/images/thumbnail/";
    public static final String Users_Fetch ="/api/user/";
    public static final String Shapes ="/api/shapes/";
    public static final String ConnectionShapes ="/api/shapes/connections/";

}
