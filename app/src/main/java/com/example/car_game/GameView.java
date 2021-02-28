package com.example.car_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView {

    private Point gameResolution = new Point(800, 600);
    private Bitmap bmp;
    private Point display;
    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
    private double distance = 0.0;          // Distance car has travelled around track
    private double curvature = 0.0;         // Current track curvature, lerped between track sections
    private double trackCurvature = 0.0;    // Accumulation of track curvature
    private double trackDistance = 0.0;     // Total distance of track
    private double carPos = 0.0;            // Current car position
    private double playerCurvature = 0.0;   // Accumulation of player curvature
    private double speed = 0.0;             // Current player speed
    private Paint grassPaint = new Paint();
    private Paint kurbPaint = new Paint();
    private Paint roadPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint skyPaint = new Paint();
    private Rect pixelRect = new Rect();
    private Rect skyRect;
    private int pixelWidth, pixelHeight;
    private int frameNumber = 0;
    // List with pairs (curvature, distance)
    private List<Pair<Double, Double>> trackSegList;

    public GameView(Context context, Point display) {
        super(context);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(20);
        skyPaint.setColor(Color.CYAN);
        pixelWidth = display.x / gameResolution.x;
        pixelHeight = display.y / gameResolution.y;
        skyRect = new Rect(0, 0, display.x, display.y / 2);
        setupTrack1();
        gameLoopThread = new GameLoopThread(this);
        this.display = display;
        holder = getHolder();
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

    }

    // Circuito 1
    private void setupTrack1() {
        trackSegList = new ArrayList<>();
        trackSegList.add(new Pair<>(0.0, 10.0));
        trackSegList.add(new Pair<>(0.0, 200.0));
        trackSegList.add(new Pair<>(1.0, 200.0));
        trackSegList.add(new Pair<>(0.0, 400.0));
        trackSegList.add(new Pair<>(-1.0, 200.0));
        trackSegList.add(new Pair<>(1.0, 200.0));
        trackSegList.add(new Pair<>(0.0, 200.0));
        trackSegList.add(new Pair<>(0.2, 500.0));
        trackSegList.add(new Pair<>(0.0, 200.0));
    }

    protected void onDraw(Canvas canvas) {
        distance = distance + 2;
        frameNumber++;
        canvas.drawText(frameNumber + "", 0, 0, textPaint);

        // Get Point on track
        float fOffset = 0;
        int nTrackSection = 0;

        // Find position on track (could optimise)
        while (nTrackSection < trackSegList.size() && fOffset <= distance) {
            fOffset += trackSegList.get(nTrackSection).second;
            nTrackSection++;
        }

        // Draw Sky
        canvas.drawRect(skyRect, skyPaint);


        // Draw Track - Each row is split into grass, clip-board and track
        for (int i = gameResolution.y / 2; i < gameResolution.y; i++) {
            double fPerspective = (double) i / (gameResolution.y / 2.0);
            double fRoadWidth = 0.1 + fPerspective * 0.8; // Min 10% Max 90%
            double fClipWidth = fRoadWidth * 0.15;
            fRoadWidth *= 0.5;    // Halve it as track is symmetrical around center of track, but offset...

            // ...depending on where the middle point is, which is defined by the current
            // track curvature.
            double fMiddlePoint = 0.5 + curvature * Math.pow((1.0 - fPerspective), 3);

            // Work out segment boundaries
            int leftGrass = (int) (fMiddlePoint - fRoadWidth - fClipWidth) * gameResolution.x;
            int leftKurb = (int) (fMiddlePoint - fRoadWidth) * gameResolution.x;
            int rightKurb = (int) (fMiddlePoint + fRoadWidth) * gameResolution.x;
            int rightGrass = (int) (fMiddlePoint + fRoadWidth + fClipWidth) * gameResolution.x;

            // I use the sinus to have the color changinng constantly
            int grassColor;
            if (Math.sin(20.0 * Math.pow(1.0 - fPerspective, 3) + distance * 0.1) > 0.0) {
                grassColor = Color.GREEN;
            } else {
                grassColor = Color.MAGENTA;
            }
            int kurbColor;
            if (Math.sin(80.0 * Math.pow(1.0 - fPerspective, 2) + distance) > 0.0) {
                kurbColor = Color.RED;
            } else {
                kurbColor = Color.WHITE;
            }
            // Start finish straight changes the road colour to inform the player lap is reset
            int roadColor;
            if ((nTrackSection - 1) == 0) {
                roadColor = Color.WHITE;
            } else {
                roadColor = Color.GRAY;
            }

            Point start;
            Point end;

            // Draw the row segments
            pixelRect.top = getRowPos(i);
            pixelRect.bottom = getRowEnd(i);

            grassPaint.setColor(grassColor);
            start = getPixelPos(0, i);
            end = getPixelEndPos(leftGrass, i);
            pixelRect.left = 0;
            pixelRect.right = leftGrass;
            canvas.drawRect(pixelRect, grassPaint);

            kurbPaint.setColor(kurbColor);
            start = getPixelPos(leftGrass, i);
            end = getPixelEndPos(leftKurb, i);
            pixelRect.left = leftGrass;
            pixelRect.right = leftKurb;
            canvas.drawRect(pixelRect, kurbPaint);

            roadPaint.setColor(roadColor);
            start = getPixelPos(leftKurb, i);
            end = getPixelEndPos(rightKurb, i);
            pixelRect.left = leftKurb;
            pixelRect.right = rightKurb;
            canvas.drawRect(pixelRect, roadPaint);

            kurbPaint.setColor(kurbColor);
            start = getPixelPos(rightKurb, i);
            end = getPixelEndPos(rightGrass, i);
            pixelRect.left = rightKurb;
            pixelRect.right = rightGrass;
            canvas.drawRect(pixelRect, kurbPaint);

            grassPaint.setColor(grassColor);
            start = getPixelPos(rightGrass, i);
            end = getPixelEndPos(gameResolution.x, i);
            pixelRect.left = rightGrass;
            pixelRect.right = gameResolution.x;
            canvas.drawRect(pixelRect, grassPaint);
        }
    }

    private int getRowPos(int y) {
        return y * pixelHeight;
    }

    private int getRowEnd(int y) {
        return getRowPos(y) + pixelHeight;
    }

    private Point getPixelPos(int x, int y) {
        return new Point(x, y);
    }

    private Point getPixelEndPos(int x, int y) {
        Point realPoint = new Point(getPixelPos(x, y));
        realPoint.x = realPoint.x + pixelWidth;
        realPoint.y = realPoint.y + pixelHeight;
        return realPoint;
    }
}

