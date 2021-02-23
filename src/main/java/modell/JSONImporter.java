package modell;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.*;

import javax.swing.*;

public class JSONImporter {

    private File file;


    public JSONImporter(File file) {
        this.file = file;
    }

    public Game LoadMap() throws Exception {
        String ERROR_MESSAGE = "Not enogh objects in the scenario!";
        String content = Files.readString(Paths.get(this.file.getAbsolutePath()), StandardCharsets.UTF_8);
        if (!content.startsWith("{") || !content.endsWith("}")) {
            throw new Exception("This is not a JSON-File!");
        }
        JSONObject json = new JSONObject(content);
        if (!json.has("buildings")) {
            throw new Exception("No buildings found!");
        }
        else if(!json.has("vehicles")) {
            throw new Exception("No vehicles found!");
        }
        else if (!json.has("commodities")) {
            throw new Exception("No commodities found!");
        }
        else if(!json.has("map")) {
            throw new Exception("No map found!");
        }
        else {

            JSONObject js_vehicles = json.getJSONObject("vehicles");
            ArrayList<Vehicle> vehicles = getVehicles(js_vehicles);
            JSONObject js_buildings = json.getJSONObject("buildings");
            ArrayList<Road> roads = getRoads(js_buildings);
            ArrayList<Railway> railways = getRailways(js_buildings);
            ArrayList<Factory> factories = getFactories(js_buildings);
            JSONArray js_commodities = json.getJSONArray("commodities");
            ArrayList<String> commodities = getCommodities(js_commodities);
            JSONObject js_map = json.getJSONObject("map");
            Map map = getMap(js_map);
            ArrayList<NatureObject> nature_objects = getNatureObjects(js_buildings);
            ArrayList<Tower> towers = getTowers(js_buildings);
            ArrayList<AirportObject> airport_objects = getAirportObjects(js_buildings);
            if(commodities == null || roads == null || railways == null || towers == null || airport_objects == null || nature_objects == null || factories == null || vehicles == null) {
                throw new Exception(ERROR_MESSAGE);
            }
            Game ggg = new Game(commodities, roads, railways, towers, airport_objects, nature_objects, factories, vehicles, map);

            return ggg;
        }
    }

    public ArrayList<Vehicle> getVehicles(JSONObject vehicles) throws Exception{
        String ERROR_MESSAGE = "Error! The vehicle is not in a supported format!";
        ArrayList<Vehicle> v = new ArrayList<>();
        for (String key : vehicles.keySet()) {
            JSONObject vehicle = vehicles.getJSONObject(key);

            if (!vehicle.has("kind") || !vehicle.has("graphic") || !vehicle.has("speed")) {
                throw new Exception(ERROR_MESSAGE);
            } else {

                String name = key;
                String kind = vehicle.getString("kind");
                String graphic = vehicle.getString("graphic");
                Double speed = vehicle.getDouble("speed");
                ArrayList<HashMap<String, Integer>> cargo = null;
                if (vehicle.has("cargo")) {
                    cargo = new ArrayList<>();
                    JSONArray cargo_data = new JSONArray();
                    if (vehicle.get("cargo") instanceof JSONObject) {
                        cargo_data.put(vehicle.getJSONObject("cargo"));
                    } else if (vehicle.get("cargo") instanceof JSONArray) {
                        cargo_data = vehicle.getJSONArray("cargo");
                    }
                    for (int i = 0; i < cargo_data.length(); i++) {
                        JSONObject obj = cargo_data.getJSONObject(i);
                        for (String item : obj.keySet()) {
                            HashMap<String, Integer> m = new HashMap<>();
                            m.put(item, obj.getInt(item));
                            cargo.add(m);

                        }
                    }
                }
                Vehicle r = new Vehicle(name, kind, graphic, speed, Optional.ofNullable(cargo));
                v.add(r);
            }
            return v;
        }
        return null;
    }

    public ArrayList<Road> getRoads(JSONObject roads) throws Exception {
        String ERROR_MESSAGE = "Error! The road is not in a supported format!";
        ArrayList<Road> r = new ArrayList<>();
        for (String key : roads.keySet()) {
            if (roads.getJSONObject(key).has("roads")) {
                JSONObject road = roads.getJSONObject(key);
                String name = key;
                if (!road.has("width") || !road.has("depth") || !road.has("roads") || !road.has("dz")) {
                    throw new Exception(ERROR_MESSAGE);
                } else {
                    int width = road.getInt("width");
                    int depth = road.getInt("depth");
                    HashMap<String, ArrayList<Double>> points = new HashMap<>();
                    JSONObject points_map_obj = road.getJSONObject("points");
                    for (String item : points_map_obj.keySet()) {
                        JSONArray item_array = points_map_obj.getJSONArray(item);
                        ArrayList<Double> d = new ArrayList<>();
                        for (int i = 0; i < item_array.length(); i++) {
                            d.add(item_array.getDouble(i));
                        }
                        points.put(item, d);
                    }
                    ArrayList<ArrayList<String>> input_roads = new ArrayList<>(); // am ende zu dem hinzufügem
                    JSONArray json_input_roads = road.getJSONArray("roads");
                    for (int i = 0; i < json_input_roads.length(); i++) {
                        ArrayList<String> s = new ArrayList<>();
                        JSONArray inner_list = json_input_roads.getJSONArray(i);
                        for (int j = 0; j < inner_list.length(); j++) {
                            s.add(inner_list.getString(j));
                        }
                        input_roads.add(s);
                    }
                    int dz = road.getInt("dz");
                    String buildmenu = null;
                    if (road.has("buildmenu")) {
                        buildmenu = road.getString("buildmenu");
                    }
                    HashMap<String, String> combines = null;
                    if (road.has("combines")) {
                        combines = new HashMap<>();
                        JSONObject combines_map_obj = road.getJSONObject("combines");
                        for (String combine : combines_map_obj.keySet()) {
                            combines.put(combine, combines_map_obj.getString(combine));
                        }
                    }
                    String special = null;
                    if (road.has("special")) {
                        special = road.getString("special");
                    }
                    Road new_r = new Road(name, width, depth, points, input_roads, dz, Optional.ofNullable(buildmenu), Optional.ofNullable(combines), Optional.ofNullable(special));
                    r.add(new_r);
                }
            }
        }
        return r;
    }

    public ArrayList<Railway> getRailways(JSONObject rails){
        ArrayList<Railway> r = new ArrayList<>();

        for (String key : rails.keySet()) {
            if(rails.getJSONObject(key).has("rails") || key.equals("railsignal")) {
                JSONObject rail = rails.getJSONObject(key);
                String name = key;
                int width = 0;
                int depth = 0;
                if (rail.has("width")) {
                     width = rail.getInt("width");
                }
                if (rail.has("depth")) {
                     depth = rail.getInt("depth");
                }
                HashMap<String, ArrayList<Double>> points = null;
                if (rail.has("points")) {
                    points = new HashMap<>();
                    JSONObject points_map_obj = rail.getJSONObject("points");
                    for (String item : points_map_obj.keySet()) {
                        JSONArray item_array = points_map_obj.getJSONArray(item);
                        ArrayList<Double> d = new ArrayList<>();
                        for (int i = 0; i < item_array.length(); i++) {
                            d.add(item_array.getDouble(i));
                        }
                        points.put(item, d);
                    }
                }

                String buildmenu = null;
                if (rail.has("buildmenu")) {
                    buildmenu = rail.getString("buildmenu");
                }

                ArrayList<ArrayList<String>> input_rails = null;
                if (rail.has("rails")) {
                    input_rails = new ArrayList<>();
                    JSONArray json_input_rails = rail.getJSONArray("rails");
                    for(int i = 0; i < json_input_rails.length(); i++) {
                        ArrayList<String> s = new ArrayList<>();
                        JSONArray inner_list = json_input_rails.getJSONArray(i);
                        for(int j = 0; j < inner_list.length(); j++) {
                            s.add(inner_list.getString(j));
                        }
                        input_rails.add(s);
                    }
                }
                Integer dz = null;
                if (rail.has("dz")) {
                    dz = rail.getInt("dz");
                }
                ArrayList<String> signals = null;
                if(rail.has("signals")) {
                    signals = new ArrayList<>();
                    JSONArray obj_arr = rail.getJSONArray("signals");
                    for (int z = 0; z < obj_arr.length(); z++) {
                        signals.add(obj_arr.getString(z));
                    }
                }
                String special = null;
                if (rail.has("special")) {
                    special = rail.getString("special");
                }
                HashMap<String, String> combines = null;
                if (rail.has("combines")) {
                    combines = new HashMap<>();
                    JSONObject combines_map_obj = rail.getJSONObject("combines");
                    for (String combine : combines_map_obj.keySet()) {
                        combines.put(combine,combines_map_obj.getString(combine));
                    }
                }
                Railway new_railway = new Railway(name,width,depth,Optional.ofNullable(buildmenu),Optional.ofNullable(points),Optional.ofNullable(input_rails),Optional.ofNullable(dz),Optional.ofNullable(signals),Optional.ofNullable(special),Optional.ofNullable(combines));
                r.add(new_railway);
            }
        }
        return r;
    }


    public ArrayList<Factory> getFactories(JSONObject factories) throws Exception{
        String ERROR_MESSAGE = "Error! The factory is not in a supported format!";
        ArrayList<Factory> f = new ArrayList<>();
        for(String key : factories.keySet()) {
            if (factories.getJSONObject(key).has("productions")) {
                JSONObject factory = factories.getJSONObject(key);
                if (!factory.has("width") || !factory.has("depth") || !factory.has("special") || !factory.has("productions") || !factory.has("dz")) {
                    throw new Exception(ERROR_MESSAGE);
                } else {
                    String name = key;
                    int width = factory.getInt("width");
                    int depth = factory.getInt("depth");
                    String special = factory.getString("special");
                    ArrayList<Production> productions = new ArrayList<>(); // hier productions arrray
                    JSONArray production_data = new JSONArray();
                    if (factory.get("productions") instanceof JSONObject) {
                        production_data.put(factory.getJSONObject("productions"));
                    } else {
                        if (factory.get("productions") instanceof JSONArray) {
                            production_data = factory.getJSONArray("productions");
                        }
                    }
                    for (int i = 0; i < production_data.length(); i++) {
                        JSONObject obj = production_data.getJSONObject(i);
                        HashMap<String, Integer> produce = null; // hier produce attribut
                        if (obj.has("produce")) {
                            produce = new HashMap<>();
                            JSONObject produce_map = obj.getJSONObject("produce");
                            for (String k : produce_map.keySet()) {
                                produce.put(k, produce_map.getInt(k));
                            }
                        }
                        HashMap<String, Integer> consume = null;
                        if (obj.has("consume")) {
                            consume = new HashMap<>();
                            JSONObject consume_map = obj.getJSONObject("consume");
                            for (String g : consume_map.keySet()) {
                                consume.put(g, consume_map.getInt(g));
                            }
                        }
                        int duration = obj.getInt("duration");
                        Production p = new Production(Optional.ofNullable(produce), Optional.ofNullable(consume), duration);
                        productions.add(p);
                    }
                    HashMap<String, Integer> storage = null;
                    if (factory.has("storage")) {
                        storage = new HashMap<>();
                        JSONObject storage_map = factory.getJSONObject("storage");
                        for (String t : storage_map.keySet()) {
                            storage.put(t, storage_map.getInt(t));
                        }
                    }
                    int dz = factory.getInt("dz");
                    Factory new_factory = new Factory(name, width, depth, special, productions, Optional.ofNullable(storage), dz);
                    f.add(new_factory);
                }
            }
        }
        return f;
    }

    public ArrayList<String> getCommodities(JSONArray comodities) throws Exception{
        String ERROR_MESSAGE = "Error! The commodity is not in a supported format!";
        ArrayList<String> c = new ArrayList<>();
        if (comodities.length() == 0) {
            throw new Exception(ERROR_MESSAGE);
        }
        else {
            for (int i = 0; i < comodities.length(); i++) {
                String item = comodities.getString(i);
                c.add(item);
            }
        }
        return c;
    }

    public Map getMap(JSONObject map) throws Exception{
        String ERROR_MESSAGE = "Error! The map is not in a supported format!";
        if (!map.has("gamemode") || !map.has("mapgen") || !map.has("width") || !map.has("depth")) {
            throw new Exception(ERROR_MESSAGE);
        }
        else {
            String gamemode = map.getString("gamemode");
            String mapgen = map.getString("mapgen");
            int width = map.getInt("width");
            int depth = map.getInt("depth");
            Map m = new Map(mapgen, gamemode, width, depth);
            return m;
        }
    }

    public ArrayList<NatureObject> getNatureObjects(JSONObject natobs) throws Exception{
        String ERROR_MESSAGE = "Error! The nature object is not in a supported format!";
        ArrayList<NatureObject> no  = new ArrayList<>();
        for(String key: natobs.keySet()) {
            if(natobs.getJSONObject(key).has("special") && natobs.getJSONObject(key).getString("special").equals("nature")) {
                JSONObject natob = natobs.getJSONObject(key);
                if (!natob.has("width") || !natob.has("depth") || !natob.has("special") || !natob.has("dz")) {
                    throw new Exception(ERROR_MESSAGE);
                }
                else {
                String name = key;
                int width = natob.getInt("width");
                int depth = natob.getInt("depth");
                String buildmenu = null;
                if (natob.has("buildmenu")) {
                    buildmenu = natob.getString("buildmenu");
                }
                String special = natob.getString("special");
                int dz = natob.getInt("dz");
                NatureObject new_natob = new NatureObject(name,width,depth,Optional.ofNullable(buildmenu),special,dz);
                no.add(new_natob);
                }
            }
        }
        return no;
    }

    public ArrayList<Tower> getTowers(JSONObject towers) throws Exception{
        String ERROR_MESSAGE = "Error! The tower is not in a supported format!";
        ArrayList<Tower> t = new ArrayList<>();
        for(String key : towers.keySet()) {
            if(key.equals("tower") || key.equals("big tower")) {
                JSONObject tower = towers.getJSONObject(key);
                if (!tower.has("width") || !tower.has("depth") || !tower.has("buildmenu") || !tower.has("special") || !tower.has("maxplanes") || !tower.has("dz")) {
                    throw new Exception(ERROR_MESSAGE);
                }
                else{
                    String name = key;
                    int width = tower.getInt("width");
                    int depth = tower.getInt("depth");
                    String buildmenu = tower.getString("buildmenu");
                    String special = tower.getString("special");
                    int maxplanes = tower.getInt("maxplanes");
                    int dz = tower.getInt("dz");
                    Tower new_tower = new Tower(width, depth, name, buildmenu, special, maxplanes, dz);
                    t.add(new_tower);
                }
            }
        }
        return t;
    }
    public ArrayList<AirportObject> getAirportObjects(JSONObject airobjs) throws Exception{
        String ERROR_MESSAGE = "Error! The airport object is not in a supported format!";
        ArrayList<AirportObject> r = new ArrayList<>();
        for(String key : airobjs.keySet()) {
            if(airobjs.getJSONObject(key).has("planes")) {

                JSONObject airob = airobjs.getJSONObject(key);
                if (!airob.has("width") || !airob.has("depth") || !airob.has("buildmenu") || !airob.has("special") || !airob.has("points") || !airob.has("planes") || !airob.has("dz")) {
                    throw new Exception(ERROR_MESSAGE);
                }
                else {
                    String name = key;
                    int width = airob.getInt("width");
                    int depth = airob.getInt("depth");

                    HashMap<String, ArrayList<Double>> points = new HashMap<>();
                    JSONObject points_map_obj = airob.getJSONObject("points");
                    for (String item : points_map_obj.keySet()) {
                        JSONArray item_array = points_map_obj.getJSONArray(item);
                        ArrayList<Double> d = new ArrayList<>();
                        for (int i = 0; i < item_array.length(); i++) {
                            d.add(item_array.getDouble(i));
                        }
                        points.put(item, d);
                    }

                    ArrayList<ArrayList<String>> input_planes = new ArrayList<>(); // am ende zu dem hinzufügem
                    JSONArray json_input_planes = airob.getJSONArray("planes");
                    for (int i = 0; i < json_input_planes.length(); i++) {
                        ArrayList<String> s = new ArrayList<>();
                        JSONArray inner_list = json_input_planes.getJSONArray(i);
                        for (int j = 0; j < inner_list.length(); j++) {
                            s.add(inner_list.getString(j));
                        }
                        input_planes.add(s);
                    }

                    int dz = airob.getInt("dz");
                    String buildmenu = null;
                    if (airob.has("buildmenu")) {
                        buildmenu = airob.getString("buildmenu");
                    }

                    String special = airob.getString("special");
                    ArrayList<String> entry = null;
                    if (airob.has("entry")) {
                        entry = new ArrayList<>();
                        JSONArray obj_array = airob.getJSONArray("entry");
                        for (int i = 0; i < obj_array.length(); i++) {
                            entry.add(obj_array.getString(i));
                        }
                    }

                    AirportObject new_r = new AirportObject(width, depth, name, buildmenu, special, points, Optional.ofNullable(entry),
                            input_planes, dz);
                    r.add(new_r);
                }
            }
        }
        return r;
    }
}



