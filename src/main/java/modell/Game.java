package modell;

import types.OnMapBuilding;

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
    private HashMap<String, int[]> directions;

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
        this.transportNetwork = new TransportNetwork();
        this.addFactoriesToMap();

        this.directions = new HashMap<>();
        this.directions.put("ne", new int[]{0,1});
        this.directions.put("se", new int[]{1,0});
        this.directions.put("sw", new int[]{0,-1});
        this.directions.put("nw", new int[]{-1,0});

        for(int i = 0; i < 100; i++) {
            this.addStonesToMap();
        }

        for(int i = 0; i < 100; i++) {
            this.addTreesToMap();
        }

        for (int i = 0; i < 10; i++) {
            this.addRuinsToMap();
        }


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

        if (pendingBuilding.model.getClass() == Road.class) {
            Road roadModel = (Road) pendingBuilding.model;

            Optional<OnMapBuilding> previousRoadOptional;
            Optional<OnMapBuilding> nextRoadOptional;
            switch (roadModel.getName()) {
                case "road-ne":
                    previousRoadOptional = this.getBuildingAtTile(pendingBuilding.getStartX(), pendingBuilding.getStartY()+1);
                    nextRoadOptional = this.getBuildingAtTile(pendingBuilding.getStartX(), pendingBuilding.getStartY()-1);
                    if (previousRoadOptional.isPresent()) {
                        OnMapBuilding previousRoad = previousRoadOptional.get();
                        if (previousRoad.model.getName().equals("road-ne") || previousRoad.model.getName().equals("road-sw")) {
                            this.roads.stream().filter(roadFilter -> roadFilter.getName().equals("road-ne-sw")).findFirst().ifPresent(previousRoad::replaceModel);
                        }
                    }
                    if (nextRoadOptional.isPresent()) {
                        OnMapBuilding nextRoad = nextRoadOptional.get();
                        if (nextRoad.model.getClass() == Road.class) {
                            Road nextRoadModel = (Road) nextRoad.model;
                            if (nextRoadModel.getPoints().containsKey("ne")) {
                                this.roads.stream().filter(roadFilter -> roadFilter.getName().equals("road-ne-sw")).findFirst().ifPresent(pendingBuilding::replaceModel);
                            }
                        }
                    }
                    break;
                case "road-sw":
                    previousRoadOptional = this.getBuildingAtTile(pendingBuilding.getStartX(), pendingBuilding.getStartY()-1);
                    nextRoadOptional = this.getBuildingAtTile(pendingBuilding.getStartX(), pendingBuilding.getStartY()+1);

                    if (previousRoadOptional.isPresent()) {
                        OnMapBuilding previousRoad = previousRoadOptional.get();
                        if (previousRoad.model.getName().equals("road-sw") || previousRoad.model.getName().equals("road-ne")) {
                            this.roads.stream().filter(roadFilter -> roadFilter.getName().equals("road-ne-sw")).findFirst().ifPresent(previousRoad::replaceModel);
                        }
                    }
                    if (nextRoadOptional.isPresent()) {
                        OnMapBuilding nextRoad = nextRoadOptional.get();
                        if (nextRoad.model.getClass() == Road.class) {
                            Road nextRoadModel = (Road) nextRoad.model;
                            if (nextRoadModel.getPoints().containsKey("ne")) {
                                this.roads.stream().filter(roadFilter -> roadFilter.getName().equals("road-ne-sw")).findFirst().ifPresent(pendingBuilding::replaceModel);
                            }
                        }
                    }
                    break;
                case "road-se":
                    previousRoadOptional = this.getBuildingAtTile(pendingBuilding.getStartX()+1, pendingBuilding.getStartY());
                    nextRoadOptional = this.getBuildingAtTile(pendingBuilding.getStartX()-1, pendingBuilding.getStartY());

                    if (previousRoadOptional.isPresent()) {
                        OnMapBuilding previousRoad = previousRoadOptional.get();
                        if (previousRoad.model.getName().equals("road-se") | previousRoad.model.getName().equals("road-nw")) {
                            this.roads.stream().filter(roadFilter -> roadFilter.getName().equals("road-nw-se")).findFirst().ifPresent(previousRoad::replaceModel);
                        }
                    }
                    if (nextRoadOptional.isPresent()) {
                        OnMapBuilding nextRoad = nextRoadOptional.get();
                        if (nextRoad.model.getClass() == Road.class) {
                            Road nextRoadModel = (Road) nextRoad.model;
                            if (nextRoadModel.getPoints().containsKey("ne")) {
                                this.roads.stream().filter(roadFilter -> roadFilter.getName().equals("road-ne-sw")).findFirst().ifPresent(pendingBuilding::replaceModel);
                            }
                        }
                    }
                    break;
                case "road-nw":
                    previousRoadOptional = this.getBuildingAtTile(pendingBuilding.getStartX()-1, pendingBuilding.getStartY());
                    nextRoadOptional = this.getBuildingAtTile(pendingBuilding.getStartX()+1, pendingBuilding.getStartY());
                    if (previousRoadOptional.isPresent()) {
                        OnMapBuilding previousRoad = previousRoadOptional.get();
                        if (previousRoad.model.getName().equals("road-nw") | previousRoad.model.getName().equals("road-se")) {
                            this.roads.stream().filter(roadFilter -> roadFilter.getName().equals("road-nw-se")).findFirst().ifPresent(previousRoad::replaceModel);
                        }
                    }
                    break;
                default:
                    break;
            }
            /*roadModel.getCombines().ifPresent(combines -> {
                ArrayList<OnMapBuilding> adjBuildings = this.getAdjRoads(pendingBuilding);
                if (!adjBuildings.isEmpty()) {
                    for (OnMapBuilding adjBuilding : adjBuildings) {
                        if (combines.containsKey(adjBuilding.model.getName())) {
                            Road replacementModel = this.roads.stream().filter(roadFilter -> roadFilter.getName().equals(combines.get(adjBuilding.model.getName()))).findFirst().orElse(null);
                            if (replacementModel != null) {
                                adjBuilding.replaceModel(replacementModel);
                            }
                        }
                        else if (adjBuilding.model.getName().equals(roadModel.getName())) {
                            Road replacementModel = this.roads.stream().filter(roadFilter -> roadFilter.getName().equals(this.roadExtensions.get(roadModel.getName()))).findFirst().orElse(null);
                            if (replacementModel != null) {
                                adjBuilding.replaceModel(replacementModel);
                            }
                        }
                    }
                }
            });*/

            if (roadModel.getSpecial().isEmpty()) {
                this.transportNetwork.addTrafficSection((double) pendingBuilding.startX, (double) pendingBuilding.startY, roadModel.getPoints(), roadModel.getRoads());
                System.out.println("Added road to transport network!");
            } else if (roadModel.getSpecial().isPresent()) {
                if (roadModel.getSpecial().get().equals("busstop")) {
                    System.out.println("Busstop added!");
                    Station newStation = new Station(pendingBuilding.width, pendingBuilding.depth, this.transportNetwork.stationnameGenerator());
                    ArrayList<OnMapBuilding> adjBuildings = this.getAdjBuildings(pendingBuilding);
                    if (!adjBuildings.isEmpty()) {
                        for (OnMapBuilding adjBuilding : adjBuildings) {
                            if (adjBuilding.model.getClass() == Factory.class) {
                                newStation.setNearFactory((Factory) adjBuilding.model);
                                System.out.println("Station " + newStation.getName() + " mit Fabrik " + adjBuilding.model.getName() + " verbunden!");
                                break;
                            }
                        }
                    }
                    this.transportNetwork.addStation(newStation, pendingBuilding.getStartX(), pendingBuilding.getStartY());
                    System.out.println("Neue Station hinzugefügt: " + newStation.getName());
                }
            }
        }

        this.sortBuildings();
    }

    public Optional<OnMapBuilding> getBuildingAtTile(int xPos, int yPos) {
        for (OnMapBuilding building : this.buildingsOnMap) {
            if (building.startX == xPos && building.startY == yPos) {
                return Optional.of(building);
            }
        }
        return Optional.empty();
    }

    public void possiblyConnectStation(OnMapBuilding newRoad) {
        if (newRoad.model.getClass() == Road.class) {
            Road roadModel = (Road) newRoad.model;
        }
    }

    public void updateRoadNetwork(OnMapBuilding newRoad) {
        ArrayList<OnMapBuilding> adjBuildings = this.getAdjBuildings(newRoad);
    }

    public ArrayList<OnMapBuilding> getAdjRoads(OnMapBuilding newRoad) {
        ArrayList<OnMapBuilding> returnList = new ArrayList<>();

        for (OnMapBuilding adjRoad : this.buildingsOnMap) {
            if (adjRoad.model.getClass() == Road.class) {
                if ((adjRoad.startX == newRoad.startX-1 && adjRoad.startY == newRoad.startY) ||
                        (adjRoad.startX == newRoad.startX+1 && adjRoad.startY == newRoad.startY) ||
                        (adjRoad.startY == newRoad.startY-1 && adjRoad.startX == newRoad.startX) ||
                        (adjRoad.startY == newRoad.startY+1 && adjRoad.startX == newRoad.startX)) {
                    returnList.add(adjRoad);
                }
            }
        }

        return returnList;
    }

    public ArrayList<OnMapBuilding> getAdjBuildings(OnMapBuilding newBuilding) {
        ArrayList<OnMapBuilding> returnList = new ArrayList<>();

        for (int xPos = newBuilding.startX-1; xPos <= newBuilding.startX + newBuilding.width; xPos++) {
            Optional<OnMapBuilding> result = this.tileHasBuilding(xPos, newBuilding.startY-1);
            result.ifPresent(returnList::add);
            result = this.tileHasBuilding(xPos, newBuilding.startY + newBuilding.depth + 1);
            result.ifPresent(returnList::add);
        }
        for (int yPos = newBuilding.startY-1; yPos <= newBuilding.startY + newBuilding.depth; yPos++) {
            Optional<OnMapBuilding> result = this.tileHasBuilding(newBuilding.startX-1, yPos);
            result.ifPresent(returnList::add);
            result = this.tileHasBuilding( newBuilding.startX + newBuilding.width + 1, yPos);
            result.ifPresent(returnList::add);
        }

        return returnList;
    }

    private void sortBuildings() {
        this.buildingsOnMap = this.buildingsOnMap.stream()
                .sorted(Comparator.comparingInt(OnMapBuilding::getStartY))
                .sorted(Comparator.comparingInt(OnMapBuilding::getStartX))
                .collect(Collectors.toList());
    }

    public Optional<OnMapBuilding> tileHasBuilding(int xPos, int yPos) {
        for (OnMapBuilding building : this.buildingsOnMap) {
            if (building.startX <= xPos && xPos <= building.startX + building.width
                && building.startY <= yPos && yPos <= building.startY + building.depth) {
                return Optional.of(building);
            }
        }
        return Optional.empty();
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

    public void addTreesToMap() {
        Random r = new Random();
        for (NatureObject natob : this.nature_objects) {
            if (natob.getBuildmenu().isPresent()) {
                int posX = r.nextInt(this.map.getWidth() - 4);
                int posY = r.nextInt(this.map.getDepth() - 4);

                while (this.isInMap(posX, posY, natob.getWidth(), natob.getDepth()) && this.isOccupied(posX, posY) && this.isInWater(posX, posY, natob.getWidth(), natob.getDepth())) {
                    posX = r.nextInt(this.map.getWidth() - 1);
                    posY = r.nextInt(this.map.getDepth() - 1);
                }
                this.addBuildingToMap(new OnMapBuilding(natob, posX, posY, this.map.getTile(posX, posY).height));
            }
        }
    }

    public void addStonesToMap() {
        Random r = new Random();
        for (NatureObject natob : this.nature_objects) {
            if (natob.getName().equals("stone")) {
                System.out.println(natob.getName());
                int posX = r.nextInt(this.map.getWidth() - 4);
                int posY = r.nextInt(this.map.getDepth() - 4);

                while (this.isInMap(posX, posY, natob.getWidth(), natob.getDepth()) && this.isOccupied(posX, posY) && this.isInWater(posX, posY, natob.getWidth(), natob.getDepth())) {
                    posX = r.nextInt(this.map.getWidth() - 1);
                    posY = r.nextInt(this.map.getDepth() - 1);
                }
                this.addBuildingToMap(new OnMapBuilding(natob, posX, posY, this.map.getTile(posX, posY).height));
            }
        }
    }


    public void addRuinsToMap() {
        Random r = new Random();
        for (NatureObject natob : this.nature_objects) {
            if (natob.getName().equals("ruine") || natob.getName().equals("road-ruine") || natob.getName().equals("wohnhaus-ruine")) {
                System.out.println(natob.getName());
                int posX = r.nextInt(this.map.getWidth() - 4);
                int posY = r.nextInt(this.map.getDepth() - 4);

                while (this.isInMap(posX, posY, natob.getWidth(), natob.getDepth()) && this.isOccupied(posX, posY) && !this.isInWater(posX, posY, natob.getWidth(), natob.getDepth())) {
                    posX = r.nextInt(this.map.getWidth() - 1);
                    posY = r.nextInt(this.map.getDepth() - 1);
                }
                this.addBuildingToMap(new OnMapBuilding(natob, posX, posY, this.map.getTile(posX, posY).height));
            }
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
