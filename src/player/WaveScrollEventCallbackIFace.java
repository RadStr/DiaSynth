package player;

public interface WaveScrollEventCallbackIFace {
    void scrollChangeCallback(int oldValue, int newValue);

    void revalidateTimestamps();

    boolean getCanZoom();

    void enableZooming();

    boolean getShouldZoomToMid();

    boolean getShouldZoomToEnd();
}
