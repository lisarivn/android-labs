package com.example.makhovyklab1var16;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private int gridSize;
    private int gameTime;
    private int speedOption;

    private int score = 0;
    private int activeCellIndex = -1;

    private TextView countdownTextView;
    private TextView timeTextView;
    private TextView scoreTextView;

    private Button finishButton;
    private TableLayout gameTable;

    private final Random random = new Random();

    private final List<TextView> cells =
            new ArrayList<>();

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private boolean gameRunning = false;
    private boolean gameFinished = false;

    private CountDownTimer startTimer;
    private CountDownTimer gameTimer;

    private final Runnable moveCellRunnable =
            new Runnable() {

                @Override
                public void run() {

                    if (!gameRunning
                            || gameFinished) {

                        return;
                    }

                    moveActiveCell();

                    handler.postDelayed(
                            this,
                            getRandomDelay()
                    );
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        gridSize = getIntent()
                .getIntExtra(
                        "GRID_SIZE",
                        3
                );

        gameTime = getIntent()
                .getIntExtra(
                        "GAME_TIME",
                        15
                );

        speedOption = getIntent()
                .getIntExtra(
                        "SPEED_OPTION",
                        0
                );

        countdownTextView =
                findViewById(
                        R.id.countdownTextView
                );

        timeTextView =
                findViewById(
                        R.id.timeTextView
                );

        scoreTextView =
                findViewById(
                        R.id.scoreTextView
                );

        finishButton =
                findViewById(
                        R.id.finishButton
                );

        gameTable =
                findViewById(
                        R.id.gameTable
                );

        timeTextView.setText(
                String.valueOf(gameTime)
        );

        scoreTextView.setText(
                String.valueOf(score)
        );

        finishButton.setOnClickListener(
                v -> finishGame()
        );

        startCountdown();
    }

    private void startCountdown() {

        countdownTextView.setVisibility(
                View.VISIBLE
        );

        startTimer = new CountDownTimer(
                3000,
                1000
        ) {

            @Override
            public void onTick(
                    long millisUntilFinished
            ) {

                int seconds =
                        (int) Math.ceil(
                                millisUntilFinished
                                        / 1000.0
                        );

                countdownTextView.setText(
                        String.valueOf(seconds)
                );
            }

            @Override
            public void onFinish() {

                if (gameFinished) {
                    return;
                }

                countdownTextView.setVisibility(
                        View.GONE
                );

                startGame();
            }
        };

        startTimer.start();
    }

    private void startGame() {

        createGrid();

        gameRunning = true;

        moveActiveCell();

        handler.postDelayed(
                moveCellRunnable,
                getRandomDelay()
        );

        startGameTimer();
    }

    private void startGameTimer() {

        gameTimer = new CountDownTimer(
                gameTime * 1000L,
                1000
        ) {

            @Override
            public void onTick(
                    long millisUntilFinished
            ) {

                int secondsLeft =
                        (int) Math.ceil(
                                millisUntilFinished
                                        / 1000.0
                        );

                timeTextView.setText(
                        String.valueOf(secondsLeft)
                );
            }

            @Override
            public void onFinish() {

                timeTextView.setText("0");

                finishGame();
            }
        };

        gameTimer.start();
    }

    private void createGrid() {

        gameTable.removeAllViews();

        cells.clear();

        activeCellIndex = -1;

        int cellSizeDp;

        if (gridSize == 5) {

            cellSizeDp = 50;

        } else if (gridSize == 4) {

            cellSizeDp = 60;

        } else {

            cellSizeDp = 75;
        }

        int cellSizePx =
                dpToPx(cellSizeDp);

        int margin =
                dpToPx(4);

        for (int row = 0;
             row < gridSize;
             row++) {

            TableRow tableRow =
                    new TableRow(this);

            tableRow.setGravity(
                    Gravity.CENTER
            );

            for (int column = 0;
                 column < gridSize;
                 column++) {

                TextView cell =
                        new TextView(this);

                TableRow.LayoutParams params =
                        new TableRow.LayoutParams(
                                cellSizePx,
                                cellSizePx
                        );

                params.setMargins(
                        margin,
                        margin,
                        margin,
                        margin
                );

                cell.setLayoutParams(params);

                cell.setBackgroundColor(
                        Color.LTGRAY
                );

                int index =
                        cells.size();

                cell.setOnClickListener(
                        v -> handleCellClick(index)
                );

                cells.add(cell);

                tableRow.addView(cell);
            }

            gameTable.addView(tableRow);
        }
    }

    private void moveActiveCell() {

        if (cells.isEmpty()) {
            return;
        }

        if (activeCellIndex >= 0
                && activeCellIndex
                < cells.size()) {

            cells.get(activeCellIndex)
                    .setBackgroundColor(
                            Color.LTGRAY
                    );
        }

        int newIndex;

        do {

            newIndex =
                    random.nextInt(
                            cells.size()
                    );

        } while (
                cells.size() > 1
                        && newIndex
                        == activeCellIndex
        );

        activeCellIndex =
                newIndex;

        cells.get(activeCellIndex)
                .setBackgroundColor(
                        Color.rgb(
                                0,
                                180,
                                120
                        )
                );
    }

    private void handleCellClick(
            int index
    ) {

        if (!gameRunning
                || gameFinished) {

            return;
        }

        if (index == activeCellIndex) {

            score += 2;

            moveActiveCell();

        } else {

            score -= 1;
        }

        scoreTextView.setText(
                String.valueOf(score)
        );
    }

    private long getRandomDelay() {

        int min;
        int max;

        switch (speedOption) {

            case 1:

                min = 700;
                max = 1000;

                break;

            case 2:

                min = 500;
                max = 700;

                break;

            case 3:

                min = 300;
                max = 600;

                break;

            default:

                min = 1000;
                max = 1500;

                break;
        }

        return min
                + random.nextInt(
                max - min + 1
        );
    }

    private void finishGame() {

        if (gameFinished) {
            return;
        }

        gameFinished = true;
        gameRunning = false;

        handler.removeCallbacks(
                moveCellRunnable
        );

        if (startTimer != null) {
            startTimer.cancel();
        }

        if (gameTimer != null) {
            gameTimer.cancel();
        }

        countdownTextView.setVisibility(
                View.GONE
        );

        showResultDialog();
    }

    private void showResultDialog() {

        new AlertDialog.Builder(this)
                .setTitle(
                        R.string.game_over
                )
                .setMessage(
                        getString(
                                R.string.your_score,
                                score
                        )
                )
                .setCancelable(false)
                .setPositiveButton(
                        R.string.ok,
                        (dialog, which) ->
                                finish()
                )
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        gameRunning = false;

        handler.removeCallbacks(
                moveCellRunnable
        );

        if (startTimer != null) {
            startTimer.cancel();
        }

        if (gameTimer != null) {
            gameTimer.cancel();
        }
    }

    private int dpToPx(int dp) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int) (
                dp * density
        );
    }
}