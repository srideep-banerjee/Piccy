package com.example.piccy.model.auth;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.piccy.BuildConfig;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FirebaseAuthenticator implements Authenticator {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private StatusUpdateListener statusUpdateListener;
    private UserAuthenticationState userAuthenticationState = UserAuthenticationState.NONE;
    private final ArrayList<FirebaseAuth.AuthStateListener> authStateListeners = new ArrayList<>();

    public FirebaseAuthenticator() {

        if (BuildConfig.USE_EMULATOR) {
            firebaseAuth.useEmulator(BuildConfig.EMULATOR_IP, 9099);
        }

        statusUpdateListener = new StatusUpdateListener() {
            @Override
            public void onStatusUpdate(String newStatus) {
                Log.i("FIREBASE_AUTH", newStatus);
            }
        };
    }

    @Override
    public void checkAuthState(@NonNull Consumer<UserAuthenticationState> onComplete, @NonNull Consumer<String> onError) {
        FirebaseAuth.AuthStateListener authStateListener = (auth) -> {

            FirebaseUser user = auth.getCurrentUser();

            if (user != null) {

                user.reload().addOnCompleteListener(Executors.newSingleThreadExecutor(), task -> {

                    if (!task.isSuccessful()) {
                        Objects.requireNonNull(task.getException()).printStackTrace();
                        onError.accept(task.getException().toString());
                        firebaseAuth.signOut();
                        return;
                    }

                    if (auth.getCurrentUser() == null) {
                        userAuthenticationState = UserAuthenticationState.NONE;
                    } else if (auth.getCurrentUser().isEmailVerified()) {
                        userAuthenticationState = UserAuthenticationState.VERIFIED;
                    } else {
                        userAuthenticationState = UserAuthenticationState.REGISTERED;
                    }

                    onComplete.accept(userAuthenticationState);
                });

            } else {
                userAuthenticationState = UserAuthenticationState.NONE;
                onComplete.accept(userAuthenticationState);
            }
        };

        firebaseAuth.addAuthStateListener(authStateListener);
        authStateListeners.add(authStateListener);
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
    public void logIn(@NonNull String email, @NonNull String password, @NonNull BiConsumer<Boolean, String> onComplete) {
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

    public void setStatusUpdateListener(StatusUpdateListener statusUpdateListener) {
        this.statusUpdateListener = statusUpdateListener;
    }

    @Override
    @NonNull
    public UserAuthenticationState getUserAuthenticationState() {
        return this.userAuthenticationState;
    }

    @Override
    public void resendVerificationEmail(Consumer<Boolean> onComplete) {
        Objects.requireNonNull(firebaseAuth.getCurrentUser())
                .sendEmailVerification()
                .addOnCompleteListener(task -> onComplete.accept(task.isSuccessful()));
    }

    @Override
    @Nullable
    public String getEmail() {
        if (firebaseAuth.getCurrentUser() == null) return null;
        else return firebaseAuth.getCurrentUser().getEmail();
    }

    @Override
    public String getUserName() {
        if (firebaseAuth.getCurrentUser() == null) return null;
        else return firebaseAuth.getCurrentUser().getDisplayName();
    }

    @Override
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
                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                    userAuthenticationState = UserAuthenticationState.VERIFIED;
                    callback.accept(true);
                } else {
                    callback.accept(false);
                }
            }
        });
    }

    public String getUid() {
        if (userAuthenticationState != UserAuthenticationState.VERIFIED) {
            return null;
        } else {
            return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        }
    }

    @Override
    public void close() {
        for (var authStateListener : authStateListeners) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
