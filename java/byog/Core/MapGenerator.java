package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

public class MapGenerator {
    Partition parent;
    private int maxwidth;
    private int maxheight;
    Random randomnumbers;
    Position player;

    /* Constructor */
    public MapGenerator(int width, int height, TETile[][] world, Random rn) {
        maxwidth = width;
        maxheight = height;
        parent = new Partition(maxwidth, maxheight, new Position(0, 0), rn);
        parent.setWorld(world);
        randomnumbers = rn;
        player = new Position(0, 0);
    }

    /* Actually implementing helper methods */
    public void generate() {
        containterhelper(parent);
        makeroomhelper(parent);
        hallhelper(parent);
        setplayer(parent);
    }

    /* Making containers for parent */
    private void containterhelper(Partition child) {
        if ((child.width < 12) || (child.height < 12)) {
            return;
        }
        if (child.splitted) {
            return;
        }
        child.makecontainer();

        if (child.left == null && child.right == null) {
            return;
        }
        containterhelper(child.left);
        containterhelper(child.right);

    }

    /* Making rooms for each child */
    private void makeroomhelper(Partition child) {
        if (child.left == null && child.right == null) {
            child.makerooms();
            return;
        }
        makeroomhelper(child.left);
        makeroomhelper(child.right);
    }

    /* Making hallways for each child */
    private void hallhelper(Partition child) {
        if (child.left == null && child.right == null) {
            return;
        } else if (child.left != null && child.right != null) {
            child.makehallways(child.left.randomgetroom(), child.right.randomgetroom());
        }
        hallhelper(child.left);
        hallhelper(child.right);
    }

    private void setplayer(Partition child) {
        if (child.room != null) {
            player.x = child.room.p.x + 1;
            player.y = child.room.p.y + 1;

            child.part[player.x][player.y] = Tileset.PLAYER;
            return;
        }
        boolean half = RandomUtils.bernoulli(randomnumbers);
        if (half) {
            setplayer(child.left);
        } else {
            setplayer(child.right);
        }
    }
}
