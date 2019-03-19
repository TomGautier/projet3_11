package com.projet3.polypaint.UserLogin;

public class UserInformation  {
    private String username;
    private String password;

    public UserInformation(String username_, String password_){
        username = username_;
        password = password_;
    }

    public String getUsername() {return username;}
    public String getPassword() {return password;}


}
