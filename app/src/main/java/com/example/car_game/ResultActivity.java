package com.example.car_game;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.car_game.game.GameActivity;

public class ResultActivity extends AppCompatActivity {

    TextView timeTextView, crashesTextView, pointsTextView, backTextView, retryTextView;
    int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_result);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setupFields();
        putValues();
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void putValues() {
        String totalTime = getIntent().getIntExtra("totalTime", 0) + "s";
        String crases = getIntent().getIntExtra("crashes", 0) + "";
        String points = getIntent().getIntExtra("points", 0) + "";

        timeTextView.setText(totalTime);
        crashesTextView.setText(crases);
        pointsTextView.setText(points);
        level = getIntent().getIntExtra("level", 1);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupFields() {
        timeTextView = findViewById(R.id.time_textview);
        crashesTextView = findViewById(R.id.crashes_textview);
        pointsTextView = findViewById(R.id.points_textview);
        backTextView = findViewById(R.id.result_back_textview);
        retryTextView = findViewById(R.id.retry_textview);

        retryTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                retryTextView.setTextColor(Color.MAGENTA);
                retryTextView.setBackgroundColor(Color.CYAN);
                startActivity(new Intent(this, GameActivity.class).putExtra("level", level));
                finish();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                retryTextView.setTextColor(Color.BLACK);
                retryTextView.setBackgroundColor(Color.WHITE);
            }
            return v.performClick();
        });

        backTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                backTextView.setTextColor(Color.MAGENTA);
                backTextView.setBackgroundColor(Color.CYAN);
                finish();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                backTextView.setTextColor(Color.BLACK);
                backTextView.setBackgroundColor(Color.WHITE);
            }
            return v.performClick();
        });
    }
}