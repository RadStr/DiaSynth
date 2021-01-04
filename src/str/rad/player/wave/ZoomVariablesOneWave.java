package str.rad.player.wave;

public class ZoomVariablesOneWave {
    public ZoomVariablesOneWave(int currentZoom, int maxAggregatedZoom) {
        this.currentZoom = currentZoom;
        this.maxAggregatedZoom = maxAggregatedZoom;
    }

    public ZoomVariablesOneWave(int maxAggregatedZoom) {
        currentZoom = 0;
        this.maxAggregatedZoom = maxAggregatedZoom;
    }


    public int maxAggregatedZoom;
    public int currentZoom;
}