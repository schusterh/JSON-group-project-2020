package modell;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    ArrayList<Vehicle> vehicles;
    ArrayList<Road> roads;
    ArrayList<Railway> railways;
    ArrayList<Factory> factories;
    ArrayList<String> commodities;
    ArrayList<NatureObject> nature_objects ;
    ArrayList<Tower> towers;
    ArrayList<AirportObject> airport_objects;
    Map map;
    HashMap<Station, HashMap<Station,Integer>> transportNetwork;

    public Game(ArrayList<String> commodities, ArrayList<Road> roads, ArrayList<Railway> railways, ArrayList<Tower> towers
    , ArrayList<AirportObject> airport_objects, ArrayList<NatureObject> nature_objects, ArrayList<Factory> factories,
                ArrayList<Vehicle> vehicles, Map map) {
        this.commodities = commodities;
        this.roads = roads;
        this.railways = railways;
        this.towers = towers;
        this.airport_objects = airport_objects;
        this.nature_objects = nature_objects;
        this.factories = factories;
        this.vehicles = vehicles;
        this.map = map;
        this.transportNetwork = new HashMap<>();
    }

    public ArrayList<Railway> getRailways() {
        return railways;
    }

    public ArrayList<Factory> getFactories() {
        return factories;
    }

    public ArrayList<Road> getRoads() {
        return roads;
    }

    public ArrayList<NatureObject> getNature_objects() {
        return nature_objects;
    }

    public ArrayList<String> getCommodities() {
        return commodities;
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public ArrayList<AirportObject> getAirport_objects() {
        return airport_objects;
    }

    public ArrayList<Tower> getTowers() {
        return towers;
    }

    public Map getMap() {
        return map;
    }

    public HashMap<Station, HashMap<Station, Integer>> getTransportNetwork() {
        return transportNetwork;
    }
}
