package com.example.simonsays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Handler backgroundHandler;
    private HandlerThread handlerThread;

    //TODO: UI Elements
    private TextView statusText;
    private Button redButton;
    private Button blueButton;
    private Button yellowButton;
    private Button greenButton;
    private Button restartButton;

    // Game variables
    private final List<Integer> sequence = new ArrayList<>();
    private int playerIndex = 0;
    private int score = 0;
    private int lifes = 0;
    private boolean ledInitialized = false;
    private final Random random = new Random();
    private final Handler handler = new Handler();

    // Used to load the 'myjni_application' library on application startup.
    static {
        System.loadLibrary("simonsays");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: Initialize UI Elements
        redButton = findViewById(R.id.circularButton1);
        blueButton = findViewById(R.id.circularButton2);
        yellowButton = findViewById(R.id.circularButton3);
        greenButton = findViewById(R.id.circularButton4);
        statusText = findViewById(R.id.sample_text);
        restartButton = findViewById(R.id.button_restart); // Changed to Button

        handlerThread = new HandlerThread ("BackgroundThread");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());

        //TODO: Set OnClickListener for buttons
        redButton.setOnClickListener(buttonClickListener);
        blueButton.setOnClickListener(buttonClickListener);
        yellowButton.setOnClickListener(buttonClickListener);
        greenButton.setOnClickListener(buttonClickListener);
        restartButton.setOnClickListener(v -> startNewGame());

        Button navigateButton = findViewById(R.id.button_back);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        //TODO: Start the game
        startNewGame();
    }

    protected void onDestroy() {
        super.onDestroy();
        if(handlerThread != null){
            handlerThread.quit();
        }
    }

    //TODO: Button click listener
    private final View.OnClickListener buttonClickListener = v -> {
        int pressedButton;
        if (v.getId() == R.id.circularButton1) {
            pressedButton = 1;
        } else if (v.getId() == R.id.circularButton2) {
            pressedButton = 2;
        } else if (v.getId() == R.id.circularButton3) {
            pressedButton = 3;
        } else if (v.getId() == R.id.circularButton4) {
            pressedButton = 4;
        } else {
            return;
        }

        Log.d(TAG, "Button " + pressedButton + " clicked");

        if (sequence.get(playerIndex) == pressedButton) {
            playerIndex++;
            if (playerIndex == sequence.size()) {
                //TODO: Player successfully completed the sequence
                //Toast.makeText(MainActivity.this, "Good job! Get ready for the next round!", Toast.LENGTH_SHORT).show();
                score += 1;
                updateStatusText("Well Done! Next Round!");

                updateScoreonLCDinBackground("Player Score", String.valueOf(score * 100));

                //TODO: Delay before starting the next round
                handler.postDelayed(() -> {
                    addNewStepToSequence();
                    showSequence();
                }, 1000);
            }
        } else {
            lifes--;
            update_lifes();
            if (lifes > 0) {
                playerIndex = 0;
                handler.postDelayed(this::showSequence, 1000);
                updateStatusText("Try Again!");
            }
            else{
                updateStatusText("");
                gameOver();
            }
        }
    };

    //TODO: Update status text
    @SuppressLint("SetTextI18n")
    private void updateStatusText(String message) {
        statusText.setText(message);
    }

    //TODO: Update the lifes_remaining
    private void update_lifes(){
        updateScoreOnLCD("Player Score", String.valueOf(score * 100));
        if (!ledInitialized) {
            initializeLED();
            ledInitialized = true;
        }
        updateLEDOnLEDController(lifes);
    }


    //TODO: Start a new game
    private void startNewGame() {
        sequence.clear();
        playerIndex = 0;
        score = 0;
        lifes = 3;

        // Enable button clicks
        redButton.setEnabled(true);
        blueButton.setEnabled(true);
        yellowButton.setEnabled(true);
        greenButton.setEnabled(true);
        initializeLED();

        updateScoreonLCDinBackground("Player Score", String.valueOf(score * 100));

        addNewStepToSequence();
        handler.postDelayed(this::showSequence, 1000); // Small delay before showing the first sequence
    }

    //TODO: Add a new step to the sequence
    @SuppressLint("SetTextI18n")
    private void addNewStepToSequence() {
        updateStatusText("Ready, Go!");
        int nextStep = random.nextInt(4) + 1;
        sequence.add(nextStep);
    }

    //TODO: Show the sequence of button highlights
    private void showSequence() {
        updateStatusText("Ready, Go!");
        playerIndex = 0;
        for (int i = 0; i < sequence.size(); i++) {
            int step = sequence.get(i);
            handler.postDelayed(() -> highlightButton(step), i * 1500L);
        }
    }

    //TODO: Highlight a button
    @SuppressLint("ResourceAsColor")
    private void highlightButton(int step) {
        Button button;
        int highlightColor, default_color;
        switch (step) {
            case 1:
                button = findViewById(R.id.circularButton1);
                highlightColor = ContextCompat.getColor(this, R.color.red_glow);
                default_color = ContextCompat.getColor(this, R.color.red);
                break;
            case 2:
                button = findViewById(R.id.circularButton2);
                highlightColor = ContextCompat.getColor(this, R.color.blue_glow);
                default_color = ContextCompat.getColor(this, R.color.blue);
                break;
            case 3:
                button = findViewById(R.id.circularButton3);
                highlightColor = ContextCompat.getColor(this, R.color.yellow_glow);
                default_color = ContextCompat.getColor(this, R.color.yellow);
                break;
            case 4:
                button = findViewById(R.id.circularButton4);
                highlightColor = ContextCompat.getColor(this, R.color.green_glow);
                default_color = ContextCompat.getColor(this, R.color.green);
                break;
            default:
                return;
        }
        button.getBackground().setColorFilter(highlightColor, PorterDuff.Mode.SRC_ATOP);
        handler.postDelayed(() -> button.getBackground().setColorFilter(default_color, PorterDuff.Mode.SRC_ATOP), 1000);
    }

    private void gameOver() {
        updateStatusText("GAME OVER!");

        // Clear the sequence
        sequence.clear();

        // Cancel any pending highlight tasks
        handler.removeCallbacksAndMessages(null);

        updateScoreonLCDinBackground("Final Score", String.valueOf(score * 100));

        // Disable button clicks
        redButton.setEnabled(false);
        blueButton.setEnabled(false);
        yellowButton.setEnabled(false);
        greenButton.setEnabled(false);
    }

    private void updateScoreonLCDinBackground(String line1, String line2){
        // Update initial score on text LCD in a background thread
        backgroundHandler.post(() -> {
            try {
                String result = updateScoreOnLCD(line1, line2);
                handler.post(() -> {
                    Log.d(TAG, result);
                });
            } catch (Exception e) {
                Log.e(TAG, "JNI call failed", e);
                handler.post(() -> Toast.makeText(MainActivity.this, "Error updating score", Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Native method to update the score on the text LCD
    public native String updateScoreOnLCD(String line1, String line2);
    public native String updateLEDOnLEDController(int lifes_remaining);
    public native void initializeLED();


}


