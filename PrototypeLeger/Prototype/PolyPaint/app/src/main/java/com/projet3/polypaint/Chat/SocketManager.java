package com.projet3.polypaint.Chat;

import android.util.Log;

import com.projet3.polypaint.Chat.NewMessageListener;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    public final String SENDMESSAGE_TAG = "MessageSent";
   // public final String SERVER_ADDRESS = "https://polypaint-11.azurewebsites.net/";
    //public final String SERVER_ADDRESS_TEST = "http://192.168.0.101:3000/"; // mon laptop
    //public final String SERVER_ADDRESS_TEST = "http://192.168.0.107:3000/"; // ma tour
    //public final String SERVER_ADDRESS_TEST = "http://10.200.2.171:3000/"; //poly

    private Socket socket;
    private NewMessageListener listener;
    private String uri;

    public SocketManager(String ipAdress_) {
        uri = formatIpToUri(ipAdress_);
        setupSocket();
    }

    public void setupNewMessageListener(NewMessageListener listener_) {
        listener = listener_;
    }
    private void setupSocket() {
        try {
            Log.d("setupSocket", "setupSocket appele");
            socket = IO.socket(uri);
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

    private String formatIpToUri(String ip) {
        return "http://" + ip + ":3000/";
    }



}
