package modell;

import java.util.*;

/**
 * This class represents all objects which have something to do with the rail system of the game.
 * Because there are a lot of paramteres which are not shared by all obects, most of the parameters are optional.
 * The upside of this is that we only have one central class for all rail objects.
 */
public class Railway extends Building {

    private String name;
    private Optional buildmenu;
    private Optional points;
    private Optional rails;
    private Optional dz;
    private Optional signals;
    private Optional special;
    private Optional combines;


    /**
     * Instantiates a new Railway.
     *
     * @param name      the name
     * @param width     the width
     * @param depth     the depth
     * @param buildmenu the buildmenu
     * @param points    the points
     * @param rails     the rails
     * @param dz        the dz
     * @param signals   the signals
     * @param special   the special
     * @param combines  the combines
     */
    public Railway(String name, int width, int depth, Optional<String> buildmenu,
                   Optional<HashMap<String, ArrayList<Double>>> points,
                   Optional<ArrayList<ArrayList<String>>> rails,
                   Optional<Integer> dz,
                   Optional<ArrayList<String>> signals,
                   Optional<String> special,
                   Optional<HashMap<String, String>> combines) {
        super(width,depth, name);
        this.name = name;
        this.buildmenu = buildmenu;
        this.points = points;
        this.rails = rails;
        this.dz=dz;
        this.signals = signals;
        this.special = special;
        this.combines = combines;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets combines.
     *
     * @return the combines
     */
    public Optional getCombines() {
        return combines;
    }

    /**
     * Gets buildmenu.
     *
     * @return the buildmenu
     */
    public Optional getBuildmenu() {
        return buildmenu;
    }

    /**
     * Gets dz.
     *
     * @return the dz
     */
    public Optional getDz() {
        return dz;
    }

    /**
     * Gets points.
     *
     * @return the points
     */
    public Optional getPoints() {
        return points;
    }

    /**
     * Gets rails.
     *
     * @return the rails
     */
    public Optional getRails() {
        return rails;
    }

    /**
     * Gets signals.
     *
     * @return the signals
     */
    public Optional getSignals() {
        return signals;
    }

    /**
     * Gets special.
     *
     * @return the special
     */
    public Optional getSpecial() {
        return special;
    }
}
