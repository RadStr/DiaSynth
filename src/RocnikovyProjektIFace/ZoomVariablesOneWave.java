package RocnikovyProjektIFace;

public class ZoomVariablesOneWave {
    public ZoomVariablesOneWave(int currentZoom, int maxCacheZoom) {
        this.currentZoom = currentZoom;
        this.maxCacheZoom = maxCacheZoom;
    }

    public ZoomVariablesOneWave(int maxCacheZoom) {
        currentZoom = 0;
        this.maxCacheZoom = maxCacheZoom;
    }


    public int maxCacheZoom;
    public int currentZoom;
}