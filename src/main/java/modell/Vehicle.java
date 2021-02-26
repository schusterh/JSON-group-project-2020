package modell;

import java.util.*;
import java.util.stream.Stream;

public class Vehicle {

    // Attribute
    private String name;
    private String kind;
    private String graphic;
    private Optional<ArrayList<HashMap<String,Integer>>> cargo;
    private double speed;
    private ArrayList<GoodsBundle> currentCargo;
    private Station currentStation;
    private Station nextStation;
    private ArrayList<Station> path;
    private HashMap<Station,String> cargoTarget;
    private Point currentPoint;
    private Point nextPoint;

    // Konstruktor
    public Vehicle(String name,String kind, String graphic, double speed, Optional<ArrayList<HashMap<String,Integer>>> cargo){

        this.name = name;
        this.kind = kind;
        this.graphic = graphic;
        this.speed = speed;
        this.cargo = cargo;

    }

    // Getter

    public String getName() {
        return name;
    }
    public String getKind() { return kind; }
    public String getGraphic() { return graphic; }
    public  Optional<ArrayList<HashMap<String,Integer>>> getCargo() { return cargo; }
    public double getSpeed() { return speed; }

    public Station getCurrentStation() {
        return currentStation;
    }

    public void setPath(ArrayList<Station> path) {
        this.path = path;
    }

    public Optional<GoodsBundle> loadCargo(GoodsBundle gb){

        Stream<HashMap> cargoStream = cargo.stream().flatMap(ArrayList::stream);
        HashMap<String,Integer> allowedCargo = new HashMap<>();
        cargoStream.filter(x -> x.containsKey(gb.getGoodType())).forEach(allowedCargo::putAll);
        int alreadyLoaded = 0;
        for (GoodsBundle goodsBundle:currentCargo)
            if (goodsBundle.getGoodType().equals(gb.getGoodType()))
                alreadyLoaded = +goodsBundle.getGoodAmount();
        int cargoSpace = allowedCargo.get(gb.getGoodType())-alreadyLoaded;
        int loaded = 0;
        GoodsBundle remain=new GoodsBundle(gb.getGoodType(),0,gb.getTargetStation());
        if (cargoSpace<gb.getGoodAmount()){
            GoodsBundle newGb = new GoodsBundle(gb.getGoodType(), cargoSpace, gb.getTargetStation());
            remain.setGoodAmount(gb.getGoodAmount()-cargoSpace);
            this.currentCargo.add(newGb);
        } else {
            this.currentCargo.add(gb);
        }
        if (remain.getGoodAmount()==0){
            remain = null;
        }
        return Optional.ofNullable(remain);
    }

    public void unloadCargo(String cargoType){
        currentCargo.remove(cargoType);
    }

    public ArrayList<Station> getPath() {
        return path;
    }

    public void setCurrentPoint(Point currentPoint) {
        this.currentPoint = currentPoint;
    }
    public void setNextPoint(Point nextPoint) {
        this.nextPoint = nextPoint;
    }

    public void setCargoTarget(String cargoType, Station targetStation) {
        this.cargoTarget.put(targetStation,cargoType);
    }

    public String getCargoTarget(Station s) {
        return cargoTarget.get(s);
    }
}

