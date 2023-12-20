package com.example.piccy.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            userAuthenticationState = UserAuthenticationState.REGISTERED;
            if (firebaseUser.isEmailVerified()) {
                userAuthenticationState = UserAuthenticationState.VERIFIED;
            }
        }
    }

    @Override
    public void signUp(@NonNull String email, @NonNull String name, @NonNull String password, @NonNull BiConsumer<Boolean, String> onComplete) {
        statusUpdateListener.updateStatus("Registering email");
        EmailAuthProvider.getCredential(email, password);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .continueWith(task -> {

                    if (task.isSuccessful()) {
                        userAuthenticationState = UserAuthenticationState.REGISTERED;

                        UserProfileChangeRequest upcr = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        return firebaseAuth.getCurrentUser().updateProfile(upcr);
                    } else {
                        throw new SignupException("Failed to register email");
                    }
                }).continueWith(task -> {

                    if (task.isSuccessful()) {

                        return firebaseAuth.getCurrentUser().sendEmailVerification();
                    } else if (task.getException() instanceof SignupException) {
                        throw task.getException();
                    } else {
                        throw new SignupException("Failed to update name");
                    }
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onComplete.accept(true, null);
                    } else if (task.getException() instanceof SignupException) {
                        onComplete.accept(false, task.getException().getMessage());
                    } else {
                        onComplete.accept(false, "Failed to send verification email");
                    }
                });
    }

    @Override
    public void logIn(@NonNull String email, @NonNull String password, @NonNull BiConsumer<Boolean,String> onComplete) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userAuthenticationState = UserAuthenticationState.REGISTERED;

                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                            userAuthenticationState = UserAuthenticationState.VERIFIED;
                        }
                    }

                    String msg = task.isSuccessful() ? "" : task.getException().getMessage();
                    if (task.isSuccessful()) onComplete.accept(task.isSuccessful(), msg);
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

    public void resendVerificationEmail(Consumer<Boolean> onComplete) {
        firebaseAuth.getCurrentUser()
                .sendEmailVerification()
                .addOnCompleteListener(task -> onComplete.accept(task.isSuccessful()));
    }
}
