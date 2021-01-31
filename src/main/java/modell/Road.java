package modell;

import javafx.util.Pair;

import java.util.*;

public class Road extends Building {

    private String name;
    private HashMap<String,ArrayList<Double>> points;
    private ArrayList<ArrayList<String>> roads;
    private int dz;
    private Optional buildmenu;
    private Optional combines;
    private Optional special;




    public Road(String name, int width, int depth, HashMap<String,ArrayList<Double>> points, ArrayList<ArrayList<String>> roads, int dz,
                Optional<String> buildmenu,Optional<HashMap<String,String>> combines, Optional<String> special) {
        super(width,depth);
        this.name = name;
        this.points = points;
        this.roads = roads;
        this.dz = dz;
        if (buildmenu.isPresent()) {this.buildmenu = buildmenu;}
        if (combines.isPresent()) {this.combines = combines;}
        if(special.isPresent()) {this.special = special;}

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

    public Optional getBuildmenu() {
        return buildmenu;
    }

    public Optional getCombines() {
        return combines;
    }

    public int getDz() {
        return dz;
    }
}
