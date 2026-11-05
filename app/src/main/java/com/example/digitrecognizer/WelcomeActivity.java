package com.example.digitrecognizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageView ivWelcomeLogo = findViewById(R.id.ivWelcomeLogo);
        TextView tvWelcomeHeader = findViewById(R.id.tvWelcomeHeader);
        TextView tvWelcomeDesc = findViewById(R.id.tvWelcomeDesc);
        Button btnStartRecognize = findViewById(R.id.btnStartRecognize);
        Button btnWelcomeExit = findViewById(R.id.btnWelcomeExit);

        // Apply smooth animations on load
        ivWelcomeLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        tvWelcomeHeader.setAlpha(0f);
        tvWelcomeDesc.setAlpha(0f);
        btnStartRecognize.setAlpha(0f);
        btnWelcomeExit.setAlpha(0f);

        tvWelcomeHeader.animate().alpha(1f).setDuration(800).start();
        tvWelcomeDesc.animate().alpha(1f).setStartDelay(200).setDuration(800).start();
        btnStartRecognize.animate().alpha(1f).setStartDelay(400).setDuration(800).start();
        btnWelcomeExit.animate().alpha(1f).setStartDelay(600).setDuration(800).start();

        // Navigate to MainActivity on click
        btnStartRecognize.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Exit App feature
        btnWelcomeExit.setOnClickListener(v -> {
            finishAffinity();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}
