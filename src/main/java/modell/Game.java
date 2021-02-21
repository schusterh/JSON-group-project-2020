package modell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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

    public Factory findTarget(Factory f, String commodity){
        HashMap<Factory, Double> possibleTargets = new HashMap<>();
        ArrayList<Factory> possTargets = new ArrayList<>();
        for (Factory g : this.getFactories()) {
            g.getProductions().stream().filter(p -> p.getConsume()
                    .isPresent()).filter(p -> p.getConsume().get()
                    .containsKey(commodity))
                    .forEach(p -> possibleTargets.put(g, null));
            double weight = (double) (g.getStorage().get(commodity) - g.getCurrentStorage().get(commodity))
                    / transportNetwork.get(f).get(g);
            possibleTargets.put(g, weight);
            possTargets.add(g);
        }
        double totalWeight = 0.0d;
        for (Factory factory : possTargets) {
            totalWeight += possibleTargets.get(factory);
        }
        int randomIndex = 0;
        for (double r = Math.random() * totalWeight; randomIndex < possTargets.size() -1; ++randomIndex){
            r -= possibleTargets.get(possTargets.get(randomIndex));
            if (r <= 0.0) break;
        }
        return possTargets.get(randomIndex);
    }
}
