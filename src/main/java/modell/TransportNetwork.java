package modell;

import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Stream;

class Station {
    HashMap<String,Integer> holdingArea;
    String label;
    Station(String label){
        this.label = label;
        this.holdingArea = new HashMap<>();
    }

    public HashMap<String, Integer> getHoldingArea() {
        return holdingArea;
    }

    public String getLabel() {
        return label;
    }

    public void setHoldingArea(HashMap<String, Integer> holdingArea) {
        this.holdingArea = holdingArea;
    }

    public void setLabel(String label) {
        this.label = label;
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
            this.vehicles.remove(-1);
        }
        if (this.vehicles.size() < vehicleAmount){
            this.vehicles.add(this.vehicles.get(-1));
        }
        vehicles.removeIf(v -> !v.getKind().equals(this.getVehicleType()));
    }
}

public class TransportNetwork {
    ArrayList<String> station_names = new ArrayList<>();
    private HashMap<Station, HashMap<Station,Integer>> adjStations;
    public HashMap<Point, ArrayList<Point>> connections;
    public ArrayList<Point> points;

    public TransportNetwork(HashMap<Station, HashMap<Station,Integer>> adjStations){
        this.adjStations = adjStations;
    }

    public void addBuild(Double xPos, Double yPos, HashMap<String,ArrayList<Double>> newPoints, ArrayList<ArrayList<String>> newConnect){
        Double diff = 0.2;
        for (String name : newPoints.keySet()){
            Point p = new Point (newPoints.get(name).get(0)+xPos, newPoints.get(name).get(1)+yPos);
            if (points.stream().noneMatch(z -> Math.abs(z.getX()) - Math.abs((p.getX())) <= diff && Math.abs(z.getY()) - Math.abs(p.getY()) <= diff)) {
                points.add(p);
                connections.put(p, new ArrayList<>());
            }
            for (ArrayList<String> c : newConnect){
                if (c.contains(name)){
                    String connection = String.valueOf(c.stream().filter(x -> !(x.equals(name))));
                    Point connectPoint = new Point(newPoints.get(connection).get(0)+xPos,newPoints.get(connection).get(1)+yPos);
                    if (points.stream().noneMatch(z -> Math.abs(z.getX()) - Math.abs(connectPoint.getX()) <= diff && Math.abs(z.getY()) - Math.abs(connectPoint.getY()) <= diff)){
                        points.add(connectPoint);
                        connections.put(connectPoint,new ArrayList<>());
                        if (!connections.get(p).contains(connectPoint)){
                            connections.get(p).add(connectPoint);
                            connections.get(connectPoint).add(p);
                        }
                    }
                }
            }
        }
    }

    public HashMap<Station, HashMap<Station,Integer>> getAdjStations() {
        return adjStations;
    }

    public void addStation(String label){
        adjStations.putIfAbsent(new Station(label),new HashMap<>());
    }

    public void removeStation(String label){
        Station s = new Station(label);
        adjStations.values().forEach(e -> e.remove(s));
        adjStations.remove(new Station(label));
    }

    public void addConnection(String label1, String label2, Integer distance){
        Station s1 = new Station(label1);
        Station s2 = new Station(label2);
        adjStations.get(s1).put(s2,distance);
        adjStations.get(s2).put(s1,distance);
    }

    public void removeConnection(String label1, String label2){
        Station s1 = new Station(label1);
        Station s2 = new Station(label2);
        HashMap<Station,Integer> connectS1 = adjStations.get(s1);
        HashMap<Station,Integer> connectS2 = adjStations.get(s2);
        if (connectS1 != null){
            connectS1.remove(s2);
        }
        if (connectS2 != null){
            connectS2.remove(s1);
        }
    }

    HashMap<Station, Integer> getAdjStations(String label){
        return adjStations.get(new Station(label));
    }

    public String stationname_generator() {
        String generatedString = null;
        boolean notGenerated = true;
        while (notGenerated) {
            byte[] array = new byte[3];
            new Random().nextBytes(array);
            generatedString = new String(array, Charset.forName("UTF-8"));
            if (!station_names.contains(generatedString)) {
                station_names.add(generatedString);
                return generatedString;
            }
        }
        return generatedString;
    }


}