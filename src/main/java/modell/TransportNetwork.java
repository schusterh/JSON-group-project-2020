package modell;

import types.Point;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Stationen haben unbegrenzten Platz für Güter, einen eindeutigen Namen, und können in der Nähe einer Fabrik stehen.
 */
class Station extends Building{
    private ArrayList<GoodsBundle> holdingArea;
    private String label;
    private Factory nearFactory;

    /**
     *
     * @param width
     * @param depth
     * @param name
     */
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
    private ArrayList<Point> points = new ArrayList<>();

    /**
     *
     * @param stations: Stationen auf dieser Route
     * @param vehicleType: Fahrzeugtyp, also Road vehicle, Engine, oder Plane
     * @param vehicleAmount: erlaubte Anzahl an Fahrzeugen auf dieser Strecke
     * @param vehicles: Fahrzeuge auf dieser Strecke
     */
    public TrafficRoute(ArrayList<Station> stations, String vehicleType, int vehicleAmount, ArrayList<Vehicle> vehicles){
        this.stations = stations;
        this.vehicleType = vehicleType;
        this.vehicleAmount = vehicleAmount;
        this.vehicles = vehicles;
    }

    /**
     * @return Stationen auf dieser Route
     */
    public ArrayList<Station> getStations() {
        return stations;
    }

    /**
     * @return Fahrzeugtyp dieser Route
     */
    public String getVehicleType() {
        return vehicleType;
    }

    /**
     * @return Erlaubte Fahrzeugmenge dieser Route
     */
    public int getVehicleAmount() {
        return vehicleAmount;
    }

    /**
     * @return Punkte auf dieser Route
     */
    public ArrayList<Point> getPoints() {
        return points;
    }

    /**
     * Fügt einen Punkt zur Route hinzu
     * @param point
     */
    public void addPoints(Point point){
        this.points.add(point);
    }

    /**
     * @return: Alle Fahrzeuge auf dieser Route
     */
    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * @param v: Zu löschendes Fahrzeug
     */
    public void removeVehicle(Vehicle v){
        this.vehicles.remove(v);
    }

    /**
     * @param amount: Anzahl der zu löschenden Fahrzeuge
     */
    public void removeVehicleAmount(int amount){
        if (amount > 0) this.vehicles.subList(0, amount).clear();
    }

    /**
     * Fügt ein Fahrzeug zur Route hinzu
     * @param v: Fahrzeug
     */
    public void addVehicle(Vehicle v){
        this.vehicles.add(v);
    }

    /**
     * Fügt eine Station zur Route hinzu
     * @param station:
     */
    public void addStation(Station station){

        this.stations.add(station);
    }

    /**
     * Entfernt eine Station aus der Route
     * @param station
     */
    public void removeStation(Station station){
        this.stations.remove(station);
    }


}

/**
 * RailSection (Schienenblöcke) sind eine Reihe von Schienen (Railway), die durch zwei Signale abgeteilt sind.
 */
class RailSection {
    private String signal1;
    private String signal2;
    private ArrayList<Point> between;

    /**
     *
     * @param signal1
     * @param signal2
     * @param between: Punkte zwischen diesen beiden Signalen
     */
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

/**
 * Das Transportnetzwerk besteht aus Routen, die Stationen miteinander verbinden, und dem Graphen der Punkte, die von
 * den verschiedenen Buildings erstellt werden.
 */
public class TransportNetwork {

    ArrayList<String> station_names = new ArrayList<>();
    private HashMap<Point, ArrayList<Point>> pointConnections;
    private HashMap<Station,ArrayList<Point>> stations;
    private HashMap<TrafficRoute,ArrayList<Point>> trafficRoutes;
    private HashMap<TrafficRoute,ArrayList<RailSection>> railSections;
    private HashMap<Factory, Station> nearStations;
    private ArrayList<String> signals;
    private Tower tower;
    private ArrayList<Vehicle> vehicles;
    private HashMap<Point,HashMap<Integer,Vehicle>> reservations;

    public TransportNetwork() {

        this.pointConnections = new HashMap<>();
        this.stations = new HashMap<>();;
        this.trafficRoutes = new HashMap<>();
        this.nearStations = new HashMap<>();
        this.signals = new ArrayList<>();
        this.railSections = new HashMap<>();
        this.vehicles = new ArrayList<>();
        this.reservations = new HashMap<>();
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * Fügt ein Signal zu einer Schienenroute hinzu.
     * @param point: Koordinate des Signals
     * @param signal: Name des Signals
     */
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

    /**
     * Entfernt ein Signal aus einer Schienenroute
     * @param point: Koordinate des Signals
     * @param signal: Name des Signals
     */
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

    /**
     * Fügt Road, Vehicle oder AirportObject zu dem Netzwerk hinzu.
     * @param xPos: x-Koordinate
     * @param yPos: y-Koordinate
     * @param newPoints: neue Punkte, die durch dieses Objekt zu dem Netzwerk hinzugefügt werden.
     * @param newConnect: neue Punktverbindungen, die durch dieses Objekt zu dem Netzwerk hinzugefügt werden.
     * @param type: Typ des Objekts (Road, Vehicle oder AirportObject)
     */

    public void addTrafficSection(Double xPos, Double yPos, HashMap<String, ArrayList<Double>> newPoints, ArrayList<ArrayList<String>> newConnect, Class type) {
        //nur für objekte, die punkte und punktverbindungen auf der karte hinzufügenw
        boolean addedToRoute = false;
        while (!addedToRoute) {
            for (Station s : stations.keySet()) {
                for (Point p : stations.get(s)) {
                    if (Math.abs(p.getX() - xPos) <= 3 && (p.getY() - yPos) <= 3) {
                        boolean routeExists = false;
                        for (TrafficRoute trafficRoute : trafficRoutes.keySet()) {
                            if (trafficRoute.getStations().contains(s)) {
                                routeExists = true;
                                for (String point : newPoints.keySet()) {
                                    Point newPoint = new Point(newPoints.get(point).get(0)+xPos, newPoints.get(point).get(1)+yPos);
                                    trafficRoute.addPoints(newPoint);
                                    System.out.println("New Point added to Route!");
                                    addedToRoute = true;
                                }
                            }
                        }
                        if (trafficRoutes.isEmpty() || !routeExists){
                            String vehicleType = "";
                            if (type.getName().equals("modell.Road")) {
                                vehicleType = "road vehicle";
                            }
                            if (type.getName().equals("modell.Railway")) {
                                vehicleType = "engine";
                            }
                            if (type.getName().equals("modell.AirportObject")) {
                                vehicleType = "plane";
                            }

                            TrafficRoute newRoute = new TrafficRoute(new ArrayList<Station>(), vehicleType, 1, new ArrayList<Vehicle>());
                            newRoute.addStation(s);
                            trafficRoutes.put(newRoute,new ArrayList<>());
                            for (String point : newPoints.keySet()) {
                                trafficRoutes.get(newRoute).add(new Point(newPoints.get(point).get(0)+xPos,
                                        newPoints.get(point).get(1)+yPos));
                            }
                            System.out.println("New Route!");
                            addedToRoute = true;
                        }
                    }

                }

            }
        }
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

    /**
     * Fügt eine Station an einer Koordinate hinzu
     * @param s: Station
     * @param x: x-Koordinate
     * @param y: y-Koordinate
     */

    public void addStation(Station s, double x, double y) {
        s.setLabel(stationnameGenerator());
        stations.put(s,new ArrayList<Point>());
        stations.get(s).add(new Point(x,y));

        for (TrafficRoute route : trafficRoutes.keySet()){
            for (Point point : route.getPoints()){
                if (Math.abs(point.getX()-x) <= 1.5 && (Math.abs(point.getY()-y) <= 1.5 )){
                    System.out.println("found near station");
                    route.addStation(s);
                    System.out.println("New Station added to route");
                    break;
                }
            }
        }
    }

    /**
     * Löscht eine Station aus dem Netzwerk
     * @param s: Station
     */
    public void removeStation(Station s) {
        //adjStations.values().forEach(e -> e.remove(s));
        //adjStations.remove(s);
        for (TrafficRoute route : trafficRoutes.keySet() ) {
            for (Station station : route.getStations()){
                if (station == s){
                    route.removeStation(s);
                }
            }
        }
    }


    /*public void addConnection(Station s1, Station s2, ArrayList <Point> distance,Building connectionType) throws Exception {
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

    }

     */


    /**
     * @return Alle Stationen mit den dazugehörigen Punkten
     */
    public HashMap<Station, ArrayList<Point>> getStationPoints() {
        return stations;
    }

    /**
     * @param f: Fabrik
     * @return Station, die nahe an dieser Fabrik gebaut ist
     */
    public Station getNearStations(Factory f) {
        return nearStations.get(f);
    }

    public void setNearStation(Factory f, Station s) {
        nearStations.put(f, s);
    }

    public Optional<Station> getStationAtPoint(Point point) {
        for (Station s : stations.keySet()) {
            for (Point p : stations.get(s)) {
                if (Math.abs(p.getX() - point.getX()) <= 1.5 && Math.abs(p.getY() - point.getY()) <= 1.5) {
                    return Optional.of(s);
                }
            }
        }
        return Optional.empty();
    }
    /**
     * @return Graph aus Punkten mit direkten Nachbarn
     */
    public HashMap<Point, ArrayList<Point>> getPointConnections() {
        return pointConnections;
    }

    /**
     * @return Die Schienenblöcke
     */
    public HashMap<TrafficRoute, ArrayList<RailSection>> getRailSections() {
        return railSections;
    }

    /**
     * @return Die Reservierungen von Punkten zu bestimmten Ticks von bestimmten Fahrzeugen
     */
    public HashMap<Point, HashMap<Integer, Vehicle>> getReservations() {
        return reservations;
    }

    /**
     * @return Die Routen mit den dazugehörigen Punkten
     */
    public HashMap<TrafficRoute, ArrayList<Point>> getTrafficRoutes() {
        return trafficRoutes;
    }

    /**
     * Berechnet, ob zwei Punkte gleich sind
     * @param p1: Punkt 1
     * @param p2: Punkt 2
     * @return true oder false
     */
    public boolean equalPoints(Point p1, Point p2){
        final double DIFF = 0.0001;
        return Math.abs(p1.getX() - p2.getX()) <= DIFF && Math.abs(p1.getY() - p2.getY()) <= DIFF;
    }

    /**
     * Reserviert einen Punkt
     * @param point: Punkt
     * @param tick: Zeitpunkt
     * @param vehicle: Fahrzeug
     */
    public void addReservations(Point point, Integer tick, Vehicle vehicle) {
        if (!this.reservations.containsKey(point)) {
            this.reservations.put(point, new HashMap<>());
        }
        this.reservations.get(point).put(tick,vehicle);
    }


    /**
     * @return Generierter einzigartiger Name für eine Station
     */
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
