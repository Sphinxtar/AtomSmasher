package com.atomsmasher;

import android.graphics.Canvas;
import android.view.SurfaceHolder;


public class AtomThread extends Thread {

    public final SurfaceHolder surfaceHolder;
    public static Canvas canvas;
    private final AtomView atomView;
    private boolean running;
 //   private double averageFPS;

    public AtomThread(SurfaceHolder surfaceHolder, AtomView myView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.atomView = myView;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        // long totalTime = 0;
        int frameCount =0;
        int targetFPS = 18;
        long targetTime = 1000 / targetFPS;

        while(running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.atomView.update(startTime);
                    this.atomView.draw(canvas);
                }
            } catch (Exception ignored) {
            }
            finally{
                if(canvas!=null)
                {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch(Exception e){e.printStackTrace(System.err);}
                }
            }

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime-timeMillis;

            try {
                    sleep(waitTime);
            }catch(Exception ignored){}

            // totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == targetFPS)
            {
                // averageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount = 0;
                // totalTime = 0;
                // System.out.println(averageFPS);
            }
        }
    }

    public void setRunning(boolean isRunning) {
        running = isRunning;
    }
}
