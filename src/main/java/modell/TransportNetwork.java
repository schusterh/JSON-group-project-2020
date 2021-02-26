package modell;

import java.nio.charset.StandardCharsets;
import java.util.*;

class Station extends Building{
    ArrayList<GoodsBundle> holdingArea;
    String label;
    Factory nearFactory;

    Station(int width, int depth, String name){
        super(width,depth, name);
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
    ArrayList<Station> stations;
    String vehicleType;
    int vehicleAmount;
    ArrayList<Vehicle> vehicles;

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

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public void addStation(Station station){
        this.stations.add(station);
    }

    public void removeStation(Station station){
        this.stations.remove(station);
    }

    public void manageVehicles(){
        if (this.vehicles.size() > vehicleAmount){
            this.vehicles.remove(0);
        }
        if (this.vehicles.size() < vehicleAmount){
            this.vehicles.add(this.vehicles.get(0));
        }
        vehicles.removeIf(v -> !v.getKind().equals(this.getVehicleType()));
    }

}

public class TransportNetwork {
    ArrayList<String> station_names = new ArrayList<>();
    public HashMap<Station, HashMap<Station, ArrayList<Point>>> adjStations;
    public HashMap<Point, ArrayList<Point>> pointConnections;
    public HashMap<ArrayList<Station>,ArrayList<Point>> stationConnections;
    public ArrayList<Point> points;
    public ArrayList<Station> stations;
    public ArrayList<TrafficRoute> trafficRoutes;
    public HashMap<Factory, Station> nearStations;
    public ArrayList<String> signals;
    public ArrayList<ArrayList<String>> railSections;

    public TransportNetwork(HashMap<Station, HashMap<Station, ArrayList<Point>>> adjStations) {
        this.adjStations = adjStations;
    }

    public void addSignal(Double xPos, Double yPos, String signal) {
        //railSections.add()
        this.signals.add(signal);
    }

    public void addTrafficObject(Double xPos, Double yPos, HashMap<String, ArrayList<Double>> newPoints, ArrayList<ArrayList<String>> newConnect) {
        //nur für objekte, die punkte und punktverbindungen auf der karte hinzufügen

        double diff = 0.2;

        for (String name : newPoints.keySet()) {
            Point p = new Point(newPoints.get(name).get(0) + xPos, newPoints.get(name).get(1) + yPos);
            if (points.stream().noneMatch(z -> Math.abs(z.getX()) - Math.abs((p.getX())) <= diff && Math.abs(z.getY()) - Math.abs(p.getY()) <= diff)) {
                points.add(p);
                pointConnections.put(p, new ArrayList<>());
            } else {
                Point similarPoint = points.stream()
                        .findAny()
                        .filter(z -> ((Math.abs(z.getX()) - Math.abs((p.getX()))) <= diff) && ((Math.abs(z.getY()) - Math.abs(p.getY())) <= diff))
                        .orElse(p); // müsste es nicht orElse(z) sein?

            }
            for (ArrayList<String> c : newConnect) {
                if (c.contains(name)) {
                    String connection = String.valueOf(c.stream().filter(x -> !(x.equals(name))));
                    Point connectPoint = new Point(newPoints.get(connection).get(0) + xPos, newPoints.get(connection).get(1) + yPos);
                    if (points.stream().noneMatch(z -> Math.abs(z.getX()) - Math.abs(connectPoint.getX()) <= diff && Math.abs(z.getY()) - Math.abs(connectPoint.getY()) <= diff)) {
                        points.add(connectPoint);
                        pointConnections.put(connectPoint, new ArrayList<>());
                        if (!pointConnections.get(p).contains(connectPoint)) {
                            pointConnections.get(p).add(connectPoint);
                            pointConnections.get(connectPoint).add(p);
                        }
                    }
                }
            }
        }
    }

    public void addStation(Station s) {
        adjStations.putIfAbsent(s, new HashMap<>());
        s.setLabel(stationnameGenerator());
    }

    public void removeStation(Station s) {
        adjStations.values().forEach(e -> e.remove(s));
        adjStations.remove(s);
    }

    public void addConnection(Station s1, Station s2, ArrayList <Point> distance) {
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

    public Station getNearStations(Factory f) {
        return nearStations.get(f);
    }

    HashMap<Station, ArrayList<Point>> getAdjStations(Station s) {
        return adjStations.get(s);
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
