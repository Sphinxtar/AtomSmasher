package com.atomsmasher;

import static android.graphics.Bitmap.createScaledBitmap;
import static android.graphics.BitmapFactory.decodeResource;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
//import android.util.Log;

public class Slides {

    public final slide[] slides = new slide[6];

    public Slides(Context context, int Wide, int High) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;

        Bitmap im = decodeResource(context.getResources(), R.drawable.splash, options);
        im.setDensity(Bitmap.DENSITY_NONE);
        slides[0] = new slide();
        slides[0].bm = createScaledBitmap(im, Wide, High,true);
        slides[0].bm.setDensity(Bitmap.DENSITY_NONE);
        slides[0].setGcode(1); // take you to first menu
        slides[0].setForesound(8);
        slides[0].setApresound(0);

        im = decodeResource(context.getResources(), R.drawable.help, options);
        slides[1] = new slide();
        slides[1].bm = createScaledBitmap(im, Wide, High, true);
        slides[1].bm.setDensity(Bitmap.DENSITY_NONE);
        slides[1].setGcode(1); // main menu
        slides[1].setForesound(4);
        slides[1].setApresound(0);

        im = decodeResource(context.getResources(), R.drawable.topten, options);
        slides[2] = new slide();
        slides[2].bm = createScaledBitmap(im, Wide, High, true);
        slides[2].bm.setDensity(Bitmap.DENSITY_NONE);
        slides[2].setGcode(1); // main menu 1
        slides[2].setForesound(4);
        slides[2].setApresound(0);

        im = decodeResource(context.getResources(), R.drawable.gbye, options);
        slides[3] = new slide();
        slides[3].bm = createScaledBitmap(im, Wide, High, true);
        slides[3].bm.setDensity(Bitmap.DENSITY_NONE);
        slides[3].setGcode(-1);
        slides[3].setForesound(4);
        slides[3].setApresound(5);

        im = decodeResource(context.getResources(), R.drawable.menu, options);
        slides[4] = new slide();
        slides[4].bm = createScaledBitmap(im, Wide, High, true);
        slides[4].bm.setDensity(Bitmap.DENSITY_NONE);
        slides[4].setGcode(1);
        slides[4].setForesound(4);
        slides[4].setApresound(0);

        im = decodeResource(context.getResources(), R.drawable.winner, options);
        slides[5] = new slide();
        slides[5].bm = createScaledBitmap(im, Wide, High, true);
        slides[5].bm.setDensity(Bitmap.DENSITY_NONE);
        slides[5].setGcode(1);
        slides[5].setForesound(4);
        slides[5].setApresound(0);
    }

    public int hitButton(int slidenum) {
        return slides[slidenum].getGcode();
    }

    public void drawSlide(Canvas canvas, int slidenum, int left, int top) {
        canvas.drawBitmap(slides[slidenum].bm, left, top, null);
    }

    public class slide {
        Bitmap bm;
        int gcode;
        int foresound;
        int apresound;

        public int getForesound() {
            return foresound;
        }
        public void setForesound(int foresound) {
            this.foresound = foresound;
        }
        public int getApresound() {
            return apresound;
        }
        public void setApresound(int apresound) {
            this.apresound = apresound;
        }
        public void setGcode(int gcode) {
            this.gcode = gcode;
        }
        public int getGcode() {
            return gcode;
        }

    }
}
