package modell;

public class Building {

    private int width;
    private int depth;
    private String name;

    public Building(int width, int depth, String name) {
        this.width = width;
        this.depth = depth;
        this.name = name;
    }

    public int getWidth() {
        return this.width;
    }

    public int getDepth() {
        return this.depth;
    }

    public String getName() { return this.name; }
}
