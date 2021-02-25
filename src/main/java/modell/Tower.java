package modell;

public class Tower extends Building{
    private String name;
    private String buildmenu;
    private String special;
    private int maxplanes;
    private int dz;

    public Tower(int width, int depth, String name, String buildmenu, String special, int maxplanes, int dz) {
        super(width, depth, name);
        this.name = name;
        this.buildmenu = buildmenu;
        this.special = special;
        this.maxplanes = maxplanes;
        this.dz = dz;
    }
}
