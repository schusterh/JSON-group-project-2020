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

    public HashMap addPoints(Double startX, Double startY){
        HashMap addToGrid = new HashMap();
        if (points.isPresent()) {
            HashMap<String,ArrayList<Double>> newPoints = (HashMap<String, ArrayList<Double>>) points.get();
            for (String point : newPoints.keySet()){
                Double xCoor = newPoints.get(point).get(0);
                Double yCoor = newPoints.get(point).get(1);
                addToGrid.put(point, Arrays.asList(startX+xCoor,startY+yCoor));
            }
        }
        return addToGrid;
    }
}
