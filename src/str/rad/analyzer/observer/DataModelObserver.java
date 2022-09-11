package str.rad.analyzer.observer;

import str.rad.analyzer.AnalyzerXML;
import str.rad.analyzer.SongLibraryPanel;
import str.rad.util.Pair;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public abstract class DataModelObserver implements DataModelObserverIFace {
    private DefaultTableModel dataModel;
    private List<Pair<String, Node>> dataModelPairs;

    public void setDataModel(DefaultTableModel dataModel, List<Pair<String, Node>> dataModelPairs) {
        this.dataModel = dataModel;
        this.dataModelPairs = dataModelPairs;
    }

    public DataModelObserver(DefaultTableModel dataModel, List<Pair<String, Node>> dataModelPairs) {
        this.dataModel = dataModel;
        this.dataModelPairs = dataModelPairs;
    }

    /**
     * Update data model by removing given node, which was deleted from XML file.
     * @return Returns True if the node was inside the data model, false otherwise
     */
    @Override
    public boolean update(Node nodeToBeRemoved) {
        String name;
        NodeList infoNodes = nodeToBeRemoved.getChildNodes();
        int len = infoNodes.getLength();

        for (int i = 0; i < len; i++) {
            Node infoNode = infoNodes.item(i);

            if (AnalyzerXML.isNodeMatchingGivenAttribute(infoNode, "name", SongLibraryPanel.HEADER_NAME_COLUMN_TITLE)) {
                name = AnalyzerXML.getInfoNodeValue(infoNode);
                int rowCount = dataModel.getRowCount();
                int col = dataModel.findColumn(SongLibraryPanel.HEADER_NAME_COLUMN_TITLE);
                for (int j = 0; j < rowCount; j++) {
                    if (dataModel.getValueAt(j, col).equals(name)) {
                        dataModel.removeRow(j);
                        if (dataModelPairs != null) {
                            Pair<String, Node> p;
                            for (int index = 0; index < dataModelPairs.size(); index++) {
                                p = dataModelPairs.get(index);
                                if (name.equals(p.getKey())) {
                                    dataModelPairs.remove(index);
                                }
                            }
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }
}
