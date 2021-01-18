import javafx.util.Pair;
import java.util.List;
import java.util.Optional;

public class Vehicle {

    // Attribute
    private String graphic;
    private double speed;
    private Optional cargo;
    private kind vehicleKind;

    public enum kind {
        ENGINE, PLANE, ROAD_VEHICLE, WAGON
    }

    // Konstruktor
    public Vehicle (kind vehicleKind, String graphic, double speed,
                    Optional<Pair<String,Integer>> singleCargo,
                    Optional<List<Pair<String,Integer>>> moreCargo){

        this.vehicleKind = vehicleKind;
        this.graphic = graphic;
        this.speed = speed;
        if (singleCargo.isPresent()) this.cargo = singleCargo;
        else if (moreCargo.isPresent()) this.cargo = moreCargo;
    }

    // Getter
    public String getGraphic() {
        return graphic;
    }

    public double getSpeed() {
        return speed;
    }

    public Optional getCargo() {
        return cargo;
    }

    public kind getVehicleKind() {
        return vehicleKind;
    }
}
