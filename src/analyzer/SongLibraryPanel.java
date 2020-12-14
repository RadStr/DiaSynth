package analyzer;

import analyzer.observer.DataModelObserver;
import analyzer.observer.DataModelObserverIFace;
import player.LeavingPanelIFace;
import util.Pair;
import analyzer.util.UneditableTableModel;
import main.AddToAudioPlayerIFace;
import player.util.ErrorFrame;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SongLibraryPanel extends JPanel implements LeavingPanelIFace {
	private static final long serialVersionUID = 1L;
	
	private JButton unmarkSelectedButton;
	private JButton unmarkAllButton;
	private JButton addToSelectedButton;
	private JButton deleteSelectedButton;
	private JButton clearSelectedButton;
	private JButton showInfoAboutSongButton;
	private JButton[] buttons;
	private JPanel buttonPanel;

	private DefaultTableModel dataModelSelectedFiles;
	private JScrollPane selectedFilesPane;
	private JTable selectedFilesTable;
	private JLabel selectedFilesLabel;
	private List<Pair<String, Node>> selectedFilesPairList;
	private DataModelObserverIFace selectedFilesObserver;
	public DataModelObserverIFace getSelectedFilesObserver() {
		return selectedFilesObserver;
	}
	private final String[] headerSelectedFiles;

	private DefaultTableModel dataModelAllFiles;
	private JTable allFilesTable;
	private JScrollPane allFilesPane;
	private JLabel allFilesLabel;
	private List<Pair<String, Node>> allFilesPairList;
	private DataModelObserverIFace allFilesObserver;
	public DataModelObserverIFace getAllFilesObserver() {
		return allFilesObserver;
	}
	private final String[] headerAllFiles;	// TODO: Asi bych mel zobrazit vsechny ... mel bych je definovat nekde mimo ... a delat to pres pluginy

	private JFrame thisFrame;

	public static String HEADER_NAME_COLUMN_TITLE = "Name";
    public static String HEADER_LENGTH_COLUMN_TITLE = "Length";
	
	public SongLibraryPanel(JFrame thisFrame, AddToAudioPlayerIFace addToAudioPlayerIFace) {
		this.thisFrame = thisFrame;
		this.setLayout(new BorderLayout());
		selectedFilesPairList = new ArrayList<>();
		headerSelectedFiles = new String[] {HEADER_NAME_COLUMN_TITLE, HEADER_LENGTH_COLUMN_TITLE};
		headerAllFiles = new String[] {HEADER_NAME_COLUMN_TITLE, HEADER_LENGTH_COLUMN_TITLE};
		
		Box boxSelectedFiles = Box.createVerticalBox();
		selectedFilesLabel = new JLabel("Selected files");

		selectedFilesTable = new JTable();
		dataModelSelectedFiles = null;
		selectedFilesObserver = new DataModelObserver(dataModelSelectedFiles, selectedFilesPairList, thisFrame) {
			@Override
			public void reloadDataModelFromXML() {
				// TODO: Provizorni reseni prozatim jen vymazeme selected files, kdyz nastane analyzovani
				selectedFilesPairList.clear();
				dataModelSelectedFiles = new UneditableTableModel(new String[0][0], headerSelectedFiles);
				selectedFilesTable.setModel(dataModelSelectedFiles);

				this.setDataModel(dataModelSelectedFiles, selectedFilesPairList);
			}
		};	
		        
		selectedFilesObserver.reloadDataModelFromXML();
	    selectedFilesPane = new JScrollPane(selectedFilesTable) {
			@Override
			public Dimension getPreferredSize() {
				Dimension size = thisFrame.getMinimumSize();
				return new Dimension(80 * size.width / 100, 2 * size.height / 3);
			}
		};
	    boxSelectedFiles.add(selectedFilesLabel);
	    boxSelectedFiles.add(selectedFilesPane);
	    this.add(boxSelectedFiles, BorderLayout.CENTER);
		
	    
		
		allFilesLabel = new JLabel("All analyzed files");
		Box boxAllFiles = Box.createVerticalBox();    	
		
	    allFilesObserver = new DataModelObserver(dataModelAllFiles, allFilesPairList, thisFrame) {		
			@Override
			public void reloadDataModelFromXML() {
			        allFilesPairList = AnalyzerXML.getPairs(AnalyzerXML.getXMLDoc());
					Comparator<Pair<String, Node>> comp = 
						   (Pair<String, Node> p1,Pair<String, Node> p2) -> p1.getKey().compareTo(p2.getKey());
			        allFilesPairList.sort(comp);
			        String[][] dataAllFiles = new String[allFilesPairList.size()][headerAllFiles.length];
			        for(int i = 0; i < dataAllFiles.length; i++) {
			        	Node n = allFilesPairList.get(i).getValue();
			        	NodeList childs = n.getChildNodes();
			        	for(int j = 0; j < dataAllFiles[i].length; j++) {
			        		Node n1 = AnalyzerXML.findFirstNodeWithGivenAttribute(childs, headerAllFiles[j]);
			        		if(n1 == null) {
			        			dataAllFiles[i][j] = "UNKNOWN";
			        		}
			        		else {
			        			dataAllFiles[i][j] = AnalyzerXML.getInfoNodeValue(n1);
			        		}
			        	}
			        }
			        
			        dataModelAllFiles = new UneditableTableModel(dataAllFiles, headerAllFiles);

					this.setDataModel(dataModelAllFiles, allFilesPairList);
			        allFilesTable.setModel(dataModelAllFiles);
			}
		};

	    allFilesTable = new JTable();
	    allFilesPane = new JScrollPane(allFilesTable) {
			@Override
			public Dimension getPreferredSize() {
				Dimension size = thisFrame.getMinimumSize();
				return new Dimension(size.width, size.height / 3);
			}
		};
		allFilesObserver.reloadDataModelFromXML();
	    
	    boxAllFiles.add(allFilesLabel);
	    boxAllFiles.add(allFilesPane);
	    this.add(boxAllFiles, BorderLayout.SOUTH);
		
		
		buttonPanel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				Dimension size = thisFrame.getMinimumSize();
				return new Dimension(20 * size.width / 100, 2 * size.height / 3);
			}
		};
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		clearSelectedButton = new JButton();
		clearSelectedButton.setLayout(new BoxLayout(clearSelectedButton, BoxLayout.Y_AXIS));
		JLabel l1 = new JLabel("Clears selected");
		JLabel l2 = new JLabel("(Only removes them");	
		JLabel l3 = new JLabel("from selected files list)");
		clearSelectedButton.add(l1);
		clearSelectedButton.add(l2);
		clearSelectedButton.add(l3);
		clearSelectedButton.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				actionClearPerformed(e);		
			}
		});
		
		deleteSelectedButton = new JButton();
		deleteSelectedButton.setLayout(new BoxLayout(deleteSelectedButton, BoxLayout.Y_AXIS));
		l1 = new JLabel("Delete selected");
		l2 = new JLabel("(Removes analyzed info)");	
		deleteSelectedButton.add(l1);
		deleteSelectedButton.add(l2);
		deleteSelectedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionDeletePerformed(e);
			}
		});
		
		showInfoAboutSongButton = new JButton("Show info about song");
		showInfoAboutSongButton.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow;
				if((selectedRow = selectedFilesTable.getSelectedRow()) != -1) {
					DataModelObserverIFace[] observers = new DataModelObserverIFace[] 
							{ allFilesObserver, selectedFilesObserver };
					Pair<String, Node> p = selectedFilesPairList.get(selectedRow);			// TODO: Budu mit navic list, kde indexy budou odpovidat presne tem indexum v tabulce selected
					Dimension size = thisFrame.getSize();
					SongInfoFrame siFrame = new SongInfoFrame(thisFrame, size.width, size.height, p.getValue(), observers, addToAudioPlayerIFace);
					siFrame.setVisible(true); 
					System.out.println("SELECTED:\t" + selectedRow + "\t" + p.getValue().getTextContent() + "\t" + selectedFilesPairList.size());
				}
				
			}
		});
		
		addToSelectedButton = new JButton("Add to selected");
		addToSelectedButton.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAddToSelectedPerformed(e);		
			}
		});
		
		unmarkSelectedButton = new JButton("Unmark selected");
		unmarkSelectedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedFilesTable.getSelectionModel().clearSelection();	
			}
		});
		
		unmarkAllButton = new JButton("Unmark from all files");
		unmarkAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				allFilesTable.getSelectionModel().clearSelection();
			}
		});
		
		buttons = new JButton[6];
		buttons[0] = showInfoAboutSongButton;
		buttons[1] = deleteSelectedButton;
		buttons[2] = clearSelectedButton;
		buttons[3] = addToSelectedButton;
		buttons[4] = unmarkSelectedButton;
		buttons[5] = unmarkAllButton;
		
		for(int i = 0; i < buttons.length; i++) {	
			buttonPanel.add(buttons[i]);
			buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		}
		Box vertical = Box.createVerticalBox();
		vertical.add(Box.createVerticalGlue());
		vertical.add(buttonPanel);
		vertical.add(Box.createVerticalGlue());
		this.add(vertical, BorderLayout.EAST);
	}
	
	
	private void actionClearPerformed(ActionEvent e) {
		int[] rows = selectedFilesTable.getSelectedRows();
		Arrays.sort(rows);
		for(int i = rows.length - 1; i >= 0; i--) {
			dataModelSelectedFiles.removeRow(rows[i]);
			selectedFilesPairList.remove(rows[i]);
		}
	}
	
	private void actionDeletePerformed(ActionEvent e) {
		Document xmlDoc = AnalyzerXML.getXMLDoc();
		Node root = xmlDoc.getFirstChild();
		System.out.println("Text context of songs node:\t" + root.getTextContent());
		int[] rows = selectedFilesTable.getSelectedRows();
		Arrays.sort(rows);
		for(int i = rows.length - 1; i >= 0; i--) {
			String name = (String) dataModelSelectedFiles.getValueAt(rows[i], 0);
			dataModelSelectedFiles.removeRow(rows[i]);	
			for(int l = 0; l < selectedFilesPairList.size(); l++) {
				System.out.println("NAME:\t" + selectedFilesPairList.get(l).getKey());
				System.out.println("Text context of node:\t" + selectedFilesPairList.get(l).getValue().getTextContent());
				System.out.println("NAME of parent node:\t" + selectedFilesPairList.get(l).getValue().getParentNode().getNodeName());
				System.out.println("Text context of parent node:\t" + selectedFilesPairList.get(l).getValue().getParentNode().getTextContent());
				System.out.println("Count of nodes with name songs:\t" + xmlDoc.getElementsByTagName("songs").getLength());
				System.out.println("Length of selectedFilesPairList:\t" + selectedFilesPairList.size());
				System.out.println("Are parent and root same:\t" + (selectedFilesPairList.get(l).getValue().getParentNode() == root));
				System.out.println("Are pairs the same:\t" + (allFilesPairList.get(l).getValue().getParentNode() == selectedFilesPairList.get(l).getValue().getParentNode()));
				System.out.println("Are allpairs and root the same:\t" + (allFilesPairList.get(l).getValue().getParentNode() == root));
			}
			root.removeChild(selectedFilesPairList.get(rows[i]).getValue());
			selectedFilesPairList.remove(rows[i]);
// TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!			removeDataFromFile(name);
			int j = 0;
			try {
				while(dataModelAllFiles.getValueAt(j, 0) != name) {
					j++;
				}
				dataModelAllFiles.removeRow(j);
				allFilesPairList.remove(j);
			}
			catch (ArrayIndexOutOfBoundsException ex) {
				AnalyzerXML.createXMLFile(AnalyzerPanel.ANALYZED_AUDIO_XML_FILENAME, root, thisFrame);
				new ErrorFrame((JFrame) SwingUtilities.getWindowAncestor(this), "File doesn't exist anymore");
			}
		}

		AnalyzerXML.removeInvalidNodes(root);
		AnalyzerXML.createXMLFile(AnalyzerPanel.ANALYZED_AUDIO_XML_FILENAME, root, thisFrame);
	}

	
	private void actionAddToSelectedPerformed(ActionEvent e) {
		int[] rows = allFilesTable.getSelectedRows();
		int colCount = dataModelSelectedFiles.getColumnCount();
		int rowCount = dataModelSelectedFiles.getRowCount();
		for(int i = 0; i < rows.length; i++) {
			// First check if the file isn't already in
			String name = (String)dataModelAllFiles.getValueAt(rows[i], 0);
			int k;
			for(k = 0; k < rowCount; k++) {
				if(name == dataModelSelectedFiles.getValueAt(k, 0)) {
					// TODO: DEBUG
					System.out.println("actionAddToSelectedPerformed ... == name");
					// TODO: DEBUG
					break;
				}
			}
			
			if(k >= rowCount) { // TODO: Tohle nemuze fungovat, protoze ty selected a allfiles maji jinak poskladany sloupce
				selectedFilesPairList.add(allFilesPairList.get(rows[i]));
				Object[] arr = new Object[colCount];
				for(int j = 0; j < colCount; j++) {
					arr[j] = dataModelAllFiles.getValueAt(rows[i], j);
				}
				dataModelSelectedFiles.addRow(arr);
			}
		}
		allFilesTable.getSelectionModel().clearSelection();
	}
	
	
	// TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// TODO: Muzu to odfocusovat tim ze to delam pres ctrl + kliknuti ale ne pres samotny kliknuti
	// TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	

	@Deprecated // Issue with the findNodeXML
	private void addRowToDataModel(DefaultTableModel tableModel, List<Pair<String, Node>> pairList, Node nodeToAdd) {
		int colCount = tableModel.getColumnCount();
		Object[] row = new Object[colCount];
		NodeList childs = nodeToAdd.getChildNodes();
		for(int i = 0; i < colCount; i++) {
			String colName = tableModel.getColumnName(i);
			Node val = AnalyzerXML.findNodeXML(childs, colName);
			if(val == null) {
				row[i] = "";
			}
			else {
				row[i] = val.getTextContent();
			}
		}
		
		tableModel.addRow(row);
		Node nameNode = AnalyzerXML.findNodeXML(childs, "name");
		if(nameNode == null) {
			pairList.add(new Pair<String, Node>("", nodeToAdd));
		}
		else {
			pairList.add(new Pair<String, Node>(nameNode.getTextContent(), nodeToAdd));
		}
	}

	@Override
	public void leavingPanel() {

	}
}
