package com.example.piccy.model.auth;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FirebaseAuthenticator implements Authenticator {
    private final FirebaseAuth firebaseAuth;
    private StatusUpdateListener statusUpdateListener;
    private UserAuthenticationState userAuthenticationState;
    private final FirebaseAuth.AuthStateListener authStateListener;

    public FirebaseAuthenticator() {
        firebaseAuth = FirebaseAuth.getInstance();

        statusUpdateListener = new StatusUpdateListener() {
            @Override
            public void onStatusUpdate(String newStatus) {
                Log.i("FIREBASE_AUTH", newStatus);
            }
        };

        userAuthenticationState = UserAuthenticationState.NONE;

        AtomicBoolean initializing = new AtomicBoolean(true);

        authStateListener = (auth)->{

            FirebaseUser user = auth.getCurrentUser();

            if (user != null) {

                user.reload().addOnCompleteListener(Executors.newSingleThreadExecutor(), task -> {

                    if (!task.isSuccessful()) return;

                    if (auth.getCurrentUser() != null) {
                        userAuthenticationState = UserAuthenticationState.REGISTERED;

                        if (user.isEmailVerified()) {
                            userAuthenticationState = UserAuthenticationState.VERIFIED;
                        }

                        initializing.set(false);
                    } else userAuthenticationState = UserAuthenticationState.NONE;

                    initializing.set(false);
                });

            } else {
                userAuthenticationState = UserAuthenticationState.NONE;
                initializing.set(false);
            }
        };

        firebaseAuth.addAuthStateListener(authStateListener);

        while (initializing.get());
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

                        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).updateProfile(upcr);
                    } else {
                        throw new SignupException("Failed to register email");
                    }
                }).continueWith(task -> {

                    if (task.isSuccessful()) {

                        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification();
                    } else if (task.getException() instanceof SignupException) {
                        throw task.getException();
                    } else {
                        throw new SignupException("Failed to update name");
                    }
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onComplete.accept(true, "");
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

                        if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                            userAuthenticationState = UserAuthenticationState.VERIFIED;
                        }
                    }

                    String msg = task.isSuccessful() ? "" : Objects.requireNonNull(task.getException()).getMessage();
                    onComplete.accept(task.isSuccessful(), msg);
                });
    }

    @Override
    public boolean isLoggedIn() {
        return userAuthenticationState != UserAuthenticationState.NONE;
    }

    public void setStatusUpdateListener(StatusUpdateListener statusUpdateListener) {
        this.statusUpdateListener = statusUpdateListener;
    }

    @NonNull
    public UserAuthenticationState getUserAuthenticationState() {
        return this.userAuthenticationState;
    }

    public void resendVerificationEmail(Consumer<Boolean> onComplete) {
        Objects.requireNonNull(firebaseAuth.getCurrentUser())
                .sendEmailVerification()
                .addOnCompleteListener(task -> onComplete.accept(task.isSuccessful()));
    }

    @Nullable
    public String getEmail() {
        if (firebaseAuth.getCurrentUser() == null) return null;
        else return firebaseAuth.getCurrentUser().getEmail();
    }

    public void isVerified(Consumer<Boolean> callback) {
        if (userAuthenticationState == UserAuthenticationState.NONE) {
            callback.accept(false);
            return;
        }
        if (userAuthenticationState == UserAuthenticationState.VERIFIED) {
            callback.accept(true);
            return;
        }

        Objects.requireNonNull(firebaseAuth.getCurrentUser()).reload().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.accept(false);
            } else {
                callback.accept(firebaseAuth.getCurrentUser().isEmailVerified());
            }
        });
    }

    @Override
    public void close() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
