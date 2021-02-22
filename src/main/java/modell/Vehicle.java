package modell;

import javafx.util.Pair;

import java.util.*;

public class Vehicle {

    // Attribute
    private String name;
    private String kind;
    private String graphic;
    private Optional<ArrayList<HashMap<String,Integer>>> cargo;
    private double speed;

    // Konstruktor
    public Vehicle(String name,String kind, String graphic, double speed, Optional<ArrayList<HashMap<String,Integer>>> cargo){
        this.name = name;
        this.kind = kind;
        this.graphic = graphic;
        this.speed = speed;
        if (cargo.isPresent()) {
            this.cargo = cargo;
        }
    }

    // Getter

    public String getName() {
        return name;
    }
    public String getKind() { return kind; }
    public String getGraphic() { return graphic; }
    public  Optional<ArrayList<HashMap<String,Integer>>> getCargo() { return cargo; }
    public double getSpeed() { return speed; }



}

