package com.example.car_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView {

    private final Point gameResolution = new Point(120, 80);
    private final CarSprite carSprite;
    private final Point display;
    private final GameLoopThread gameLoopThread;
    private double distance = 0.0;          // Distance car has travelled around track
    private double curvature = 0.0;         // Current track curvature, lerped between track sections
    private double trackDistance = 0.0;     // Total distance of track
    private double speed = 1;             // Current player speed
    private final Paint grassPaint = new Paint();
    private final Paint kurbPaint = new Paint();
    private final Paint roadPaint = new Paint();
    private final Paint textPaint = new Paint();
    private final Paint skyPaint = new Paint();
    private final Rect pixelRect = new Rect();
    private final Rect skyRect;
    private final double pixelWidth;
    private final double pixelHeight;
    private int frameNumber = 0;
    private float sensorValue;
    private int crashedFrames = 0;
    private boolean crashed = false;
    private double initalCrashSpeed;
    // List with pairs (curvature, distance)
    private List<Pair<Double, Double>> trackSegList;

    public GameView(Context context, Point display) {
        super(context);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.LEFT);
        skyPaint.setColor(Color.rgb(0, 200, 250));
        pixelWidth = (double) display.x / gameResolution.x;
        pixelHeight = (double) display.y / gameResolution.y;
        skyRect = new Rect(0, 0, display.x, display.y / 2 + 1);
        setupTrack1();
        gameLoopThread = new GameLoopThread(this);
        this.display = display;
        SurfaceHolder holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                try {
                    gameLoopThread.join();
                } catch (InterruptedException ignored) {
                }
            }
        });
        Bitmap carBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car_sprite);
        Bitmap crashedCarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car_crash_sprite);
        carSprite = new CarSprite(this, carBitmap, crashedCarBitmap);
    }

    // Circuito 1
    private void setupTrack1() {
        trackSegList = new ArrayList<>();
        trackSegList.add(new Pair<>(0.0, 10.0));
        trackSegList.add(new Pair<>(0.0, 20.0));
        trackSegList.add(new Pair<>(1.0, 20.0));
        trackSegList.add(new Pair<>(0.0, 40.0));
        trackSegList.add(new Pair<>(-1.0, 20.0));
        trackSegList.add(new Pair<>(1.0, 20.0));
        trackSegList.add(new Pair<>(0.0, 20.0));
        trackSegList.add(new Pair<>(0.2, 50.0));
        trackSegList.add(new Pair<>(0.0, 20.0));

        trackDistance = 0;
        trackSegList.forEach(pair -> trackDistance = trackDistance + pair.second);
    }

    protected void onDraw(Canvas canvas) {
        distance = distance + speed;
        frameNumber++;

        if (crashed) {
            if (crashedFrames == 0) {
                initalCrashSpeed = speed;
            }
            if (speed > 0) {
                speed = speed - (initalCrashSpeed / 10);
            }
            crashedFrames++;
        } else {
            speed = 1;
        }

        // Draw Sky
        canvas.drawRect(skyRect, skyPaint);


        // Get Point on track
        float fOffset = 0;
        int nTrackSection = 0;

        // Find position on track (could optimise)
        while (nTrackSection < trackSegList.size() && fOffset <= distance) {
            fOffset += trackSegList.get(nTrackSection).second;
            nTrackSection++;
        }

        try {
            double targetCurvature = trackSegList.get(nTrackSection).first;
            if (curvature < targetCurvature) {
                curvature = curvature + 0.05 * speed;
            } else if (curvature > targetCurvature) {
                curvature = curvature - 0.05 * speed;
            }
            if (curvature <= 0.05 && curvature >= -0.05 && targetCurvature == 0) {
                curvature = 0;
            }
        } catch (IndexOutOfBoundsException ex) {
            distance = 0;
            curvature = trackSegList.get(0).first;
        }

        // Draw track
        for (int i = gameResolution.y / 2; i < gameResolution.y; i++) {
            double perspective = i / (gameResolution.y / 2.0);
            double roadWidth = perspective * 0.4;
            double kurbWidth = roadWidth * 0.1;
            roadWidth *= 0.5;

            double middlePoint = 0.5 + curvature * Math.pow((2 - perspective), 3) / 2;

            // Row bounds
            double leftGrass = (middlePoint - roadWidth - kurbWidth) * gameResolution.x;
            double leftKurb = (middlePoint - roadWidth) * gameResolution.x;
            double rightKurb = (middlePoint + roadWidth) * gameResolution.x;
            double rightGrass = (middlePoint + roadWidth + kurbWidth) * gameResolution.x;

            // I use the sinus to have the color changinng constantly
            int grassColor;
            if (Math.sin(5 * Math.pow(perspective / 4 - 6, 2) + distance) > 0.6) {
                grassColor = Color.rgb(0, 150, 0);
            } else {
                grassColor = Color.rgb(0, 200, 0);
            }
            int kurbColor;
            if (Math.sin(5 * Math.pow(perspective - 6, 2) + distance) > 0.0) {
                kurbColor = Color.RED;
            } else {
                kurbColor = Color.WHITE;
            }
            // Start finish straight changes the road colour to inform the player lap is reset
            int roadColor;
            if (Math.sin(5 * Math.pow(perspective / 4 - 6, 2) + distance) > 0.0) {
                roadColor = Color.rgb(150, 150, 150);
            } else {
                roadColor = Color.rgb(160, 160, 160);
            }

            // Draw the row segments
            pixelRect.top = getRowPos(i);
            pixelRect.bottom = getRowEnd(i);

            grassPaint.setColor(grassColor);
            pixelRect.left = 0;
            pixelRect.right = (int) (leftGrass * pixelWidth);
            canvas.drawRect(pixelRect, grassPaint);

            kurbPaint.setColor(kurbColor);
            pixelRect.left = (int) (leftGrass * pixelWidth);
            pixelRect.right = (int) (leftKurb * pixelWidth);
            canvas.drawRect(pixelRect, kurbPaint);

            roadPaint.setColor(roadColor);
            pixelRect.left = (int) (leftKurb * pixelWidth);
            pixelRect.right = (int) (rightKurb * pixelWidth);
            canvas.drawRect(pixelRect, roadPaint);

            pixelRect.left = (int) (rightKurb * pixelWidth);
            pixelRect.right = (int) (rightGrass * pixelWidth);
            canvas.drawRect(pixelRect, kurbPaint);

            pixelRect.left = (int) (rightGrass * pixelWidth);
            pixelRect.right = (int) (gameResolution.x * pixelWidth);
            canvas.drawRect(pixelRect, grassPaint);
        }

        carSprite.setxSpeed(sensorValue * 10 - curvature * 40);
        carSprite.onDraw(canvas, (int) sensorValue / 2 + 3);
        canvas.drawText(String.valueOf(frameNumber), 0, display.y, textPaint);
        crashedFrames = 0;
    }

    private int getRowPos(int y) {
        return (int) (y * pixelHeight) + 1;
    }

    private int getRowEnd(int y) {
        return (int) (getRowPos(y) + pixelHeight) + 1;
    }

    public void setSensorValue(float sensorValue) {
        this.sensorValue = sensorValue;
    }

    public int getDisplayWidth() {
        return display.x;
    }

    public int getDisplayHeight() {
        return display.y;
    }

    public void pause() {
        gameLoopThread.setRunning(false);
    }

    public void resume() {
        gameLoopThread.setRunning(true);
    }

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }
}

