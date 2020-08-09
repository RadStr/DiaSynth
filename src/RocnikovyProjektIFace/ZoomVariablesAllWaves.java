package RocnikovyProjektIFace;

public class ZoomVariablesAllWaves {

    public ZoomVariablesAllWaves() {
        this(0);
    }

    public ZoomVariablesAllWaves(int maxAllowedZoom) {
        setMaxAllowedZoom(maxAllowedZoom);
        zoom = 0;
    }

    private final int CACHE_DIFFERENCE = 4;

    private int maxAllowedZoom;
    public int getMaxAllowedZoom() {
        return maxAllowedZoom;
    }
    public void setMaxAllowedZoom(int val) {
        maxAllowedZoom = val;
    }

    public int zoom;

    public boolean getIsZoomAtZero() {
        return zoom == 0;
    }

    public boolean getIsZoomAtMax() {
        return getMaxAllowedZoom() == zoom;
    }
}
