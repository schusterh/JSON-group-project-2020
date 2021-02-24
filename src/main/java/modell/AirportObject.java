package modell;

import java.util.*;

public class AirportObject extends Building {
    private String name;
    private String buildmenu;
    private String special;
    private HashMap<String, ArrayList<Double>> points;
    private Optional entry;
    private ArrayList<ArrayList<String>> planes;
    private int dz;

    public AirportObject(int width, int depth, String name, String buildmenu, String special,
                         HashMap<String, ArrayList<Double>> points, Optional<ArrayList<String>> entry, ArrayList<ArrayList<String>> planes, int dz) {
        super(width, depth);
        this.name = name;
        this.buildmenu = buildmenu;
        this.special = special;
        this.points = points;
        this.entry = entry;
        this.planes = planes;
        this.dz = dz;
    }

}
