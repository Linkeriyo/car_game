package com.example.car_game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    TextView timeTextView, crashesTextView, pointsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        setupFields();
        putValues();
    }

    private void putValues() {
        String totalTime = getIntent().getIntExtra("totalTime", 0) + "s";
        String crases = getIntent().getIntExtra("crashes", 0) + "";
        String points = getIntent().getIntExtra("points", 0) + "";

        timeTextView.setText(totalTime);
        crashesTextView.setText(crases);
        pointsTextView.setText(points);
    }

    private void setupFields() {
        timeTextView = findViewById(R.id.time_textview);
        crashesTextView = findViewById(R.id.crashes_textview);
        pointsTextView = findViewById(R.id.points_textview);
    }
}