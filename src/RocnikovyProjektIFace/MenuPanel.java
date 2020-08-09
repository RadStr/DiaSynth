package RocnikovyProjektIFace;

import PartsConnectingGUI.AddToAudioPlayerIFace;
import PartsConnectingGUI.ChangeJMenuBarIFace;
import Rocnikovy_Projekt.MyLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel implements ChangeJMenuBarIFace {
	private static final long serialVersionUID = 1L;



	private JFrame thisFrame;
	
	public MenuPanel(JFrame frame, AddToAudioPlayerIFace addToAudioPlayerIFace) {
		XML.setXMLDoc(AnalyzerPanel.ANALYZED_AUDIO_XML_FILENAME, frame, "songs");

    	thisFrame = frame;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		songLib = new SongLibraryFrame(frame, frameWidth, frameHeight);

		MyLogger.log("Creating song library panel", 1);
		songLibraryPanel = new SongLibraryPanel(frame, addToAudioPlayerIFace);
		DataModelObserverIFace[] observers = new DataModelObserverIFace[] {
				songLibraryPanel.getAllFilesObserver(), songLibraryPanel.getSelectedFilesObserver()
		};
		MyLogger.log("Created song library panel", -1);
		MyLogger.log("Creating analyzer panel", 1);
		analyzerPanel = new AnalyzerPanel(frame, observers);
//		analyzer = new AnalyzerFrame(frame, frameWidth, frameHeight, observers);
		MyLogger.log("Created analyzer panel", -1);
		menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menuBar.add(menu);

		JMenuItem menuItem;

		menuItem = new JMenuItem("Show analyzed files");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				swapPanels(songLibraryPanel);
			}
		});


		menuItem = new JMenuItem("Choose files to analyze");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				swapPanels(analyzerPanel);
			}
		});


		this.add(songLibraryPanel);
		currentPanel = songLibraryPanel;
	}


	private SongLibraryPanel songLibraryPanel;
	private AnalyzerPanel analyzerPanel;

	private LeavingPanelIFace currentPanel = null;
	private void swapPanels(LeavingPanelIFace newPanel) {
		currentPanel.leavingPanel();
		this.remove((Component)currentPanel);
		currentPanel = newPanel;
		this.add((Component)currentPanel);

		this.revalidate();
		this.repaint();
	}

	private JMenuBar menuBar;
	@Override
	public void changedTabAction(boolean isNewlyVisible) {
		if(isNewlyVisible) {
			thisFrame.setJMenuBar(menuBar);
		}
	}
}
