package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.Random;
import java.awt.Color;
import java.awt.Font;
import edu.princeton.cs.introcs.StdDraw;

public class Game {
    TERenderer ter = new TERenderer();

    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    public void playWithKeyboard() {
        Menu game = new Menu(WIDTH, HEIGHT);
        game.initialize();
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        game.startPhase("CS61B : The Game", "New Game (N)", "Load Game (L)", "Quit (Q)");
        StdDraw.pause(500);
        boolean finish = true;
        boolean worldloaded = false;
        Font bigFont = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.white);
        String numbers = "";
        while (finish) {
            StdDraw.clear(Color.black);
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            if (key == 's') {
                game.input += String.valueOf(key);
                finish = false;
                break;
            } else if (key == 'n') {
                game.input += String.valueOf(key);
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "Enter Your Seed and Press S");
                StdDraw.show();
            } else if (key == 'l') {
                finish = false;
                String s = game.loadString();
                game.input += s;
                int parse = s.indexOf("s");
                if (parse < 0) {
                    numbers = "0";
                } else {
                    numbers = s.substring(1, parse);
                }
                Game load = new Game();
                world = load.playWithInputString(s);
                worldloaded = true;
                break;
            } else if (key == 'q') {
                System.exit(0);
            } else {
                game.input += String.valueOf(key);
                numbers += String.valueOf(key);
                StdDraw.text(WIDTH / 2, HEIGHT / 2, numbers);
                StdDraw.show();
            }
        }
        game.setSeed(Long.parseLong(numbers));
        ter.initialize(WIDTH, HEIGHT);
        Random pseudo = new Random(game.seed);
        MapGenerator test = new MapGenerator(WIDTH, HEIGHT, world, pseudo);
        Position player = new Position(0, 0);
        if (!worldloaded) {
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    world[x][y] = Tileset.NOTHING;
                }
            }
            test.generate();
            player = test.player;
        } else {
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    if (world[x][y] == Tileset.PLAYER) {
                        player.x = x;
                        player.y = y;
                    }
                }
            }
        }
        ter.renderFrame(world);
        StdDraw.show();
        game.processevents(world, ter, player);
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        Menu game = new Menu(WIDTH, HEIGHT);
        char[] arr = input.toCharArray();
        String number = "";
        if (Character.toUpperCase(input.charAt(0)) == 'L') {
            String s = game.loadString();
            s += input.substring(1, input.length());
            System.out.println(s + " " + input);
            return playWithInputString(s);
        }
        int count = input.indexOf("s");
        for (int i = 1; i < count; i++) {
            number += arr[i];
        }
        long randomnumbers = Long.parseLong(number);
        game.setSeed(randomnumbers);
        Random pseudo = new Random(randomnumbers);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        MapGenerator test = new MapGenerator(WIDTH, HEIGHT, world, pseudo);
        test.generate();
        Position player = test.player;
        String action = input.substring(count + 1, input.length());
        if (action == null) {
            return world;
        }

        for (int i = 0; i < action.length(); i++) {
            char key = Character.toUpperCase(action.charAt(i));
            switch (key) {
                case ':' :
                    break;
                case 'Q' :
                    if (Character.toUpperCase(action.charAt(action.length() - 2)) == ':') {
                        game.saveString(input.substring(0, input.length() - 2));
                    }
                    return world;
                case 'W' :
                case 'A' :
                case 'S' :
                case 'D' :
                case 'G' :
                    game.playerhelper(key, world, player);
                    break;
                case 'U' :
                    String actionsub = action.substring(0, i);
                    game.undohelper(world, player, actionsub, 0);
                    break;
                case 'R':
                    String s = action.substring(0, i + 1);
                    if (s.length() > 3) {
                        if (Character.toUpperCase(action.charAt(i - 1)) == 'O') {
                            if (Character.toUpperCase(action.charAt(i - 2)) == 'O') {
                                if (Character.toUpperCase(action.charAt(i - 3)) == 'D') {
                                    world[player.x + 1][player.y] = Tileset.UNLOCKED_DOOR;
                                }
                            }
                        }
                    }
                    break;
                default :
                    break;
            }
        }
        return world;
    }
}
