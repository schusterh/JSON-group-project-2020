package modell;

import java.util.*;

/**
 * This class represents airport objects.
 * An airport object is everything that can be found in the airport build menu and placed within an airport.
 */
public class AirportObject extends Building {
    private String name;
    private Optional buildmenu;
    private String special;
    private HashMap<String, ArrayList<Double>> points;
    private Optional entry;
    private ArrayList<ArrayList<String>> planes;
    private int dz;

    /**
     * Instantiates a new Airport object.
     *
     * @param width     the width
     * @param depth     the depth
     * @param name      the name
     * @param buildmenu the buildmenu
     * @param special   the special
     * @param points    the points
     * @param entry     the entry
     * @param planes    the planes
     * @param dz        the dz
     */
    public AirportObject(int width, int depth, String name, Optional buildmenu, String special,
                         HashMap<String, ArrayList<Double>> points, Optional<ArrayList<String>> entry, ArrayList<ArrayList<String>> planes, int dz) {
        super(width, depth, name);
        this.name = name;
        this.buildmenu = buildmenu;
        this.special = special;
        this.points = points;
        this.entry = entry;
        this.planes = planes;
        this.dz = dz;

    }

    /**
     * Gets buildmenu.
     *
     * @return the buildmenu
     */
    public Optional getBuildmenu() { return this.buildmenu; }
}
