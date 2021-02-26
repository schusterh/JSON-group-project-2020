package modell;

import types.OnMapBuilding;
import ui.tiles.BuildingLayer;

import java.util.*;
import java.util.stream.Collectors;

public class Game {

    private ArrayList<Vehicle> vehicles;
    private ArrayList<Road> roads;
    private ArrayList<Railway> railways;
    private ArrayList<Factory> factories;
    private ArrayList<String> commodities;
    private ArrayList<NatureObject> nature_objects ;
    private ArrayList<Tower> towers;
    private ArrayList<AirportObject> airport_objects;
    private List<OnMapBuilding> buildingsOnMap;
    private Map map;
    private ArrayList<String> music;
    private TransportNetwork transportNetwork;

    int[] currentMouseTileIndex;

    public Game(ArrayList<String> commodities, ArrayList<Road> roads, ArrayList<Railway> railways, ArrayList<Tower> towers
    , ArrayList<AirportObject> airport_objects, ArrayList<NatureObject> nature_objects, ArrayList<Factory> factories,
                ArrayList<Vehicle> vehicles, Map map, ArrayList<String> music) {
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
        this.music = music;
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

    public void drive(Vehicle v,int tick){
        if (v.getKind().equals("road vehicle")){
            if (v.getPath()!=null){
                v.setCurrentStation(v.getPath().get(0));
                if (this.transportNetwork.stations.contains(v.getPath().get(1))){
                    v.setNextStation(v.getPath().get(1));
                } else {
                    findPath(v,tick,v.getPath().get(-1));
                    drive(v,tick);
                }
                v.setNextStation(v.getPath().get(1));

            } else {
                // Wenn es keinen Weg mehr gibt
                v.unloadCargo(v.getCargoTarget(v.getCurrentStation()));
            }
        } if (v.getKind().equals("engine")){
            if (v.getPath()!=null){


            }
        } if (v.getKind().equals("plane")){

        }
    }

    public void findTarget(GoodsBundle gb, Factory f, String commodity){
        HashMap<Factory, Double> possibleTargets = new HashMap<>();
        ArrayList<Factory> possTargets = new ArrayList<>();
        for (Factory g : this.getFactories()) {
            g.getProductions().stream().filter(p -> p.getConsume()
                    .isPresent()).filter(p -> p.getConsume().get()
                    .containsKey(commodity))
                    .forEach(p -> possibleTargets.put(g, null));
            Station nearF = transportNetwork.getNearStations(f);
            Station nearG = transportNetwork.getNearStations(g);
            double weight = (double) (g.getStorage().get(commodity) - g.getCurrentStorage().get(commodity))
                    / transportNetwork.getAdjStations(nearF).get(nearG);
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
        Factory targetFactory = possTargets.get(randomIndex);
        gb.setTargetStation(this.transportNetwork.nearStations.get(targetFactory));
    }
    public ArrayList<Station> bfs (Station startStation, Station targetStation){
        ArrayList<Station> shortestPath = new ArrayList<>();
        PriorityQueue<PrioPair> pq = new PriorityQueue<>();
        HashMap<Station, Station> origins = new HashMap<>();
        pq.add(new PrioPair(startStation,0));
        boolean targetFound = false;
        while (!targetFound){
            if (pq.peek() != null) {
                PrioPair currentPair = pq.peek();
                Station currentStation = currentPair.getStation();
                HashMap<Station, Integer> nextStations = getTransportNetwork().getAdjStations(currentStation);
                for (Station s : nextStations.keySet()) {
                    int dist = nextStations.get(s) + currentPair.getDistance();
                    if (origins.containsKey(s)){
                        int previousDist = pq.stream().filter(x -> x.getStation()
                                .equals(s))
                                .findFirst()
                                .orElseThrow()
                                .getDistance();
                        if (previousDist<dist){
                            pq.remove(s);
                            origins.remove(s);
                            origins.put(s,currentStation);
                        }
                    } else { origins.put(s,currentStation); }

                    pq.add(new PrioPair(s, dist));
                    if (s == targetStation) {
                        targetFound = true;
                    }
                }
                pq.remove(currentPair);
            } else {
                throw new IllegalArgumentException("No connecting path can be found.");
            }
        }
        shortestPath.add(targetStation);
        Station backtrack = origins.get(targetStation);
        while (backtrack != null){
            shortestPath.add(0,backtrack);
            backtrack = origins.get(backtrack);
        }
        return shortestPath;
    }
    public void findPath(Vehicle v, int startTick, Station target){
        if (v.getKind().equals("road vehicle")){
            v.setPath(bfs(v.getCurrentStation(), target));
        } else if (v.getKind().equals("plane")){


        } else if (v.getKind().equals("wagon")){

        }
    }



    public void addBuildingToMap(Building model, int startX, int startY, int height) {
        this.map.plainGround(startX, startY, model.getWidth(), model.getDepth(), height, model.getClass() != NatureObject.class);
        this.buildingsOnMap.add(new OnMapBuilding(model, startX, startY, height));
        this.sortBuildings();
    }

    public void addBuildingToMap(OnMapBuilding pendingBuilding) {
        this.map.plainGround(pendingBuilding.startX, pendingBuilding.startY, pendingBuilding.width, pendingBuilding.depth, pendingBuilding.height, pendingBuilding.model.getClass() != NatureObject.class);
        this.buildingsOnMap.add(pendingBuilding);
        this.sortBuildings();
    }

    private void sortBuildings() {
        this.buildingsOnMap = this.buildingsOnMap.stream()
                .sorted(Comparator.comparingInt(OnMapBuilding::getStartY))
                .sorted(Comparator.comparingInt(OnMapBuilding::getStartX))
                .collect(Collectors.toList());
    }

    public Map getMap() {
        return map;
    }


    public void setCurrentMouseTileIndex(int[] pos) {
        this.currentMouseTileIndex = pos;
    }

    public int[] getCurrentMouseTileIndex() { return currentMouseTileIndex; }

    public TransportNetwork getTransportNetwork() {
        return transportNetwork;
    }

    public String getBackgroundMusic() {
        return this.music.get(0);
    }

    public String getMenuMusic() {
        return this.music.get(1);
    }
}
class PrioPair implements Comparable<PrioPair>{
    final Station station;
    final Integer distance;

    public PrioPair(Station station, Integer distance){
        this.station = station;
        this.distance = distance;
    }
    @Override
    public int compareTo(PrioPair other) {
        return (this.distance - other.distance);
    }

    public Station getStation() {
        return station;
    }

    public Integer getDistance() {
        return distance;
    }

    boolean containsStation(Station s){
        if(this.getStation().equals(s)){
            return true;
        } else {
            return false;
        }
    }
}
