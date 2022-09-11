package str.rad.analyzer.observer;

import org.w3c.dom.Node;

public interface DataModelSubjectIFace {
    public void registerObserver(DataModelObserverIFace obs);
    public void unregisterObserver(DataModelObserverIFace obs);

    /**
     * Notify observers about removal of XML node, which needs to be propagated to the data model.
     * @param removedNode is the removed XML node
     */
    public void notifyObservers(Node removedNode);

    /**
     * Notify observers that there was non-trivial change in the XML file, which needs to be propagated to the data model.
     * Therefore all the data needs to be reloaded from the XML file to the data model.
     */
    public void notifyObservers();
}
