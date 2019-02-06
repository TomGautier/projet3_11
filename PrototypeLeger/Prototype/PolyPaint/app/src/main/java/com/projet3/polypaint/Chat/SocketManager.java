package com.projet3.polypaint.Chat;

import android.util.Log;
import com.projet3.polypaint.Chat.NewMessageListener;

import com.projet3.polypaint.LoginListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    public final String SENDMESSAGE_TAG = "MessageSent";
    public final String LOGINATTEMPT_TAG = "LoginAttempt";
    public final String USEREXIST_TAG = "UsernameAlreadyExists";
    public final String USERLOGGED_TAG = "UserLogged";
    public final String USERLEFT_TAG = "UserLeft";

    private final String DATE = "date";
    private final String USERNAME = "username";
    private final String MESSAGE = "message";

    public static SocketManager currentInstance;
    private Socket socket;
    private NewMessageListener newMessagelistener;
    private LoginListener loginListener;

    private String uri;

    public SocketManager(String ipAddress_) {
        uri = formatIpToUri(ipAddress_);
        setupSocket();
        currentInstance = this;
    }

    public void setupNewMessageListener(NewMessageListener listener_) {
        newMessagelistener = listener_;
    }
    public void setupLoginListener(LoginListener listener_) {
        loginListener = listener_;
    }
    private void setupSocket() {
        try {
            socket = IO.socket(uri);
            socket.connect();


            socket.on(SENDMESSAGE_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String date = "";
                    String username = "";
                    String message = "";
                    String fullMessage = "";
                    try {
                        JSONObject json = new JSONObject((String)args[0]);
                        date = json.getString(DATE);
                        username = json.getString(USERNAME);
                        message = json.getString(MESSAGE);
                    }
                    catch(JSONException e) {}

                    fullMessage = date + " " + username + " " + message;

                    newMessagelistener.onNewMessage(fullMessage);
                }
            });

            socket.on(USEREXIST_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    loginListener.onUserAlreadyExists();
                }
            });

            socket.on(USERLOGGED_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    loginListener.onUserLogged();
                }
            });
        }catch(Exception e) {}

    }
    public void verifyUser(String username) {
        socket.emit(LOGINATTEMPT_TAG, username);
    }
    public void sendMessage(String date, String username, String message) {
        String messageJSON = "";
        try {
            messageJSON = new JSONObject().put(DATE,date).put(USERNAME, username).put(MESSAGE, message).toString();
        }catch(JSONException e) {}

        if (!messageJSON.isEmpty())
            socket.emit(SENDMESSAGE_TAG, messageJSON);

    }

    public void leave(String username) {
        socket.emit(USERLEFT_TAG, username);
        socket.disconnect();
    }

    private String formatIpToUri(String ip) {
        return "http://" + ip + ":3000/";
    }



}
