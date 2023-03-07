package byog.Core;

import byog.TileEngine.TETile;

import java.util.Random;

public class Partition {
    private Position p;
    int width, height;
    Partition left, right;
    Room room;
    private Hallway vhallway, hhallway;
    private boolean hasroom;
    boolean splitted;
    TETile[][] part;
    Random randomnumbers;

    /* Initialize Partition */
    public Partition(int w, int h, Position p, Random rn) {
        width = w;
        height = h;
        this.p = p;
        hasroom = false;
        randomnumbers = rn;
    }

    /* Setting the tile worlds */
    public void setWorld(TETile[][] world) {
        this.part = world;
    }

    /* Splitting the partition into two parts. */
    public void makecontainer() {
        int divwhere = 0;
        boolean vert = RandomUtils.bernoulli(randomnumbers);
        if (width >= height * 1.25) {
            vert = true;
        } else if (height >= width * 1.25) {
            vert = false;
        }
        //Minimum size for container is 6 * 6
        if (vert) {
            divwhere = (int) RandomUtils.uniform(randomnumbers, width * 0.4, width * 0.6);
            if ((divwhere < 6) || (width - divwhere < 6)) {
                return;
            }
            Position newp = new Position(p.x + divwhere, p.y);
            left = new Partition(divwhere, height, p, randomnumbers);
            right = new Partition(width - divwhere, height, newp, randomnumbers);
        } else {
            divwhere = (int) RandomUtils.uniform(randomnumbers, height * 0.4, height * 0.6);
            if ((divwhere < 6) || (height - divwhere < 6)) {
                return;
            }
            Position newp = new Position(p.x, p.y + divwhere);
            left = new Partition(width, divwhere, p, randomnumbers);
            right = new Partition(width, height - divwhere, newp, randomnumbers);
        }
        left.setWorld(part);
        right.setWorld(part);
        splitted = true;
    }

    /* Making rooms for each partitions. */
    public void makerooms() {
        int roomw = RandomUtils.uniform(randomnumbers, 3, width - 2);
        int roomh = RandomUtils.uniform(randomnumbers, 3, height - 2);

        int newxp = RandomUtils.uniform(randomnumbers, p.x, p.x + 4);
        int newyp = RandomUtils.uniform(randomnumbers, p.y, p.y + 4);

        room = new Room(roomw, roomh, new Position(newxp, newyp), part, randomnumbers);
        hasroom = true;
    }

    /* Getting the room. If it has both rooms left and right, it returns the room randomly */
    public Room randomgetroom() {
        if (hasroom) {
            return room;
        }
        Room leftroom = new Room();
        Room rightroom = new Room();
        boolean half = RandomUtils.bernoulli(randomnumbers);
        if (left != null || right != null) {
            if (left != null) {
                leftroom = left.randomgetroom();
            }
            if (right != null) {
                rightroom = right.randomgetroom();
            }
            if (leftroom == null || half) {
                return rightroom;
            } else {
                return leftroom;
            }
        } else {
            return null;
        }
    }

    /* Making the hallways which connects two rooms(left, right) */
    public void makehallways(Room lroom, Room rroom) {
        int wid = rroom.hall.x - lroom.hall.x;
        int hei = rroom.hall.y - lroom.hall.y;
        int widabs = Math.abs(wid) + 1;
        int heiabs = Math.abs(hei) + 1;
        boolean half = RandomUtils.bernoulli(randomnumbers);

        if (wid > 0 && hei > 0) {
            if (half) {
                hhallway = new Hallway(widabs, 1,
                        new Position(lroom.hall.x, lroom.hall.y), part);
                vhallway = new Hallway(1, heiabs,
                        new Position(rroom.hall.x, lroom.hall.y), part);
            } else {
                hhallway = new Hallway(widabs, 1,
                        new Position(lroom.hall.x, rroom.hall.y), part);
                vhallway = new Hallway(1, heiabs,
                        new Position(lroom.hall.x, lroom.hall.y), part);
            }
        } else if (wid > 0 && hei <= 0) {
            if (half) {
                hhallway = new Hallway(widabs, 1,
                        new Position(lroom.hall.x, lroom.hall.y), part);
                vhallway = new Hallway(1, heiabs,
                        new Position(rroom.hall.x, rroom.hall.y), part);
            } else {
                hhallway = new Hallway(widabs, 1,
                        new Position(lroom.hall.x, rroom.hall.y), part);
                vhallway = new Hallway(1, heiabs,
                        new Position(lroom.hall.x, rroom.hall.y), part);
            }
        } else {
            if (half) {
                hhallway = new Hallway(widabs, 1,
                        new Position(rroom.hall.x, lroom.hall.y), part);
                vhallway = new Hallway(1, heiabs,
                        new Position(rroom.hall.x, lroom.hall.y), part);
            } else {
                hhallway = new Hallway(widabs, 1,
                        new Position(rroom.hall.x, rroom.hall.y), part);
                vhallway = new Hallway(1, heiabs,
                        new Position(lroom.hall.x, lroom.hall.y), part);
            }
        }
    }
}
