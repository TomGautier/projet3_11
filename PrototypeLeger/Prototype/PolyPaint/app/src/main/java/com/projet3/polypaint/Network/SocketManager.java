package com.projet3.polypaint.Network;


import com.projet3.polypaint.Chat.NewMessageListener;
import com.projet3.polypaint.DrawingCollabSession.CollabShape;
import com.projet3.polypaint.DrawingCollabSession.CollabShapeProperties;
import com.projet3.polypaint.DrawingCollabSession.DrawingCollabSessionListener;
import com.projet3.polypaint.UserList.UsersListListener;

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

    //Liste d'utilisateurs
    public final String NEW_USER_CONNECTED_TAG = "UserJoinedChat";




    //Properties
    private final String ELEMENTS_ID_TAG ="id";
    private final String ELEMENTS_IDS_TAG ="ids";
    private final String IMAGE_TAG = "image";
    private final String VISIBILITY_TAG = "visibility";
    private final String NAME_TAG = "name";
    private final String DATE_TAG = "date";
    private final String USERNAME_TAG = "username";
    private final String MESSAGE_TAG = "message";
    private final String SESSION_ID_TAG = "sessionId";
    private final String CONVERSATION_ID_TAG = "conversationId";

    public static SocketManager currentInstance;
    private Socket socket;
    private NewMessageListener newMessagelistener;
    private DrawingCollabSessionListener drawingCollabSessionListener;
    private UsersListListener usersListListener;

    private String uri;
    private String sessionId;
    private String drawingSessionId;

    public SocketManager(String ipAddress_, String sessionId_) {
        uri = formatIpToUri(ipAddress_);
        sessionId = sessionId_;
        setupSocket();
    }


    public void setupNewMessageListener(NewMessageListener listener_) {
        newMessagelistener = listener_;
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
                    try {
                        //JSONObject json = new JSONObject((JSONObject)args[0]);
                        JSONObject json = (JSONObject)args[0];
                        date = json.getString(DATE_TAG);
                        username = json.getString(USERNAME_TAG);
                        message = json.getString(MESSAGE_TAG);
                    }
                    catch(JSONException e) {}
                    newMessagelistener.onNewMessage(formatMessage(date,username,message));
                }
            });
            socket.on(NEW_USER_CONNECTED_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //try{
                        //JSONObject obj = (JSONObject)args[0];
                        //String username = obj.getString(USERNAME_TAG);
                        String username = (String)args[0];
                        usersListListener.onUserConnected(username);
                    //} //catch (JSONException e) {
                       // e.printStackTrace();
                    //}
                }
            });
            socket.on(CREATED_COLLAB_SESSION_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //drawingSessionId = (String)args[0];
                }
            });
            socket.on(JOINED_COLLAB_SESSION_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                   // drawingSessionId = (String)args[0];
                    //drawingCollabSessionListener.onJoinedSession(drawingSessionId);
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

                    } catch (/*JSONException e*/Exception e) {
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
                        String id = obj.getString(ELEMENTS_ID_TAG);
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


    public void createCollabSession(String name, String visibility, String image){
        JSONObject json = null;
        try {
            json = new JSONObject().put(NAME_TAG, name).put(VISIBILITY_TAG, visibility).put(IMAGE_TAG, image);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json != null)
            socket.emit(CREATE_COLLAB_SESSION_TAG,json);
    }
    public void joinCollabSession(String drawingSessionId){
        JSONObject json = null;
        try {
            json = new JSONObject().put(SESSION_ID_TAG, sessionId).put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put(CollabShape.DRAWING_SESSION_TAG, drawingSessionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(json != null){
            socket.emit(JOIN_COLLAB_SESSION_TAG, json.toString());
            this.drawingSessionId = drawingSessionId;
            this.drawingCollabSessionListener.onJoinedSession(this.drawingSessionId);
        }

       //socket.emit(JOIN_COLLAB_SESSION_TAG, drawingSessionId, FetchManager.currentInstance.getUserUsername());
       //this.drawingSessionId = drawingSessionId;
       //this.drawingCollabSessionListener.onJoinedSession(this.drawingSessionId);
    }

    public void addElement(CollabShape shape){
        JSONObject json = null;
        JSONObject shapeJson;
        JSONObject shapePropertiesJson;
        try {
            shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shape.getProperties().getType())
                    .put(CollabShapeProperties.FILLING_COLOR_TAG, shape.getProperties().getFillingColor())
                    .put(CollabShapeProperties.BORDER_COLOR_TAG, shape.getProperties().getBorderColor())
                    .put(CollabShapeProperties.STROKE_TAG, shape.getProperties().getStrokeType().toString())
                    .put(CollabShapeProperties.MIDDLE_POINT_TAG, new JSONArray(shape.getProperties().getMiddlePointCoord()))
                    .put(CollabShapeProperties.HEIGHT_TAG, shape.getProperties().getHeight())
                    .put(CollabShapeProperties.WIDTH_TAG, shape.getProperties().getWidth())
                    .put(CollabShapeProperties.ROTATION_TAG, shape.getProperties().getRotation())
                    .put(CollabShapeProperties.LABEL_TAG, shape.getProperties().getText())
                    .put(CollabShapeProperties.ATTRIBUTE_TAG, shape.getProperties().getAttributesJson())
                    .put(CollabShapeProperties.METHOD_TAG, shape.getProperties().getMethodsJson());

            shapeJson = new JSONObject().put(CollabShape.ID_TAG, shape.getId())
                    .put(CollabShape.DRAWING_SESSION_TAG, shape.getDrawingSessionId())
                    .put(CollabShape.AUTHOR_TAG, shape.getAuthor())
                    .put(CollabShape.PROPERTIES_TAG, shapePropertiesJson);
            json = new JSONObject().put(SESSION_ID_TAG,sessionId).put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put("shape", shapeJson);

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
                CollabShape shape = shapes[i];

                shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shape.getProperties().getType())
                        .put(CollabShapeProperties.FILLING_COLOR_TAG, shape.getProperties().getFillingColor())
                        .put(CollabShapeProperties.BORDER_COLOR_TAG, shape.getProperties().getBorderColor())
                        .put(CollabShapeProperties.STROKE_TAG, shape.getProperties().getStrokeType().toString())
                        .put(CollabShapeProperties.MIDDLE_POINT_TAG, new JSONArray(shape.getProperties().getMiddlePointCoord()))
                        .put(CollabShapeProperties.HEIGHT_TAG, shape.getProperties().getHeight())
                        .put(CollabShapeProperties.WIDTH_TAG, shape.getProperties().getWidth())
                        .put(CollabShapeProperties.ROTATION_TAG, shape.getProperties().getRotation())
                        .put(CollabShapeProperties.LABEL_TAG, shape.getProperties().getText())
                        .put(CollabShapeProperties.ATTRIBUTE_TAG, shape.getProperties().getAttributesJson())
                        .put(CollabShapeProperties.METHOD_TAG, shape.getProperties().getMethodsJson());

                shapeJson = new JSONObject().put(CollabShape.ID_TAG, shape.getId())
                        .put(CollabShape.DRAWING_SESSION_TAG, shape.getDrawingSessionId())
                        .put(CollabShape.AUTHOR_TAG, shape.getAuthor())
                        .put(CollabShape.PROPERTIES_TAG, shapePropertiesJson);

                System.out.println("Drawing session id : " + shape.getDrawingSessionId());

                if (shapeJson != null && shapePropertiesJson != null)
                    array.put(shapeJson);
            }
            json = new JSONObject().put(CollabShape.DRAWING_SESSION_TAG, drawingSessionId).put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
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

                shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shapes[i].getProperties().getType())
                        .put(CollabShapeProperties.FILLING_COLOR_TAG, shapes[i].getProperties().getFillingColor())
                        .put(CollabShapeProperties.BORDER_COLOR_TAG, shapes[i].getProperties().getBorderColor())
                        .put(CollabShapeProperties.MIDDLE_POINT_TAG, new JSONArray(shapes[i].getProperties().getMiddlePointCoord()))
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

                shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shapes[i].getProperties().getType())
                        .put(CollabShapeProperties.FILLING_COLOR_TAG, shapes[i].getProperties().getFillingColor())
                        .put(CollabShapeProperties.BORDER_COLOR_TAG, shapes[i].getProperties().getBorderColor())
                        .put(CollabShapeProperties.MIDDLE_POINT_TAG, new JSONArray(shapes[i].getProperties().getMiddlePointCoord()))
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
            json = new JSONObject().put(CollabShape.DRAWING_SESSION_TAG, drawingSessionId).put(ELEMENTS_IDS_TAG, new JSONArray(ids))
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
            json = new JSONObject().put(CollabShape.DRAWING_SESSION_TAG, drawingSessionId).put(ELEMENTS_IDS_TAG, new JSONArray(ids))
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
            json = new JSONObject().put(CollabShape.DRAWING_SESSION_TAG, drawingSessionId)
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
            json = new JSONObject().put(CollabShape.DRAWING_SESSION_TAG, drawingSessionId)
                    .put(USERNAME_TAG, FetchManager.currentInstance.getUserUsername())
                    .put(ELEMENTS_ID_TAG, id);
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
            shapePropertiesJson = new JSONObject().put(CollabShapeProperties.TYPE_TAG, shape.getProperties().getType())
                    .put(CollabShapeProperties.FILLING_COLOR_TAG, shape.getProperties().getFillingColor())
                    .put(CollabShapeProperties.BORDER_COLOR_TAG, shape.getProperties().getBorderColor())
                    .put(CollabShapeProperties.MIDDLE_POINT_TAG, new JSONArray(shape.getProperties().getMiddlePointCoord()))
                    .put(CollabShapeProperties.HEIGHT_TAG, shape.getProperties().getHeight())
                    .put(CollabShapeProperties.WIDTH_TAG, shape.getProperties().getWidth())
                    .put(CollabShapeProperties.ROTATION_TAG, shape.getProperties().getRotation());

            shapeJson = new JSONObject().put(CollabShape.ID_TAG, shape.getId())
                    .put(CollabShape.DRAWING_SESSION_TAG, shape.getDrawingSessionId())
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
    /*public void resizeCanvas(){
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
