package com.example.piccy.model;

public interface Authenticator {
    void signUp(String email, String password);
    void logIn(String email, String password);
}
