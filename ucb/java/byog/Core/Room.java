package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

public class Room {
    Position p;
    Position hall;
    private int width;
    private int height;
    private int left, right, top, bottom;
    Random randomnumbers;

    /*  Null Constructor */
    public Room() {
        width = 0;
        height = 0;
        p = null;
    }

    /* Constructor */
    public Room(int w, int h, Position p, TETile[][] world, Random rn) {
        width = w;
        height = h;
        this.p = p;
        left = p.x;
        right = p.x + width;
        bottom = p.y;
        top = p.y + height;
        randomnumbers = rn;
        for (int i = p.x; i < p.x + width; i++) {
            for (int j = p.y; j < p.y + height; j++) {
                world[i][j] = Tileset.WALL;
            }
        }
        for (int i = p.x + 1; i < p.x + width - 1; i++) {
            for (int j = p.y + 1; j < p.y + height - 1; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }
        int newx = RandomUtils.uniform(randomnumbers, this.left + 1, this.right - 1);
        int newy = RandomUtils.uniform(randomnumbers, this.bottom + 1, this.top - 1);
        hall = new Position(newx, newy);
    }
}
