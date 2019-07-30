package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

public class LevelManager {

    private String level;
    int mapWidth;
    int mapHeight;
    Player player;
    int playerIndex;

    private boolean playing;
    float gravity;

    LevelData levelData;
    ArrayList<GameObject> gameObjects;
    //ArrayList<Background> backgrounds;
    ArrayList<Rect> currentButtons;

    Bitmap[] bitmapsArray;


    public LevelManager(Context context, int pixelsPerMetre, int screenWidth, InputController ic, String level, float px, float py) {

        this.level = level;
        switch (level) {
            case "LevelCave":
                levelData = new LevelCave();
                break;
           /*
            case "LevelCity":
                levelData = new LevelCity();
                break;

            case "LevelForest":
                levelData = new LevelForest();
                break;

            case "LevelMountain":
                levelData = new LevelMountain();
                break;*/
        }

        // To hold all our GameObjects
        gameObjects = new ArrayList<>();

        // To hold 1 of every Bitmap
        bitmapsArray = new Bitmap[25];

        // Load all the GameObjects and Bitmaps
        loadMapData(context, pixelsPerMetre, px, py);
        //loadBackgrounds(context, pixelsPerMetre, screenWidth);

        setWaypoints();


        //playing = true;
    }
   /* private void loadBackgrounds(Context context, int pixelsPerMetre, int screenWidth) {
        backgrounds = new ArrayList<Background>();
        //load the background data into the Background objects and
        // place them in our GameObject arraylist
        for (BackgroundData bgData : levelData.backgroundDataList) {
            backgrounds.add(new Background(context, pixelsPerMetre, screenWidth, bgData));
        }

    }*/
   
    public void setWaypoints() {
        for (GameObject guard: gameObjects) {
            if (guard.getType() == 'g') {
                int feetTileIndex = -1; // index of the tile beneath the guard
                float leftEnd = -1, rightEnd = -1;  // left and right ends of the calculated route
                for (GameObject tile: gameObjects) {
                    feetTileIndex++;
                    if (tile.getWorldLocation().y == guard.getWorldLocation().y + 2
                            &&  tile.getWorldLocation().x == guard.getWorldLocation().x) {
                        leftEnd = gameObjects.get(feetTileIndex - 5).getWorldLocation().x;
                        for (int i = 1; i <= 5; i++) {
                            GameObject left = gameObjects.get(feetTileIndex - i);
                            if (left.getWorldLocation().x != guard.getWorldLocation().x - i
                                    || left.getWorldLocation().y != guard.getWorldLocation().y + 2
                                    || !left.isTraversable()) {
                                leftEnd = gameObjects.get(feetTileIndex - (i - 1)).getWorldLocation().x;
                                break; } }

                        rightEnd = gameObjects.get(feetTileIndex + 5).getWorldLocation().x;
                        for (int i = 1; i <= 5; i++) {
                            GameObject right = gameObjects.get(feetTileIndex + i);
                            if (right.getWorldLocation().x != guard.getWorldLocation().x + i
                                    || right.getWorldLocation().y != guard.getWorldLocation().y + 2
                                    || !right.isTraversable()) {
                                rightEnd = gameObjects.get(feetTileIndex + (i - 1)).getWorldLocation().x;
                                break; } }

                        ((Guard) guard).setWaypoints(leftEnd, rightEnd);
                    }
                }
            }
        }
    }



    public void switchPlayingStatus() {
        playing = !playing;
        if (playing) {
            gravity = 6;
        } else {
            gravity = 0;
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public Bitmap getBitmap(char blockType) {

        int index;
        switch (blockType) {
            case '.':
                index = 0;
                break;

            case '1':
                index = 1;
                break;

            case 'p':
                index = 2;
                break;

            case 'c':
                index = 3;
                break;

            case 'u':
                index = 4;
                break;

            case 'e':
                index = 5;
                break;
            case 'd':
                index = 6;
                break;
            case 'g':
                index = 7;
                break;
            default:
                index = 0;
                break;
        }

        return bitmapsArray[index];
    }


    public int getBitmapIndex(char blockType) {

        int index;
        switch (blockType) {
            case '.':
                index = 0;
                break;

            case '1':
                index = 1;
                break;


            case 'p':
                index = 2;
                break;

            case 'c':
                index = 3;
                break;

            case 'u':
                index = 4;
                break;

            case 'e':
                index = 5;
                break;
            case 'd':
                index = 6;
                break;
            case 'g':
                index = 7;
                break;

            default:
                index = 0;
                break;
        }

        return index;
    }

    private void loadMapData(Context context, int pixelsPerMeter, float px, float py) {
        int currentIndex = -1;

        mapHeight = levelData.tiles.size();
        mapWidth = levelData.tiles.get(0).length();

        for (int i = 0; i < levelData.tiles.size(); i++) {
            for (int j = 0; j < levelData.tiles.get(i).length(); j++) {

                char c = levelData.tiles.get(i).charAt(j);
                if (c != '.') {
                    currentIndex++;
                    switch (c) {
                        case '1':
                            gameObjects.add(new Grass(j, i, c));
                            break;
                        case 'p':
                            player = new Player(context, px, py, pixelsPerMeter);
                            gameObjects.add(player);
                            playerIndex = currentIndex;
                            break;
                        case 'c':
                            gameObjects.add(new Coin(j, i, c));
                            break;
                        case 'u':
                            gameObjects.add(new MachineGunUpgrade(j, i, c));
                            break;
                        case 'e':
                            gameObjects.add(new ExtraLife(j, i, c));

                            break;
                        case 'd':
                            gameObjects.add(new Drone(j, i, c));
                            break;
                        case 'g':
                            gameObjects.add(new Guard(context, j, i, c, pixelsPerMeter));
                            break;

                    }
                }
                if (bitmapsArray[getBitmapIndex(c)] == null) {
                        GameObject go = gameObjects.get(currentIndex);
                        bitmapsArray[getBitmapIndex(c)] = go.prepareBitmap(context,
                                go.getBitmapName(), pixelsPerMeter); }
            }
        }
    }
}
