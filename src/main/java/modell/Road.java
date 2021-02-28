package modell;

import javafx.util.Pair;

import java.util.*;

/**
 * This class represents all road types of the game.
 */
public class Road extends Building {

    private String name;
    private HashMap<String,ArrayList<Double>> points;
    private ArrayList<ArrayList<String>> roads;
    private int dz;
    private Optional<String> buildmenu;
    private Optional<HashMap<String, String>> combines;
    private Optional<String> special;


    /**
     * Instantiates a new Road.
     *
     * @param name      the name
     * @param width     the width
     * @param depth     the depth
     * @param points    the points
     * @param roads     the roads
     * @param dz        the dz
     * @param buildmenu the buildmenu
     * @param combines  the combines
     * @param special   the special
     */
    public Road(String name, int width, int depth, HashMap<String,ArrayList<Double>> points, ArrayList<ArrayList<String>> roads, int dz,
                Optional<String> buildmenu,Optional<HashMap<String,String>> combines, Optional<String> special) {
        super(width,depth, name);
        this.name = name;
        this.points = points;
        this.roads = roads;
        this.dz = dz;
        this.buildmenu = buildmenu;
        this.combines = combines;
        this.special = special;

    }

    public String getName() {
        return name;
    }

    /**
     * Gets points.
     *
     * @return the points
     */
    public HashMap<String, ArrayList<Double>> getPoints() {
        return points;
    }

    /**
     * Gets roads.
     *
     * @return the roads
     */
    public ArrayList<ArrayList<String>> getRoads() {
        return roads;
    }

    /**
     * Gets buildmenu.
     *
     * @return the buildmenu
     */
    public Optional<String> getBuildmenu() {
        return buildmenu;
    }

    /**
     * Gets combines.
     *
     * @return the combines
     */
    public Optional<HashMap<String, String>> getCombines() {
        return combines;
    }

    /**
     * Gets dz.
     *
     * @return the dz
     */
    public int getDz() {
        return dz;
    }

    /**
     * Gets special.
     *
     * @return the special
     */
    public Optional<String> getSpecial() { return special; }

}
