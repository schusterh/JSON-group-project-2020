package modell;

import types.OnMapBuilding;
import types.Point;

import java.util.*;
import java.util.stream.Collectors;


/**
 * This class represents the acutal game with all its components.
 * Most of the objects that can be seen in the game are stored in arraylists of a specific type.
 */
public class Game {

    private ArrayList<Vehicle> vehicles;
    private ArrayList<Vehicle> vehiclesOnMap = new ArrayList<>();
    private ArrayList<Road> roads;
    private ArrayList<Railway> railways;
    private ArrayList<Factory> factories;
    private ArrayList<Factory> factoriesOnMap = new ArrayList<>();
    private ArrayList<String> commodities;
    private ArrayList<NatureObject> nature_objects ;
    private ArrayList<Tower> towers;
    private ArrayList<AirportObject> airport_objects;
    private List<OnMapBuilding> buildingsOnMap;
    private Map map;
    private ArrayList<String> music;
    private TransportNetwork transportNetwork;
    private HashMap<String, int[]> directions;

    /**
     * The Current mouse tile index.
     */
    int[] currentMouseTileIndex;

    /**
     * Instantiates a new Game.
     *
     * @param commodities     the commodities
     * @param roads           the roads
     * @param railways        the railways
     * @param towers          the towers
     * @param airport_objects the airport objects
     * @param nature_objects  the nature objects
     * @param factories       the factories
     * @param vehicles        the vehicles
     * @param map             the map
     * @param music           the music
     */
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


        // the amount of trees,stones and ruins that will be randomly placed on the map
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

    /**
     * Gets railways.
     *
     * @return the railways
     */
    public ArrayList<Railway> getRailways() {
        return railways;
    }

    /**
     * Gets factories.
     *
     * @return the factories
     */
    public ArrayList<Factory> getFactories() {
        return factories;
    }

    /**
     * Gets roads.
     *
     * @return the roads
     */
    public ArrayList<Road> getRoads() {
        return roads;
    }

    /**
     * Gets nature objects.
     *
     * @return the nature objects
     */
    public ArrayList<NatureObject> getNatureObjects() {
        return nature_objects;
    }

    /**
     * Gets commodities.
     *
     * @return the commodities
     */
    public ArrayList<String> getCommodities() {
        return commodities;
    }

    /**
     * Gets vehicles.
     *
     * @return the vehicles
     */
    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * Gets airport objects.
     *
     * @return the airport objects
     */
    public ArrayList<AirportObject> getAirportObjects() {
        return airport_objects;
    }

    /**
     * Gets towers.
     *
     * @return the towers
     */
    public ArrayList<Tower> getTowers() {
        return towers;
    }

    /**
     * Gets buildings on map.
     *
     * @return the buildings on map
     */
    public List<OnMapBuilding> getBuildingsOnMap() { return buildingsOnMap; }

    /**
     * Ein Fahrzeug fährt jeden Tick einen Punkt weiter
     * @param v: Fahrzeug
     * @param tick: Tick
     */
    public void drive (Vehicle v, int tick){
        if (v.getPath()!=null) {
            if (transportNetwork.getPointConnections().containsKey(v.getNextPoint())){
                v.setCurrentPoint(v.getNextPoint());
                v.setNextPoint(v.getPath().get(0));
                v.getPath().remove(0);
            } else {
                findPath(v,tick);
                drive(v,tick);
            }
        } else {
            if (!v.getCurrentCargo().isEmpty()) {
                v.unloadCargo(v.getCurrentCargo().get(0));
                findPath(v, tick);
            }
        }
    }

    /**
     * Ein Güterpaket sucht sich von seiner ursprünglichen Fabrik aus ein Ziel
     * @param gb: Güterpaket
     * @param station: Ursprungs-Station
     */
    public void findTarget(GoodsBundle gb, Station station){
        HashMap<Factory, Double> possibleTargets = new HashMap<>();
        ArrayList<Factory> possTargets = new ArrayList<>();
        for (Factory g : this.getFactories()) {
            g.getProductions().stream().filter(p -> p.getConsume()
                    .isPresent()).filter(p -> p.getConsume().get()
                    .containsKey(gb.getGoodType()))
                    .forEach(p -> possibleTargets.put(g, null));
            Station nearG = transportNetwork.getNearStations(g);
            double distance = Math.sqrt((transportNetwork.getStationPoints().get(nearG).get(0).getX()
                    - transportNetwork.getStationPoints().get(station).get(0).getX())
                    * (transportNetwork.getStationPoints().get(nearG).get(0).getX()
                    - transportNetwork.getStationPoints().get(station).get(0).getX())
                    + (transportNetwork.getStationPoints().get(nearG).get(0).getY()
                    - transportNetwork.getStationPoints().get(station).get(0).getY())
                    * (transportNetwork.getStationPoints().get(nearG).get(0).getY()
                    - transportNetwork.getStationPoints().get(station).get(0).getY()
                    ));
            double weight = (double) (g.getStorage().get(gb.getGoodType())
                    - g.getCurrentStorage().get(gb.getGoodType()))
                    / distance;
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

    /**
     * Breitensuche über die Punkte, die das Netzwerk bilden, evtl unter Berücksichtigung der Reservierungen
     * @param startPoint: Startpunkt
     * @param targetStation: Zielfabrik
     * @param vehicleType: Verkehrstyp
     * @param startTime: Starttick
     * @return kürzester Weg von startPoint zu targetStation
     */
    public ArrayList<Point> bfs (Point startPoint, Station targetStation, String vehicleType, int startTime ){

        ArrayList<Point> shortestPath = new ArrayList<>();
        Deque<Point> deq = new ArrayDeque<>();

        HashMap<Point, Point> origins = new HashMap<>();

        deq.add(startPoint);
        boolean targetFound = false;
        while (!targetFound){
            if (deq.peek() != null) {

                Point currentPoint = deq.peekFirst();

                ArrayList<Point> nextPoints = transportNetwork.getPointConnections().get(currentPoint);

                int depth=0;
                Point point = deq.peekFirst();
                while (origins.containsKey(point)){
                    point = origins.get(point);
                    depth++;
                }
                for (Point p : nextPoints) {
                    if (vehicleType.equals("plane")){
                        if (!transportNetwork.getReservations().get(p).containsKey(depth+startTime)){
                            origins.put(p,currentPoint);
                            deq.add(p);

                            if (transportNetwork.getStationPoints().get(targetStation).contains(p)){
                                targetFound = true;
                                shortestPath.add(p);
                                Point backtrack = origins.get(p);
                            }
                        }
                    }
                    if (vehicleType.equals("engine")){
                        for (TrafficRoute tf : transportNetwork.getRailSections().keySet()){
                            for (RailSection rs : transportNetwork.getRailSections().get(tf)) {
                                if (rs.getBetween().contains(p)){
                                    for (Point b : rs.getBetween()) {
                                        if (!transportNetwork.getReservations().get(b).containsKey(depth+startTime)){
                                            origins.put(p,currentPoint);
                                            deq.add(p);
                                            if (transportNetwork.getStationPoints().get(targetStation).contains(p)){
                                                targetFound = true;
                                                shortestPath.add(p);
                                                Point backtrack = origins.get(p);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        origins.put(p, currentPoint);
                        deq.add(p);
                        if (transportNetwork.getStationPoints().get(targetStation).contains(p)) {
                            targetFound = true;
                            shortestPath.add(p);
                            Point backtrack = origins.get(p);
                        }
                    }

                }
                deq.remove(currentPoint);

            } else {
                throw new IllegalArgumentException("No connecting path can be found.");
            }
        }
        Point backtrack = shortestPath.get(0);
        while (backtrack != null) {
            shortestPath.add(0, backtrack);
            backtrack = origins.get(backtrack);
        }
        return shortestPath;
    }

    /**
     * Fahrzeuge suchen sich den Weg für einen Teil ihres Cargos.
     * @param v: Fahrzeug
     * @param startTick: Startzeitpunkt
     * @return
     */
    public ArrayList<Point> findPath(Vehicle v, int startTick){
        if (!v.getCurrentCargo().isEmpty()) {
            GoodsBundle goodsBundle = v.getCurrentCargo().get(0);
            ArrayList<Point> path = new ArrayList<>();

            if (v.getKind().equals("road vehicle")) {
                path.addAll(bfs(v.getCurrentPoint(), goodsBundle.getTargetStation(), v.getKind(), startTick));
            }

            if (v.getKind().equals("plane")) {
                path.addAll(bfs(v.getCurrentPoint(), goodsBundle.getTargetStation(), v.getKind(), startTick));

                for (int i = 0; i < path.size(); i++) {
                    transportNetwork.addReservations(path.get(i), startTick + i, v);
                }
            }
            if (v.getKind().equals("engine")) {
                path.addAll(bfs(v.getCurrentPoint(), goodsBundle.getTargetStation(), v.getKind(), startTick));

                for (int i = 0; i < path.size(); i++) {
                    transportNetwork.addReservations(path.get(i), startTick + i, v);
                }
            }
            return path;
        }
        else return new ArrayList<>();


    }

    /**
     * Löscht Fahrzeuge, wenn es auf der Route zu viele gibt, oder wenn sie dem Typ der Route nicht entsprechen.
     * Fügt Fahrzeuge hinzu, wenn es auf der Route zu wenige gibt.
     * @param route
     */
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
                Vehicle vehicle = possVehicles.get(rand.nextInt(possVehicles.size()));
                route.addVehicle(vehicle);
                vehiclesOnMap.add(vehicle);
                vehicle.setCurrentPoint(transportNetwork.getStationPoints().get(route.getStations().get(0)).get(0));


            }
        }
        for (Vehicle v : route.getVehicles()){
            if (!v.getKind().equals(route.getVehicleType())){
                route.removeVehicle(v);
                vehiclesOnMap.remove(v);
                manageVehicles(route);

            }
        }
    }

    /**
     * Add building to map.
     *
     * @param model  the model
     * @param startX the start x
     * @param startY the start y
     * @param height the height
     */
    public void addBuildingToMap(Building model, int startX, int startY, int height) {

        this.map.plainGround(startX, startY, model.getWidth(), model.getDepth(), height, model.getClass() != Road.class);
        this.buildingsOnMap.add(new OnMapBuilding(model, startX, startY, height));
        this.sortBuildings();
    }

    /**
     * Add building to map.
     *
     * @param pendingBuilding the pending building
     * @param isCombination   the is combination
     */
    public void addBuildingToMap(OnMapBuilding pendingBuilding, boolean isCombination) {

        boolean isConcrete = pendingBuilding.model.getClass() != NatureObject.class && pendingBuilding.model.getClass() != Road.class;
        this.map.plainGround(pendingBuilding.startX, pendingBuilding.startY, pendingBuilding.width, pendingBuilding.depth, pendingBuilding.height, isConcrete);

        if (isCombination) {
            Optional<OnMapBuilding> underlyingRoad = getBuildingAtTile(pendingBuilding.startX, pendingBuilding.startY);
            underlyingRoad.ifPresent(buildingsOnMap::remove);
        }
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
                this.transportNetwork.addTrafficSection((double) pendingBuilding.startX, (double) pendingBuilding.startY, roadModel.getPoints(), roadModel.getRoads(),Road.class);
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

    /**
     * Gets building at tile.
     *
     * @param xPos the x pos
     * @param yPos the y pos
     * @return the building at tile
     */
    public Optional<OnMapBuilding> getBuildingAtTile(int xPos, int yPos) {
        for (OnMapBuilding building : this.buildingsOnMap) {
            if (building.startX == xPos && building.startY == yPos) {
                return Optional.of(building);
            }
        }
        return Optional.empty();
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

    /**
     * Tile has building optional.
     *
     * @param xPos the x pos
     * @param yPos the y pos
     * @return the optional
     */
    public Optional<OnMapBuilding> tileHasBuilding(int xPos, int yPos) {
        for (OnMapBuilding building : this.buildingsOnMap) {
            if (building.startX <= xPos && xPos <= building.startX + building.width
                && building.startY <= yPos && yPos <= building.startY + building.depth) {
                return Optional.of(building);
            }
        }
        return Optional.empty();
    }

    /**
     * Gets map.
     *
     * @return the map
     */
    public Map getMap() {
        return map;
    }

    /**
     * Add factories to map.
     */
    public void addFactoriesToMap() {
        Random r = new Random();

        for (Factory factory : this.factories) {
            int posX = r.nextInt(this.map.getWidth()-4);
            int posY = r.nextInt(this.map.getDepth()-4);

            while (!this.isInMap(posX, posY, factory.getWidth(), factory.getDepth()) || this.isOccupied(posX, posY) || this.isInWater(posX, posY, factory.getWidth(), factory.getDepth())) {
                posX = r.nextInt(this.map.getWidth()-1);
                posY = r.nextInt(this.map.getDepth()-1);
            }

            this.addBuildingToMap(new OnMapBuilding(factory, posX, posY, this.map.getTile(posX, posY).height), false);
        }
    }

    /**
     * Add trees to map.
     */
    public void addTreesToMap() {
        Random r = new Random();
        for (NatureObject natob : this.nature_objects) {
            if (natob.getBuildmenu().isPresent()) {
                int posX = r.nextInt(this.map.getWidth() - 1);
                int posY = r.nextInt(this.map.getDepth() - 1);

                while (!this.isInMap(posX, posY, natob.getWidth(), natob.getDepth()) || this.isOccupied(posX, posY) || this.isInWater(posX, posY, natob.getWidth(), natob.getDepth())) {
                    posX = r.nextInt(this.map.getWidth() - 1);
                    posY = r.nextInt(this.map.getDepth() - 1);
                }
                this.addBuildingToMap(new OnMapBuilding(natob, posX, posY, this.map.getTile(posX, posY).height), false);
            }
        }
    }

    /**
     * Add stones to map.
     */
    public void addStonesToMap() {
        Random r = new Random();
        for (NatureObject natob : this.nature_objects) {
            if (natob.getName().equals("stone")) {
                System.out.println(natob.getName());
                int posX = r.nextInt(this.map.getWidth() - natob.getWidth() - 1);
                int posY = r.nextInt(this.map.getDepth() - natob.getDepth() - 1);

                while (!this.isInMap(posX, posY, natob.getWidth(), natob.getDepth()) || this.isOccupied(posX, posY) || this.isInWater(posX, posY, natob.getWidth(), natob.getDepth())) {
                    posX = r.nextInt(this.map.getWidth() - 1);
                    posY = r.nextInt(this.map.getDepth() - 1);
                }
                this.addBuildingToMap(new OnMapBuilding(natob, posX, posY, this.map.getTile(posX, posY).height), false);
            }
        }
    }


    /**
     * Add ruins to map.
     */
    public void addRuinsToMap() {
        Random r = new Random();
        for (NatureObject natob : this.nature_objects) {
            if (natob.getName().equals("ruine") || natob.getName().equals("road-ruine") || natob.getName().equals("wohnhaus-ruine")) {
                System.out.println(natob.getName());
                int posX = r.nextInt(this.map.getWidth() - 4);
                int posY = r.nextInt(this.map.getDepth() - 4);

                while (this.isInMap(posX, posY, natob.getWidth(), natob.getDepth()) && this.isOccupied(posX, posY) && this.isInWater(posX, posY, natob.getWidth(), natob.getDepth())) {
                    posX = r.nextInt(this.map.getWidth() - 1);
                    posY = r.nextInt(this.map.getDepth() - 1);
                }
                this.addBuildingToMap(new OnMapBuilding(natob, posX, posY, this.map.getTile(posX, posY).height), false);
            }
        }
    }


    /**
     * Is occupied boolean.
     *
     * @param x the x
     * @param y the y
     * @return the boolean
     */
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

    /**
     * Is in water boolean.
     *
     * @param x     the x
     * @param y     the y
     * @param width the width
     * @param depth the depth
     * @return the boolean
     */
    public boolean isInWater(int x, int y, int width, int depth) {
        int waterCount = 0;
        for (int xPos = x; xPos < x + width; xPos++) {
            for (int yPos = y; yPos < y+depth; yPos++) {
                if (this.map.getTile(xPos, yPos).height == -1) {
                    waterCount++;
                }
            }
        }
        return waterCount >= 1;
    }

    /**
     * Is in map boolean.
     *
     * @param x     the x
     * @param y     the y
     * @param width the width
     * @param depth the depth
     * @return the boolean
     */
    public boolean isInMap(int x, int y, int width, int depth) {
        return x >= 0 && x + width < this.map.getWidth() - 1 &&
                y >= 0 && y + depth < this.map.getDepth() - 1;
    }

    /**
     * Sets current mouse tile index.
     *
     * @param pos the pos
     */
    public void setCurrentMouseTileIndex(int[] pos) {
        this.currentMouseTileIndex = pos;
    }

    /**
     * Get current mouse tile index int [ ].
     *
     * @return the int [ ]
     */
    public int[] getCurrentMouseTileIndex() { return currentMouseTileIndex; }

    /**
     * Gets transport network.
     *
     * @return the transport network
     */
    public TransportNetwork getTransportNetwork() {
        return transportNetwork;
    }

    /**
     * Gets background music.
     *
     * @return the background music
     */
    public String getBackgroundMusic() {
        return this.music.get(0);
    }

    public String getMenuMusic() {
        return this.music.get(1);
    }

    public void handleUpdate(int tick) {
        if (!factoriesOnMap.isEmpty()) {
            for (Factory factory : factoriesOnMap) {
                factory.produce(transportNetwork.getNearStations(factory));
            }
        }
        for (TrafficRoute trafficRoute : transportNetwork.getTrafficRoutes().keySet()){
            manageVehicles(trafficRoute);
        }
        if (!vehiclesOnMap.isEmpty()) {
            for (Vehicle v : vehiclesOnMap) {
                drive(v, tick);

            }
        }
        for (Station station:transportNetwork.getStationPoints().keySet()) {
            for (GoodsBundle goodsBundle : station.getHoldingArea()){
                if (goodsBundle.getTargetStation() == null){
                    findTarget(goodsBundle,station);
                }
            }
        }
        System.out.println("läuft");
    }
}
