package AudioMixers;

public interface UpdateIFace {
    /**
     * Should be overridden to update inside values to perform some calculation. For example calculate sum to divide the samples with it.
     * @param multFactors
     */
    void update(double[][] multFactors);
}
