package modell;

import modell.Building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

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
        super(width,depth);
        this.name = name;
        if (buildmenu.isPresent()){this.buildmenu = buildmenu;}
        if (points.isPresent()){this.points = points;}
        if (rails.isPresent()){this.rails = rails;}
        if (dz.isPresent()){this.dz=dz;}
        if(signals.isPresent()){this.signals = signals;}
        if(special.isPresent()){this.special = special;}
        if(combines.isPresent()){this.combines = combines;}
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
