package RocnikovyProjektIFace;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public abstract class DataModelObserver implements DataModelObserverIFace {
	DefaultTableModel dataModel;
	JFrame frame;
	List<Pair<String, Node>> dataModelPair;
	
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
		
		System.out.println("!!!!!!!!!!!!!!DATAMODEL!!!!!!!!!!!!!");
		for(int i = 0; i < dataModel.getColumnCount(); i++) {
			System.out.println(dataModel.getColumnName(i));
		}
		System.out.println("!!!!!!!!!!!!!!UPDATE!!!!!!!!!!!!!");
		for(int i = 0; i < len; i++) {
			Node infoNode = infoNodes.item(i);
			
			System.out.println(infoNode.getNodeName() + "\t" + infoNode.getTextContent());
			System.out.println("--------------------");
			
			if(AnalyzerXML.isMatchingGivenAttribute(infoNode, "name", SongLibraryPanel.HEADER_NAME_COLUMN_TITLE)) {
				name = AnalyzerXML.getInfoNodeValue(infoNode);
				int rowCount = dataModel.getRowCount();
				int col = dataModel.findColumn(SongLibraryPanel.HEADER_NAME_COLUMN_TITLE);
				for(int j = 0; j < rowCount; j++) {
					if(dataModel.getValueAt(j, col).equals(name)) {
						dataModel.removeRow(j);
						if(dataModelPair != null) {
							System.out.println(dataModelPair.size());
							Pair<String, Node> p;
							for(int index = 0; index < dataModelPair.size(); index++) {
								p = dataModelPair.get(index);
								if(name.equals(p.getKey())) {
									dataModelPair.remove(index);
								}
							}
							System.out.println("datamodelpair removed" + dataModelPair.size());
						}
						return true;
					}
				}				
			}
		}
		
		return false;
	}
}
