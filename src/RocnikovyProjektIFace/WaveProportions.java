package RocnikovyProjektIFace;

public class WaveProportions {
    public WaveProportions(int visibleWaveStartX, int visibleWaveEndX, int totalWaveWidth) {
        this.setVisibleWaveStartX(visibleWaveStartX);
        this.setVisibleWaveEndX(visibleWaveEndX);
        this.setTotalWaveWidth(totalWaveWidth);
    }

    private int visibleWaveStartX;
    /**
     * Gets the left visible pixel (based on current zoom/scroll). Where the most left x is 0.
     *<br>So it isn't the same value which returns getX() called on panel.
     * @return Returns the start relative to zoom/scroll.
     */
    public int getVisibleWaveStartX() {
        return visibleWaveStartX;
    }
    public void setVisibleWaveStartX(int val) {
        visibleWaveStartX = val;
        shouldRecalculateVisibleWidth = true;
    }

    private int visibleWaveEndX;
    /**
     * Gets the right visible pixel (based on current zoom/scroll). Where the most left x is 0.
     *<br>So it isn't the same value which returns getWidth() called on panel.
     * @return Returns the end relative to zoom/scroll.
     */
    public int getVisibleWaveEndX() {
        return visibleWaveEndX;
    }
    public void setVisibleWaveEndX(int val) {
        visibleWaveEndX = val;
        shouldRecalculateVisibleWidth = true;
    }

    private boolean shouldRecalculateVisibleWidth = false;
    private int visibleWaveWidth;
    /**
     * Gets the visibleWidth of the visible wave
     * @return Returns the visible wave visibleWidth.
     */
    public int getVisibleWaveWidth() {
        if(shouldRecalculateVisibleWidth) {
            shouldRecalculateVisibleWidth = false;
            visibleWaveWidth = visibleWaveEndX - visibleWaveStartX;
        }
        return visibleWaveWidth;
    }

    private int totalWaveWidth;
    public int getTotalWaveWidth() {
        return totalWaveWidth;
    }
    public void setTotalWaveWidth(int val) {
        this.totalWaveWidth = val;
    }


}
