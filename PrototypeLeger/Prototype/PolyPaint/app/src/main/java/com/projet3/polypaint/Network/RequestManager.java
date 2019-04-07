package com.projet3.polypaint.Network;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Base64;

import com.projet3.polypaint.CanvasElement.Comment;
import com.projet3.polypaint.CanvasElement.GenericShape;
import com.projet3.polypaint.CanvasElement.PaintStyle;
import com.projet3.polypaint.CanvasElement.TextBox;
import com.projet3.polypaint.CanvasElement.UMLActivity;
import com.projet3.polypaint.CanvasElement.UMLArtefact;
import com.projet3.polypaint.CanvasElement.UMLClass;
import com.projet3.polypaint.CanvasElement.UMLPhase;
import com.projet3.polypaint.CanvasElement.UMLRole;
import com.projet3.polypaint.Chat.Conversation;
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
        return fetchGalleryContent(Request.ImagesCommon); // TODO: replace with line below
        //return fetchGalleryContent("public");
    }
    public ArrayList<JSONObject> fetchPrivateGallery() {
        return fetchGalleryContent(Request.Images);// TODO: replace with line below
        //return fetchGalleryContent("private");
    }
    private ArrayList<JSONObject> fetchGalleryContent(String request) {
        url = formatUrl(request,null); // TODO: replace with line below
        //url = formatUrl(Request.Images, request);
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

    public void postImage(String id, String visibility, String protection, String author) {
        url = formatUrl(Request.Images, null);
        UserJsonPostTask task = new UserJsonPostTask();

        JSONObject image = new JSONObject();
        try {
        image.put("id", id)
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
        url = formatUrl(Request.Thumbnail, imageID);
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

        try {
            boolean response = task.get(TIMEOUT_DELAY, TimeUnit.SECONDS) != null;

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
        }
    }

    public ArrayList<GenericShape> getAllShapes(String imageId) {
        url = formatUrl(Request.Shapes, imageId);
        UserGetTask getTask = new UserGetTask();
        getTask.execute(url);

        try{
            ArrayList<GenericShape> shapes = configureGetAllShapesResponse(getTask.get(TIMEOUT_DELAY,TimeUnit.SECONDS));

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
    private ArrayList<GenericShape> configureGetAllShapesResponse(JSONArray jsons) {
        ArrayList<GenericShape> shapes = new ArrayList<>();
        if (jsons == null || jsons.length() == 0)
            return shapes;
        else {
            try {
                for (int i = 0; i < jsons.length(); i++) {
                    JSONObject object = jsons.getJSONObject(i);
                    JSONObject properties = object.getJSONObject("properties");
                    JSONArray position = properties.getJSONArray("middlePointCoord");
                    int width = properties.getInt("width");
                    int height = properties.getInt("height");
                    float rotation = (float) properties.getDouble("rotation");

                    String text = "";
                    String attributes = "";
                    String methods = "";
                    // TODO: Check that the shape contains the text fields and fill them accordingly
                    /*if (properties.has("label"))
                        text = properties.getString("label");
                    if (properties.has("attributes"))
                        attributes = properties.getString("attributes");
                    if (properties.has("methods"))
                        attributes = properties.getString("methods");*/

                    int borderColor = Integer.decode(properties.getString("borderColor")) + 0xff000000;
                    int backgroundColor = Integer.decode(properties.getString("fillingColor")) + 0xff000000;

                    Paint borderPaint = new Paint();
                    borderPaint.setColor(borderColor);
                    Paint backgroundPaint = new Paint();
                    backgroundPaint.setColor(backgroundColor);

                    PaintStyle.StrokeType strokeType = PaintStyle.StrokeType.full;
                    //TODO: reactivate this part when server implements strokeType field.
                    //PaintStyle.StrokeType strokeType = PaintStyle.StrokeType.valueOf(properties.getString("strokeType"));

                    PaintStyle style = new PaintStyle(borderPaint, backgroundPaint, new Paint(borderPaint), strokeType);

                    GenericShape nShape = null;
                    switch (properties.getString("type")) {
                        case "Activity" :
                            nShape = new UMLActivity(object.getString("id"), position.getInt(0), position.getInt(1), width, height, style, rotation);
                            break;
                        case "Artefact" :
                            nShape = new UMLArtefact(object.getString("id"), position.getInt(0), position.getInt(1), width, height, style, rotation);
                            break;
                        case "Comment" :
                            nShape = new Comment(object.getString("id"), position.getInt(0), position.getInt(1), width, height,style, text, rotation);
                            break;
                        case "Phase" :
                            nShape = new UMLPhase(object.getString("id"), position.getInt(0), position.getInt(1), width, height, style, text, rotation);
                            break;
                        case "Role" :
                            nShape = new UMLRole(object.getString("id"), position.getInt(0), position.getInt(1), width, height, style, rotation);
                            break;
                        case "text_box" :
                            nShape = new TextBox(object.getString("id"), position.getInt(0), position.getInt(1), width, height,style, text, rotation);
                            break;
                        case "UmlClass" :
                            nShape = new UMLClass(object.getString("id"), position.getInt(0), position.getInt(1), width, height, text, attributes, methods, style, rotation);
                            break;
                        default:
                            System.out.println("Unrecognised shape type when loading drawing session : " + properties.getString("type"));
                    }

                    if (nShape != null) shapes.add(nShape);
                    else System.out.println("Shape is null!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return shapes;
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
    public static final String Thumbnail = "/api/images/thumbnail/";
    public static final String Users_Fetch ="/api/user/";
    public static final String Shapes ="/api/shapes/";
    public static final String ConnectionShapes ="/api/shapes/connections/";

}
