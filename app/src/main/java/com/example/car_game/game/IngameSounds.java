package com.example.car_game.game;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.car_game.R;

import java.io.IOException;

public class IngameSounds {

    private final MediaPlayer countdownMP;
    private final MediaPlayer carstartMP;
    private final PerfectLoopMediaPlayer engineloopMP;
    private final PerfectLoopMediaPlayer tiresqualMP;
    private final MediaPlayer crashMP;

    public IngameSounds(Context context) {
        countdownMP = MediaPlayer.create(context, R.raw.countdown);
        countdownMP.setVolume(0.8f, 0.8f);
        carstartMP = MediaPlayer.create(context, R.raw.carstartgarage);
        engineloopMP = PerfectLoopMediaPlayer.create(context, R.raw.engineloop);
        engineloopMP.setVolume(0.4f,0.4f);
        tiresqualMP = PerfectLoopMediaPlayer.create(context, R.raw.tiresqual);
        tiresqualMP.pause();
        crashMP = MediaPlayer.create(context, R.raw.crash);
    }

    public void playCountdown() {
        if (!countdownMP.isPlaying()) {
            countdownMP.start();
            countdownMP.setOnCompletionListener(mp -> {
                try {
                    mp.stop();
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void playCarStart() {
        if (!carstartMP.isPlaying()) {
            carstartMP.start();
            carstartMP.setOnCompletionListener(mp -> {
                try {
                    mp.stop();
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void playCrash() {
        if (!crashMP.isPlaying()) {
            crashMP.start();
            crashMP.setOnCompletionListener(mp -> {
                try {
                    mp.stop();
                    mp.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void startEngineLoop() {
        if (!engineloopMP.isPlaying()) {
            engineloopMP.start();
        }
    }

    public void stopEngineLoop() {
        if (engineloopMP.isPlaying()) {
            engineloopMP.pause();
        }
    }

    public void startSqualLoop() {
        if (!tiresqualMP.isPlaying()){
            tiresqualMP.start();
        }
    }

    public void stopSqualLoop() {
        if (tiresqualMP.isPlaying()) {
            tiresqualMP.pause();
        }

    }

    public void stopAll() {
        try {
            carstartMP.stop();
            countdownMP.stop();
            engineloopMP.stop();
            tiresqualMP.stop();
            crashMP.stop();
        } catch (Exception ignored) {}
    }
}
