package analyzer.observer;

import org.w3c.dom.Node;

public interface DataModelObserverIFace {
    public void reloadDataModelFromXML();

    public boolean update(Node nodeToBeRemoved);
}
