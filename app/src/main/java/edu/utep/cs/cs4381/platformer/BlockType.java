package edu.utep.cs.cs4381.platformer;


public enum BlockType {
    PLAYER('p'),
    TURF('1');

    public final char symbol;

    BlockType(char symbol) {
        this.symbol = symbol;
    }

    public static int index(char symbol) {
        for (BlockType v: BlockType.values()) {
            if (v.symbol == symbol) {
                return v.ordinal();
            }
        }
        return -1;
    }
}