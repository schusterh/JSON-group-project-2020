package modell;

import types.OnMapBuilding;
import ui.tiles.BuildingLayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Game {

    ArrayList<Vehicle> vehicles;
    ArrayList<Road> roads;
    ArrayList<Railway> railways;
    ArrayList<Factory> factories;
    ArrayList<String> commodities;
    ArrayList<NatureObject> nature_objects ;
    ArrayList<Tower> towers;
    ArrayList<AirportObject> airport_objects;
    List<OnMapBuilding> buildingsOnMap;
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
        this.buildingsOnMap = new ArrayList<>();
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

    public ArrayList<NatureObject> getNatureObjects() {
        return nature_objects;
    }

    public ArrayList<String> getCommodities() {
        return commodities;
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public ArrayList<AirportObject> getAirportObjects() {
        return airport_objects;
    }

    public ArrayList<Tower> getTowers() {
        return towers;
    }

    public List<OnMapBuilding> getBuildingsOnMap() { return buildingsOnMap; }

    public void addBuildingToMap(Building model, int startX, int startY, int height) {
        this.map.plainGround(startX, startY, model.getWidth(), model.getDepth(), height, model.getClass() == NatureObject.class ? false : true);
        this.buildingsOnMap.add(new OnMapBuilding(model, startX, startY, height));
        this.buildingsOnMap = this.buildingsOnMap.stream()
                .sorted(Comparator.comparingInt(OnMapBuilding::getStartY))
                .sorted(Comparator.comparingInt(OnMapBuilding::getStartX))
                .collect(Collectors.toList());
    }

    public Map getMap() {
        return map;
    }

    public HashMap<Station, HashMap<Station, Integer>> getTransportNetwork() {
        return transportNetwork;
    }
}
