package com.example.piccy.model;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthenticator implements Authenticator {
    FirebaseAuth firebaseAuth;

    public FirebaseAuthenticator() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void signUp(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Sign up successful");
                    } else {
                        System.out.println("Sign up unsuccessful");
                    }
                });
    }

    @Override
    public void logIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.isSuccessful()) {
                            System.out.println("Sign in successful");
                        } else {
                            System.out.println("Sign in unsuccessful");
                        }
                    }
                });
    }
}
