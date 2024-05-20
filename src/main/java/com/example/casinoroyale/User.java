package com.example.casinoroyale;

import java.net.MalformedURLException;

public class User {
    String username;
    String password;
    String css;

    public User(String username, String password) throws MalformedURLException {
        this.username = username;
        this.password = password;
        css = username + ".css";
    }
}
