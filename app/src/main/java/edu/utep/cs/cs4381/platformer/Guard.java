package edu.utep.cs.cs4381.platformer;

import android.content.Context;

public class Guard extends GameObject {
        // Guards just move on x axis between 2 waypoints
        private float waypointLeft;// always on left
        private float waypointRight;// always on right
        private int currentWaypoint;
        final float MAX_X_VELOCITY = 3;

    Guard(Context context, float worldStartX, float worldStartY, char type, int pixelsPerMetre) {
        final int ANIMATION_FPS = 8;
        final int ANIMATION_FRAME_COUNT = 5;
        final String BITMAP_NAME = "guard";
        final float HEIGHT = 2f;
        final float WIDTH = 1;
        setHeight(HEIGHT); // 2 metre tall
        setWidth(WIDTH); // 1 metres wide
        setType(type);
        setBitmapName("guard");
        // Now for the player's other attributes
        // Our game engine will use these
        setMoves(true);
        setActive(true);
        setVisible(true);
        // Set this object up to be animated
        setAnimFps(ANIMATION_FPS);
        setAnimFrameCount(ANIMATION_FRAME_COUNT);
        setBitmapName(BITMAP_NAME);
        setAnimated(context, pixelsPerMetre, true);
        // Where does the tile start
        // X and y locations from constructor parameters
        setWorldLocation(worldStartX, worldStartY, 0);
        setxVelocity(-MAX_X_VELOCITY);
        currentWaypoint = 1;
    }

    public void update(long fps, float gravity) {
        if(currentWaypoint == 1) {// Heading left
            if (getWorldLocation().x <= waypointLeft) {
                // Arrived at waypoint 1
                currentWaypoint = 2;
                setxVelocity(MAX_X_VELOCITY);
                setFacing(RIGHT);
            }
        }
        if(currentWaypoint == 2){
            if (getWorldLocation().x >= waypointRight) {
                // Arrived at waypoint 2
                currentWaypoint = 1;
                setxVelocity(-MAX_X_VELOCITY);
                setFacing(LEFT);
            }
        }
        move(fps);
        // update the guards hitbox
        setRectHitbox();
    }
    public void setWaypoints(float left, float right){
        waypointLeft = left;
        waypointRight = right;
    }

}
