package player.wave;

/**
 * Class used in LimitDocumentFilterInt
 */
public interface LimitGetterIFace {
    /**
     * Gets the highest possible int which can be in text.
     * @return
     */
    int getLimit();

    /**
     * Calls revalidate on the main component.
     */
    void revalidateMethod();
}
