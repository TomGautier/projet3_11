package com.projet3.polypaint.Chat;

import android.util.Log;

import com.projet3.polypaint.Chat.NewMessageListener;

import org.json.JSONArray;
import org.json.JSONException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    public final String SENDMESSAGE_TAG = "MessageSent";

    private Socket socket;
    private NewMessageListener listener;
    private String uri;

    public SocketManager(String ipAddress_) {
        uri = formatIpToUri(ipAddress_);
        setupSocket();
    }

    public void setupNewMessageListener(NewMessageListener listener_) {
        listener = listener_;
    }
    private void setupSocket() {
        try {
            socket = IO.socket(uri);
            socket.connect();
            socket.on(SENDMESSAGE_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONArray array = ((JSONArray)args[0]).getJSONArray(0);
                        String message =  array.getString(0);
                        listener.onNewMessage(message);
                    }catch (JSONException e) {
                        Log.d("SOCKET_ERROR", "un erreur JSON est survenu lors d'une reception de message");
                    }
                }
            });
        }catch(Exception e) {}

    }
    public void sendMessage(String message) {
        if (socket.connected()){
            socket.emit(SENDMESSAGE_TAG, message);
        }
    }

    private String formatIpToUri(String ip) {
        return "http://" + ip + ":3000/";
    }



}
