package com.atomsmasher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
public class Npc {

    public final Bot[] bots = new Bot[4];
    public final Zone[] zones = new Zone[17];
    public final Point[] spots = new Point[4];

    public Npc(Context context, PlayingField pf) {
        for (int i = 0; i < spots.length; i++)
            spots[i] = new Point();
        loadBots(context, pf);
    }
    private void loadBots(Context context, PlayingField pf) {
        // n :type:state:x,y  :rection:speed:tts:respawn:sprite:
        // n:0:0:40,40:0:2:60:1000:4:
        String line;
        String[] words;
        String[] woids;
        int botnum = 0;
        int zonenum = 0;
        InputStream in;
        try {
            in = context.getAssets().open("npc");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while (true) {
            try {
                if ((line = reader.readLine()) == null) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (line.isEmpty()) {
                continue;
            }
            words = line.split(":");
            if (words[0]. equals("n")) {
                bots[botnum] = new Bot();
                bots[botnum].spot = new Point();
                bots[botnum].setType(Integer.parseInt(words[1]));
                bots[botnum].setState(Integer.parseInt(words[2]));
                woids = words[3].split(",");
                bots[botnum].setSpot((pf.getVportLeft() + (Integer.parseInt(woids[0]) * pf.scalefactor)),
                        (pf.getVportTop() + (Integer.parseInt(woids[1]) * pf.scalefactor)));
                spots[botnum].x = bots[botnum].getSpot().x; // save for reset
                spots[botnum].y = bots[botnum].getSpot().y;
                bots[botnum].setRection(Integer.parseInt(words[4]));
                if (bots[botnum].getRection() == 0) {
                    if (Dragon.getRandom(1, 49) % 2 == 0)
                        bots[botnum].setRection(Dragon.getRandom(1, 4));
                    else
                        bots[botnum].setRection(Dragon.getRandom(6, 9));
                }
                bots[botnum].setSpeed(Integer.parseInt(words[5]) + pf.scalefactor);
                bots[botnum].setTts(Integer.parseInt(words[6]));
                bots[botnum].setRespawn(Integer.parseInt(words[7]));
                bots[botnum].setSprite(Integer.parseInt(words[8]));
                botnum++;
            }
            if (words[0]. equals("z")) {
                zones[zonenum] = new Zone();
                zones[zonenum].area = new Rect();
                zones[zonenum].area.left = (pf.getVportLeft() + (Integer.parseInt(words[1]) * pf.scalefactor));
                zones[zonenum].area.top = (pf.getVportTop() + (Integer.parseInt(words[2]) * pf.scalefactor));
                zones[zonenum].area.right = (pf.getVportLeft() + (Integer.parseInt(words[3]) * pf.scalefactor));
                zones[zonenum].area.bottom = (pf.getVportTop() + (Integer.parseInt(words[4]) * pf.scalefactor));
                zones[zonenum].sprite[0] = (Integer.parseInt(words[5]));
                zones[zonenum].sprite[1] = (Integer.parseInt(words[6]));
                zonenum++;
            }
        }
        try {
            in.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void collisions(PlayingField pf, Rect[] hotzones, Racket racket, Player player) {
        int zonenum = 0;
        int r;
        for (Zone z : zones) {
            for (Bot b : bots) {
                if (b.getState() == 1 && b.respawn > 0)
                    b.respawn--;
                if (b.respawn <= 0)
                    b.setState(0);
                for (int i=0; i < 3; i++) { // player collision
                    if (hotzones[i].contains(b.getSpot().x, b.getSpot().y)) {
                        switch (i) {
                            case 0:
                                switch (b.getRection()) {
                                    case 1:
                                    case 9:                     // orange : purple
                                        b.setSprite(b.getState() == 0 ? 14 : 18); // 45 squish
                                        continue;
                                    case 2:
                                    case 8:
                                        b.setSprite(b.getState() == 0 ? 7 : 12); // horizontal squish
                                        continue;
                                    case 3:
                                    case 7:
                                        b.setSprite(b.getState() == 0 ? 16 : 20); // 270 squish
                                        continue;
                                    case 4:
                                    case 6:
                                        b.setSprite(b.getState() == 0 ? 5 : 10); // vertical squish
                                        continue;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + b.getRection());
                                }
                            case 1:
                                // racket.playBounce();
                                switch (b.getRection()) {
                                    case 1:
                                    case 9:                     // orange : purple
                                        b.setSprite(b.getState() == 0 ? 15 : 19); // 45 squished
                                        continue;
                                    case 2:
                                    case 8:
                                        b.setSprite(b.getState() == 0 ? 8 : 13); // horizontal squished
                                        continue;
                                    case 3:
                                    case 7:
                                        b.setSprite(b.getState() == 0  ? 17 : 21); // 270 squished
                                        continue;
                                    case 4:
                                    case 6:
                                        b.setSprite(b.getState() == 0 ? 6 : 11); // vertical squished
                                        continue;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + b.rection);
                                }
                            case 2:
                                if (b.getRection() < 5)
                                    b.setRection(Dragon.getRandom(1, 4));
                                else
                                    b.setRection(Dragon.getRandom(6, 9));
                                if (b.getState() == 0) {
                                    b.setState(1);
                                    b.setRespawn(1200 - (300 * player.getLevel()));
                                    player.setSpin(true);
                                    // racket.playBounce();
                                }
                            default:
                                break;
                        }
                    }
                }

                if (z.area.contains(b.spot.x, b.spot.y)) { // edge bounce
                    b.setSprite(z.sprite[b.state]);
                    switch (zonenum) {
                        case 5: // left vertical
                            b.setRection(Dragon.getRandom(1, 3) * 3);
                            break;
                        case 6: // top horizontal
                            b.setRection(Dragon.getRandom(7, 9));
                            break;
                        case 7: // right vertical
                            r = Dragon.getRandom(1, 3);
                            if (r == 1)
                                b.setRection(1);
                            else if (r == 2)
                                b.setRection(4);
                             else if (r == 3)
                                b.setRection(7);
                            break;
                        case 8: // bottom horizontal
                            b.setRection(Dragon.getRandom(1, 3));
                            break;
                        case 13: // top left corner
                            b.setRection(9);
                            break;
                        case 14: // top right corner
                            b.setRection(7);
                            break;
                        case 15: // bottom right corner
                            b.setRection(1);
                            break;
                        case 16: // bottom left corner
                            b.setRection(3);
                        default:
                            break;
                    }
                }
                if (b.speed > 0) { // adjust bot
                    switch (b.rection) {
                        case 1:
                            if ((b.spot.x - b.speed) > pf.getVportLeft())
                                b.spot.x -= b.speed;
                            if ((b.spot.y - b.speed) > pf.getVportTop())
                                b.spot.y -= b.speed;
                            break;
                        case 2:
                            if ((b.spot.y - b.speed) > pf.getVportTop())
                                b.spot.y -= b.speed;
                            break;
                        case 3:
                            if ((b.spot.y - b.speed) > pf.getVportTop())
                                b.spot.y -= b.speed;
                            if ((b.spot.x + b.speed) < pf.getVportRight())
                                b.spot.x += b.speed;
                            break;
                        case 4:
                            if ((b.spot.x - b.speed) > pf.getVportLeft())
                                b.spot.x -= b.speed;
                            break;
                        case 5:
                            break;
                        case 6:
                            if ((b.spot.x + b.speed) < pf.getVportRight())
                                b.spot.x += b.speed;
                            break;
                        case 7:
                            if ((b.spot.x - b.speed) > pf.getVportLeft())
                                b.spot.x -= b.speed;
                            if ((b.spot.y + b.speed) < pf.getVportBottom())
                                b.spot.y += b.speed;
                            break;
                        case 8:
                            if ((b.spot.y + b.speed) < pf.getVportBottom())
                                b.spot.y += b.speed;
                            break;
                        case 9:
                            if ((b.spot.y + b.speed) < pf.getVportBottom())
                                b.spot.y += b.speed;
                            if ((b.spot.x + b.speed) < pf.getVportRight())
                                b.spot.x += b.speed;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + b.rection);
                    }
                }
            }
            zonenum++;
        }
    }

    public void resetBots(int level, Racket racket) {
        for (int i = 0; i < bots.length; i++) {
            bots[i].setSpot(spots[i].x, spots[i].y);
            bots[i].setState(0);
            bots[i].setRespawn(1000 - (250 * level));
            racket.play(4);
        }
    }
    public void connectOrange(Canvas canvas, Paint p) {
        float[] bones = new float[16];
        int lines = 0;
        for (Bot b : bots) {
            if (b.state == 0) {
                bones[lines++] = b.spot.x;
                bones[lines++] = b.spot.y;
                if (lines > 2) {
                    bones[lines++] = b.spot.x;
                    bones[lines++] = b.spot.y;
                }
            }
        }
        bones[lines++] = bones[0];
        bones[lines] = bones[1];
        if (((lines + 1) % 4) > 0)
            lines = lines - lines % 4;
        if (lines >= 3) {
            canvas.drawLines(bones, 0, lines + 1, p);
        }
    }

    public static class Zone {
        Rect area;
        final int[] sprite = new int[2];
    }

    public static class Bot {
        int type;
        int state;
        Point spot; // virtual world coordinates
        int speed; // amount to increment
        int rection; // direct on keypad to increment x and y 0 or 5 is stopped
        int tts; // time til spin - at 0 reset to a random amount and changes direction/speed
        int respawn;
        int sprite;

//        public int getRespawn() { return respawn; }
        public void setRespawn(int respawn) { this.respawn = respawn; }
//        public int getSpeed() { return speed; }
        public void setSpeed(int speed) { this.speed = speed; }
//        public int getTts() { return tts; }
        public void setTts(int tts) { this.tts = tts; }
//        public int getBirthsound() { return birthsound; }
        public void setSprite(int sprite) { this.sprite = sprite; }
        public int getRection() { return rection; }
        public void setRection(int rection) { this.rection = rection; }
        public void setType(int type) { this.type = type; }
//        public int getType() { return this.type; }
        public void setState(int state) { this.state = state; }
        public int getState() { return state; }
        public void setSpot(int x, int y){
            this.spot.x = x; this.spot.y = y;
        }
        public Point getSpot() { return this.spot; }
    }
}
