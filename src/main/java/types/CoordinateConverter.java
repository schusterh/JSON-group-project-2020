package types;

public class CoordinateConverter {

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
