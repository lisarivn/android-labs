package com.example.makhovyklab1var16;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner gridSizeSpinner;
    private Spinner timerSpinner;
    private Spinner speedSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gridSizeSpinner =
                findViewById(R.id.gridSizeSpinner);

        timerSpinner =
                findViewById(R.id.timerSpinner);

        speedSpinner =
                findViewById(R.id.speedSpinner);

        Button startButton =
                findViewById(R.id.startButton);

        startButton.setOnClickListener(
                v -> startGame()
        );
    }

    private void startGame() {

        int gridSize;

        switch (gridSizeSpinner.getSelectedItemPosition()) {

            case 1:
                gridSize = 4;
                break;

            case 2:
                gridSize = 5;
                break;

            default:
                gridSize = 3;
                break;
        }

        int gameTime;

        switch (timerSpinner.getSelectedItemPosition()) {

            case 1:
                gameTime = 30;
                break;

            case 2:
                gameTime = 45;
                break;

            default:
                gameTime = 15;
                break;
        }

        int speedOption =
                speedSpinner.getSelectedItemPosition();

        Intent intent = new Intent(
                MainActivity.this,
                GameActivity.class
        );

        intent.putExtra(
                "GRID_SIZE",
                gridSize
        );

        intent.putExtra(
                "GAME_TIME",
                gameTime
        );

        intent.putExtra(
                "SPEED_OPTION",
                speedOption
        );

        startActivity(intent);
    }
}