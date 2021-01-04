package player.wave;

/**
 * This interface is used to propagate change in mixWithChannelsInSecondDim slider to the class in which
 * performs the mixing and plays the result.
 */
public interface WaveMixPanelUpdaterIFace {
    void update(int index, double newValue);
}
