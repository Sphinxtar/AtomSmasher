package com.atomsmasher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

public class AtomView extends SurfaceView implements SurfaceHolder.Callback {
    public final AtomThread thread;
    public final Racket racket;
    public final Moodmusic moodmusic;
    public final PlayingField pf;
    public Sprite pix;
    public Dpad dpad;
    public Menus menu;
    public Slides slides;
    public Player player;
    public Npc npc;
    public Winner winner;
    public int gstate = 4; // splash
    boolean ffe = false; // fire for effect
    boolean youwon = false; // get name or keep going

    public int counter = 0; // frames to ffe
    private Context ctext;

    public AtomView(Context context) {
        super(context);
        setCtext(context);
        getHolder().addCallback(this);
        pf = new PlayingField();
        moodmusic = new Moodmusic(context);
        racket = new Racket(getCtext());
        thread = new AtomThread(getHolder(), this);
        moodmusic.pausePlaying();
        setFocusable(true);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int newstate = 0;
        int eventAction = event.getAction();
        boolean retval = super.onTouchEvent(event);
        if (gstate == 0) {  // playing the game
            int button = dpad.hitButton(event);
            if (button > 0 && button < 10) { // dpad hit
                switch (eventAction) {
                    case MotionEvent.ACTION_UP:
                        button = 5;
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                           retval = true;
                        break;
                    default:
                        break;
                }
                player.setDirection(button);
                if (button == 5)
                    player.setSpeed(0); // deceleration
                else
                    player.setSpeed(8 * pf.getScaleFactor());
            } else if (button == 10) {  // BLUE
                player.setLevel(0);
                player.setSprite(0);
                player.resetSpot();
                npc.resetBots(0, racket);
            } else if (button == 11) { // GREEN
                player.setLevel(1);
                player.resetSpot();
                player.setSprite(1);
                npc.resetBots(1, racket);
            } else if (button == 13) { // YELLOW
                player.setLevel(2);
                player.resetSpot();
                player.setSprite(2);
                npc.resetBots(2, racket);
            } else if (button == 12) { // RED
                player.setLevel(3);
                player.setSprite(3);
                player.resetSpot();
                npc.resetBots(3, racket);
            } else if (button == 14) {
                player.setLevel(-1);
                newstate = 1; // back to menu 1
                npc.resetBots(player.getLevel(), racket);
                player.setSprite(22);
            }
        } else if (gstate == 1) { // menu 1
            newstate = menu.hitButton(event, racket);
            if (newstate == 0) // the game
                player.resetSpot();
            performClick();
        } else if (gstate == 2) { // top name entry
            newstate = winner.hitButton(getContext(), player.getLevel(), event, racket);
            performClick();
            if (newstate == 3) {
                youwon = true;
                player.setLevel(-1);
                npc.resetBots(player.getLevel(), racket);
                player.setSprite(22);
                player.resetSpot();
            }
        } else if (gstate == 3) { // high scores
            newstate = winner.hitDonut(getContext(), event, racket);
            performClick();
        } else if (gstate > 3) { // slides
            newstate = slides.hitButton(gstate - 4);
            if (newstate != gstate)
                racket.play(slides.slides[gstate - 4].getApresound());
            performClick();
        }
        if (gstate < 0 ) {
            racket.play(0);
            thread.setRunning(true);
            System.exit(0);
        } else {
            gstate = newstate;
        }
        return retval;
    }

    @Override
    public void draw(Canvas canvas) {
        Paint p = new Paint();
        if (canvas != null) {
            super.draw(canvas);
            if (pf.changed(canvas)) {
                pf.setScaleFactor(canvas);
                dpad = new Dpad(pf.getVport());
                menu = new Menus( pf.getSidemargin(), pf.getTopmargin() + 10,pf.getVportRight() - pf.getVportLeft(), pf.getVportBottom() - pf.getVportTop());
                slides = new Slides(getCtext(), pf.getVportRight() - pf.getVportLeft(), pf.getVportBottom() - pf.getVportTop());
                pix = new Sprite(getCtext(), pf);
                winner = new Winner(getCtext(), pf);
                player = new Player(pf);
                npc = new Npc(getCtext(), pf);
                racket.play(3);
            }
            if (gstate == 0) { // PLAY THE GAME
                p.setColor(Color.LTGRAY);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(2);
                canvas.drawRect(pf.getVportLeft(), pf.getVportTop(), pf.getVportRight(), pf.getVportBottom(), p);
                dpad.draw(canvas);
                canvas.save();
                canvas.clipRect(pf.getVportLeft(), pf.getVportTop(), pf.getVportRight(), pf.getVportBottom());
                if(player.getLevel() >= 0) {
                    p.setColor(Color.WHITE);
                    p.setStrokeWidth(4);
                    npc.connectOrange(canvas, p);
                    for (Npc.Bot b : npc.bots) {
                        pix.drawCenterSprite(canvas, b.sprite, b.spot.x, b.spot.y);
                        if (b.halo > 0) {
                            pix.drawCenterSprite(canvas, 45, b.spot.x, b.spot.y);
                            b.halo--;
                        }
                    }
                }
                if (ffe) {
                    winner.drawStar(canvas, player.spot.x, player.spot.y, player.getLevel());
                    pix.drawCenterSprite(canvas, 23, player.spot.x, player.spot.y); // halo
                }
                if (player.isSpin()) {
                    pix.drawCenterSprite(canvas, 44, player.spot.x, player.spot.y); // halo too
                }
                pix.drawCenterSprite(canvas, player.getSprite(), player.spot.x, player.spot.y);
                canvas.restore();
                player.adjustPlayer(pf);
                if (player.getLevel() >= 0)
                    npc.collisions(pf, player.getHotz(), racket, player);
            } else if (gstate == 1) { // main menu
                slides.drawSlide(canvas, 4, pf.getVportLeft(), pf.getVportTop());
                menu.draw(canvas);
            } else if ( gstate == 2 ) { // winner entry
                slides.drawSlide(canvas, 5, pf.getVportLeft(), pf.getVportTop());
                pix.drawAlphie(canvas, winner.getBigwinner(), winner.getEdit());
            } else if ( gstate == 3 ) { // high scores
                slides.drawSlide(canvas, 2, pf.getVportLeft(), pf.getVportTop());
                winner.displayTopten(canvas, pix);
            } else if (gstate > 3) {
                slides.drawSlide(canvas, gstate - 4, pf.getVportLeft(), pf.getVportTop());
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();

            } catch(InterruptedException e){
                e.printStackTrace(System.out);
            }
            retry = false;
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    // Check for winner
    public void update(long startTime) {
        int purple;
        if (npc != null) {
            purple = 0;
            for (int i = 0; i < npc.bots.length; i++) {
                if (npc.bots[i].getState() == 1)
                    purple++;
            }
            if ((winner.getWintime() == 0) && (purple == npc.bots.length)) { // all purple start clock
                winner.setWintime(startTime);
                racket.play(3);
                ffe = true;
                counter = 54;
            }
            if ((winner.getWintime() > 0) && (purple < npc.bots.length)) { // clock running and somebody turned orange
                winner.setScore(startTime);
                youwon = winner.checkScore(player.getLevel());
                if (youwon) { // top ten qualified
                    ffe = true;
                    winner.setWintime(0); // reset clock
                }
                counter = 9;
            }
            if (counter > 0)
                counter--;
            if (ffe && (counter == 0)) {
                ffe = false;
                if (youwon) {
                    npc.resetBots(player.getLevel(), racket);
                    youwon = false;
                    player.setSpin(false);
                    gstate = 2; // get a name
                }
            }
        }
    }

    public Context getCtext() {
        return ctext;
    }

    public void setCtext(Context ctext) {
        this.ctext = ctext;
    }
}
