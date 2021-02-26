package modell;

import types.OnMapBuilding;
import ui.tiles.BuildingLayer;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

enum OpposingSides {
    NW("se"),
    SW("ne"),
    SE("nw"),
    NE("sw");

    public final String label;

    private OpposingSides(String label) {
        this.label = label;
    }
}

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
    private HashMap<String, String> roadExtensions;

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
        this.transportNetwork = new TransportNetwork(new HashMap<Station, HashMap<Station, Integer>>());
        this.addFactoriesToMap();
        this.roadExtensions = new HashMap<>();
        this.roadExtensions.put("road-sw", "road-ne-sw");
        this.roadExtensions.put("road-ne", "road-ne-sw");
        this.roadExtensions.put("road-nw", "road-nw-se");
        this.roadExtensions.put("road-se", "road-nw-se");
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

    public Factory findTarget(Factory f, String commodity){
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
        return possTargets.get(randomIndex);
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

        if (pendingBuilding.model.getClass() == Road.class) {
            Road roadModel = (Road) pendingBuilding.model;

            roadModel.getCombines().ifPresent(combines -> {
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
            });

            //this.transportNetwork.addTrafficSection((double) pendingBuilding.startX, (double) pendingBuilding.startY, roadModel.getPoints(), roadModel.getRoads());
            System.out.println("Added road to transport network!");
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
        ArrayList<OnMapBuilding> adjBuildings = this.getAdjBuildings(newRoad);
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
