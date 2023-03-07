package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Hallway {
    private int width;
    private int height;
    Position p;

    /* Constructor */
    public Hallway(int w, int h, Position p, TETile[][] world) {
        width = w;
        height = h;
        this.p = p;
        for (int i = p.x; i < width + p.x; i++) {
            for (int j = p.y; j < height + p.y; j++) {
                world[i][j] = Tileset.FLOOR;
                setWallhelper(i, j, world);
            }
        }
    }

    /* Setting the walls for hallways */
    private void setWallhelper(int i, int j, TETile[][] world) {
        if (world[i + 1][j] == Tileset.NOTHING) {
            world[i + 1][j] = Tileset.WALL;
        }
        if (world[i - 1][j] == Tileset.NOTHING) {
            world[i - 1][j] = Tileset.WALL;
        }
        if (world[i][j + 1] == Tileset.NOTHING) {
            world[i][j + 1] = Tileset.WALL;
        }
        if (world[i][j - 1] == Tileset.NOTHING) {
            world[i][j - 1] = Tileset.WALL;
        }
        if (world[i + 1][j + 1] == Tileset.NOTHING) {
            world[i + 1][j + 1] = Tileset.WALL;
        }
        if (world[i + 1][j - 1] == Tileset.NOTHING) {
            world[i + 1][j - 1] = Tileset.WALL;
        }
        if (world[i - 1][j + 1] == Tileset.NOTHING) {
            world[i - 1][j + 1] = Tileset.WALL;
        }
        if (world[i - 1][j - 1] == Tileset.NOTHING) {
            world[i - 1][j - 1] = Tileset.WALL;
        }
    }
}
