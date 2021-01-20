public class Engine {

    // Attribute
    private String kind;
    private String graphic;
    private double speed;

    // Konstruktor
    public Engine(String graphic, double speed){
        this.kind = "engine";
        this.graphic = graphic;
        this.speed = speed;
    }

    // Getter
    public String getKind() { return kind; }
    public String getGraphic() { return graphic; }
    public double getSpeed() { return speed; }
}
