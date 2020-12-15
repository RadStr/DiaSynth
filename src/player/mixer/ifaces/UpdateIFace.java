package player.mixer.ifaces;

public interface UpdateIFace {
    /**
     * Should be overridden to update values inside class which are used in mixing. For example calculate sum to divide the samples with it.
     * @param multFactors
     */
    void update(double[][] multFactors);
}