package com.example.car_game.game;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.car_game.R;

import java.util.List;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    GameView gameView;
    SensorManager sensorManager;
    Sensor accelerometerSensor;
    MediaPlayer mediaPlayer;
    private boolean mediaPlayerPaused = true;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Point display = new Point();
        getDisplay().getSize(display);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!sensorList.isEmpty()) {
            accelerometerSensor = sensorList.get(0);
            sensorManager.registerListener(this, accelerometerSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.ingamemusic);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.7f, 0.7f);
        mediaPlayerPaused = false;

        gameView = new GameView(this, display, this, getIntent().getIntExtra("level", 1));
        setContentView(gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        setContentView(gameView);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        if (mediaPlayerPaused) {
            mediaPlayer.start();
            mediaPlayerPaused = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayerPaused = true;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float y = event.values[1];
                if (y < 1 && y > -1) {
                    y = 0;
                }
                gameView.setSensorValue(y);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}