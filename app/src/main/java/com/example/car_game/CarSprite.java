package com.example.car_game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class CarSprite {
    private int x;
    private final int y;
    private double xSpeed = 0;
    private final GameView gameView;
    private final Bitmap bmp;
    private static final int BMP_ROWS = 1;
    private static final int BMP_COLUMNS = 7;
    private final int width;
    private final int height;

    public CarSprite(GameView gameView, Bitmap bmp) {
        this.gameView = gameView;
        this.bmp = bmp;
        this.width = bmp.getWidth() / BMP_COLUMNS;
        this.height = bmp.getHeight() / BMP_ROWS;
        x = gameView.getDisplayWidth()/2 - width/2;
        y = gameView.getDisplayHeight()/10 * 8 - height/2;
    }

    private void update() {
        if (x > gameView.getWidth() - width - xSpeed) {
            xSpeed = -1;
        }
        if (x + xSpeed< 0) {
            xSpeed = 1;
        }
        x = (int) (x + xSpeed);
    }

    public void onDraw(Canvas canvas, int orientation) {
        update();
        if (orientation < 0) {
            orientation = 0;
        } else if (orientation > BMP_COLUMNS - 1) {
            orientation = BMP_COLUMNS - 1;
        }
        int srcX = orientation * width;
        int srcY = 0;
        Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
        Rect dst = new Rect(x, y, x + width, y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    public void setxSpeed(double xSpeed) {
        this.xSpeed = xSpeed;
    }
}
