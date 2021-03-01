package com.example.car_game.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class CarSprite {
    private final int cwidth;
    private int x;
    private final int y;
    private double xSpeed = 0;
    private final GameView gameView;
    private final Bitmap bmp;
    private final Bitmap cbmp;
    private static final int BMP_ROWS = 1;
    private static final int BMP_COLUMNS = 7;
    private static final int C_BMP_COLUMNS = 8;
    private final int width;
    private final int height;
    private boolean crashed = false;
    private int crashedFrame = 0;

    public CarSprite(GameView gameView, Bitmap bmp, Bitmap cbmp) {
        this.gameView = gameView;
        this.bmp = bmp;
        this.cbmp = cbmp;
        this.width = bmp.getWidth() / BMP_COLUMNS;
        this.height = bmp.getHeight() / BMP_ROWS;
        this.cwidth = cbmp.getWidth() / C_BMP_COLUMNS;
        x = gameView.getDisplayWidth() / 2 - width / 2;
        y = gameView.getDisplayHeight() / 10 * 8 - height / 2;
    }

    private void update() {
        if (x > gameView.getWidth() - width - xSpeed) {
            xSpeed = -1;
        }
        if (x + xSpeed < 0) {
            xSpeed = 1;
        }
        x = (int) (x + xSpeed);
    }

    public void onDraw(Canvas canvas, int orientation) {
        if (x < gameView.getDisplayWidth() / 20
                || x > (gameView.getDisplayWidth() / 20 * 19) - width) {
            crash();
        }
        if (!crashed) {
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
            crashedFrame = 0;
        } else {
            int srcX = getCrashedBitmapNumber(crashedFrame) * cwidth;
            int srcY = 0;
            Rect src = new Rect(srcX, srcY, srcX + cwidth, srcY + height);
            Rect dst = new Rect(x, y, x + cwidth, y + height);
            canvas.drawBitmap(cbmp, src, dst, null);
            crashedFrame++;
            if (crashedFrame > 60) {
                crashed = false;
                gameView.setCrashed(false);
                x = gameView.getDisplayWidth() / 2 - width / 2;
            }
        }
    }

    private int getCrashedBitmapNumber(int crashedFrame) {
        if (crashedFrame < 4) {
            return 0;
        } else if (crashedFrame < 8) {
            return 1;
        } else if (crashedFrame < 12) {
            return 2;
        } else if (crashedFrame < 16) {
            return 3;
        } else if (crashedFrame < 20) {
            return 4;
        } else if (crashedFrame < 25) {
            return 5;
        } else if (crashedFrame < 35) {
            return 6;
        } else {
            return 7;
        }
    }

    public void setxSpeed(double xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void crash() {
        gameView.setCrashed(true);
        crashed = true;
    }
}
