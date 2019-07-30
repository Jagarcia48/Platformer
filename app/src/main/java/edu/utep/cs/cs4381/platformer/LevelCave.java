package edu.utep.cs.cs4381.platformer;

import java.util.ArrayList;

public class LevelCave extends LevelData {
    LevelCave() {
        tiles = new ArrayList<String>();
        this.tiles.add("p.............................................");
        this.tiles.add("..............................................");
        this.tiles.add(".....................................d........");
        this.tiles.add("..............................................");
        this.tiles.add("..........d..........c........................");
        this.tiles.add("....................11.......u................");
        this.tiles.add("..................c.........u1................");
        this.tiles.add(".................11........u11................");
        this.tiles.add("......11111....c..........u111.............d..");
        this.tiles.add("..............11.........u1111......g.........");
        this.tiles.add("......................e..11111..e.....e.......");
        this.tiles.add("...111111111111111111111111111111111111111111.");
    }


}
