package modell;

/**
 * This class represents a tower of the game.
 */
public class Tower extends Building{
    private String name;
    private String buildmenu;
    private String special;
    private int maxplanes;
    private int dz;

    /**
     * Instantiates a new Tower.
     *
     * @param width     the width
     * @param depth     the depth
     * @param name      the name
     * @param buildmenu the buildmenu
     * @param special   the special
     * @param maxplanes the maxplanes
     * @param dz        the dz
     */
    public Tower(int width, int depth, String name, String buildmenu, String special, int maxplanes, int dz) {
        super(width, depth, name);
        this.name = name;
        this.buildmenu = buildmenu;
        this.special = special;
        this.maxplanes = maxplanes;
        this.dz = dz;
    }

    /**
     * Gets maxplanes.
     *
     * @return the maxplanes
     */
    public int getMaxplanes() {
        return maxplanes;
    }

    /**
     * Gets buildmenu.
     *
     * @return the buildmenu
     */
    public String getBuildmenu() {
        return buildmenu;
    }

    /**
     * Gets special.
     *
     * @return the special
     */
    public String getSpecial() {
        return special;
    }
}
