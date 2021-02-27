package modell;

import javafx.util.Pair;

import java.util.*;

public class Road extends Building {

    private String name;
    private HashMap<String,ArrayList<Double>> points;
    private ArrayList<ArrayList<String>> roads;
    private int dz;
    private Optional<String> buildmenu;
    private Optional<HashMap<String, String>> combines;
    private Optional<String> special;




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
    public HashMap<String, ArrayList<Double>> getPoints() {
        return points;
    }

    public ArrayList<ArrayList<String>> getRoads() {
        return roads;
    }

    public Optional<String> getBuildmenu() {
        return buildmenu;
    }

    public Optional<HashMap<String, String>> getCombines() {
        return combines;
    }

    public int getDz() {
        return dz;
    }

    public Optional<String> getSpecial() { return special; }

}
