package str.rad.analyzer.observer;

import org.w3c.dom.Node;

public interface DataModelObserverIFace {
    public void reloadDataModel();

    /**
     * Update data model by removing given node, which was deleted from XML file.
     * @return Returns True if the node was inside the data model, false otherwise
     */
    public boolean update(Node nodeToBeRemoved);
}
