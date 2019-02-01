package com.projet3.polypaint;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {

    public final String SERVER_ADDRESS = "https://polypaint-11.azurewebsites.net/";
    public final String SERVER_ADDRESS_TEST = "http://192.168.0.101:3000/";
    public final String SERVER_ADDRESS_TEST2 ="http://localhost:3000/";
    private Socket socket;

    public SocketManager() {
        setupSocket();
    }

    private void setupSocket() {
        try {
            socket = IO.socket(SERVER_ADDRESS_TEST);
            socket.connect();
        }catch(Exception e) {}

    }
    public void emit(String tag, String message) {
        if (socket.connected()){
            socket.emit(tag, message);
        }
    }



}
