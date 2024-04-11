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
        if (gstate == 0) {  // playing the game
            int button = dpad.hitButton(event);
            if (button > 0 && button < 10) { // dpad hit
                player.setDirection(button);
                if (button == 5)
                    player.setSpeed(player.getSpeed() - 1); // deceleration
                else
                    player.setSpeed(6 * pf.getScaleFactor());
            }
            else if (button == 10) {  // BLUE
                player.setSprite(0);
                player.resetSpot();
                npc.resetBots(0);
            } else if (button == 11) { // GREEN
                player.resetSpot();
                player.setSprite(1);
                npc.resetBots(1);
            } else if (button == 13) { // YELLOW
                player.resetSpot();
                player.setSprite(2);
                npc.resetBots(2);
            } else if (button == 12) { // RED
                player.setSprite(3);
                player.resetSpot();
                npc.resetBots(3);
            } else if (button == 14) {
                newstate = 1; // back to menu 1
                npc.resetBots(player.getSprite());
                player.setSprite(22);
            }
        } else if (gstate == 1) { // menu 1
            newstate = menu.hitButton(event, racket);
            if (newstate == 0) // the game
                player.resetSpot();
            performClick();
        } else if (gstate == 2) { // top name entry
            newstate = winner.hitButton(getContext(), player.getSprite(), event, racket);
            performClick();
            if(newstate == 3) {
                npc.resetBots(player.getSprite());
                player.setSprite(22);
                player.resetSpot();
            }
        } else if (gstate == 3) { // high scores
            newstate = winner.hitDonut(getContext(), event, racket);
            performClick();
        } else if (gstate > 3) { // slides
            newstate = slides.hitButton(gstate - 4);
            performClick();
            racket.play(0);
        }
        if (gstate < 0 ) {
            racket.play(5);
            thread.setRunning(false);
            System.exit(0);
        } else {
            gstate = newstate;
        }
        return super.onTouchEvent(event);
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
            }
            if (gstate == 0) { // PLAY THE GAME
                p.setColor(Color.LTGRAY);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(6);
                canvas.drawRect(pf.getVportLeft(), pf.getVportTop(), pf.getVportRight(), pf.getVportBottom(), p);
                dpad.draw(canvas);
                canvas.save();
                canvas.clipRect(pf.getVportLeft(), pf.getVportTop(), pf.getVportRight(), pf.getVportBottom());
                if(player.getSprite() < 4) {
                    p.setColor(Color.WHITE);
                    p.setStrokeWidth(4);
                    npc.connectOrange(canvas, p);
                    for (Npc.Bot b : npc.bots) {
                        pix.drawCenterSprite(canvas, b.sprite, b.spot.x, b.spot.y);
                    }
                }
                if (winner.getWintime() > 0) {
                    winner.drawStar(canvas, player.spot.x, player.spot.y);
                    pix.drawCenterSprite(canvas, 23, player.spot.x, player.spot.y);
                }
                pix.drawCenterSprite(canvas, player.sprite, player.spot.x, player.spot.y);
                canvas.restore();
                player.adjustPlayer(pf);
                if(player.getSprite() < 4)
                    npc.collisions(pf, player.getHotz(), racket);
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
        long stamp;
        if (npc != null) {
            purple = 0;
            for (int i = 0; i < npc.bots.length; i++) {
                if (npc.bots[i].getState() == 1)
                    purple++;
            }
            stamp = winner.getWintime();
            if ((stamp == 0) && (purple == npc.bots.length)) { // all purple start clock
                winner.setWintime(startTime);
                racket.play(0);
            }
            if ((stamp > 0) && (purple < npc.bots.length)) { // clock running and somebody turned orange
                winner.setScore(startTime);
                winner.setWintime(0); // reset clock
                if (winner.checkScore(player.getSprite())) { // top ten qualified
                    npc.resetBots(player.getSprite());
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
