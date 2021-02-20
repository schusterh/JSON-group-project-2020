package modell;

import java.util.*;

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
public class TransportNetwork {
    private HashMap<Station, HashMap<Station,Integer>> adjStations;
    public TransportNetwork(HashMap<Station, HashMap<Station,Integer>> adjStations){
        this.adjStations = adjStations;
    }

    public HashMap<Station, HashMap<Station,Integer>> getAdjStations() {
        return adjStations;
    }

    void addStation(String label){
        adjStations.putIfAbsent(new Station(label),new HashMap<>());
    }

    void removeStation(String label){
        Station s = new Station(label);
        adjStations.values().forEach(e -> e.remove(s));
        adjStations.remove(new Station(label));
    }

    void addConnection(String label1, String label2,Integer distance){
        Station s1 = new Station(label1);
        Station s2 = new Station(label2);
        adjStations.get(s1).put(s2,distance);
        adjStations.get(s2).put(s1,distance);
    }

    void removeConnection(String label1, String label2){
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
}