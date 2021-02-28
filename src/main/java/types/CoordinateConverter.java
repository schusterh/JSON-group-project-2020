package types;

/**
 * Coordinate converter is used for statically render tile boundaries for a specific tile coordinate
 */
public class CoordinateConverter {

    /**
     * Takes in a coordinate and returns the isometric rectangle border as polygon.
     * @param posX X position of coordinate
     * @param posY Y position of coordinate
     * @param tileResolution resolution of base tile
     * @param tileOffsetY Height offset of tile
     * @param zoomFactor current zoom factor
     * @return list of X coordinates at [0], list of Y coordinates at [1]
     */
    public static double[][] createPolygonFromPoint(double posX, double posY, int tileResolution, int tileOffsetY, double zoomFactor) {
        double[] polyPoiX = new double[]{
                posX,
                ( posX + (double) tileResolution / 2 * zoomFactor) ,
                ( posX + (double) tileResolution * zoomFactor) ,
                ( posX + (double) tileResolution / 2 * zoomFactor) };
        double[] polyPoiY = new double[]{
                posY + ( (double) tileResolution * (1f/4f) + tileOffsetY) * zoomFactor,
                posY + ( (double) tileResolution * (0f/4f) + tileOffsetY) * zoomFactor,
                posY + ( (double) tileResolution * (1f/4f) + tileOffsetY) * zoomFactor,
                posY + ( (double) tileResolution * (1f/2f) + tileOffsetY) * zoomFactor};

        return new double[][]{polyPoiX, polyPoiY};
    }
}
