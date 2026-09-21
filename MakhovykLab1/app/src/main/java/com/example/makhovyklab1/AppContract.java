package com.example.makhovyklab1;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public interface AppContract {

    void toOptionsScreen(Fragment target, @Nullable Student student);

    void toResultsScreen(Fragment target, Student student);

    void cancel();

    <T> void publish(T data);

    <T> void registerListener(
            Fragment fragment,
            Class<T> clazz,
            ResponseListener<T> listener
    );

    void unregisterListeners(Fragment fragment);
}