package com.example.piccy.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirebaseAuthenticator implements Authenticator {
    private FirebaseAuth firebaseAuth;
    private StatusUpdateListener statusUpdateListener;
    private UserAuthenticationState userAuthenticationState;

    public FirebaseAuthenticator() {
        firebaseAuth = FirebaseAuth.getInstance();

        statusUpdateListener = new StatusUpdateListener() {
            @Override
            public void onStatusUpdate(String newStatus) {
                Log.i("FIREBASE_AUTH", newStatus);
            }
        };

        userAuthenticationState = UserAuthenticationState.NONE;
    }

    @Override
    public void signUp(@NonNull String email, @NonNull String name, @NonNull String password) {
        statusUpdateListener.updateStatus("Registering email");
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .continueWith(task -> {

                    if (task.isSuccessful()) {
                        userAuthenticationState = UserAuthenticationState.REGISTERED;
                        statusUpdateListener.updateStatus("Email registered, updating name");

                        UserProfileChangeRequest upcr = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        return firebaseAuth.getCurrentUser().updateProfile(upcr);
                    } else {
                        throw new SignupException("Failed to register email");
                    }
                }).continueWith(task -> {

                    if (task.isSuccessful()) {
                        statusUpdateListener.updateStatus("Name updated, sending verification email");

                        return firebaseAuth.getCurrentUser().sendEmailVerification();
                    } else if (task.getException() instanceof SignupException) {
                        throw task.getException();
                    } else {
                        throw new SignupException("Failed to update name");
                    }
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userAuthenticationState = UserAuthenticationState.VERIFYING;
                        statusUpdateListener.updateStatus("Verification email sent");
                    } else if (task.getException() instanceof SignupException) {
                        statusUpdateListener.updateStatus(task.getException().getMessage());
                    } else {
                        statusUpdateListener.updateStatus("Failed to send verification email");
                    }
                });
    }

    @Override
    public void logIn(@NonNull String email, @NonNull String password) {
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

    @Override
    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void setStatusUpdateListener(StatusUpdateListener statusUpdateListener) {
        this.statusUpdateListener = statusUpdateListener;
    }

    public UserAuthenticationState getUserAuthenticationState() {
        return this.userAuthenticationState;
    }
}
