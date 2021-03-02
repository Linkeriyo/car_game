package com.example.car_game.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.example.car_game.R;
import com.example.car_game.ResultActivity;

import java.util.ArrayList;
import java.util.List;


public class GameView extends SurfaceView {

    private final Point gameResolution = new Point(120, 80);
    private final CarSprite carSprite;
    private final Point display;
    private final GameLoopThread gameLoopThread;
    private final GameActivity activity;
    private final IngameSounds ingameSounds;
    private double distance = 0.0;
    private double curvature = 0.0;
    private double trackDistance = 0.0;
    private double speed = 0;
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
    private int minLaptime;
    private int remainingTime;
    private int totalTime;
    // List with pairs (curvature, distance)
    private List<Pair<Double, Double>> trackSecList;
    private boolean gameOver;
    private double initialGameOverSpeed;
    private int points;
    private int crashes = 0;
    private final Thread endThread;
    private int gameStoppedFrames;

    public GameView(Context context, Point display, GameActivity activity, int level) {
        super(context);
        this.activity = activity;
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(70);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.press_start_2p));
        skyPaint.setColor(Color.rgb(0, 200, 250));
        pixelWidth = (double) display.x / gameResolution.x;
        pixelHeight = (double) display.y / gameResolution.y;
        skyRect = new Rect(0, 0, display.x, display.y / 2 + 1);
        setupTrack(level);
        gameLoopThread = new GameLoopThread(this);
        this.display = display;
        Bitmap carBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car_sprite);
        Bitmap crashedCarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.car_crash_sprite);
        carSprite = new CarSprite(this, carBitmap, crashedCarBitmap);
        endThread = new Thread(() -> {
            activity.startActivity(new Intent(activity, ResultActivity.class)
                    .putExtra("totalTime", totalTime)
                    .putExtra("crashes", crashes)
                    .putExtra("points", points)
                    .putExtra("level", level)
            );
            gameLoopThread.setRunning(false);
            activity.finish();
        });
        ingameSounds = new IngameSounds(getContext());
        SurfaceHolder holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                gameLoopThread.setRunning(false);
                ingameSounds.stopAll();
                activity.finish();
            }
        });
    }

    private void setupTrack(int trackNumber) {
        trackSecList = new ArrayList<>();
        switch (trackNumber) {
            case 1:
                trackSecList.add(new Pair<>(0.0, 100.0));
                trackSecList.add(new Pair<>(0.6, 60.0));
                trackSecList.add(new Pair<>(1.0, 60.0));
                trackSecList.add(new Pair<>(0.0, 80.0));
                trackSecList.add(new Pair<>(-1.0, 60.0));
                trackSecList.add(new Pair<>(1.0, 60.0));
                trackSecList.add(new Pair<>(0.0, 60.0));
                trackSecList.add(new Pair<>(0.4, 100.0));
                trackSecList.add(new Pair<>(0.0, 50.0));
                minLaptime = 30;
                remainingTime = minLaptime + 15;
                break;
            case 2:
                trackSecList.add(new Pair<>(0.0, 100.0));
                trackSecList.add(new Pair<>(-0.4, 20.0));
                trackSecList.add(new Pair<>(1.0, 60.0));
                trackSecList.add(new Pair<>(0.0, 40.0));
                trackSecList.add(new Pair<>(1.0, 60.0));
                trackSecList.add(new Pair<>(-1.0, 80.0));
                trackSecList.add(new Pair<>(-0.5, 60.0));
                trackSecList.add(new Pair<>(0.0, 40.0));
                trackSecList.add(new Pair<>(0.5, 60.0));
                trackSecList.add(new Pair<>(-1.0, 80.0));
                trackSecList.add(new Pair<>(0.0, 30.0));
                minLaptime = 35;
                remainingTime = minLaptime + 8;
                break;
            case 3:
                trackSecList.add(new Pair<>(0.0, 80.0));
                trackSecList.add(new Pair<>(1.0, 15.0));
                trackSecList.add(new Pair<>(-1.0, 60.0));
                trackSecList.add(new Pair<>(0.0, 30.0));
                trackSecList.add(new Pair<>(1.0, 50.0));
                trackSecList.add(new Pair<>(-1.0, 90.0));
                trackSecList.add(new Pair<>(-0.5, 30.0));
                trackSecList.add(new Pair<>(2.0, 45.0));
                trackSecList.add(new Pair<>(-2.0, 15.0));
                trackSecList.add(new Pair<>(0.0, 40.0));
                minLaptime = 25;
                remainingTime = minLaptime + 2;
                break;
        }
        trackDistance = 0;
        trackSecList.forEach(pair -> trackDistance = trackDistance + pair.second);
    }

    protected void onDraw(Canvas canvas) {
        if (gameOver && speed == 0) {
            if (gameStoppedFrames == 0) {
                endThread.start();
            }
            gameStoppedFrames++;
        } else {
            distance = distance + speed;
            frameNumber++;

            // Draw Sky
            canvas.drawRect(skyRect, skyPaint);

            // Get track section
            double offset = 0;
            int trackSection = 0;
            while (trackSection < trackSecList.size() && offset <= distance) {
                offset += trackSecList.get(trackSection).second;
                trackSection++;
            }

            try {
                double targetCurvature = trackSecList.get(trackSection).first;
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
                curvature = trackSecList.get(0).first;
            }

            //If still on countdown
            if (frameNumber < 80) {
                speed = 0;
                if (frameNumber == 1) {
                    ingameSounds.playCountdown();
                    ingameSounds.playCarStart();
                }
                //Out of countdown
            } else {
                if (distance == 0 && frameNumber > 120) {
                    remainingTime = remainingTime + minLaptime;
                }

                //On countdown end
                if (frameNumber == 80) {
                    activity.mediaPlayer.start();
                    ingameSounds.startEngineLoop();
                }

                //Time over
                if (remainingTime == 0) {
                    gameOver = true;
                    initialGameOverSpeed = speed;
                    ingameSounds.stopEngineLoop();
                }

                //Each second
                if (frameNumber % 20 == 0) {
                    remainingTime--;
                    if (!gameOver) {
                        totalTime++;
                    }
                }

                //Game running
                if (!gameOver) {
                    if (crashed) {
                        if (crashedFrames == 0) {
                            crashes++;
                            ingameSounds.playCrash();
                            ingameSounds.stopEngineLoop();
                        }
                        if (speed > 0) {
                            speed = speed - (0.1);
                        }
                        if (speed < 0.1) {
                            speed = 0;
                        }
                        crashedFrames++;
                        if (crashedFrames == 60) {
                            ingameSounds.startEngineLoop();
                        }
                    } else {
                        speed = 1;
                        crashedFrames = 0;
                    }

                //Game over
                } else {
                    if (speed > 0) {
                        speed = speed - (initialGameOverSpeed / 10);
                    } else if (speed < 0.5) {
                        speed = 0;
                    }
                }
            }

            points = (int) (totalTime * 31.4 - crashes * 100);
            if (points < 0) {
                points = 0;
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

            if (frameNumber < 80) {
                carSprite.onDraw(canvas, 3);
                if (frameNumber < 20) {
                    canvas.drawText("3",display.x / 2 - 30, display.y / 2, textPaint);
                } else if (frameNumber < 40) {
                    canvas.drawText("2",display.x / 2 - 30, display.y / 2, textPaint);
                } else {
                    canvas.drawText("1",display.x / 2 - 30, display.y / 2, textPaint);
                }
            } else {
                int orientation = (int) sensorValue / 2 + 3;
                carSprite.setxSpeed(sensorValue * 10 - curvature * 60);
                carSprite.onDraw(canvas, orientation);
                if (orientation <= 1 || orientation >= 5) {
                    ingameSounds.startSqualLoop();
                } else {
                    ingameSounds.stopSqualLoop();
                }
                if (frameNumber < 120) {
                    canvas.drawText("Go!",display.x / 2 - 50, display.y / 2, textPaint);
                }
                if (distance > 0 && distance < 20 && frameNumber > 200) {
                    canvas.drawText("+Tiempo", display.x / 2 - 300, display.y/2, textPaint);
                }
                if (remainingTime >= 0) {
                    canvas.drawText("Tiempo restante: " + remainingTime, 16, textPaint.getTextSize() + 10, textPaint);
                    canvas.drawText("Tiempo total: " + totalTime, 16, textPaint.getTextSize() * 2 + 10, textPaint);
                    canvas.drawText("Puntos: " + points, 16, textPaint.getTextSize() * 3 + 10, textPaint);
                } else {
                    canvas.drawText("Has perdido.", display.x / 2 - 300, display.y / 2, textPaint);
                }
            }
        }
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

    public void setCrashed(boolean crashed) {
        this.crashed = crashed;
    }
}