package RocnikovyProjektIFace;

import PartsConnectingGUI.AddToAudioPlayerIFace;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class SongInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private FrameChangeWithDisposeButton returnButton;
	private JButton addToAudioPlayerButton;
	private JButton playSongButton;
	private JButton deleteMetadata;

	private JTable table;
	private JScrollPane scrollPane;

	private DataModelSubjectIFace subject;
	// TODO: Budu mit metodu String[] getData(String songName) a to mi vrati vsechny ty data o tom souboru (ty hodnoty)
	public SongInfoPanel(JFrame previousWindow, JFrame thisWindow, Node node, DataModelObserverIFace[] observers,
						 AddToAudioPlayerIFace addToAudioPlayerIFace) {
		this.setLayout(new BorderLayout());
		subject = new DataModelSubject(observers, thisWindow);
		
        String[] header = {"Property name", "Property value"};   
// TODO: Novy uz primo pres ty nody
        NodeList childs = node.getChildNodes();
        int childsLen = AnalyzerXML.getValidInfoNodeCount(childs);
        String[][] data = new String[childsLen][2];
        
        for(int i = 0, j = 0; i < data.length; j++) {
        	Node nTmp = childs.item(j);
			data[i][0] = AnalyzerXML.getInfoNodeName(nTmp);
			if(data[i][0] == null) {
				continue;
			}
			data[i][1] = AnalyzerXML.getInfoNodeValue(nTmp);
			i++;
        }
//        
/*      //  TODO: Bylo stary jen na testovani  
        String[] names = new String[] {"RMS", "LENGTH"};// TODO: Dostat jmena tech properties - to mi preda hlavni program
        												// Nebo si to spis nactu ze souboru s metadaty      
        String[][] data = new String[names.length][2];
        for (int ii=0; ii<names.length; ii++) {
            data[ii][0] = names[ii];
            data[ii][1] = ((Integer)ii).toString();
        }
*/        
        DefaultTableModel dataModel = new UneditableTableModel(data, header);
	    table = new JTable(dataModel);
	    scrollPane = new JScrollPane(table);
	    this.add(scrollPane, BorderLayout.CENTER);


		returnButton = new FrameChangeWithDisposeButton(thisWindow, previousWindow, "Close");

		addToAudioPlayerButton = new JButton("Add to audio player");
		addToAudioPlayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String path = getPathFromXML(node);
				if(path != null) {
					addToAudioPlayerIFace.addToAudioPlayer(path);
				}
			}
		});


		playSongButton = new JButton("Play Song");
		playSongButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				String path = getPathFromXML(node);
				if(path != null) {
					String[] arr = new String[2];
					arr[0] = "C:\\Program Files (x86)\\Windows Media Player\\wmplayer.exe";		// TODO: Specific for my system
					arr[1] = path;
					System.out.println(arr[1]);
					ProcessBuilder pb = new ProcessBuilder(arr);	
					try {						
						pb.start();
					} catch (IOException e1) {
						System.exit(-10);           // TODO:
					}	
				}	
			}
		});
		
		deleteMetadata = new JButton("Delete metadata");
		deleteMetadata.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				Node root = AnalyzerXML.getXMLDoc().getElementsByTagName("songs").item(0);
				NodeList childs = root.getChildNodes();
				int len = childs.getLength();
				boolean nodeExists = false;
				for(int i = 0; i < len; i++) {
					if(childs.item(i).isSameNode(node)) {
						nodeExists = true;
						break;
					}
				}
				if(nodeExists) {
					root.removeChild(node);	// There should be only one such tag ("songs")
					AnalyzerXML.removeInvalidNodes(root);
					AnalyzerXML.createXMLFile(AnalyzerPanel.ANALYZED_AUDIO_XML_FILENAME, root, previousWindow);
					subject.notifyObservers(node);
				}
			}
		});
		
	    
		// TODO: Nejak si jeste pohrat s tema set prefered size atd
	    JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(playSongButton);
	    buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
	    buttonPanel.add(addToAudioPlayerButton);
	    buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonPanel.add(deleteMetadata);
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonPanel.add(returnButton);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}


	private static String getPathFromXML(Node node) {
		NodeList childs = node.getChildNodes();
		Node n = AnalyzerXML.findFirstNodeWithGivenAttribute(childs, "Path");
		if (n != null) {
			return AnalyzerXML.getInfoNodeValue(n);
		}

		return null;
	}
	
}
