package modell;

public class Map {

    private String mapgen;
    private String gamemode;
    private int width;
    private int depth;

    public Map(String mapgen, String gamemode, int width, int depth) {
        this.mapgen = mapgen;
        this.gamemode = gamemode;
        this.width = width;
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    public int getWidth() {
        return width;
    }

    public String getGamemode() {
        return gamemode;
    }

    public String getMapgen() {
        return mapgen;
    }
}
