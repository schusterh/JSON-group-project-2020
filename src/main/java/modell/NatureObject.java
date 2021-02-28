package modell;

import java.util.Optional;

/**
 * This class represents a nature object.
 * Nature objects are trees, stones, ruins and buildings which have been conquered by nature.
 */
public class NatureObject extends Building {
    private String name;
    private Optional buildmenu;
    private String special;
    private int dz;

    /**
     * Instantiates a new Nature object.
     *
     * @param name      the name
     * @param width     the width
     * @param depth     the depth
     * @param buildmenu the buildmenu
     * @param special   the special
     * @param dz        the dz
     */
    public NatureObject(String name,int width, int depth, Optional<String> buildmenu, String special, int dz) {
        super(width,depth, name);
        this.name = name;
        this.buildmenu = buildmenu;
        this.special = special;
        this.dz = dz;
    }

    /**
     * Gets buildmenu.
     *
     * @return the buildmenu
     */
    public Optional getBuildmenu() {
        return buildmenu;
    }

    /**
     * Gets dz.
     *
     * @return the dz
     */
    public int getDz() {
        return dz;
    }

}
