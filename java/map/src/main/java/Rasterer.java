import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    Tile queryTile;

    public Rasterer() {
        queryTile = new Tile(MapServer.ROOT_ULLON, MapServer.ROOT_ULLAT, MapServer.ROOT_LRLON,
                MapServer.ROOT_LRLAT, 0);
    }



    public class Tile {
        private double ullon;
        private double ullat;
        private double lrlon;
        private double lrlat;
        private int depth;
        private double lonDPP;
        Tile northWest;
        Tile northEast;
        Tile southWest;
        Tile southEast;

        public Tile(double ullon, double ullat, double lrlon, double lrlat, int depth) {
            this.ullon = ullon;
            this.ullat = ullat;
            this.lrlon = lrlon;
            this.lrlat = lrlat;
            this.depth = depth;
            if (depth < 7) {
                this.northWest = new Tile(ullon, ullat, (ullon + lrlon) / 2,
                        (ullat + lrlat) / 2, depth + 1);
                this.northEast = new Tile((ullon + lrlon) / 2, ullat, lrlon,
                        (ullat + lrlat) / 2, depth + 1);
                this.southWest = new Tile(ullon, (ullat + lrlat) / 2,
                        (ullon + lrlon) / 2, lrlat, depth + 1);
                this.southEast = new Tile((ullon + lrlon) / 2,
                        (ullat + lrlat) / 2, lrlon, lrlat, depth + 1);
            }
            this.lonDPP = (lrlon - ullon) / MapServer.TILE_SIZE;
        }
        public boolean lonDPPchecker(Double queryLonDPP) {
            return depth == 7 || queryLonDPP >= lonDPP;
        }
    }

    public void query(Tile root, double parullon, double parullat, double parlrlon, double parlrlat,
                      double lonDPP, Map<String, Object> result) {
        if ((root.ullon > parlrlon) || (parlrlat > root.ullat)
                || (parullon > root.lrlon) || (root.lrlat > parullat)) {
            return;
        }
        if (!(root.lonDPPchecker(lonDPP))) {
            query(root.northWest, parullon, parullat, parlrlon, parlrlat, lonDPP, result);
            query(root.northEast, parullon, parullat, parlrlon, parlrlat, lonDPP, result);
            query(root.southWest, parullon, parullat, parlrlon, parlrlat, lonDPP, result);
            query(root.southEast, parullon, parullat, parlrlon, parlrlat, lonDPP, result);
        } else {
            if (root.ullon < (double) result.get("raster_ul_lon")) {
                result.replace("raster_ul_lon", root.ullon);
            }
            if (root.ullat > (double) result.get("raster_ul_lat")) {
                result.replace("raster_ul_lat", root.ullat);
            }
            if (root.lrlon > (double) result.get("raster_lr_lon")) {
                result.replace("raster_lr_lon", root.lrlon);
            }
            if (root.lrlat < (double) result.get("raster_lr_lat")) {
                result.replace("raster_lr_lat", root.lrlat);
            }
            if (root.depth > (int) result.get("depth")) {
                result.replace("depth", root.depth);
            }
        }
    }

    String[][] rendergrid(Tile root, double ullon, double ullat,
                          double lrlon, double lrlat, int depth) {
        double pixelofwidth = (root.lrlon - root.ullon) / Math.pow(2, depth);
        double pixelofheight = (root.ullat - root.lrlat) / Math.pow(2, depth);
        double pixelinraster1 = (lrlon - ullon) / pixelofwidth;
        double pixelinraster2 = (ullat - lrlat) / pixelofheight;
        double pixelinwidth = (ullon - root.ullon) / pixelofwidth;
        double pixelinheight = (root.ullat - ullat) / pixelofheight;

        int xcount = (int) pixelinraster1;
        int ycount = (int) Math.round(pixelinraster2);
        int xstart = (int) pixelinwidth;
        int ystart = (int) Math.round(pixelinheight);

        String[][] comp = new String[ycount][xcount];
        for (int i = 0; i < ycount; i++) {
            for (int j = 0; j < xcount; j++) {
                comp[i][j] = "d" + depth + "_x" + (xstart + j) + "_y" + (ystart + i) + ".png";
            }
        }
        return comp;
    }


    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        // System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        results.put("raster_ul_lon", Double.MAX_VALUE);
        results.put("raster_ul_lat", Double.MIN_VALUE);
        results.put("raster_lr_lon", Double.NEGATIVE_INFINITY);
        results.put("raster_lr_lat", Double.MAX_VALUE);
        results.put("depth", 0);

        Rasterer raster = new Rasterer();
        raster.query(raster.queryTile, params.get("ullon"), params.get("ullat"),
                params.get("lrlon"), params.get("lrlat"),
                (params.get("lrlon") - params.get("ullon")) / params.get("w"), results);

        String[][] rendergrid = raster.rendergrid(raster.queryTile,
                (double) results.get("raster_ul_lon"), (double) results.get("raster_ul_lat"),
                (double) results.get("raster_lr_lon"), (double) results.get("raster_lr_lat"),
                (int) results.get("depth"));
        results.put("render_grid", rendergrid);
        results.put("query_success", true);

        return results;
    }

}
