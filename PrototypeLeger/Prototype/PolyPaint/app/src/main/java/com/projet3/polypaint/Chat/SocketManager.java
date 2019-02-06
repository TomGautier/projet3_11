package com.projet3.polypaint.Chat;

import android.util.Log;

import com.projet3.polypaint.Chat.NewMessageListener;
import com.projet3.polypaint.LoginListener;

import org.json.JSONArray;
import org.json.JSONException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    public final String SENDMESSAGE_TAG = "MessageSent";
    public final String LOGINATTEMPT_TAG = "LoginAttempt";
    public final String USEREXIST_TAG = "UsernameAlreadyExists";
    public final String USERLOGGED_TAG = "UserLogged";


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
                    //try {
                       // JSONArray array = ((JSONArray)args[0]).getJSONArray(0);
                        //String message =  array.getString(0);
                        String message = (String)args[0];
                        newMessagelistener.onNewMessage(message);
                    //}//catch (JSONException e) {
                       // Log.d("SOCKET_ERROR", "un erreur JSON est survenu lors d'une reception de message");
                    //}
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
    public void sendMessage(String message) {
        if (socket.connected()){
            socket.emit(SENDMESSAGE_TAG, message);
        }
    }

    public void disconnect() {
        socket.disconnect();
    }

    private String formatIpToUri(String ip) {
        return "http://" + ip + ":3000/";
    }



}
