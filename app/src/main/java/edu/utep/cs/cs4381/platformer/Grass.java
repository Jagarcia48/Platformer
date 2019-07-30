package edu.utep.cs.cs4381.platformer;

public class Grass extends GameObject {

    public Grass(float worldStartX, float worldStartY, char type) {
        final float HEIGHT = 1;
        final float WIDTH = 1;
        setHeight(HEIGHT); // 1 meter tall
        setWidth(WIDTH); // 1 meter wide
        setType(type);
        setBitmapName("turf");
        setWorldLocation(worldStartX, worldStartY, 0);
        setRectHitbox();
        setTraversable();
    }

    @Override
    public void update(long fps, float gravity) {}
}