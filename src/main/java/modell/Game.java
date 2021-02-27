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
                    findPath(v,tick);
                    drive(v,tick);
                }
            } else {
                v.unloadCargo(v.getCurrentCargo().get(0));
                if (!v.getCurrentCargo().isEmpty())
                    findPath(v,tick);
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
    public ArrayList<Point> bfs (Point startPoint, Station targetStation, HashMap<Point, HashMap<Integer, Vehicle>> obstacles ){

        ArrayList<Point> shortestPath = new ArrayList<>();
        Deque<Point> deq = new ArrayDeque<>();

        HashMap<Point, Point> origins = new HashMap<>();

        deq.add(startPoint);
        boolean targetFound = false;
        while (!targetFound){
            if (deq.peek() != null) {

                Point currentPoint = deq.peekFirst();

                ArrayList<Point> nextPoints = transportNetwork.getPointConnections().get(currentPoint);

                for (Point p : nextPoints) {

                    origins.put(p,currentPoint);
                    deq.add(p);
                    if (transportNetwork.getStationPoints().get(targetStation).contains(p)){
                        targetFound = true;
                        shortestPath.add(p);
                        Point backtrack = origins.get(p);
                        while (backtrack != null){
                            shortestPath.add(0,backtrack);
                            backtrack = origins.get(backtrack);
                        }
                        return shortestPath;
                    }

                }
                deq.remove(currentPoint);

            } else {
                throw new IllegalArgumentException("No connecting path can be found.");
            }
        }
        return shortestPath;
    }

    public ArrayList<Point> findPath(Vehicle v, int startTick){
        GoodsBundle goodsBundle = v.getCurrentCargo().get(0);
        ArrayList <Point> path = new ArrayList<>();
        if (v.getKind().equals("road vehicle"))
            path.addAll(bfs(v.getCurrentPoint(), goodsBundle.getTargetStation(), new HashMap<>()));

        if (v.getKind().equals("plane"))
            path.addAll(bfs (v.getCurrentPoint(), goodsBundle.getTargetStation(),transportNetwork.getReservations()));

        if (v.getKind().equals("engine"))

        if (!path.isEmpty()){
            if (v.getKind().equals("plane")){
                HashMap <Point, Integer> reservations = new HashMap<>();
                for (int i = 0; i < path.size(); i++){
                    reservations.put(path.get(i+startTick),i);

                }
                for (Point p : reservations.keySet()){
                    for (Integer i : transportNetwork.getReservations().get(p).keySet()){
                        if (reservations.get(p).equals(i)){

                        }
                    }
                }

            } else if (v.getKind().equals("wagon")){

            }
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
                route.removeVehicle(v);
                manageVehicles(route);
            }
        }
    }

    public void addBuildingToMap(Building model, int startX, int startY, int height) {

        this.map.plainGround(startX, startY, model.getWidth(), model.getDepth(), height, model.getClass() != Road.class);
        this.buildingsOnMap.add(new OnMapBuilding(model, startX, startY, height));
        this.sortBuildings();
    }

    public void addBuildingToMap(OnMapBuilding pendingBuilding) {

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
