package com.example.digitrecognizer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        TextView tvSplashTitle = findViewById(R.id.tvSplashTitle);
        TextView tvSplashSubtitle = findViewById(R.id.tvSplashSubtitle);

        // Apply smooth fade-in and scale animations to logo
        ivLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up));

        ObjectAnimator titleAlpha = ObjectAnimator.ofFloat(tvSplashTitle, "alpha", 0f, 1f);
        ObjectAnimator subtitleAlpha = ObjectAnimator.ofFloat(tvSplashSubtitle, "alpha", 0f, 1f);
        titleAlpha.setDuration(1200);
        subtitleAlpha.setDuration(1200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(titleAlpha, subtitleAlpha);
        animatorSet.start();

        // Transition to WelcomeActivity after 2.5 seconds with smooth fade transition
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }
}
