package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class Menu {
    private int width;
    private int height;
    Random rand;
    long seed;
    String input = "";

    public Menu(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void initialize() {
        StdDraw.setCanvasSize(width * 16, height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }
    public void setSeed(long seed) {
        this.seed = seed;
        rand = new Random(seed);
    }

    public void startPhase(String s1, String s2, String s3, String s4) {
        double divWidth = width / 2;
        double divHeight = height / 1.2;

        StdDraw.clear(Color.black);
        Font bigFont = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(divWidth, divHeight, s1);
        StdDraw.text(divWidth, height / 2.5, s2);
        StdDraw.text(divWidth, height / 3.1, s3);
        StdDraw.text(divWidth, height / 4.1, s4);
        StdDraw.show();
    }

    public void processevents(TETile[][] world, TERenderer ter, Position player) {
        boolean finish = true;
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();
        String kind = "";
        while (finish) {
            if (StdDraw.mouseX() != x || StdDraw.mouseY() != y) {
                x = StdDraw.mouseX();
                y = StdDraw.mouseY();
                StdDraw.clear(Color.black);
                int inx = (int) StdDraw.mouseX();
                int iny = (int) StdDraw.mouseY();
                if (inx >= width) {
                    inx = width - 1;
                }
                if (iny >= height) {
                    iny = height - 1;
                }
                if (inx < 0) {
                    inx = 0;
                }
                if (iny < 0) {
                    iny = 0;
                }
                kind = worldset(world, inx, iny);
            } else {
                if (!StdDraw.hasNextKeyTyped()) {
                    continue;
                }
                char key = StdDraw.nextKeyTyped();
                switch (Character.toUpperCase(key)) {
                    case 'W' :
                    case 'A' :
                    case 'S' :
                    case 'D' :
                    case 'R' :
                    case 'O' :
                        playerhelper(key, world, player);
                        input += String.valueOf(key);
                        break;
                    case ':' :
                        input += String.valueOf(key);
                        break;
                    case 'U' :
                        String undosub = input.substring(input.indexOf("s") + 1, input.length());
                        undohelper(world, player, undosub, 0);
                        input += String.valueOf(key);
                        break;
                    case 'Q' :
                        if (input.charAt(input.length() - 1) == ':') {
                            finish = false;
                            saveString(input.substring(0, input.length() - 1));
                            System.exit(0);
                        }
                        break;
                    default:
                        break;
                }
            }
            ter.renderFrame(world);
            Font bigFont = new Font("Monaco", Font.BOLD, 10);
            StdDraw.setFont(bigFont);
            StdDraw.setPenColor(Color.white);
            StdDraw.textLeft(1, height - 1, kind);
            StdDraw.show();
        }
    }

    public String worldset(TETile[][] world, int inx, int iny) {
        if (world[inx][iny] == Tileset.FLOOR) {
            return "FLOOR";
        } else if (world[inx][iny] == Tileset.PLAYER) {
            return "PLAYER";
        } else if (world[inx][iny] == Tileset.WALL) {
            return "WALL";
        } else if (world[inx][iny] == Tileset.NOTHING) {
            return "NOTHING";
        } else if (world[inx][iny] == Tileset.UNLOCKED_DOOR) {
            return "UNLOCKED_DOOR";
        } else if (world[inx][iny] == Tileset.LOCKED_DOOR) {
            return "LOCKED_DOOR";
        }
        return null;
    }

    public void undohelper(TETile[][] world, Position player, String action, int minus) {
        char key = Character.toUpperCase(action.charAt(action.length() - (minus + 1)));
        if (key == 'U') {
            int count = 0;
            while (true) {
                count++;
                key = action.charAt(action.length() - (count + 1));
                if (key != 'u') {
                    break;
                }
            }
            int cmp = action.length() - (2 * count + 1);
            if (cmp < 0) {
                return;
            }
            key = Character.toUpperCase(action.charAt(cmp));
        }
        if (key == 'W') {
            key = 'S';
        } else if (key == 'S') {
            key = 'W';
        } else if (key == 'A') {
            key = 'D';
        } else if (key == 'D') {
            key = 'A';
        }
        playerhelper(key, world, player);
    }

    public void playerhelper(char key, TETile[][] world, Position player) {
        switch (Character.toUpperCase(key)) {
            case 'W':
                if (world[player.x][player.y + 1] == Tileset.UNLOCKED_DOOR) {
                    System.exit(0);
                } else if (world[player.x][player.y + 1] == Tileset.FLOOR) {
                    player.y += 1;
                    world[player.x][player.y] = Tileset.PLAYER;
                    world[player.x][player.y - 1] = Tileset.FLOOR;
                }
                break;
            case 'A':
                if (world[player.x - 1][player.y] == Tileset.UNLOCKED_DOOR) {
                    System.exit(0);
                } else if (world[player.x - 1][player.y] == Tileset.FLOOR) {
                    player.x -= 1;
                    world[player.x][player.y] = Tileset.PLAYER;
                    world[player.x + 1][player.y] = Tileset.FLOOR;
                }
                break;
            case 'S':
                if (world[player.x][player.y - 1] == Tileset.UNLOCKED_DOOR) {
                    System.exit(0);
                } else if (world[player.x][player.y - 1] == Tileset.FLOOR) {
                    player.y -= 1;
                    world[player.x][player.y] = Tileset.PLAYER;
                    world[player.x][player.y + 1] = Tileset.FLOOR;
                }
                break;
            case 'D':
                if (world[player.x + 1][player.y] == Tileset.UNLOCKED_DOOR) {
                    System.exit(0);
                } else if (world[player.x + 1][player.y] == Tileset.FLOOR) {
                    player.x += 1;
                    world[player.x][player.y] = Tileset.PLAYER;
                    world[player.x - 1][player.y] = Tileset.FLOOR;
                }
                break;
            case 'R' :
                if (input.length() > 2) {
                    if (Character.toUpperCase(input.charAt(input.length() - 1)) == 'O') {
                        if (Character.toUpperCase(input.charAt(input.length() - 2)) == 'O') {
                            if (Character.toUpperCase(input.charAt(input.length() - 3)) == 'D') {
                                world[player.x + 1][player.y] = Tileset.UNLOCKED_DOOR;
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    public static String loadString() {
        File f = new File("./String.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                String s = (String) os.readObject();
                os.close();
                return s;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        return null;
    }

    public static void saveString(String s) {
        File f = new File("./String.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(s);
            os.close();
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}
