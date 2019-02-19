package com.projet3.polypaint;


public class SocketLoginGuard implements SocketLoginListener {


    private Boolean enableLogin;

    public SocketLoginGuard() {
        reset();
    }

    @Override
    public void onUserAlreadyExists() {
        enableLogin = false;
    }
    @Override
    public void onUserLogged() {
        enableLogin = true;
    }

    public boolean isLogged() {
        return enableLogin;
    }

    public void reset() {
        enableLogin = null;
    }

    public Boolean waitingForResponse() {
        return enableLogin == null;
    }
}
