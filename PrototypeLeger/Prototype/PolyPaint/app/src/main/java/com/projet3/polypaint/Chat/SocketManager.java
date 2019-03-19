package com.projet3.polypaint.Chat;

import com.google.gson.JsonObject;
import com.projet3.polypaint.CollabShape;
import com.projet3.polypaint.CollabShapeProperties;
import com.projet3.polypaint.USER.UserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager  {

    //Chat
    public final String SENDMESSAGE_TAG = "MessageSent";
    public final String LOGINATTEMPT_TAG = "LoginAttempt";
    public final String USEREXIST_TAG = "UsernameAlreadyExists";
    public final String USERLOGGED_TAG = "UserLogged";
    public final String USERLEFT_TAG = "UserLeft";
    public final String JOIN_CONVERSATION_TAG = "UserJoinedConversation";
    public final String LEAVE_CONVERSATION_TAG = "UserLeftConversation";

    //Session collaborative
    public final String JOIN_COLLAB_SESSION_TAG = "JoinDrawingSession";
    public final String ADD_FORM_TAG = "AddElement";
    public final String DELETE_FORM_TAG = "DeleteElements";
    public final String MODIFY_FORM_TAG = "ModifyElement";
    public final String SELECT_FORM_TAG = "SelectElements";
    public final String REZIZE_CANVAS_TAG = "ResizeCanvas";





    private final String DATE_TAG = "date";
    private final String USERNAME_TAG = "username";
    private final String MESSAGE_TAG = "message";
    private final String SESSION_ID_TAG = "sessionId";
    private final String CONVERSATION_ID_TAG = "conversationId";

    public static SocketManager currentInstance;
    private Socket socket;
    private NewMessageListener newMessagelistener;

    private String uri;
    private String sessionId;

    public SocketManager(String ipAddress_, String sessionId_) {
        uri = formatIpToUri(ipAddress_);
        sessionId = sessionId_;
        setupSocket();
    }


    public void setupNewMessageListener(NewMessageListener listener_) {
        newMessagelistener = listener_;
    }
    private void setupSocket() {
        try {
            socket = IO.socket(uri);
            socket.connect();


            socket.on(SENDMESSAGE_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String date = "";
                    String message = "";
                    String username = "";
                    try {
                        //JSONObject json = new JSONObject((JSONObject)args[0]);
                        JSONObject json = (JSONObject) args[0];
                        date = json.getString(DATE_TAG);
                        username = json.getString(USERNAME_TAG);
                        message = json.getString(MESSAGE_TAG);
                    }
                    catch(JSONException e) {}
                    newMessagelistener.onNewMessage(formatMessage(date,username,message));
                }
            });

        }catch(Exception e) {}

    }

    public void sendMessage(String conversationId, String message) {
        JSONObject args = null;
       // JSONObject messageJSON = null;
        try {
           // messageJSON = new JSONObject().put(DATE_TAG,date).put(USERNAME_TAG, RequestManager.currentInstance.getUserUsername()).put(MESSAGE_TAG, message);
            args = new JSONObject().put(SESSION_ID_TAG, sessionId).put(USERNAME_TAG, UserManager.currentInstance.getUserUsername())
                    .put(CONVERSATION_ID_TAG, conversationId).put(MESSAGE_TAG, message);
        }catch(JSONException e) {}

        if (args != null)
            socket.emit(SENDMESSAGE_TAG, args);

    }
    public void joinConversation(String conversationID){
        JSONObject json = null;
        try{
           json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG, UserManager.currentInstance.getUserUsername())
                    .put(CONVERSATION_ID_TAG, conversationID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(JOIN_CONVERSATION_TAG, json);
    }
    public void leaveConversation(String conversationID){
        JSONObject json = null;
        try{
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG, UserManager.currentInstance.getUserUsername())
                    .put(CONVERSATION_ID_TAG, conversationID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(LEAVE_CONVERSATION_TAG, json);
    }
    //SESSION COLLABORATIVE

    public void joinCollabSession(String name){
        socket.emit(JOIN_COLLAB_SESSION_TAG, name);
    }
    public void addForm(CollabShape shape){
        JSONObject json = null;
        JSONObject shapeJson = null;
        JSONObject shapePropertiesJson = null;
        try {
            shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shape.getProperties().getType())
                    .put(CollabShapeProperties.FILLING_COLOR_TAG, shape.getProperties().getFillingColor())
                    .put(CollabShapeProperties.BORDER_COLOR_TAG, shape.getProperties().getBorderColor())
                    .put(CollabShapeProperties.MIDDLE_POINTX_TAG, shape.getProperties().getMiddlePointCoordX())
                    .put(CollabShapeProperties.MIDDLE_POINTY_TAG, shape.getProperties().getMiddlePointCoordY())
                    .put(CollabShapeProperties.HEIGHT_TAG, shape.getProperties().getHeight())
                    .put(CollabShapeProperties.WIDTH_TAG, shape.getProperties().getWidth())
                    .put(CollabShapeProperties.ROTATION_TAG, shape.getProperties().getRotation());

            shapeJson = new JSONObject().put(CollabShape.ID_TAG, shape.getId())
                    .put(CollabShape.DRAWING_SESSION_TAG, shape.getDrawingSessionId())
                    .put(CollabShape.AUTHOR_TAG, shape.getAuthor())
                    .put(CollabShape.PROPERTIES_TAG, shapePropertiesJson);
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG, UserManager.currentInstance.getUserUsername())
                    .put("shape", shapeJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json != null)
            socket.emit(ADD_FORM_TAG, json.toString());
    }
    public void modifyForms(CollabShape[] shapes){
        JSONArray array = new JSONArray();
        JSONObject json = null;
        try {
            for (int i = 0; i < shapes.length; i++) {
                JSONObject shapeJson = null;
                JSONObject shapePropertiesJson = null;

                shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shapes[i].getProperties().getType())
                        .put(CollabShapeProperties.FILLING_COLOR_TAG, shapes[i].getProperties().getFillingColor())
                        .put(CollabShapeProperties.BORDER_COLOR_TAG, shapes[i].getProperties().getBorderColor())
                        .put(CollabShapeProperties.MIDDLE_POINTX_TAG, shapes[i].getProperties().getMiddlePointCoordX())
                        .put(CollabShapeProperties.MIDDLE_POINTY_TAG, shapes[i].getProperties().getMiddlePointCoordY())
                        .put(CollabShapeProperties.HEIGHT_TAG, shapes[i].getProperties().getHeight())
                        .put(CollabShapeProperties.WIDTH_TAG, shapes[i].getProperties().getWidth())
                        .put(CollabShapeProperties.ROTATION_TAG, shapes[i].getProperties().getRotation());

                shapeJson = new JSONObject().put(CollabShape.ID_TAG, shapes[i].getId())
                        .put(CollabShape.DRAWING_SESSION_TAG, shapes[i].getDrawingSessionId())
                        .put(CollabShape.AUTHOR_TAG, shapes[i].getAuthor())
                        .put(CollabShape.PROPERTIES_TAG, shapePropertiesJson);

                if (shapeJson != null && shapePropertiesJson != null)
                    array.put(shapeJson);
            }
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG, UserManager.currentInstance.getUserUsername())
                    .put("shapes", array);
        }catch (JSONException e) {
                e.printStackTrace();
            }
        if (array != null && array.length() > 0 )
            socket.emit(DELETE_FORM_TAG, json.toString());
    }
   /* public void deleteForm(){
        socket.emit(MODIFY_FORM_TAG,);
    }
    public void selectForm(){
        socket.emit(SELECT_FORM_TAG, );
    }
    public void resizeCanvas(){
        socket.emit(REZIZE_CANVAS_TAG, );
    }*/



    public boolean isConnected() {
        return socket.connected();
    }

    public void leave(String username) {
        socket.emit(USERLEFT_TAG, username);
        socket.disconnect();
    }

    private String formatIpToUri(String ip) {
        return "http://" + ip + ":3000/";
    }
    private String formatMessage(String date,String username, String message){
        return date + " " + "[ " + username + " ] : " + message;
    }




}
