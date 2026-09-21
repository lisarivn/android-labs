package com.example.makhovyklab1;

public interface ResponseListener<T> {
    void onResults(T results);
}