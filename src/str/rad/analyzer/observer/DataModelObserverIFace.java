package str.rad.analyzer.observer;

import org.w3c.dom.Node;

public interface DataModelObserverIFace {
    public void reloadDataModelFromXML();

    /**
     * Update data model by removing given node, which was deleted from XML file.
     * @return Returns True if the node was inside the dataModel, false otherwise
     */
    public boolean update(Node nodeToBeRemoved);
}
