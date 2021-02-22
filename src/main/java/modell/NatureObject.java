package modell;

import java.util.Optional;

public class NatureObject extends Building {
    public String name;
    public String buildmenu;
    private String special;
    private int dz;

    public NatureObject(String name, int width, int depth, Optional<String> buildmenu, String special, int dz) {
        super(width,depth, name);
        this.name = name;
        this.buildmenu = "";
        buildmenu.ifPresent(s -> this.buildmenu = s);
        this.special = special;
        this.dz = dz;
    }

    public String getBuildmenu() {
        return buildmenu;
    }

    public int getDz() {
        return dz;
    }
}
