package com.example.simonsays;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button navigateButton = findViewById(R.id.startButton);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button aboutButton = findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                startActivity(intent);
            }
        });

        Button howToPlayButton = findViewById(R.id.htpButton); // Corrected button ID
        howToPlayButton.setOnClickListener(new View.OnClickListener() { // Corrected button variable
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity4.class);
                startActivity(intent);
            }
        });

        // Initialize MediaPlayer and start playing the music
        mediaPlayer = MediaPlayer.create(this, R.raw.main_page_music_new);
        mediaPlayer.setLooping(true); // Loop the music
        mediaPlayer.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop and release the MediaPlayer when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the music when the activity is paused
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume the music when the activity is resumed
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}
