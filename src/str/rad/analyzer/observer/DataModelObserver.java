package str.rad.analyzer.observer;

import str.rad.analyzer.AnalyzerXML;
import str.rad.analyzer.SongLibraryPanel;
import str.rad.util.Pair;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public abstract class DataModelObserver implements DataModelObserverIFace {
    private DefaultTableModel dataModel;
    private JFrame frame;
    private List<Pair<String, Node>> dataModelPair;

    public void setDataModel(DefaultTableModel dataModel, List<Pair<String, Node>> dataModelPair) {
        this.dataModel = dataModel;
        this.dataModelPair = dataModelPair;
    }

    public DataModelObserver(DefaultTableModel dm, List<Pair<String, Node>> dmp, JFrame frame) {
        this.dataModel = dm;
        dataModelPair = dmp;
        this.frame = frame;
    }

    @Override
    public boolean update(Node nodeToBeRemoved) {
        String name;
        NodeList infoNodes = nodeToBeRemoved.getChildNodes();
        int len = infoNodes.getLength();

        for (int i = 0; i < len; i++) {
            Node infoNode = infoNodes.item(i);

            if (AnalyzerXML.isMatchingGivenAttribute(infoNode, "name", SongLibraryPanel.HEADER_NAME_COLUMN_TITLE)) {
                name = AnalyzerXML.getInfoNodeValue(infoNode);
                int rowCount = dataModel.getRowCount();
                int col = dataModel.findColumn(SongLibraryPanel.HEADER_NAME_COLUMN_TITLE);
                for (int j = 0; j < rowCount; j++) {
                    if (dataModel.getValueAt(j, col).equals(name)) {
                        dataModel.removeRow(j);
                        if (dataModelPair != null) {
                            Pair<String, Node> p;
                            for (int index = 0; index < dataModelPair.size(); index++) {
                                p = dataModelPair.get(index);
                                if (name.equals(p.getKey())) {
                                    dataModelPair.remove(index);
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
