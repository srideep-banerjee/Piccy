package com.example.piccy.model;

import androidx.annotation.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Authenticator {

    void signUp(@NonNull String email, @NonNull String name, @NonNull String password, @NonNull BiConsumer<Boolean, String> onComplete);

    void logIn(@NonNull String email, @NonNull String password, @NonNull BiConsumer<Boolean, String> onComplete);

    boolean isLoggedIn();
}
