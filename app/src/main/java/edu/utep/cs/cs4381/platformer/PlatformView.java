package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

public class PlatformView extends SurfaceView implements Runnable {

    private boolean debugging = false;

    private volatile boolean running;
    private Thread gameThread = null;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder holder;

    private Context context;

    private LevelManager lm;
    private Viewport vp;

    InputController ic;
    SoundManager soundManager;
    private PlayerState ps;

    private long startFrameTime;
    private long timeThisFrame;
    private long fps;



    public PlatformView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.context = context;
        holder = getHolder();
        paint = new Paint();
        vp = new Viewport(screenWidth, screenHeight);
        soundManager = SoundManager.instance(context);
        ps = new PlayerState();
        loadLevel("LevelCave",15,2);
    }

    private void loadLevel(String level, float px, float py) {
        lm = new LevelManager(context, vp.getPixelsPerMetreX(), vp.getScreenWidth(), ic, level, px, py);
        ic = new InputController(vp.getScreenWidth(), vp.getScreenHeight());

        PointF location = new PointF(px, py);
        vp.setWorldCenter(lm.gameObjects.get(lm.playerIndex).getWorldLocation().x, lm.gameObjects.get(lm.playerIndex).getWorldLocation().y);
    }


    @Override
    public void run() {
        while (running) {
            startFrameTime = System.currentTimeMillis();
            update();
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }
    private void update() {
        for (GameObject go : lm.gameObjects) {
            if (go.isActive()) {
                if (!vp.clipObject(go.getWorldLocation().x, go.getWorldLocation().y, go.getWidth(), go.getHeight())) {
                    go.setVisible(true);
                    // check collisions with player
                    int hit = lm.player.checkCollisions(go.getRectHitbox());
                    if (hit > 0) {
                        switch (go.getType()) {
                            case 'c':
                                soundManager.play(SoundManager.Sound.COIN_PICKUP);
                                go.setActive(false);
                                go.setVisible(false);
                                ps.gotCredit();
                                if (hit != 2) {// Any hit except feet
                                    lm.player.restorePreviousVelocity();
                                }
                                break;
                            case 'u':
                                soundManager.play(SoundManager.Sound.GUN_UPGRADE);
                                go.setActive(false);
                                go.setVisible(false);
                                lm.player.bfg.upgradeRateOfFire();
                                ps.increaseFireRate();
                                if (hit != 2) {// Any hit except feet
                                    lm.player.restorePreviousVelocity();
                                }
                                break;
                            case 'e':
//extralife
                                go.setActive(false);
                                go.setVisible(false);
                                soundManager.play(SoundManager.Sound.EXTRA_LIFE);
                                ps.addLife();
                                if (hit != 2) {
                                    lm.player.restorePreviousVelocity();
                                }
                                break;
                            case 'd':
                                PointF location;
                                //hit by drone
                                soundManager.play(SoundManager.Sound.EXPLODE);
                                ps.loseLife();
                                location = new PointF(ps.loadLocation().x,
                                        ps.loadLocation().y);
                                lm.player.setWorldLocationX(location.x);
                                lm.player.setWorldLocationY(location.y);
                                lm.player.setxVelocity(0);
                                break;
                            case 'g':
                                // Hit by guard
                                soundManager.play(SoundManager.Sound.EXPLODE);
                                ps.loseLife();
                                location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                                lm.player.setWorldLocationX(location.x);
                                lm.player.setWorldLocationY(location.y);
                                lm.player.setxVelocity(0);
                                break;
                            default:
                                if (hit == 1) { // left or right
                                    lm.player.setxVelocity(0);
                                    lm.player.setPressingRight(false);
                                }
                                if (hit == 2) { // feet
                                    lm.player.isFalling = false;
                                }
                                break;
                        }
                    }
                    if (lm.isPlaying()) {
                        go.update(fps, lm.gravity);
                        if (go.getType() == 'd') {
                            Drone d = (Drone) go;
                            d.setWaypoint(lm.player.getWorldLocation());
                        }
                    }

                } else {
                    go.setVisible(false);
                }
            }
        }
        if (lm.isPlaying()) {
            vp.setWorldCenter(lm.player.getWorldLocation().x, lm.player.getWorldLocation().y);
            if (lm.player.getWorldLocation().x < 0 ||
                    lm.player.getWorldLocation().x > lm.mapWidth ||
                    lm.player.getWorldLocation().y > lm.mapHeight) {


                //soundManager.playSound("player_burn");
                //ps.loseLife();
                PointF location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                lm.player.setWorldLocationX(location.x);
                lm.player.setWorldLocationY(location.y);
                lm.player.setxVelocity(0);
            }

            // Check if game is over


           if (ps.getLives() == 0) {
                ps = new PlayerState();
                loadLevel("LevelCave", 1, 16);
            }

        }
    }

        private void draw(){
            if (holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
                paint.setColor(Color.argb(255, 0, 0, 255));
                canvas.drawColor(Color.argb(255, 0, 0, 255));
                Rect toScreen2d = new Rect();
                for (int layer = -1; layer <= 1; layer++) {
                    for (GameObject go : lm.gameObjects) {
                        if (go.isVisible() && go.getWorldLocation().z == layer) {
                            toScreen2d.set(vp.worldToScreen(go.getWorldLocation().x,  go.getWorldLocation().y, go.getWidth(), go.getHeight()));
                            if (go.isAnimated()) {
                                // Get the next frame of the bitmap
                                // Rotate if necessary
                                if (go.getFacing() == 1) {
                                    // Rotate
                                    Matrix flipper = new Matrix();
                                    flipper.preScale(-1, 1);
                                    Rect r = go.getRectToDraw(System.currentTimeMillis());
                                    Bitmap b = Bitmap.createBitmap(
                                            lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                            r.left,
                                            r.top,
                                            r.width(),
                                            r.height(),
                                            flipper,
                                            true);
                                    canvas.drawBitmap(b, toScreen2d.left, toScreen2d.top, paint);
                                } else {
                                    // draw it the regular way round
                                    canvas.drawBitmap(
                                            lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                            go.getRectToDraw(System.currentTimeMillis()),
                                            toScreen2d, paint);
                                }
                            } else { // Just draw the whole bitmap
                                canvas.drawBitmap(
                                        lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                        toScreen2d.left,
                                        toScreen2d.top, paint);
                            }
                           /* canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                    toScreen2d.left, toScreen2d.top, paint);*/
                        }
                    }
                }
                //draw the bullets
                paint.setColor(Color.argb(255, 255, 255, 255));
                for (int i = 0; i < lm.player.bfg.getNumBullets(); i++) {
                // Pass in the x and y coords as usual
                // then .25 and .05 for the bullet width and height
                    toScreen2d.set(vp.worldToScreen(lm.player.bfg.getBulletX(i), lm.player.bfg.getBulletY(i), .25f, .05f));
                    canvas.drawRect(toScreen2d, paint);
                }

                if(debugging){
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(40);
                    //canvas.drawText("Hello");
                    canvas.drawText("playerY:" + lm.gameObjects.get(lm.playerIndex).getWorldLocation().y, 10, 140, paint);
                    canvas.drawText("Gravity:" + lm.gravity, 10, 160, paint);
                    canvas.drawText("X velocity:" + lm.gameObjects.get(lm.playerIndex).getxVelocity(), 10, 180, paint);
                    canvas.drawText("Y velocity:" + lm.gameObjects.get(lm.playerIndex).getyVelocity(), 10, 200, paint);
                }

                // draw buttons
                paint.setColor(Color.argb(80, 255, 255, 255));
                List<Rect> buttonsToDraw = ic.getButtons();
                for (Rect r: buttonsToDraw) {
                    RectF rf = new RectF(r.left, r.top, r.right, r.bottom);
                    canvas.drawRoundRect(rf, 15f, 15f, paint);
                }

                // draw paused text
                if (!lm.isPlaying()) {
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setColor(Color.argb(255, 255, 255, 255));
                    paint.setTextSize(120);
                    canvas.drawText("Paused", vp.getScreenWidth() / 2,
                            vp.getScreenHeight() / 2, paint);
                }

                holder.unlockCanvasAndPost(canvas);
            }
        }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if(lm != null){
            ic.handleInput(motionEvent, lm, soundManager, vp);
        }
        return true;
        /*

        switch (motionEvent.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                lm.switchPlayingStatus();
                break;
        }
        return true;*/
    }




    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("error", "failed to pause thread");
        }
    }

    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }



}