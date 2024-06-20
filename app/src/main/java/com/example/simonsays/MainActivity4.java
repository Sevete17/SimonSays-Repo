package com.example.simonsays;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity4 extends AppCompatActivity {

    private ObjectAnimator animator;
    private TextView scrollDownIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        scrollDownIndicator = findViewById(R.id.scrollDownIndicator);

        // Create and start animation
        animator = ObjectAnimator.ofFloat(scrollDownIndicator, "alpha", 0f, 1f);
        animator.setDuration(1000);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animator != null) {
            animator.cancel(); // Stop animation on destroy
        }
        if (scrollDownIndicator != null) {
            scrollDownIndicator.clearAnimation();
        }
        animator = null;
        scrollDownIndicator = null;
    }
}
