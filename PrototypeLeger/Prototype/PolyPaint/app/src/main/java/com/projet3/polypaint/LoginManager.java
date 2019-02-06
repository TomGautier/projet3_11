package com.projet3.polypaint;

import com.projet3.polypaint.Chat.SocketManager;

public class LoginManager implements LoginListener{


    Boolean enableLogin;
    public LoginManager() {}


    @Override
    public void onUserAlreadyExists() {
        enableLogin = false;
    }
    @Override
    public void onUserLogged() {
        enableLogin = true;
    }
}
