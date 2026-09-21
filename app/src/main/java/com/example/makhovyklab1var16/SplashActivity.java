package com.example.makhovyklab1var16;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3000;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private boolean opened = false;

    private final Runnable openMainScreen =
            this::openMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        handler.postDelayed(
                openMainScreen,
                SPLASH_DELAY
        );

        View root = findViewById(R.id.main);

        root.setOnClickListener(
                v -> openMainActivity()
        );
    }

    private void openMainActivity() {

        if (opened) {
            return;
        }

        opened = true;

        handler.removeCallbacks(openMainScreen);

        Intent intent = new Intent(
                SplashActivity.this,
                MainActivity.class
        );

        startActivity(intent);

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(openMainScreen);
    }
}