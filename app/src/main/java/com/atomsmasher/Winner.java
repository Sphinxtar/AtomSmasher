package com.atomsmasher;

import static java.lang.Integer.*;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;

// top ten list
public class Winner {
    public final Rect edit = new Rect();
    private static final short[] bigwinner = new short[11];
    private static final Topten[] topten = new Topten[10];
    private static int cursor; // current char in bigwinner
    public final Rect[] keyboard = new Rect[28];
    public final Rect trashcan = new Rect();
    public final Rect hamburger = new Rect();
    public final float[] star = new float[456];
    public long wintime = 0;
    public long score = 0;
    public int header; // topten.png header height
    public int center; // center of top ten display
    public int leading; // lines between top ten scores
    private static boolean flip;

    public Winner(Context context, PlayingField pf) {
        loadKeyBoard(context, pf);
        for (int i = 0; i < topten.length; i++)
            topten[i] = new Topten();
        loadTopTen(context);
        flip = false;
    }

    private void loadKeyBoard(Context context, PlayingField pf) {
        String line;
        String[] words;
        int keynum = 0;
        InputStream in;
        int px = 2; // index into star
        cursor = 0;
        try {
            in = context.getAssets().open("winner");
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
            switch (words[0]) {
                case "k":
                    keyboard[keynum] = new Rect();
                    keyboard[keynum].left = pf.getVportLeft() + (parseInt(words[1]) * pf.scalefactor);
                    keyboard[keynum].top = pf.getVportTop() + (parseInt(words[2]) * pf.scalefactor);
                    keyboard[keynum].right = keyboard[keynum].left + (parseInt(words[3]) * pf.scalefactor);
                    keyboard[keynum].bottom = keyboard[keynum].top + (parseInt(words[4]) * pf.scalefactor);
                    keynum++;
                    break;

                case "e":
                    edit.left = pf.getVportLeft() + (parseInt(words[1]) * pf.scalefactor);
                    edit.top = pf.getVportTop() + (parseInt(words[2]) * pf.scalefactor);
                    edit.right = edit.left + (parseInt(words[3]) * pf.scalefactor);
                    edit.bottom = edit.top + (parseInt(words[4]) * pf.scalefactor);
                    break;

                case "h":
                    header = pf.getVportTop() + (parseInt(words[1]) * pf.scalefactor);
                    center = pf.getVportLeft() + (parseInt(words[2]) * pf.scalefactor);
                    leading = pf.getVportTop() + (parseInt(words[3]) * pf.scalefactor);
                    break;

                case "m":
                    hamburger.left = pf.getVportLeft() + (parseInt(words[1]) * pf.scalefactor);
                    hamburger.top = pf.getVportTop() + (parseInt(words[2]) * pf.scalefactor);
                    hamburger.right = hamburger.left + (parseInt(words[3]) * pf.scalefactor);
                    hamburger.bottom = hamburger.top + (parseInt(words[4]) * pf.scalefactor);
                    break;

                case "t":
                    trashcan.left = pf.getVportLeft() + (parseInt(words[1]) * pf.scalefactor);
                    trashcan.top = pf.getVportTop() + (parseInt(words[2]) * pf.scalefactor);
                    trashcan.right = trashcan.left + (parseInt(words[3]) * pf.scalefactor);
                    trashcan.bottom = trashcan.top + (parseInt(words[4]) * pf.scalefactor);
                    break;
                case "p":
                    star[px] = pf.getVportLeft() + parseInt(words[1]) * pf.scalefactor;
                    star[px + 1] = pf.getVportTop() + parseInt(words[2]) * pf.scalefactor;
                    px += 4;
                    break;
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTopTen(Context context) {
        FileReader fr;
        BufferedReader br;
        String[] words;
        String[] woids;
        int tencount = 0;
        try {
            File file = new File(context.getFilesDir(), "MotasTopTen.txt");
            if (file.exists()) {
                // System.err.println("SIZE: " + file.length());
                try {
                    fr = new FileReader(file);
                    br = new BufferedReader(fr);
                    String line = br.readLine();
                    while (line != null) {
                        if (line.isEmpty()) {
                            continue;
                        }
                        words = line.split(":");
                        if (words[0].equals("t")) {
                            topten[tencount].color = parseInt(words[1]);
                            woids = words[2].split(",");
                            for (int i = 0; i < woids.length; i++) {
                                topten[tencount].name[i] = Short.parseShort(woids[i]);
                            }
                            topten[tencount].score = Long.parseUnsignedLong(words[3]);
                            tencount++;
                        } else if (words[0].equals("w")) {
                            cursor = 0;
                            woids = words[1].split(",");
                            for (int i = 0; i < woids.length; i++) {
                                short d = Short.parseShort(woids[i]);
                                if (d > 0) {
                                    bigwinner[i] = d;
                                    cursor++;
                                }
                            }
                        }
                        line = br.readLine();
                    }
                    br.close();
                    fr.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace(System.err);
                }
            } else {
                bigwinner[0] = 1;  // A
                bigwinner[1] = 4;  // D
                bigwinner[2] = 1;  // A
                bigwinner[3] = 13; // M
                bigwinner[4] = 19; // S
                bigwinner[5] = 13; // M
                bigwinner[6] = 1;  // A
                bigwinner[7] = 19; // S
                bigwinner[8] = 8;  // H
                cursor = 9;
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private void saveTopten(@NonNull Context context) {
        FileWriter fw;
        BufferedWriter bw;
        try {
            File file = new File(context.getFilesDir(), "MotasTopTen.txt");
            fw = new FileWriter(file.getAbsolutePath());
            if (file.getFreeSpace() > 1200) {
                bw = new BufferedWriter(fw);
                bw.write("w:" + bigwinner[0] + "," +
                        bigwinner[1] + "," +
                        bigwinner[2] + "," +
                        bigwinner[3] + "," +
                        bigwinner[4] + "," +
                        bigwinner[5] + "," +
                        bigwinner[6] + "," +
                        bigwinner[7] + "," +
                        bigwinner[8] + "," +
                        bigwinner[9] + "," +
                        bigwinner[10] + ":"); // w:1,2,3,4,5,6,7,8,9,10:
                bw.newLine();
                for (Topten t : topten) {
                    bw.write("t:" + t.color + ":" +
                            t.name[0] + "," +
                            t.name[1] + "," +
                            t.name[2] + "," +
                            t.name[3] + "," +
                            t.name[4] + "," +
                            t.name[5] + "," +
                            t.name[6] + "," +
                            t.name[7] + "," +
                            t.name[8] + "," +
                            t.name[9] + "," +
                            t.name[10] + ":" +
                            t.score + ":"); // t:0:1,2,3,4,5,6,7,8,9,10:2134567:
                    bw.newLine();
                }
                bw.flush();
                bw.close();
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public void displayTopten(Canvas canvas, Sprite sprite) {
        // header, center and leading
        int len;
        int top = header;
        int left;
        for (Topten value : topten) {
            if (value.score > 0) {
                len = sprite.alphieLength(value.name);
                left = center - len;
                sprite.alphieLine(canvas, value.name, left, top);
                if (value.selected)
                    sprite.fruitLoop(canvas, value.color + 4, center, top);
                else
                    sprite.fruitLoop(canvas, value.color, center, top);
                value.setFruit((center - (sprite.fruitloops[0].getWidth()) / 2), top, center + (sprite.fruitloops[0].getWidth() / 2), top + sprite.fruitloops[0].getHeight());
                sprite.numbieLine(canvas, value.score, center + sprite.fruitloops[0].getWidth(), top);
                top += (sprite.alphieHeight() + (leading / 2) + 1);
            }
        }
    }
    public void unselectDonuts() {
        for (Topten score : topten) {
            if (score.getSelected())
                score.setSelected(false);
        }
    }


    public int hitDonut(Context context, MotionEvent event, Racket racket) {
        int retval = 3;
        int x = (int) event.getX();
        int y = (int) event.getY();
        for (Topten value : topten) {
            if (value.fruit.contains(x, y)) {
                value.setSelected(!value.getSelected());
                racket.play(2);
            }
        }
        if (trashcan.contains(x, y)) {
            rollUp();
            saveTopten(context);
            racket.play(3);
        }
        if (hamburger.contains(x,y)) {
            unselectDonuts();
            racket.play(0);
            retval = 1; // menu
        }
        return retval;
    }

    public int hitButton(Context context, int color, @NonNull MotionEvent event, Racket racket) {
        int retval = 2;
        int x = (int) event.getX();
        int y = (int) event.getY();
        for (short i = 0; i < keyboard.length; i++) {
            if (keyboard[i].contains(x, y)) {
                racket.play(2);
                if (cursor < bigwinner.length && i < 26) {
                    bigwinner[cursor] = (short) (i + 1);
                    ++cursor;
                    break;
                }
                if (i == 26) {
                    if (cursor > 0) {
                        racket.play(2);
                        --cursor;
                        bigwinner[cursor] = 0;
                        break;
                    }
                }
                if (i == 27) { // DONE
                    orderedInsert(color, score);
                    saveTopten(context);
                    retval = 3; // top ten
                    racket.play(0);
                    break;
                }
            }
        }
        return retval; // keep going gstate 2
    }

    public boolean checkScore(int color) { // better than the worst
        return (color > topten[9].color) || ((color == topten[9].color) && (score > topten[9].score));
    }

    public void orderedInsert(int color, long highscore) {
        int t;
        for (t=0; t < topten.length; t++) {
            if(color == topten[t].color && highscore > topten[t].score) {
                rollDown(t, color, bigwinner, highscore);
                break;
            }
            if (color > topten[t].color) {
                rollDown(t, color, bigwinner, highscore);
                break;
            }
        }
    }

    public void rollUp() { // remove empties
        int t, next;
        short[] blank = {0,0,0,0,0,0,0,0,0,0,0};
        for (t=0; t < topten.length; t++) { // clear selected entries
            if (topten[t].selected) {
                System.arraycopy(blank, 0, topten[t].name, 0, blank.length);
                topten[t].color = 0;
                topten[t].score = 0;
                topten[t].selected = false;
                topten[t].fruit.left = 0;
                topten[t].fruit.top = 0;
                topten[t].fruit.right = 0;
                topten[t].fruit.bottom = 0;
            }
        }
        for (t=0; t < topten.length; t++) { // find first empty
            if (topten[t].score == 0) {
                for (next = t + 1; next < topten.length; next++) { // find next full
                    if (topten[next].score > 0) {
                        swapTopTen(next, t);
                        next = topten.length; // keep looking
                    }
                }
            }
        }
    }

    public void rollDown(int t, int color, short[] name, long highscore) {
        for (int i = topten.length - 2, j = topten.length - 1; j > t; i--, j--) {
            swapTopTen(i, j);
        }
        topten[t].color = color;
        System.arraycopy(name, 0, topten[t].name, 0, topten[t].name.length);
        topten[t].score = highscore;
    }

    public void swapTopTen(int src, int dst) {
        Topten temp = new Topten();
        // save dst to tmp
        temp.color = topten[dst].color;
        System.arraycopy(topten[dst].name, 0, temp.name, 0, topten[dst].name.length);
        temp.score = topten[dst].score;
        // copy src to dst
        topten[dst].color = topten[src].color;
        System.arraycopy(topten[src].name, 0, topten[dst].name, 0, topten[src].name.length);
        topten[dst].score = topten[src].score;
        // copy temp to src
        topten[src].color = temp.color;
        System.arraycopy(temp.name, 0, topten[src].name, 0, temp.name.length);
        topten[src].score = temp.score;
    }

    public Rect getEdit() {
        return edit;
    }

    public short[] getBigwinner() {
        return bigwinner;
    }

    public void setWintime(long stamp) {
        wintime = stamp;
    }

    public long getWintime() {
        return wintime;
    }

    public void setScore(long stamp) {
        score = stamp - wintime;
    }

    public void drawStar(Canvas canvas, int x, int y) {
        Paint p = new Paint();
        for (int i = 0; i < star.length; i+=4) {
            star[i] = x;
            star[i+1] = y + 4;
        }
        flip = !flip;
        p.setStrokeWidth(4);
        if (flip)
            p.setColor(Color.WHITE);
        else
            p.setColor(Color.CYAN);
        canvas.drawLines(star, 0, 228, p);
        if (flip)
            p.setColor(Color.CYAN);
        else
            p.setColor(Color.WHITE);
        canvas.drawLines(star, 228, 228, p);
    }

    private static class
    Topten {
        boolean selected;
        int color;
        final short[] name;
        long score;
        Rect fruit;

        public Topten() {
            selected = false;
            color = 0; // blue default
            name = new short[11];
            score = 0;
            fruit =  new Rect();
        }

        void setFruit(int left, int top, int right, int bottom) {
           fruit.left = left;
           fruit.top = top;
           fruit.right  = right;
           fruit.bottom = bottom;
        }

        boolean getSelected() {
            return (selected);
        }
        void setSelected(boolean selection) {
            selected = selection;
        }
    }
}