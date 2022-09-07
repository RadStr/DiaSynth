package str.rad.analyzer.observer;

import org.w3c.dom.Node;

public interface DataModelSubjectIFace {
    public void addObserver(DataModelObserverIFace obs);
    public void removeObserver(DataModelObserverIFace obs);
    /**
     * Notify observers about removal of given node in the data model.
     */
    public void notifyObservers(Node removedNode);

    /**
     * Notify observers that there was non-trivial change in the data model.
     * Therefore the data needs to be reloaded from the XML to the data model.
     */
    public void notifyObservers();
}
