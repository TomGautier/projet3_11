package com.projet3.polypaint.Network;


import com.projet3.polypaint.Chat.ChatListener;
import com.projet3.polypaint.DrawingCollabSession.CollabConnectionProperties;
import com.projet3.polypaint.DrawingCollabSession.CollabShape;
import com.projet3.polypaint.DrawingCollabSession.CollabShapeProperties;
import com.projet3.polypaint.DrawingCollabSession.DrawingCollabSessionListener;
import com.projet3.polypaint.HomeActivityListener;
import com.projet3.polypaint.UserList.User;
import com.projet3.polypaint.UserList.UsersListListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager  {

    //chat
    public final String SENDMESSAGE_TAG = "MessageSent";
    public final String LOGINATTEMPT_TAG = "LoginAttempt";
    public final String USEREXIST_TAG = "UsernameAlreadyExists";
    public final String USERLOGGED_TAG = "UserLogged";
    public final String USERLEFT_TAG = "UserLeft";
    public final String JOIN_CONVERSATION_TAG = "UserJoinedConversation";
    public final String LEAVE_CONVERSATION_TAG = "UserLeftConversation";

    //Session collaborative
    public final String NEW_USER_JOINED = "NewUserJoined";
    public final String CREATE_COLLAB_SESSION_TAG = "CreateDrawingSession";
    public final String CREATED_COLLAB_SESSION_TAG = "CreatedDrawingSession";
    public final String JOIN_COLLAB_SESSION_TAG = "JoinDrawingSession";
    public final String JOINED_COLLAB_SESSION_TAG = "JoinedDrawingSession";
    public final String ADD_FORM_TAG = "AddElement";
    public final String ADDED_FORM_TAG = "AddedElement";
    public final String DELETE_FORM_TAG = "DeleteElements";
    public final String DELETED_FORM_TAG = "DeletedElements";
    public final String MODIFY_FORM_TAG = "ModifyElement";
    public final String MODIFIED_FORM_TAG = "ModifiedElement";
    public final String SELECT_FORM_TAG = "SelectElements";
    public final String SELECTED_FORM_TAG = "SelectedElements";
    public final String REZIZE_CANVAS_TAG = "ResizeCanvas";
    public final String REZIZED_CANVAS_TAG = "ResizedCanvas";
    public final String CUT_FORMS_TAG = "CutElements";
    public final String CUTED_FORMS_TAG = "CutedElements";
    public final String DUPLICATE_FORMS_TAG = "DuplicateElements";
    public final String DUPLICATED_FORMS_TAG = "DuplicatedElements";
    public final String DUPLICATE_CUT_FORMS_TAG = "DuplicateCutElements";
    public final String DUPLICATED_CUT_FORMS_TAG = "DuplicatedCutElements";
    public final String STACK_FORM_TAG = "StackElement";
    public final String STACKED_FORM_TAG = "StackedElement";
    public final String UNSTACK_FORM_TAG = "UnstackElement";
    public final String UNSTACKED_FORM_TAG = "UnstackedElement";
    public final String INVITE_TO_CONVERSATION = "InviteToConversation";
    public final String INVITE_TO_DRAWING_SESSION = "InviteToDrawingSession";
    public final String INVITED_TO_CONVERSATION = "InvitedToConversation";
    public final String INVITED_TO_DRAWING_SESSION = "InvitedToDrawingSession";
    public final String RESPOND_TO_DRAWING_SESSION_INVITATION = "RespondToDrawingInvite";
    public final String RESPOND_TO_CONVERSATION_INVITATION = "RespondToConversationInvite";
    public final String RESPONDED_TO_DRAWING_SESSION_INVITATION = "RespondedToDrawingInvite";
    public final String RESPONDED_TO_CONVERSATION_INVITATION = "RespondedToConversationInvite";
    //Liste d'utilisateurs
    public final String NEW_USER_CONNECTED_TAG = "UserJoinedChat";




    //Properties
    public final String RESPONSE_TO_INVITATION = "response";
    private final String NEW_CANVAS_DIMENSIONS = "newCanvasDimensions";
    private final String ELEMENT_ID_TAG ="elementId";
    private final String ELEMENTS_IDS_TAG ="elementIds";
    private final String IMAGE_TAG = "image";
    private final String VISIBILITY_TAG = "visibility";
    private final String NAME_TAG = "name";
    private final String DATE_TAG = "date";
    private final String USERNAME_TAG = "username";
    private final String INVITED_USERNAME_TAG = "invitedUsername";

    private final String MESSAGE_TAG = "message";
    private final String SESSION_ID_TAG = "sessionId";
    private final String CONVERSATION_ID_TAG = "conversationId";
    private final String USER_CONNECTED_TAG = "UserConnected";


    public static SocketManager currentInstance;
    private Socket socket;
    private ChatListener chatListener;
    private DrawingCollabSessionListener drawingCollabSessionListener;
    private UsersListListener usersListListener;
    private HomeActivityListener homeListener;

    private String uri;
    private String sessionId;
    private String imageId;

    public SocketManager(String ipAddress_, String sessionId_) {
        uri = formatIpToUri(ipAddress_);
        sessionId = sessionId_;
        setupSocket();
    }
    public void setupHomeListener(HomeActivityListener listener_) {
        homeListener = listener_;
    }

    public void setupChatListener(ChatListener listener_) {
        chatListener = listener_;
    }
    public void setupDrawingCollabSessionListener(DrawingCollabSessionListener listener_) {
        drawingCollabSessionListener = listener_;
    }
    public void setupUsersListListener(UsersListListener listener_){
        usersListListener = listener_;
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
                    String conversation = "";
                    try {
                        //JSONObject json = new JSONObject((JSONObject)args[0]);
                        JSONObject json = (JSONObject)args[0];
                        date = json.getString(DATE_TAG);
                        username = json.getString(USERNAME_TAG);
                        message = json.getString(MESSAGE_TAG);
                        conversation = json.getString(CONVERSATION_ID_TAG);
                    }
                    catch(JSONException e) {}
                    chatListener.onNewMessage(formatMessage(date,username,message), conversation);
                }
            });
            socket.on(NEW_USER_CONNECTED_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //try{
                    //JSONObject obj = (JSONObject)args[0];
                    //String username = obj.getString(USERNAME_TAG);
                    String username = (String)args[0];
                    if (usersListListener != null)
                        usersListListener.onUserConnected(username);
                    //} //catch (JSONException e) {
                    // e.printStackTrace();
                    //}
                }
            });
            socket.on(USERLEFT_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String username = (String)args[0];
                    if (usersListListener != null)
                        usersListListener.onUserDisconnected(username);
                }
            });
            socket.on(CREATED_COLLAB_SESSION_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //imageId = (String)args[0];
                }
            });
            socket.on(JOINED_COLLAB_SESSION_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                   // imageId = (String)args[0];
                    //drawingCollabSessionListener.onJoinedSession(imageId);
                }
            });
            socket.on(NEW_USER_JOINED, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONArray array = (JSONArray)args[0];
                    if (array != null){
                        String[] players = new String[array.length()];
                        try{
                            for (int i = 0; i < array.length(); i++){
                                players[i] = array.getString(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        drawingCollabSessionListener.onNewUserJoined(players);
                    }
                }
            });
            socket.on(SELECTED_FORM_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject obj = (JSONObject)args[0];
                        JSONArray olds = obj.getJSONArray("oldElementIds");
                        JSONArray news = obj.getJSONArray("newElementIds");
                        String author = obj.getString(USERNAME_TAG);
                        String[] stringOlds = new String[olds.length()];
                        String[] stringNews = new String[news.length()];
                        for (int i = 0; i < olds.length(); i++)
                            stringOlds[i] = (String)olds.get(i);
                        for (int j = 0; j < news.length(); j++)
                            stringNews[j] = (String)news.get(j);
                        drawingCollabSessionListener.onSelectedElements(stringOlds, stringNews,author);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            socket.on(ADDED_FORM_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject) args[0];

                    try{
                        JSONObject shape = obj.getJSONObject("shape");
                        String author = obj.getString(USERNAME_TAG);
                        drawingCollabSessionListener.onAddElement(new CollabShape(shape), author);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on(DUPLICATED_FORMS_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        JSONArray array = obj.getJSONArray("shapes");
                        String author = obj.getString(USERNAME_TAG);
                        CollabShape[] shapes = new CollabShape[array.length()];
                        for (int i = 0; i < shapes.length; i++) {
                            shapes[i] = new CollabShape(array.getJSONObject(i));
                        }
                        drawingCollabSessionListener.onDuplicateElements(shapes, author);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            socket.on(DELETED_FORM_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        JSONArray array = (obj.getJSONArray(ELEMENTS_IDS_TAG));
                        String author = obj.getString(USERNAME_TAG);
                        String[] response = new String[array.length()];
                        for (int i = 0; i < array.length(); i++) {
                            response[i] = array.getString(i);
                        }
                        drawingCollabSessionListener.onDeleteElement(response, author);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on(CUTED_FORMS_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        JSONArray array = (obj.getJSONArray(ELEMENTS_IDS_TAG));
                        String author = obj.getString(USERNAME_TAG);
                        String[] response = new String[array.length()];
                        for (int i = 0; i < array.length(); i++) {
                            response[i] = array.getString(i);
                        }
                        drawingCollabSessionListener.onCutElements(response, author);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on(DUPLICATED_CUT_FORMS_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try{
                        JSONObject obj = (JSONObject)args[0];
                        JSONArray array = obj.getJSONArray("shapes");
                        String author = obj.getString(USERNAME_TAG);
                        CollabShape[] shapes = new CollabShape[array.length()];
                        for (int i = 0; i < shapes.length; i++){
                            shapes[i] = new CollabShape(array.getJSONObject(i));
                        }
                        drawingCollabSessionListener.onDuplicateCutElements(shapes,author);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on(MODIFIED_FORM_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try{
                        JSONObject obj = (JSONObject)args[0];
                        JSONArray array = obj.getJSONArray("shapes");
                        String author = obj.getString(USERNAME_TAG);
                        CollabShape[] shapes = new CollabShape[array.length()];
                        for (int i = 0; i < shapes.length; i++){
                            shapes[i] = new CollabShape(array.getJSONObject(i));
                        }
                        drawingCollabSessionListener.onModifyElements(shapes,author);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //drawingCollabSessionListener.onModifyElements((CollabShape[])args[0]);
                }
            });
            socket.on(STACKED_FORM_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject obj = (JSONObject) args[0];
                        String id = obj.getString(ELEMENT_ID_TAG);
                        String author = obj.getString(USERNAME_TAG);
                        drawingCollabSessionListener.onStackElement(id, author);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on(UNSTACKED_FORM_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject) args[0];
                    try{
                        JSONObject shape = obj.getJSONObject("shape");
                        String author = obj.getString(USERNAME_TAG);
                        drawingCollabSessionListener.onUnstackElement(new CollabShape(shape), author);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on(REZIZED_CANVAS_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject json = (JSONObject)args[0];
                    //JSONObject dimensions;
                    int x = 0;
                    int y = 0;
                    try{
                        //dimensions = json.getJSONObject(NEW_CANVAS_DIMENSIONS);
                        x = Integer.parseInt(json.getString("x"));
                        y = Integer.parseInt(json.getString("y"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    drawingCollabSessionListener.onResizeCanvas(x,y);

                }
            });
            socket.on(INVITED_TO_CONVERSATION, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject)args[0];
                    String username = "";
                    String conversation = "";
                    try{
                        username = obj.getString(USERNAME_TAG);
                        conversation = obj.getString(CONVERSATION_ID_TAG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    chatListener.onInviteToConversation(username,conversation);
                }
            });
            socket.on(INVITED_TO_DRAWING_SESSION, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject)args[0];
                    String username = "";
                    String imageId = "";
                    try{
                        username = obj.getString(USERNAME_TAG);
                        imageId = obj.getString(CollabShape.IMAGE_ID_TAG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    homeListener.onInviteToDrawingSession(username,imageId);
                }
            });
            socket.on(RESPONDED_TO_DRAWING_SESSION_INVITATION, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject) args[0];
                    boolean response = false;
                    String username = "";
                    String imageId = "";
                    try{
                        response = obj.getBoolean(RESPONSE_TO_INVITATION);
                        username = obj.getString(INVITED_USERNAME_TAG);
                        imageId = obj.getString(IMAGE_TAG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    homeListener.onResponseToDrawingSessionInvitation(username,imageId,response);
                }
            });
            socket.on(RESPONDED_TO_CONVERSATION_INVITATION, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject) args[0];
                    boolean response = false;
                    String username = "";
                    String conversation = "";
                    try{
                        response = obj.getBoolean(RESPONSE_TO_INVITATION);
                        username = obj.getString(INVITED_USERNAME_TAG);
                        conversation = obj.getString(CONVERSATION_ID_TAG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    chatListener.onResponseToConversationInvitation(username,conversation,response);
                }
            });


        }catch(Exception e) {}

    }

    public void sendMessage(String conversationId, String message) {
        JSONObject args = null;
        // JSONObject messageJSON = null;
        try {
            // messageJSON = new JSONObject().put(DATE_TAG,date).put(USERNAME_TAG, RequestManager.currentInstance.getUserUsername()).put(MESSAGE_TAG, message);
            args = new JSONObject().put(SESSION_ID_TAG, sessionId).put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put(CONVERSATION_ID_TAG, conversationId).put(MESSAGE_TAG, message);
        }catch(JSONException e) {}

        if (args != null)
            socket.emit(SENDMESSAGE_TAG, args.toString());

    }
    public void joinConversation(String conversationID){
        JSONObject json = null;
        try{
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put(CONVERSATION_ID_TAG, conversationID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(JOIN_CONVERSATION_TAG, json.toString());
    }

    public void leaveConversation(String conversationID){
        JSONObject json = null;
        try{
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put(CONVERSATION_ID_TAG, conversationID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(LEAVE_CONVERSATION_TAG, json);
    }
    //SESSION COLLABORATIVE ------------------------------------------------------------------------------------------------------------------


    public void joinCollabSession(String imageId){
        JSONObject json = null;
        try {
            json = new JSONObject().put(SESSION_ID_TAG, sessionId)
                    .put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put(CollabShape.IMAGE_ID_TAG, imageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(json != null){
            socket.emit(JOIN_COLLAB_SESSION_TAG, json.toString());
            this.imageId = imageId;
            this.drawingCollabSessionListener.onJoinedSession(this.imageId);
        }

       //socket.emit(JOIN_COLLAB_SESSION_TAG, imageId, FetchManager.currentInstance.getUserUsername());
       //this.imageId = imageId;
       //this.drawingCollabSessionListener.onJoinedSession(this.imageId);
    }

    public void addElement(CollabShape shape){
        JSONObject json = null;
        JSONObject shapeJson;
        JSONObject shapePropertiesJson;
        try {
            if (CollabConnectionProperties.class.equals(shape.getProperties().getClass())){
                CollabConnectionProperties properties = (CollabConnectionProperties)shape.getProperties();
                shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shape.getProperties().getType())
                        .put(CollabShapeProperties.FILLING_COLOR_TAG, shape.getProperties().getFillingColor())
                        .put(CollabShapeProperties.BORDER_COLOR_TAG, shape.getProperties().getBorderColor())
                        .put(CollabShapeProperties.MIDDLE_POINT_TAG, null)
                        .put(CollabShapeProperties.HEIGHT_TAG, properties.getThick())
                        .put(CollabShapeProperties.WIDTH_TAG, null)
                        .put(CollabShapeProperties.ROTATION_TAG, null)
                        .put(CollabConnectionProperties.CATEGORY_TAG, properties.getConnectionType())
                        .put(CollabConnectionProperties.VERTICESX_TAG,new JSONArray(properties.getVerticesX()))
                        .put(CollabConnectionProperties.VERTICESY_TAG,new JSONArray(properties.getVerticesY()))
                        .put(CollabConnectionProperties.ID_SHAPE1_TAG,properties.getIdShape1())
                        .put(CollabConnectionProperties.ID_SHAPE2_TAG,properties.getIdShape2())
                        .put(CollabConnectionProperties.INDEX1_TAG,properties.getIndex1())
                        .put(CollabConnectionProperties.INDEX2_TAG, properties.getIndex2())
                        .put(CollabConnectionProperties.Q1_TAG,properties.getQ1())
                        .put(CollabConnectionProperties.Q2_TAG,properties.getQ2());
            }
            else{
                shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shape.getProperties().getType())
                        .put(CollabShapeProperties.FILLING_COLOR_TAG, shape.getProperties().getFillingColor())
                        .put(CollabShapeProperties.BORDER_COLOR_TAG, shape.getProperties().getBorderColor())
                        .put(CollabShapeProperties.MIDDLE_POINT_TAG, new JSONArray(shape.getProperties().getMiddlePointCoord()))
                        .put(CollabShapeProperties.HEIGHT_TAG, shape.getProperties().getHeight())
                        .put(CollabShapeProperties.WIDTH_TAG, shape.getProperties().getWidth())
                        .put(CollabShapeProperties.ROTATION_TAG,shape.getProperties().getRotation())
                        .put(CollabShapeProperties.ATTRIBUTES_TAG,new JSONArray(shape.getProperties().getAttributes()))
                        .put(CollabShapeProperties.METHODS_TAG,new JSONArray(shape.getProperties().getMethods()))
                        .put(CollabShapeProperties.LABEL_TAG,shape.getProperties().getLabel())
                        .put(CollabShapeProperties.BORDER_TYPE_TAG,shape.getProperties().getBorderType());
            }

            shapeJson = new JSONObject().put(CollabShape.ID_TAG, shape.getId())
                    .put(CollabShape.IMAGE_ID_TAG, shape.getImageId())
                    .put(CollabShape.AUTHOR_TAG, shape.getAuthor())
                    .put(CollabShape.PROPERTIES_TAG, shapePropertiesJson);
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put("shape", shapeJson);

            System.out.println(json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json != null)
            socket.emit(ADD_FORM_TAG, json.toString());
    }
    public void modifyElements(CollabShape[] shapes){
        JSONArray array = new JSONArray();
        JSONObject json = null;
        try {
            for (int i = 0; i < shapes.length; i++) {
                JSONObject shapeJson = null;
                JSONObject shapePropertiesJson = null;
                if (CollabConnectionProperties.class.equals(shapes[i].getProperties().getClass())){
                    CollabConnectionProperties properties = (CollabConnectionProperties)shapes[i].getProperties();
                    shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shapes[i].getProperties().getType())
                            .put(CollabShapeProperties.FILLING_COLOR_TAG, shapes[i].getProperties().getFillingColor())
                            .put(CollabShapeProperties.BORDER_COLOR_TAG, shapes[i].getProperties().getBorderColor())
                            .put(CollabShapeProperties.MIDDLE_POINT_TAG, null)
                            .put(CollabShapeProperties.HEIGHT_TAG, properties.getThick())
                            .put(CollabShapeProperties.WIDTH_TAG, null)
                            .put(CollabShapeProperties.ROTATION_TAG, null)
                            .put(CollabConnectionProperties.CATEGORY_TAG, properties.getConnectionType())
                            .put(CollabConnectionProperties.VERTICESX_TAG,new JSONArray(properties.getVerticesX()))
                            .put(CollabConnectionProperties.VERTICESY_TAG,new JSONArray(properties.getVerticesY()))
                            .put(CollabConnectionProperties.ID_SHAPE1_TAG,properties.getIdShape1())
                            .put(CollabConnectionProperties.ID_SHAPE2_TAG,properties.getIdShape2())
                            .put(CollabConnectionProperties.INDEX1_TAG,properties.getIndex1())
                            .put(CollabConnectionProperties.INDEX2_TAG, properties.getIndex2())
                            .put(CollabConnectionProperties.Q1_TAG,properties.getQ1())
                            .put(CollabConnectionProperties.Q2_TAG,properties.getQ2());
                }
                else{
                    shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shapes[i].getProperties().getType())
                            .put(CollabShapeProperties.FILLING_COLOR_TAG, shapes[i].getProperties().getFillingColor())
                            .put(CollabShapeProperties.BORDER_COLOR_TAG, shapes[i].getProperties().getBorderColor())
                            .put(CollabShapeProperties.MIDDLE_POINT_TAG, new JSONArray(shapes[i].getProperties().getMiddlePointCoord()))
                            .put(CollabShapeProperties.HEIGHT_TAG, shapes[i].getProperties().getHeight())
                            .put(CollabShapeProperties.WIDTH_TAG, shapes[i].getProperties().getWidth())
                            .put(CollabShapeProperties.ROTATION_TAG, shapes[i].getProperties().getRotation())
                            .put(CollabShapeProperties.ATTRIBUTES_TAG,new JSONArray(shapes[i].getProperties().getAttributes()))
                            .put(CollabShapeProperties.METHODS_TAG,new JSONArray(shapes[i].getProperties().getMethods()))
                            .put(CollabShapeProperties.LABEL_TAG,shapes[i].getProperties().getLabel())
                            .put(CollabShapeProperties.BORDER_TYPE_TAG,shapes[i].getProperties().getBorderType());
                }


                shapeJson = new JSONObject().put(CollabShape.ID_TAG, shapes[i].getId())
                        .put(CollabShape.IMAGE_ID_TAG, shapes[i].getImageId())
                        .put(CollabShape.AUTHOR_TAG, shapes[i].getAuthor())
                        .put(CollabShape.PROPERTIES_TAG, shapePropertiesJson);

                //System.out.println("Drawing session id : " + shape.getDrawingSessionId());

                if (shapeJson != null && shapePropertiesJson != null)
                    array.put(shapeJson);
            }
            json = new JSONObject().put(CollabShape.IMAGE_ID_TAG, imageId).put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put("shapes", array);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        if (array != null && array.length() > 0 )
            socket.emit(MODIFY_FORM_TAG, json.toString());
    }
    //MEME CODE QUE modifyElements --- A CHANGER
    public void duplicateElements(CollabShape[] shapes){
        JSONArray array = new JSONArray();
        JSONObject json = null;
        try {
            for (int i = 0; i < shapes.length; i++) {
                JSONObject shapeJson = null;
                JSONObject shapePropertiesJson = null;
                if (CollabConnectionProperties.class.equals(shapes[i].getProperties().getClass())){
                    CollabConnectionProperties properties = (CollabConnectionProperties)shapes[i].getProperties();
                    shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shapes[i].getProperties().getType())
                            .put(CollabShapeProperties.FILLING_COLOR_TAG, shapes[i].getProperties().getFillingColor())
                            .put(CollabShapeProperties.BORDER_COLOR_TAG, shapes[i].getProperties().getBorderColor())
                            .put(CollabShapeProperties.MIDDLE_POINT_TAG, null)
                            .put(CollabShapeProperties.HEIGHT_TAG, properties.getThick())
                            .put(CollabShapeProperties.WIDTH_TAG, null)
                            .put(CollabShapeProperties.ROTATION_TAG, null)
                            .put(CollabConnectionProperties.CATEGORY_TAG, properties.getConnectionType())
                            .put(CollabConnectionProperties.VERTICESX_TAG,new JSONArray(properties.getVerticesX()))
                            .put(CollabConnectionProperties.VERTICESY_TAG,new JSONArray(properties.getVerticesY()))
                            .put(CollabConnectionProperties.ID_SHAPE1_TAG,properties.getIdShape1())
                            .put(CollabConnectionProperties.ID_SHAPE2_TAG,properties.getIdShape2())
                            .put(CollabConnectionProperties.INDEX1_TAG,properties.getIndex1())
                            .put(CollabConnectionProperties.INDEX2_TAG, properties.getIndex2())
                            .put(CollabConnectionProperties.Q1_TAG,properties.getQ1())
                            .put(CollabConnectionProperties.Q2_TAG,properties.getQ2());
                }
                else{

                    shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shapes[i].getProperties().getType())
                            .put(CollabShapeProperties.FILLING_COLOR_TAG, shapes[i].getProperties().getFillingColor())
                            .put(CollabShapeProperties.BORDER_COLOR_TAG, shapes[i].getProperties().getBorderColor())
                            .put(CollabShapeProperties.MIDDLE_POINT_TAG, new JSONArray(shapes[i].getProperties().getMiddlePointCoord()))
                            .put(CollabShapeProperties.HEIGHT_TAG, shapes[i].getProperties().getHeight())
                            .put(CollabShapeProperties.WIDTH_TAG, shapes[i].getProperties().getWidth())
                            .put(CollabShapeProperties.ROTATION_TAG, shapes[i].getProperties().getRotation())
                            .put(CollabShapeProperties.ATTRIBUTES_TAG,new JSONArray(shapes[i].getProperties().getAttributes()))
                            .put(CollabShapeProperties.METHODS_TAG,new JSONArray(shapes[i].getProperties().getMethods()))
                            .put(CollabShapeProperties.LABEL_TAG,shapes[i].getProperties().getLabel())
                            .put(CollabShapeProperties.BORDER_TYPE_TAG,shapes[i].getProperties().getBorderType());
                }


                shapeJson = new JSONObject().put(CollabShape.ID_TAG, shapes[i].getId())
                        .put(CollabShape.IMAGE_ID_TAG, shapes[i].getImageId())
                        .put(CollabShape.AUTHOR_TAG, shapes[i].getAuthor())
                        .put(CollabShape.PROPERTIES_TAG, shapePropertiesJson);

                if (shapeJson != null && shapePropertiesJson != null)
                    array.put(shapeJson);
            }
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put("shapes", array);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        if (array != null && array.length() > 0 )
            socket.emit(DUPLICATE_FORMS_TAG, json.toString());
    }
    //MEME CODE QUE duplicateElements --- A CHANGER
    public void duplicateCutElements(CollabShape[] shapes){
        JSONArray array = new JSONArray();
        JSONObject json = null;
        try {
            for (int i = 0; i < shapes.length; i++) {
                JSONObject shapeJson = null;
                JSONObject shapePropertiesJson = null;
                if (CollabConnectionProperties.class.equals(shapes[i].getProperties().getClass())){
                    CollabConnectionProperties properties = (CollabConnectionProperties)shapes[i].getProperties();
                    shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shapes[i].getProperties().getType())
                            .put(CollabShapeProperties.FILLING_COLOR_TAG, shapes[i].getProperties().getFillingColor())
                            .put(CollabShapeProperties.BORDER_COLOR_TAG, shapes[i].getProperties().getBorderColor())
                            .put(CollabShapeProperties.MIDDLE_POINT_TAG, null)
                            .put(CollabShapeProperties.HEIGHT_TAG, properties.getThick())
                            .put(CollabShapeProperties.WIDTH_TAG, null)
                            .put(CollabShapeProperties.ROTATION_TAG, null)
                            .put(CollabConnectionProperties.CATEGORY_TAG, properties.getConnectionType())
                            .put(CollabConnectionProperties.VERTICESX_TAG,new JSONArray(properties.getVerticesX()))
                            .put(CollabConnectionProperties.VERTICESY_TAG,new JSONArray(properties.getVerticesY()))
                            .put(CollabConnectionProperties.ID_SHAPE1_TAG,properties.getIdShape1())
                            .put(CollabConnectionProperties.ID_SHAPE2_TAG,properties.getIdShape2())
                            .put(CollabConnectionProperties.INDEX1_TAG,properties.getIndex1())
                            .put(CollabConnectionProperties.INDEX2_TAG, properties.getIndex2())
                            .put(CollabConnectionProperties.Q1_TAG,properties.getQ1())
                            .put(CollabConnectionProperties.Q2_TAG,properties.getQ2());
                }
                else{

                    shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shapes[i].getProperties().getType())
                            .put(CollabShapeProperties.FILLING_COLOR_TAG, shapes[i].getProperties().getFillingColor())
                            .put(CollabShapeProperties.BORDER_COLOR_TAG, shapes[i].getProperties().getBorderColor())
                            .put(CollabShapeProperties.MIDDLE_POINT_TAG, new JSONArray(shapes[i].getProperties().getMiddlePointCoord()))
                            .put(CollabShapeProperties.HEIGHT_TAG, shapes[i].getProperties().getHeight())
                            .put(CollabShapeProperties.WIDTH_TAG, shapes[i].getProperties().getWidth())
                            .put(CollabShapeProperties.ROTATION_TAG, shapes[i].getProperties().getRotation())
                            .put(CollabShapeProperties.ATTRIBUTES_TAG,new JSONArray(shapes[i].getProperties().getAttributes()))
                            .put(CollabShapeProperties.METHODS_TAG,new JSONArray(shapes[i].getProperties().getMethods()))
                            .put(CollabShapeProperties.LABEL_TAG,shapes[i].getProperties().getLabel())
                            .put(CollabShapeProperties.BORDER_TYPE_TAG,shapes[i].getProperties().getBorderType());
                }

                shapeJson = new JSONObject().put(CollabShape.ID_TAG, shapes[i].getId())
                        .put(CollabShape.IMAGE_ID_TAG, shapes[i].getImageId())
                        .put(CollabShape.AUTHOR_TAG, shapes[i].getAuthor())
                        .put(CollabShape.PROPERTIES_TAG, shapePropertiesJson);

                if (shapeJson != null && shapePropertiesJson != null)
                    array.put(shapeJson);
            }
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put("shapes", array);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        if (array != null && array.length() > 0 )
            socket.emit(DUPLICATE_CUT_FORMS_TAG, json.toString());
    }
    public void deleteElements(String[] ids){
        JSONObject json = null;
        try {
            json = new JSONObject().put(CollabShape.IMAGE_ID_TAG, imageId).put(ELEMENTS_IDS_TAG, new JSONArray(ids))
                    .put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json != null)
            socket.emit(DELETE_FORM_TAG, json.toString());
    }
    public void cutElements(String[] ids){
        JSONObject json = null;
        try {
            json = new JSONObject().put(CollabShape.IMAGE_ID_TAG, imageId).put(ELEMENTS_IDS_TAG, new JSONArray(ids))
                    .put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json != null)
            socket.emit(CUT_FORMS_TAG, json.toString());

    }
    public void selectElements(String[] oldSelections, String[] newSelections){
        JSONObject json = null;
        try {
            json = new JSONObject().put(CollabShape.IMAGE_ID_TAG, imageId)
                    .put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put("oldElementIds", new JSONArray(oldSelections)).put("newElementIds", new JSONArray(newSelections));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json != null)
            socket.emit(SELECT_FORM_TAG, json.toString());
    }

    public void stackElement(String id){
        JSONObject json = null;
        try {
            json = new JSONObject().put(CollabShape.IMAGE_ID_TAG, imageId)
                    .put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put(ELEMENT_ID_TAG, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json != null)
            socket.emit(STACK_FORM_TAG, json.toString());
    }
    public void unstackElement(CollabShape shape){
        JSONObject json = null;
        JSONObject shapeJson;
        JSONObject shapePropertiesJson;
        try {
            if (CollabConnectionProperties.class.equals(shape.getProperties().getClass())){
                CollabConnectionProperties properties = (CollabConnectionProperties)shape.getProperties();
                shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shape.getProperties().getType())
                        .put(CollabShapeProperties.FILLING_COLOR_TAG, shape.getProperties().getFillingColor())
                        .put(CollabShapeProperties.BORDER_COLOR_TAG, shape.getProperties().getBorderColor())
                        .put(CollabShapeProperties.MIDDLE_POINT_TAG, null)
                        .put(CollabShapeProperties.HEIGHT_TAG, properties.getThick())
                        .put(CollabShapeProperties.WIDTH_TAG, null)
                        .put(CollabShapeProperties.ROTATION_TAG, null)
                        .put(CollabConnectionProperties.CATEGORY_TAG, properties.getConnectionType())
                        .put(CollabConnectionProperties.VERTICESX_TAG,new JSONArray(properties.getVerticesX()))
                        .put(CollabConnectionProperties.VERTICESY_TAG,new JSONArray(properties.getVerticesY()))
                        .put(CollabConnectionProperties.ID_SHAPE1_TAG,properties.getIdShape1())
                        .put(CollabConnectionProperties.ID_SHAPE2_TAG,properties.getIdShape2())
                        .put(CollabConnectionProperties.INDEX1_TAG,properties.getIndex1())
                        .put(CollabConnectionProperties.INDEX2_TAG, properties.getIndex2())
                        .put(CollabConnectionProperties.Q1_TAG,properties.getQ1())
                        .put(CollabConnectionProperties.Q2_TAG,properties.getQ2());
            }
            else{
                shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shape.getProperties().getType())
                        .put(CollabShapeProperties.FILLING_COLOR_TAG, shape.getProperties().getFillingColor())
                        .put(CollabShapeProperties.BORDER_COLOR_TAG, shape.getProperties().getBorderColor())
                        .put(CollabShapeProperties.MIDDLE_POINT_TAG, new JSONArray(shape.getProperties().getMiddlePointCoord()))
                        .put(CollabShapeProperties.HEIGHT_TAG, shape.getProperties().getHeight())
                        .put(CollabShapeProperties.WIDTH_TAG, shape.getProperties().getWidth())
                        .put(CollabShapeProperties.ROTATION_TAG, shape.getProperties().getRotation())
                        .put(CollabShapeProperties.ATTRIBUTES_TAG,new JSONArray(shape.getProperties().getAttributes()))
                        .put(CollabShapeProperties.METHODS_TAG,new JSONArray(shape.getProperties().getMethods()))
                        .put(CollabShapeProperties.LABEL_TAG,shape.getProperties().getLabel())
                        .put(CollabShapeProperties.BORDER_TYPE_TAG,shape.getProperties().getBorderType());

            }

            shapeJson = new JSONObject().put(CollabShape.ID_TAG, shape.getId())
                    .put(CollabShape.IMAGE_ID_TAG, shape.getImageId())
                    .put(CollabShape.AUTHOR_TAG, shape.getAuthor())
                    .put(CollabShape.PROPERTIES_TAG, shapePropertiesJson);
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put("shape", shapeJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json != null)
            socket.emit(UNSTACK_FORM_TAG, json.toString());

    }
    public void resizeCanvas(int width, int height){
        JSONObject json = null;
        //JSONObject dimensionsJson;
        try{
            //dimensionsJson = new JSONObject().put("x",width).put("y",height);
            json = new JSONObject().put(CollabShape.IMAGE_ID_TAG,imageId).put(USERNAME_TAG,FetchManager.currentInstance.getUserUsername())
                    .put("x", width).put("y", height);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(json != null)
            socket.emit(REZIZE_CANVAS_TAG, json.toString());
    }
    public void sendInviteToDrawingSession(String username){
        JSONObject json = null;
        if (!FetchManager.currentInstance.getUsersNames().contains(username))
            return;
        try{
            //dimensionsJson = new JSONObject().put("x",width).put("y",height);
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG,FetchManager.currentInstance.getUserUsername())
                    .put(INVITED_USERNAME_TAG, username).put(CollabShape.IMAGE_ID_TAG,imageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(json != null)
            socket.emit(INVITE_TO_DRAWING_SESSION, json.toString());
    }
    public void sendInviteToConversation(String username, String conversation){
        JSONObject json = null;
        if (!FetchManager.currentInstance.getUsersNames().contains(username))
            return;
        if (!FetchManager.currentInstance.getUserConversationsNames().contains(conversation)){
            if(!RequestManager.currentInstance.addUserConversation(conversation))
                return;
        }
        try{
            //dimensionsJson = new JSONObject().put("x",width).put("y",height);
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG,FetchManager.currentInstance.getUserUsername())
                    .put(INVITED_USERNAME_TAG, username).put(CONVERSATION_ID_TAG,conversation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(json != null)
            socket.emit(INVITE_TO_CONVERSATION, json.toString());
    }
    public void sendResponseToDrawingSessionInvitation(String username, String imageId,boolean response){
        JSONObject json = null;
        if (!FetchManager.currentInstance.getUsersNames().contains(username))
            return;
        try{
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG,username)
                    .put(INVITED_USERNAME_TAG, FetchManager.currentInstance.getUserUsername()).put(RESPONSE_TO_INVITATION,response).put(CollabShape.IMAGE_ID_TAG,imageId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(json != null)
            socket.emit(RESPOND_TO_DRAWING_SESSION_INVITATION, json.toString());
    }
    public void sendResponseToConversationInvitation(String username, String conversation,boolean response){
        JSONObject json = null;
        if (!FetchManager.currentInstance.getUsersNames().contains(username))
            return;
        try{
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG,username)
                    .put(INVITED_USERNAME_TAG, FetchManager.currentInstance.getUserUsername()).put(RESPONSE_TO_INVITATION,response).put(CONVERSATION_ID_TAG,conversation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(json != null)
            socket.emit(RESPOND_TO_CONVERSATION_INVITATION, json.toString());
    }

    public void sendUsername(){
        socket.emit(USER_CONNECTED_TAG,FetchManager.currentInstance.getUserUsername());
    }



    public boolean isConnected() {
        return socket.connected();
    }

    public void leave(String username) {
        socket.emit(USERLEFT_TAG, username);
        socket.disconnect();
    }
    public boolean isInDrawingSession(){
        return imageId != null && !imageId.isEmpty();
    }

    private String formatIpToUri(String ip) {
        return "http://" + ip + ":3000/";
    }
    private String formatMessage(String date,String username, String message){
        return date + " " + "[ " + username + " ] : " + message;
    }

}
