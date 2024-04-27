package com.atomsmasher;
/*
  load a bitmap and cut it into an array of smaller bitmaps according to a text file in assets
  draw them where specified by index and center coordinates
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static android.graphics.Bitmap.createScaledBitmap;
import static android.graphics.BitmapFactory.decodeResource;
import androidx.annotation.NonNull;

public class Sprite {
    private final Bitmap[] sprites = new Bitmap[45]; // array of sprites cut and scaled
    public final Alphie[] alphie = new Alphie[26]; // the alphabet
    public final Bitmap[] numbie = new Bitmap[11];
    public final Bitmap[] fruitloops = new Bitmap[8];

    public Sprite( Context context, PlayingField pf ) {
        loadSprites(context, pf.scalefactor);
    }

    private void loadSprites(Context context, int scaling) {
        BufferedReader reader;
        int x;
        int y;
        int wide;
        int high;
        InputStream in;
        Bitmap im = null; // intermediate bitmap for scaling
        Bitmap SpriteSheet = decodeResource(context.getResources(), R.drawable.sprites, null);
        String line;
        String[] words;
        try {
            in = context.getAssets().open("sprites");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        reader = new BufferedReader(new InputStreamReader(in));
        int spritenum = 0;
        int alfnum = 0;
        int numnum = 0;
        int fruitnum = 0;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (line.isEmpty())
                continue;
            words = line.split(",");
            if (words[0].equals("q")) {
                x = (Short.parseShort(words[1]));
                y = (Short.parseShort(words[2]));
                wide = (Short.parseShort(words[3]));
                high = (Short.parseShort(words[4]));
                im = Bitmap.createBitmap(SpriteSheet, x, y, wide, high);
                sprites[spritenum++] = createScaledBitmap(im, wide * scaling, high * scaling, true);
            }
            if (words[0].equals("a")) {
                x = (Short.parseShort(words[1]));
                y = (Short.parseShort(words[2]));
                wide = (Short.parseShort(words[3]));
                high = (Short.parseShort(words[4]));
                im = Bitmap.createBitmap(SpriteSheet, x, y, wide, high);
                alphie[alfnum] = new Alphie();
                alphie[alfnum].letter = createScaledBitmap(im, wide * scaling, high * scaling, true);
                alphie[alfnum++].kerning = scaling * Integer.parseInt(words[5]);
            }
            if (words[0].equals("n")) {
                x = (Short.parseShort(words[1]));
                y = (Short.parseShort(words[2]));
                wide = (Short.parseShort(words[3]));
                high = (Short.parseShort(words[4]));
                im = Bitmap.createBitmap(SpriteSheet, x, y, wide, high);
                numbie[numnum++] = createScaledBitmap(im, wide * scaling, high * scaling, true);
            }
            if (words[0].equals("f")) {
                x = (Short.parseShort(words[1]));
                y = (Short.parseShort(words[2]));
                wide = (Short.parseShort(words[3]));
                high = (Short.parseShort(words[4]));
                im = Bitmap.createBitmap(SpriteSheet, x, y, wide, high);
                fruitloops[fruitnum++] = createScaledBitmap(im, wide * scaling, high * scaling, true);
            }
        }
        try {
            in.close();
            reader.close();
            SpriteSheet.recycle();
            if (im != null) {
                im.recycle();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void numbieLine(@NonNull Canvas canvas, long digits, int left, int top) {
        String numero = Long.toString(digits);
        for(int i=0; i < numero.length(); i++) {
            if ((i > 0)&&((numero.length() - i) % 3 == 0)) {
                canvas.drawBitmap(numbie[10], left, top, null); // comma comma comma chameleon you come and go
                left += numbie[10].getWidth();
            }
            canvas.drawBitmap(numbie[numero.charAt(i) - 48], left, top, null);
            left += numbie[numero.charAt(i) - 48].getWidth();
        }
    }

    public void fruitLoop(@NonNull Canvas canvas, int fruit, int center, int top) {
        int left = center - (fruitloops[0].getWidth() / 2);
        canvas.drawBitmap(fruitloops[fruit], left, top, null);
    }

    public int alphieLength(short[] name) {
        int total = alphie[13].letter.getWidth(); // M space
        for (int i = 0; i < name.length && name[i] > 0; i++) {
            total += (alphie[name[i] - 1].letter.getWidth() + alphie[name[i] - 1].kerning);
        }
        return total;
    }

    public int alphieHeight() {
        return(alphie[0].letter.getHeight()); // A height
    }

    public void alphieLine(@NonNull Canvas canvas, short[] name, int left, int top) {
        for (int i = 0; i < name.length && name[i] > 0; i++) {
            canvas.drawBitmap(alphie[name[i] - 1].letter, left, top, null);
            left = left + alphie[name[i] - 1].letter.getWidth() + alphie[name[i] - 1].kerning;
        }
    }

    public void drawAlphie(@NonNull Canvas canvas, short[] winner, Rect box) {
        int top;
        int left = box.left + (alphie[0].letter.getWidth() / 2); // A width box margin
        int line = box.top + ((box.bottom - box.top) / 2); // center line of box
        for (int i = 0; i < winner.length && winner[i] > 0; i++) {
            top = line - (alphie[winner[i] - 1].letter.getHeight() / 2);
            canvas.drawBitmap(alphie[winner[i] - 1].letter, left, top, null);
            left = left + alphie[winner[i] - 1].letter.getWidth() + alphie[winner[i] - 1].kerning;
        }
    }

    public void drawCenterSprite(@NonNull Canvas canvas, int sprite, int column, int row ) {
        int top = (row - (sprites[sprite].getHeight() / 2));
        int left = (column - (sprites[sprite].getWidth() / 2));
        canvas.drawBitmap(sprites[sprite], left, top, null);
    }
    public static class Alphie {
        Bitmap letter;
        int kerning;
    }
}