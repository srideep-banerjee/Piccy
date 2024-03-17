package com.example.piccy.model.auth;

import androidx.annotation.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Authenticator {

    @NonNull
    UserAuthenticationState getUserAuthenticationState();

    void signUp(@NonNull String email, @NonNull String name, @NonNull String password, @NonNull BiConsumer<Boolean, String> onComplete);

    void logIn(@NonNull String email, @NonNull String password, @NonNull BiConsumer<Boolean, String> onComplete);

    void isVerified(Consumer<Boolean> callback);

    void resendVerificationEmail(Consumer<Boolean> onComplete);

    String getEmail();

    String getUserName();

    String getUid();

    void close();
}
