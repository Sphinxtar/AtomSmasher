package com.atomsmasher;

import android.graphics.Point;
import android.graphics.Rect;

public class Player {
    int sprite;
    int level;
    boolean spin;
    int cursor;  // current sprite index in cube[level][cursor]
    int tts; // time to sprite change
    int speed;
    int direction;
    final int score;
    final Point spot;
    final Point start; // restart here
    final Rect[] hotz = new Rect[3];
    final int[] zsizes;
    final int[][] cube = new int[][]{
            { 0, 24, 25, 26, 27, 28 },
            { 1, 34, 35, 36, 37, 38 },
            { 2, 39, 40, 41, 42, 43 },
            { 3, 29, 30, 31, 32, 33 }
    };

    /**
     * spot x is vertical y is horizontal center of player sprite not left corner
     */
    public Player (PlayingField pf) {
        level = -1;
        sprite = 22;
        spin = false;
        speed = 0;
        direction = 0;
        score = 0;
        tts = 0;
        final int x = (pf.getVport().width() / 2) + pf.getVportLeft();
        final int y = (pf.getVport().height() / 2) + pf.getVportTop();
        spot = new Point(x, y);
        start = new Point(x, y);
        zsizes = new int[] { 36 * pf.getScaleFactor(), 24 * pf.getScaleFactor(), 16 * pf.getScaleFactor() };
        for (int i = 0; i < 3; i++) {
            hotz[i] = new Rect();
        }
    }

    public Rect[] getHotz() { // hot zones surround player one
         // squish
        hotz[0].left = spot.x - zsizes[0];
        hotz[0].top = spot.y - zsizes[0];
        hotz[0].right = spot.x + zsizes[0];
        hotz[0].bottom = spot.y + zsizes[0];
        // squished
        hotz[1].left = spot.x - zsizes[1];
        hotz[1].top = spot.y - zsizes[1];
        hotz[1].right = spot.x + zsizes[1];
        hotz[1].bottom = spot.y + zsizes[1];

        hotz[2].left = spot.x - zsizes[2];
        hotz[2].top = spot.y - zsizes[2];
        hotz[2].right = spot.x + zsizes[2];
        hotz[2].bottom = spot.y + zsizes[2];
        return hotz;
    }

    public void adjustPlayer(PlayingField pf) {
        int x = spot.x;
        int y = spot.y;
        if (speed > 0) {
            switch (direction){
                case 1:
                    if ((x - speed) > pf.getVportLeft())
                        spot.x = x - speed;
                    if ((y - speed) > pf.getVportTop())
                        spot.y = y - speed;
                    break;
                case 2:
                    if ((y - speed) > pf.getVportTop())
                        spot.y = y - speed;
                    break;
                case 3:
                    if ((y - speed) > pf.getVportTop())
                        spot.y = y - speed;
                    if ((x + speed) < pf.getVportRight())
                        spot.x = x + speed;
                    break;
                case 4:
                    if ((x - speed) > pf.getVportLeft())
                        spot.x = x - speed;
                    break;
                case 5:
                    break;
                case 6:
                    if ((x + speed) < pf.getVportRight())
                        spot.x = x + speed;
                    break;
                case 7:
                    if ((x - speed) > pf.getVportLeft())
                        spot.x = x - speed;
                    if ((y + speed) < pf.getVportBottom())
                        spot.y = y + speed;
                    break;
                case 8:
                    if ((y + speed) < pf.getVportBottom())
                        spot.y = y + speed;
                    break;
                case 9:
                    if ((y + speed) < pf.getVportBottom())
                        spot.y = y + speed;
                    if ((x + speed) < pf.getVportRight())
                        spot.x = x + speed;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + direction);
            }
        }
    }

    public void resetSpot() {
        spot.x = start.x;
        spot.y = start.y;
        speed = 0;
    }

    public int getSprite() {
        if (level < 0)
            sprite = 22;
        else {
            if (spin) {
                cursor++;
                if (cursor > cube[0].length) {
                    cursor = 0;
                    spin = false;
                }
            }
            else
                cursor =  0;
            sprite = cube[level][cursor];
        }
        return sprite;
    }

    public void setSprite(int sprite) {
        this.sprite = sprite;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setSpin(boolean spin) {
        this.spin = spin;
    }

    public boolean isSpin() {
        return spin;
    }
}

