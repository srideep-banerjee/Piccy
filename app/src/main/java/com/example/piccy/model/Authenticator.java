package com.example.piccy.model;

public interface Authenticator {
    void signUp(String email, String name, String password);
    void logIn(String email, String password);
    boolean isLoggedIn();
}
