package com.projet3.polypaint;

import android.graphics.Color;
import android.util.Log;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    public final String SENDMESSAGE_TAG = "MessageSent";
    public final String SERVER_ADDRESS = "https://polypaint-11.azurewebsites.net/";
    public final String SERVER_ADDRESS_TEST = "http://192.168.0.107:3000/";
    public final String SERVER_ADDRESS_TEST2 ="http://localhost:3000/";
    private Socket socket;
    private NewMessageListener listener;

    public SocketManager() {
        setupSocket();
    }

    public void setupNewMessageListener(NewMessageListener listener_) {
        listener = listener_;
    }
    private void setupSocket() {
        try {
            Log.d("setupSocket", "setupSocket appele");
            socket = IO.socket(SERVER_ADDRESS_TEST);
            socket.connect();
            socket.on(SENDMESSAGE_TAG, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.d("message", args[0].toString());
                   listener.onNewMessage(args[0].toString());
                }
            });
        }catch(Exception e) {}

    }
    public void sendMessage(String message) {
        if (socket.connected()){
            socket.emit(SENDMESSAGE_TAG, message);
        }
    }



}
