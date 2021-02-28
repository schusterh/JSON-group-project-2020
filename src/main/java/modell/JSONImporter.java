package modell;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import org.json.*;

import javax.swing.*;

/**
 * The JSON Importer is used to choose a scenario specified as a JSON-file. All information that is necessary for the game to run,
 * must be specified in the file. The JSON Imported reads the file and checks if all obligatory data is present and if that data is in a
 * correct format. If this is not the case, then the JSON-Importer gives back an error message wich includes the problem.
 */
public class JSONImporter {


    private File file;


    /**
     * Instantiates a new Json importer.
     *
     * @param file the JSON-file from the file chooser window.
     */
    public JSONImporter(File file) {
        this.file = file;
    }

    /**
     * LoadMap() is the main function of the JSON-Importer. Its main purpose is to call all other server functions
     * and gather all the necesary information into the correct type of data. With that data it creates a Game Object
     * which represents the actual game later being played.
     *
     * @return the game
     * @throws Exception Throws a set of different exception-messages, depending on where it occurs.
     */
    public Game LoadMap() throws Exception {
        String ERROR_MESSAGE = "Not enogh objects in the scenario!";
        final String STANDARD_BG_MUSIC = "happy_tune.mp3";
        JSONObject json;
        String content = Files.readString(Paths.get(this.file.getAbsolutePath()), StandardCharsets.UTF_8);
        try {
            json = new JSONObject(content);
        }
        catch (Exception ex) {
            throw new Exception("This is not a correct JSON file!");
        }

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
            System.out.println(vehicles);

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

            ArrayList<String> music;
            if(json.has("music")) {
                JSONObject json_music = json.getJSONObject("music");
                music = getMusic(json_music);
            }
            else {
                music = new ArrayList<>();
                music.add(STANDARD_BG_MUSIC);
            }

            // If there is at least one type of every necessary object
            if(commodities == null || roads == null || railways == null || towers == null || airport_objects == null || nature_objects == null || factories == null || vehicles == null) {
                throw new Exception(ERROR_MESSAGE);
            }
            Game ggg = new Game(commodities, roads, railways, towers, airport_objects, nature_objects, factories, vehicles, map,music);

            return ggg;
        }
    }

    /**
     * Gets an arraylist of all the vehicles specified in the json file.
     *
     * @param vehicles the jsonobject with the name "vehicles" in the json file.
     * @return the arraylist of vehicle objects
     * @throws Exception with a specified error message depending on what the error is.
     */
    public ArrayList<Vehicle> getVehicles(JSONObject vehicles) throws Exception{

        String ERROR_MESSAGE = "Error! The vehicle is not in a supported format!";
        ArrayList<Vehicle> v = new ArrayList<>();

        for (String key : vehicles.keySet()) {
            JSONObject vehicle = vehicles.getJSONObject(key);

            if (!vehicle.has("kind") || !vehicle.has("graphic") || !vehicle.has("speed")) {
                throw new Exception(ERROR_MESSAGE);
            }
            else {

                String name = key;
                String kind;
                try {
                    kind = vehicle.getString("kind");
                }
                catch (Exception error1) {
                    throw new Exception("Kind in a vehicle is not in a correct format!");
                }

                String graphic;
                try {
                    graphic = vehicle.getString("graphic");
                }
                catch (Exception error2) {
                    throw new Exception("Graphic in a vehicle is not in a correct format!");
                }


                Double speed;
                try {
                    speed = vehicle.getDouble("speed");
                }
                catch (Exception error3) {
                    throw new Exception("Speed in a vehicle is not in a correct format!");
                }

                ArrayList<HashMap<String, Integer>> cargo = null;
                if (vehicle.has("cargo")) {
                    try {
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
                    catch (Exception error4) {
                        throw new Exception("Cargo in a vehicle is not in a correct format!");
                    }
                }
                Vehicle r = new Vehicle(name, kind, graphic, speed, Optional.ofNullable(cargo));
                v.add(r);
            }
        }
        return v;
    }

    /**
     * Gets an arraylist of all the roads specified in the json file.
     *
     * @param roads a json object of all elements in "buildings" of the json file.
     * @return the arraylist of all road types objects
     * @throws Exception with a specified error message depending on what the error is.
     */
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

                    int width;
                    int depth;
                    try {
                         width = road.getInt("width");
                         depth = road.getInt("depth");
                    }
                    catch (Exception error1) {
                        throw new Exception("Width and/or depth attributes in a road are not in a correct format!");
                    }


                    HashMap<String, ArrayList<Double>> points;
                    try {
                        points = new HashMap<>();
                        JSONObject points_map_obj = road.getJSONObject("points");
                        for (String item : points_map_obj.keySet()) {
                            JSONArray item_array = points_map_obj.getJSONArray(item);
                            ArrayList<Double> d = new ArrayList<>();
                            for (int i = 0; i < item_array.length(); i++) {
                                d.add(item_array.getDouble(i));
                            }
                            points.put(item, d);
                        }
                    }
                    catch (Exception error2) {
                        throw new Exception("Points in a road are not in a correct format!");
                    }


                    ArrayList<ArrayList<String>> input_roads;
                    try {
                        input_roads = new ArrayList<>();
                        JSONArray json_input_roads = road.getJSONArray("roads");
                        for (int i = 0; i < json_input_roads.length(); i++) {
                            ArrayList<String> s = new ArrayList<>();
                            JSONArray inner_list = json_input_roads.getJSONArray(i);
                            for (int j = 0; j < inner_list.length(); j++) {
                                s.add(inner_list.getString(j));
                            }
                            input_roads.add(s);
                        }
                    }
                    catch (Exception error3) {
                        throw new Exception("Roads in a road are not in a correct format!");
                    }

                    int dz;
                    try {
                        dz = road.getInt("dz");
                    }
                    catch (Exception error4) {
                        throw new Exception("Dz in a road is not in a correct format!");
                    }


                    String buildmenu = null;
                    if (road.has("buildmenu")) {
                        try {
                            buildmenu = road.getString("buildmenu");
                        }
                        catch (Exception error5) {
                            throw new Exception("Buildmenu in a road is not in a correct format!");
                        }
                    }


                    HashMap<String, String> combines = null;
                    if (road.has("combines")) {
                        try {
                            combines = new HashMap<>();
                            JSONObject combines_map_obj = road.getJSONObject("combines");
                            for (String combine : combines_map_obj.keySet()) {
                                combines.put(combine, combines_map_obj.getString(combine));
                            }
                        }
                        catch (Exception error6) {
                            throw new Exception("Combines in a road are not in a correct format!");
                        }
                    }


                    String special = null;
                    if (road.has("special")) {
                        try {
                            special = road.getString("special");
                        }
                        catch (Exception error6) {
                            throw new Exception("Special  in a road is not in a correct format!");
                        }
                    }
                    Road new_r = new Road(name, width, depth, points, input_roads, dz, Optional.ofNullable(buildmenu), Optional.ofNullable(combines), Optional.ofNullable(special));
                    r.add(new_r);
                }
            }
        }
        return r;
    }

    /**
     * Gets an arraylist of all the rails specified in the json file.
     *
     * @param rails a json object of all elements in "buildings" of the json file.
     * @return the arraylist of all rail types
     * @throws Exception with a specified error message depending on what the error is.
     */
    public ArrayList<Railway> getRailways(JSONObject rails) throws Exception{
        ArrayList<Railway> r = new ArrayList<>();

        for (String key : rails.keySet()) {
            if(rails.getJSONObject(key).has("rails") || key.equals("railsignal")) {
                JSONObject rail = rails.getJSONObject(key);
                String name = key;
                int width = 0;
                int depth = 0;

                if (rail.has("width")) {
                    try {
                        width = rail.getInt("width");
                    }
                    catch (Exception error1) {
                        throw new Exception("Width in a rail is not in a correct format!");
                    }
                }

                if (rail.has("depth")) {
                    try {
                        depth = rail.getInt("depth");
                    }
                    catch (Exception error2) {
                        throw new Exception("Depth in a rail is not in a correct format!");
                    }
                }

                HashMap<String, ArrayList<Double>> points = null;
                if (rail.has("points")) {
                    try {
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
                    catch (Exception error3) {
                        throw new Exception("Points in a rail are not in a correct format");
                    }
                }

                String buildmenu = null;
                if (rail.has("buildmenu")) {
                    try {
                        buildmenu = rail.getString("buildmenu");
                    }
                    catch (Exception error4) {
                        throw new Exception("Buildmenu in a rail is not in a correct format!");
                    }
                }

                ArrayList<ArrayList<String>> input_rails = null;
                if (rail.has("rails")) {
                    try {
                        input_rails = new ArrayList<>();
                        JSONArray json_input_rails = rail.getJSONArray("rails");
                        for (int i = 0; i < json_input_rails.length(); i++) {
                            ArrayList<String> s = new ArrayList<>();
                            JSONArray inner_list = json_input_rails.getJSONArray(i);
                            for (int j = 0; j < inner_list.length(); j++) {
                                s.add(inner_list.getString(j));
                            }
                            input_rails.add(s);
                        }
                    }
                    catch (Exception error5) {
                        throw new Exception("Rails in a rail are not in a correct format!");
                    }
                }


                Integer dz = null;
                if (rail.has("dz")) {
                    try {
                        dz = rail.getInt("dz");
                    }
                    catch (Exception error6) {
                        throw new Exception("Dz in a rail is not in a correct format!");
                    }
                }


                ArrayList<String> signals = null;
                if(rail.has("signals")) {
                    try {
                        signals = new ArrayList<>();
                        JSONArray obj_arr = rail.getJSONArray("signals");
                        for (int z = 0; z < obj_arr.length(); z++) {
                            signals.add(obj_arr.getString(z));
                        }
                    }
                    catch (Exception error7) {
                        throw new Exception("Signals in a rail are not in a corect format!");
                    }
                }


                String special = null;
                if (rail.has("special")) {
                    try {
                        special = rail.getString("special");
                    }
                    catch (Exception error8) {
                        throw new Exception("Special in a rail is not in a correct format!");
                    }
                }


                HashMap<String, String> combines = null;
                if (rail.has("combines")) {
                    try {
                        combines = new HashMap<>();
                        JSONObject combines_map_obj = rail.getJSONObject("combines");
                        for (String combine : combines_map_obj.keySet()) {
                            combines.put(combine, combines_map_obj.getString(combine));
                        }
                    }
                    catch (Exception error9) {
                        throw new Exception("Combines in a rail are not in a correct format!");
                    }
                }
                Railway new_railway = new Railway(name,width,depth,Optional.ofNullable(buildmenu),Optional.ofNullable(points),Optional.ofNullable(input_rails),Optional.ofNullable(dz),Optional.ofNullable(signals),Optional.ofNullable(special),Optional.ofNullable(combines));
                r.add(new_railway);
            }
        }
        return r;
    }


    /**
     * Gets an arraylist of all the factories specified in the json file.
     *
     * @param factories a json object of all elements in "buildings" of the json file.
     * @return the arraylist of all factory types objects
     * @throws Exception with a specified error message depending on what the error is.
     */
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

                    int width;
                    try {
                        width = factory.getInt("width");
                    }
                    catch (Exception error1) {
                        throw new Exception("Width of factory not in a correct format!");
                    }

                    int depth;
                    try {
                        depth = factory.getInt("depth");
                    }
                    catch (Exception error2) {
                        throw new Exception("Depth of a factory not in a correct format!");
                    }

                    String special;
                    try {
                        special = factory.getString("special");
                    }
                    catch (Exception error3) {
                        throw new Exception("Special of a factory not in a correct format!");
                    }

                    ArrayList<Production> productions;
                    try {
                        productions = new ArrayList<>(); // hier productions arrray
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
                    }
                    catch (Exception error4) {
                        throw new Exception("Productions of a factory are not in a correct format!");
                    }


                    HashMap<String, Integer> storage = null;
                    if (factory.has("storage")) {
                        try {
                            storage = new HashMap<>();
                            JSONObject storage_map = factory.getJSONObject("storage");
                            for (String t : storage_map.keySet()) {
                                storage.put(t, storage_map.getInt(t));
                            }
                        }
                        catch (Exception error5) {
                            throw new Exception("Storage of a factory not in a correct format!");
                        }
                    }


                    int dz;
                    try {
                        dz = factory.getInt("dz");
                    }
                    catch (Exception error6) {
                        throw new Exception("Dz of a factory not in a correct format!");
                    }

                    Factory new_factory = new Factory(name, width, depth, special, productions, Optional.ofNullable(storage), dz);
                    f.add(new_factory);
                }
            }
        }
        return f;
    }

    /**
     * Gets an arraylist of all the comodities specified in the json file.
     *
     * @param comodities the jsonobject with the key "comodities" in the json file.
     * @return the arraylist of all commodities
     * @throws Exception with a specified error message depending on what the error is.
     */
    public ArrayList<String> getCommodities(JSONArray comodities) throws Exception{
        String ERROR_MESSAGE = "Error! The commodity-list is empty!";
        ArrayList<String> c = new ArrayList<>();
        if (comodities.length() == 0) {
            throw new Exception(ERROR_MESSAGE);
        }
        else {
            try {
                for (int i = 0; i < comodities.length(); i++) {
                    String item = comodities.getString(i);
                    c.add(item);
                }
            }
            catch (Exception error1) {
                throw new Exception("Commodity not in a correct format!");
            }
        }
        return c;
    }

    /**
     * Gets a map object specified in the json file.
     *
     * @param map the jsonobject with the key "map" in the json file.
     * @return map object
     * @throws Exception with a specified error message depending on what the error is.
     */
    public Map getMap(JSONObject map) throws Exception{
        String ERROR_MESSAGE = "Error! The map is not in a supported format!";
        if (!map.has("gamemode") || !map.has("mapgen") || !map.has("width") || !map.has("depth")) {
            throw new Exception(ERROR_MESSAGE);
        }
        else {
            String gamemode;
            try {
                gamemode = map.getString("gamemode");
            }
            catch (Exception error1) {
                throw new Exception("Gamemode of the map not in a correct format!");
            }

            String mapgen;
            try {
                mapgen = map.getString("mapgen");
            }
            catch (Exception error2) {
                throw new Exception("Mapgen of the map not in a correct format!");
            }

            int width;
            try {
                width = map.getInt("width");
            }
            catch (Exception error3) {
                throw new Exception("Width of the map not in a correct format!");
            }

            int depth;
            try {
                depth = map.getInt("depth");
            }
            catch (Exception error4) {
                throw new Exception("Depth of the map not in a supported format!");
            }

            Map m = new Map(mapgen, gamemode, width, depth);
            return m;
        }
    }

    /**
     * Gets an arraylist of all the nature objects specified in the json file.
     *
     * @param natobs a json object of all elements in "buildings" of the json file.
     * @return the arraylist of all objects which can be identified as nature objects
     * @throws Exception with a specified error message depending on what the error is.
     */
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

                int width;
                try {
                    width = natob.getInt("width");
                }
                catch (Exception error1) {
                    throw new Exception("Width of a nature object not in a correct format!");
                }

                int depth;
                try {
                   depth = natob.getInt("depth");
                }
                catch (Exception error2) {
                    throw new Exception("Depth of a nature object not in a correct format!");
                }

                String buildmenu = null;
                if (natob.has("buildmenu")) {
                    try {
                        buildmenu = natob.getString("buildmenu");
                    }
                    catch (Exception error3) {
                        throw new Exception("Buildmenu of a nature object not in a correct format!");
                    }
                }


                String special;
                try {
                  special = natob.getString("special");
                }
                catch (Exception error4) {
                    throw new Exception("Special of a nature object not in a correct format!");
                }

                int dz;
                try {
                  dz = natob.getInt("dz");
                }
                catch (Exception error5) {
                    throw new Exception("Dz of a nature object not in a correct format!");
                }

                NatureObject new_natob = new NatureObject(name,width,depth,Optional.ofNullable(buildmenu),special,dz);
                no.add(new_natob);
                }
            }
        }
        return no;
    }

    /**
     * Gets towers.
     *
     * @param towers the towers
     * @return the towers
     * @throws Exception the exception
     */
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

    /**
     * Gets airport objects.
     *
     * @param airobjs the airobjs
     * @return the airport objects
     * @throws Exception the exception
     */
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

                    int width;
                    try {
                        width = airob.getInt("width");
                    }
                    catch (Exception error1) {
                        throw new Exception("Width of an airport object not in a correct format!");
                    }

                    int depth;
                    try {
                        depth  = airob.getInt("depth");
                    }
                    catch (Exception error2) {
                        throw new Exception("Depth of a airport object not in a correct format!");
                    }

                    HashMap<String, ArrayList<Double>> points;
                    try {
                    points = new HashMap<>();
                        JSONObject points_map_obj = airob.getJSONObject("points");
                        for (String item : points_map_obj.keySet()) {
                            JSONArray item_array = points_map_obj.getJSONArray(item);
                            ArrayList<Double> d = new ArrayList<>();
                            for (int i = 0; i < item_array.length(); i++) {
                                d.add(item_array.getDouble(i));
                            }
                            points.put(item, d);
                        }
                    }
                    catch (Exception error3) {
                        throw new Exception("Points of an airport object are not in a correct format!");
                    }

                    ArrayList<ArrayList<String>> input_planes;
                    try {
                    input_planes = new ArrayList<>(); // am ende zu dem hinzufügem
                        JSONArray json_input_planes = airob.getJSONArray("planes");
                        for (int i = 0; i < json_input_planes.length(); i++) {
                            ArrayList<String> s = new ArrayList<>();
                            JSONArray inner_list = json_input_planes.getJSONArray(i);
                            for (int j = 0; j < inner_list.length(); j++) {
                                s.add(inner_list.getString(j));
                            }
                            input_planes.add(s);
                        }
                    }
                    catch (Exception error4) {
                        throw new Exception("Planes of an airport object are not in a correct format!");
                    }

                    int dz;
                    try {
                        dz =airob.getInt("dz");
                    }
                    catch (Exception error5) {
                        throw new Exception("Dz of an airport object not in a correct format!");
                    }

                    String buildmenu = null;
                    if (airob.has("buildmenu")) {
                        try {
                            buildmenu = airob.getString("buildmenu");
                        }
                        catch (Exception error6) {
                            throw new Exception("Buildmenu of an airport object not in a correct format!");
                        }
                    }

                    String special;
                    try {
                        special = airob.getString("special");
                    }
                    catch (Exception error7) {
                        throw new Exception("Special of an airport object not in a correct format!");
                    }

                    ArrayList<String> entry = null;
                    if (airob.has("entry")) {
                        try {
                            entry = new ArrayList<>();
                            JSONArray obj_array = airob.getJSONArray("entry");
                            for (int i = 0; i < obj_array.length(); i++) {
                                entry.add(obj_array.getString(i));
                            }
                        }
                        catch (Exception error8) {
                            throw new Exception("Entry of an airport object not in a correct format!");
                        }
                    }

                    AirportObject new_r = new AirportObject(width, depth, name, Optional.ofNullable(buildmenu), special, points, Optional.ofNullable(entry),
                            input_planes, dz);
                    r.add(new_r);
                }
            }
        }
        return r;
    }

    /**
     * Gets music.
     *
     * @param music the music
     * @return the music
     * @throws Exception the exception
     */
    public ArrayList<String> getMusic(JSONObject music) throws Exception{
        ArrayList<String> m = new ArrayList<>(1);
        if (!music.has("background_music_path")) {
            throw new Exception("Music not in a correct format!");
        }
        String path_to_backgroundmusic;
        try {
            path_to_backgroundmusic = music.getString("background_music_path");
            m.add(path_to_backgroundmusic);
        }
        catch (Exception error1) {
            throw new Exception("Path to background music in music not in a correct format!");
        }

        return m;
    }
}



