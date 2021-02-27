package modell;

import java.nio.charset.StandardCharsets;
import java.util.*;

class Station extends Building{
    private ArrayList<GoodsBundle> holdingArea;
    private String label;
    private Factory nearFactory;

    Station(int width, int depth, String name){
        super(width,depth, name);
        this.holdingArea = new ArrayList<>();
    }

    public ArrayList<GoodsBundle> getHoldingArea() {
        return holdingArea;
    }

    public String getLabel() {
        return label;
    }

    public void addGoods(GoodsBundle gb){
        this.holdingArea.add(gb);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Factory getNearFactory() {
        return nearFactory;
    }

    public void setNearFactory(Factory nearFactory) {
        this.nearFactory = nearFactory;
    }
}

class Point {
    Double x;
    Double y;
    Point (Double x, Double y){
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }
}

/*
Routen verbinden mehrere Stationen miteinander, beginnen und enden an Stationen und haben
festgelegte Verkehrsmittel. Sie müssen nicht zwangsweise Fabriken verbinden.
Wenn die Anzahl oder der Typ von Fahrzeugen nicht stimmt, werden diese gelöscht.
 */
class TrafficRoute {
    private ArrayList<Station> stations;
    private String vehicleType;
    private int vehicleAmount;
    private ArrayList<Vehicle> vehicles;
    private ArrayList<Point> points;


    public TrafficRoute(ArrayList<Station> stations, String vehicleType, int vehicleAmount, ArrayList<Vehicle> vehicles){
        this.stations = stations;
        this.vehicleType = vehicleType;
        this.vehicleAmount = vehicleAmount;
        this.vehicles = vehicles;
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public int getVehicleAmount() {
        return vehicleAmount;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }
    public void removeVehicle(Vehicle v){
        this.vehicles.remove(v);
    }
    public void removeVehicleAmount(int amount){
        if (amount > 0) {
            this.vehicles.subList(0, amount).clear();
        }
    }
    public void addVehicle(Vehicle v){
        this.vehicles.add(v);
    }

    public void addStation(Station station){
        this.stations.add(station);
    }

    public void removeStation(Station station){
        this.stations.remove(station);
    }


}
class RailSection {
    private String signal1;
    private String signal2;
    private ArrayList<Point> between;
    public RailSection(String signal1, String signal2,ArrayList<Point> between){
        this.signal1 = signal1;
        this.signal2 = signal2;
        this.between = between;
    }

    public ArrayList<Point> getBetween() {
        return between;
    }
    

    public String getSignal1() {
        return signal1;
    }

    public String getSignal2() {
        return signal2;
    }
}

public class TransportNetwork {
    ArrayList<String> station_names = new ArrayList<>();
    private HashMap<Station, HashMap<Station, ArrayList<Point>>> adjStations;
    private HashMap<Point, ArrayList<Point>> pointConnections;
    private HashMap<Station,ArrayList<Point>> stationPoints;
    private ArrayList<Point> points;
    private ArrayList<Station> stations;
    private HashMap<TrafficRoute,ArrayList<Point>> trafficRoutes;
    private HashMap<Factory, Station> nearStations;
    private ArrayList<String> signals;
    private HashMap<TrafficRoute,ArrayList<RailSection>> railSections;
    private Tower tower;
    private ArrayList<Vehicle> vehicles;
    private HashMap<Point,HashMap<Integer,Vehicle>> reservations;

    public TransportNetwork() {
        this.adjStations = new HashMap<>();
        this.pointConnections = new HashMap<>();
        this.stationPoints = new HashMap<>();
        this.points = new ArrayList<>();
        this.stations = new ArrayList<>();
        this.trafficRoutes = new HashMap<>();
        this.nearStations = new HashMap<>();
        this.signals = new ArrayList<>();
        this.railSections = new HashMap<>();
        this.vehicles = new ArrayList<>();
        this.reservations = new HashMap<>();
    }

    public void addSignal(Point point, String signal) {
        signals.add(signal);
        for (TrafficRoute tr : trafficRoutes.keySet()){
            if (tr.getPoints().contains(point)){
                int signalPos = tr.getPoints().indexOf(point);
                if (railSections.containsKey(tr)) {
                    for (RailSection rs : railSections.get(tr)){
                        if (rs.getBetween().contains(point)) {
                            RailSection rs1 = new RailSection(rs.getSignal1(), signal,
                                    new ArrayList<>(rs.getBetween().subList(0, signalPos)));
                            RailSection rs2 = new RailSection(signal, rs.getSignal2(),
                                    new ArrayList<>(rs.getBetween().subList(signalPos, rs.getBetween().size() - 1)));
                            railSections.get(tr).add(rs1);
                            railSections.get(tr).add(rs2);
                            railSections.get(tr).remove(rs);
                        }
                    }   
                }
            }
        }
    }
    public void removeSignal (Point point, String signal) {
        ArrayList<RailSection> connecting = new ArrayList<>();
        TrafficRoute tr = null;
        RailSection newSection;
        for (TrafficRoute trafficRoute : railSections.keySet()) {
            for (RailSection rs : railSections.get(trafficRoute)) {
                if (rs.getBetween().contains(point)) {
                    connecting.add(rs);
                    tr = trafficRoute;
                }
            }
        }
        if (connecting.size() == 2) {
            for (RailSection connect : connecting){
                if (connect.getBetween().get(0).equals(point)){
                    ArrayList <Point> points = new ArrayList<>();
                    points.addAll(connecting.get(1).getBetween());
                    points.addAll(connecting.get(0).getBetween());
                    newSection = new RailSection(connecting.get(1).getSignal1(),
                            connecting.get(0).getSignal2(),points);
                }

            }
            railSections.remove(tr,connecting.get(0));
            
            
        }
    }

    public void addTrafficSection(Double xPos, Double yPos, HashMap<String, ArrayList<Double>> newPoints, ArrayList<ArrayList<String>> newConnect) {
        //nur für objekte, die punkte und punktverbindungen auf der karte hinzufügen

        HashMap<Point,ArrayList<String>> connections = new HashMap<>();
        for (String name : newPoints.keySet()) {
            Point p = new Point(newPoints.get(name).get(0) + xPos, newPoints.get(name).get(1) + yPos);

            connections.put(p,new ArrayList<>());

            for (ArrayList<String> c : newConnect){
                if (c.contains(name)) {
                    for (String s : c){
                        if (!s.equals(name)) {
                            connections.get(p).add(s);
                        }
                    }
                }
            }
        }
        for (Point point : connections.keySet()){
            ArrayList<Point> connectingPoints = new ArrayList<>();
            for (String s : connections.get(point)) {
                connectingPoints.add(new Point(newPoints.get(s).get(0),newPoints.get(s).get(1)));
            }
            boolean duplicate = false;
            if (!pointConnections.isEmpty()) {
                for (Point p : pointConnections.keySet()) {
                    if (equalPoints(point, p)) {
                        duplicate = true;
                        for (Point c : connectingPoints) {
                            pointConnections.get(p).add(c);
                        }
                    }
                }
            }
            if (!duplicate){
                pointConnections.put(point,connectingPoints);
            }
        }
    }

    public void addStation(Station s, double x, double y) {
        adjStations.putIfAbsent(s, new HashMap<>());
        s.setLabel(stationnameGenerator());
        for (TrafficRoute route : trafficRoutes.keySet()){
            for (Point point : route.getPoints()){
                if (Math.abs(point.getX()-x) <= 1.5 && (Math.abs(point.getY()-y) <= 1.5 )){
                    route.addStation(s);
                }
            }
        }
    }

    public void removeStation(Station s) {
        adjStations.values().forEach(e -> e.remove(s));
        adjStations.remove(s);
    }


    public void addConnection(Station s1, Station s2, ArrayList <Point> distance,Building connectionType) throws Exception {
        ArrayList<TrafficRoute> routeCombine = new ArrayList<>();
        for (TrafficRoute trafficRoute : trafficRoutes.keySet()){

            if (trafficRoute.getStations().contains(s1) || trafficRoute.getStations().contains(s2)){
                routeCombine.add(trafficRoute);
            }
        }
        if (!routeCombine.isEmpty()) {
            String vehicleType = routeCombine.get(0).getVehicleType();
            ArrayList<Station> stationsCombine = new ArrayList<>();
            ArrayList<Vehicle> vehiclesCombine = new ArrayList<>();
            int vehicleAmount = 0;
            ArrayList<Point> pointsCombine = new ArrayList<>();

            for (TrafficRoute route : routeCombine) {
                if (!route.getVehicleType().equals(vehicleType)) {
                    throw new Exception("this route does not have a consistent vehicle type");
                }
                stationsCombine.addAll(route.getStations());
                vehiclesCombine.addAll(route.getVehicles());
                pointsCombine.addAll(trafficRoutes.get(route));
                vehicleAmount =+ route.getVehicleAmount();
                trafficRoutes.remove(route);
            }

            TrafficRoute combined = new TrafficRoute(stationsCombine, vehicleType, vehicleAmount, vehiclesCombine);
            trafficRoutes.put(combined,pointsCombine);
        } else {
            ArrayList<Station> newStations = new ArrayList<>();
            newStations.add(s1);
            newStations.add(s2);
            String vehicleType = "";
            int vehicleAmount = 0;
            Class c = connectionType.getClass();
            if (c.getName().equals("Road")){
                vehicleType = "road vehicle";
                vehicleAmount = distance.size()/10;
            } if (c.getName().equals("Railway")){
                vehicleType = "engine";
                vehicleAmount = distance.size()/20;
            } if (c.getName().equals("AirportObject")){
                vehicleType = "plane";
                vehicleAmount = this.tower.getMaxplanes();
            }
            TrafficRoute newRoute = new TrafficRoute(newStations,vehicleType,vehicleAmount,new ArrayList<>());

        }

        adjStations.get(s1).put(s2, distance);
        adjStations.get(s2).put(s1, distance);
    }

    public void removeConnection(Station s1, Station s2) {
        HashMap<Station, ArrayList<Point>> connectS1 = adjStations.get(s1);
        HashMap<Station, ArrayList<Point>> connectS2 = adjStations.get(s2);
        if (connectS1 != null) {
            connectS1.remove(s2);
        }
        if (connectS2 != null) {
            connectS2.remove(s1);
        }
    }

    public HashMap<Station, ArrayList<Point>> getStationPoints() {
        return stationPoints;
    }

    public boolean equalPoints(Point p1, Point p2){
        double diff = 0.2;
        return Math.abs(p1.getX() - p2.getX()) <= diff && Math.abs(p1.getY() - p2.getY()) <= diff;
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }

    public Station getNearStations(Factory f) {
        return nearStations.get(f);
    }

    HashMap<Station, ArrayList<Point>> getAdjStations(Station s) {
        return adjStations.get(s);
    }

    public HashMap<Point, ArrayList<Point>> getPointConnections() {
        return pointConnections;
    }

    public void addReservations(Point point, Integer tick, Vehicle vehicle) {
        if (!this.reservations.containsKey(point)) {
            this.reservations.put(point, new HashMap<>());
        }
        this.reservations.get(point).put(tick,vehicle);
    }

    public HashMap<TrafficRoute, ArrayList<RailSection>> getRailSections() {
        return railSections;
    }

    public HashMap<Point, HashMap<Integer, Vehicle>> getReservations() {
        return reservations;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public HashMap<TrafficRoute, ArrayList<Point>> getTrafficRoutes() {
        return trafficRoutes;
    }

    public String stationnameGenerator() {
        String generatedString = null;
        boolean notGenerated = true;
        while (notGenerated) {
            byte[] array = new byte[3];
            new Random().nextBytes(array);
            generatedString = new String(array, StandardCharsets.UTF_8);
            if (!station_names.contains(generatedString)) {
                station_names.add(generatedString);
                notGenerated = false;
            }
        }
        return generatedString;
    }
}
