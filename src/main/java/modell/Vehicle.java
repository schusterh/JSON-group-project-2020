package modell;

import javafx.util.Pair;

import java.util.*;
import java.util.stream.Stream;

public class Vehicle {

    // Attribute
    private String name;
    private String kind;
    private String graphic;
    private Optional<ArrayList<HashMap<String,Integer>>> cargo;
    private double speed;
    private HashMap<String,Integer> currentCargo;
    private Station currentStation;
    private Station nextStation;
    private ArrayList<Station> path;

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

    public int loadCargo(String cargoType, Integer cargoAmount){

        if (cargoAmount<0) {throw new IllegalArgumentException("Cargo Amount should be positive");}
        Stream<HashMap> cargoStream = cargo.stream().flatMap(ArrayList::stream);
        HashMap<String,Integer> allowedCargo = new HashMap<>();
        cargoStream.filter(x -> x.containsKey(cargoType)).forEach(allowedCargo::putAll);
        int cargoSpace = allowedCargo.get(cargoType)-this.currentCargo.get(cargoType);
        int loaded = 0;
        if (cargoSpace<cargoAmount){
            loaded = cargoSpace;
        } else if (cargoSpace > cargoAmount){
            loaded = cargoAmount;
        }
        this.currentCargo.put(cargoType,this.currentCargo.get(cargoType)+loaded);
        return loaded;
    }

    public void unloadCargo(String cargoType){
        currentCargo.remove(cargoType);
    }

    public void drive(){
        if (this.kind.equals("road vehicle")){
            this.currentStation = this.path.get(0);

        }
    }

}

