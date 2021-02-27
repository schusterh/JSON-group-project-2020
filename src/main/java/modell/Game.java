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
        this.addFactoriesToMap();
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

    public void drive (Vehicle v, int tick){
        if (v.getKind().equals("road vehicle")){
            if (v.getPath()!=null) {
                if (transportNetwork.getPoints().contains(v.getNextPoint())){
                    v.setCurrentPoint(v.getNextPoint());
                    v.setNextPoint(v.getPath().get(0));
                } else {
                    findPath(v, tick, v.getCurrentCargo().get(0));
                    drive(v,tick);
                }
            } else {
                v.unloadCargo(v.getCurrentCargo().get(0));
                if (!v.getCurrentCargo().isEmpty())
                    findPath(v, tick, v.getCurrentCargo().get(0));
            }
        } if (v.getKind().equals("engine")){
            if (v.getPath()!=null){

            }
        }
    }


    public void findTarget(GoodsBundle gb, Factory f){
        HashMap<Factory, Double> possibleTargets = new HashMap<>();
        ArrayList<Factory> possTargets = new ArrayList<>();
        for (Factory g : this.getFactories()) {
            g.getProductions().stream().filter(p -> p.getConsume()
                    .isPresent()).filter(p -> p.getConsume().get()
                    .containsKey(gb.getGoodType()))
                    .forEach(p -> possibleTargets.put(g, null));
            Station nearF = transportNetwork.getNearStations(f);
            Station nearG = transportNetwork.getNearStations(g);
            double weight = (double) (g.getStorage().get(gb.getGoodType())
                    - g.getCurrentStorage().get(gb.getGoodType()))
                    / transportNetwork.getAdjStations(nearF).get(nearG).size();
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
        gb.setTargetStation(this.transportNetwork.getNearStations(targetFactory));
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
                HashMap<Station, ArrayList<Point>> nextStations = getTransportNetwork().getAdjStations(currentStation);
                for (Station s : nextStations.keySet()) {
                    int dist = nextStations.get(s).size() + currentPair.getDistance();
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
    public ArrayList<Point> findPath(Vehicle v, int startTick, GoodsBundle goodsBundle){
        ArrayList<Point> path = new ArrayList<>();
        if (v.getKind().equals("road vehicle")){
            ArrayList<Station> stations = bfs(v.getCurrentStation(),goodsBundle.getTargetStation());
            int i = 0;
            while (stations.get(i+1) != null) {
                for (TrafficRoute tr : transportNetwork.getTrafficRoutes().keySet()){
                    if (tr.getStations().contains(stations.get(i+1)) && tr.getVehicleType().equals(v.getKind())){
                        path.addAll(transportNetwork.getAdjStations(stations.get(i)).get(stations.get(i + 1)));
                        break;
                    }
                }
                i++;
            }
        } else if (v.getKind().equals("plane")){


        } else if (v.getKind().equals("wagon")){

        }
        return path;
    }
    public void manageVehicles(TrafficRoute route){
        int diff = route.getVehicles().size()-route.getVehicleAmount();
        if (diff > 0){
            route.removeVehicleAmount(diff);
        } if (diff < 0){
            ArrayList<Vehicle> possVehicles = new ArrayList<>();
            for (Vehicle v : this.vehicles) {
                if (v.getKind().equals(route.getVehicleType())) {
                    possVehicles.add(v);
                }
            }
            Random rand = new Random();
            for (int i = 0; i < Math.abs(diff);i++){
                route.addVehicle(possVehicles.get(rand.nextInt(possVehicles.size())));
            }
        }
        for (Vehicle v : route.getVehicles()){
            if (!v.getKind().equals(route.getVehicleType())){
                route.getVehicles().remove(v);
                manageVehicles(route);
            }
        }
    }

    public void addBuildingToMap(Building model, int startX, int startY, int height) {
        System.out.println(model.getClass());
        this.map.plainGround(startX, startY, model.getWidth(), model.getDepth(), height, model.getClass() != Road.class);
        this.buildingsOnMap.add(new OnMapBuilding(model, startX, startY, height));
        this.sortBuildings();
    }

    public void addBuildingToMap(OnMapBuilding pendingBuilding) {
        System.out.println(pendingBuilding.model.getClass());
        boolean isConcrete = pendingBuilding.model.getClass() != NatureObject.class && pendingBuilding.model.getClass() != Road.class;
        this.map.plainGround(pendingBuilding.startX, pendingBuilding.startY, pendingBuilding.width, pendingBuilding.depth, pendingBuilding.height, isConcrete);
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

    public void addFactoriesToMap() {
        Random r = new Random();
        for (Factory factory : this.factories) {
            int posX = r.nextInt(this.map.getWidth()-4);
            int posY = r.nextInt(this.map.getDepth()-4);

            while (this.isInMap(posX, posY, factory.getWidth(), factory.getDepth()) && this.isOccupied(posX, posY) && this.isInWater(posX, posY, factory.getWidth(), factory.getDepth())) {
                posX = r.nextInt(this.map.getWidth()-1);
                posY = r.nextInt(this.map.getDepth()-1);
            }
            this.addBuildingToMap(new OnMapBuilding(factory, posX, posY, this.map.getTile(posX, posY).height));
        }
    }

    public boolean isOccupied(int x, int y) {
        for (OnMapBuilding building : this.buildingsOnMap) {
            if (x >= building.startX &&
                x <= building.startX + building.width &&
                y >= building.startY &&
                y <= building.startY + building.depth
            ) {
                return true;
            }
        }
        return false;
    }

    public boolean isInWater(int x, int y, int width, int depth) {
        int waterCount = 0;
        for (int xCount = 0; xCount < width; xCount++) {
            for (int yCount = 0; yCount < depth; yCount++) {
                if (this.map.getTile(xCount, yCount).height == -1) {
                    waterCount++;
                }
            }
        }
        return waterCount >= 2;
    }

    public boolean isInMap(int x, int y, int width, int depth) {
        return x >= 0 && x + width < this.map.getWidth() - 1 &&
                y >= 0 && y + depth < this.map.getDepth() - 1;
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

    public void handleUpdate() {
        for (Factory factory : factories) {
            factory.produce();
        }
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
        return this.getStation().equals(s);
    }
}
