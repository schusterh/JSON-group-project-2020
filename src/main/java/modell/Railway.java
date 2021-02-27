package modell;

import java.util.*;

public class Railway extends Building {

    private String name;
    private Optional buildmenu;
    private Optional points;
    private Optional rails;
    private Optional dz;
    private Optional signals;
    private Optional special;
    private Optional combines;



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

    public Optional getCombines() {
        return combines;
    }

    public Optional getBuildmenu() {
        return buildmenu;
    }

    public Optional getDz() {
        return dz;
    }

    public Optional getPoints() {
        return points;
    }

    public Optional getRails() {
        return rails;
    }

    public Optional getSignals() {
        return signals;
    }

    public Optional getSpecial() {
        return special;
    }
}
