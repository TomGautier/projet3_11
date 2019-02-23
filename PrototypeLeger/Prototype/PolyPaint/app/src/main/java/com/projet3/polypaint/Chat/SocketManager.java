package com.projet3.polypaint.Chat;

import com.projet3.polypaint.SocketLoginListener;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager  {

    public final String SENDMESSAGE_TAG = "MessageSent";
    public final String LOGINATTEMPT_TAG = "LoginAttempt";
    public final String USEREXIST_TAG = "UsernameAlreadyExists";
    public final String USERLOGGED_TAG = "UserLogged";
    public final String USERLEFT_TAG = "UserLeft";


    private final String DATE_TAG = "date";
    private final String USERNAME_TAG = "username";
    private final String MESSAGE_TAG = "message";

    public static SocketManager currentInstance;
    private Socket socket;
    private NewMessageListener newMessagelistener;

    private String uri;

    public SocketManager(String ipAddress_) {
        uri = formatIpToUri(ipAddress_);
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
                        JSONObject json = new JSONObject((String)args[0]);
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
    public void sendMessage(String date,String username, String message) {
        String messageJSON = "";
        try {
            messageJSON = new JSONObject().put(DATE_TAG,date).put(USERNAME_TAG, username).put(MESSAGE_TAG, message).toString();
        }catch(JSONException e) {}

        if (!messageJSON.isEmpty())
            socket.emit(SENDMESSAGE_TAG, messageJSON);

    }
    public void joinConversation(){

    }

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
