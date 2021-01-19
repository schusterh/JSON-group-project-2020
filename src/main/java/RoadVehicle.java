import javafx.util.Pair;
import java.util.List;
import java.util.Optional;

public class RoadVehicle {

    // Attribute
    private String kind;
    private String graphic;
    private Optional cargo;
    private double speed;

    // Konstruktor
    public RoadVehicle(String graphic, double speed,
                       Optional<Pair<String,Integer>> singleCargo,
                       Optional<List<Pair<String,Integer>>> moreCargo){
        this.kind = "road vehicle";
        this.graphic = graphic;
        this.speed = speed;
        if (singleCargo.isPresent()) this.cargo = singleCargo;
        else if (moreCargo.isPresent()) this.cargo = moreCargo;
    }

    // Getter
    public String getKind() { return kind; }
    public String getGraphic() { return graphic; }
    public Optional getCargo() { return cargo; }
    public double getSpeed() { return speed; }
}
