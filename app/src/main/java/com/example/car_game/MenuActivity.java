package com.example.car_game;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MenuActivity extends AppCompatActivity {

    TextView timeTextView, crashesTextView, pointsTextView;
    MediaPlayer mediaPlayer;
    private boolean mediaPlayerPaused = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mediaPlayer = MediaPlayer.create(this, R.raw.space_love_adventure);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        mediaPlayerPaused = false;
    }

    public void resetMediaPlayer() {
        mediaPlayer.stop();
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        if (mediaPlayerPaused) {
            mediaPlayer.start();
            mediaPlayerPaused = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayerPaused = true;
        }
    }
}