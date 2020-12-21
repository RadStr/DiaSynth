package analyzer;

import analyzer.observer.DataModelObserverIFace;
import main.AddToAudioPlayerIFace;
import org.w3c.dom.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class SongInfoFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel wholeWindowPanel;
	
	public SongInfoFrame(JFrame previousWindow, int frameWidth, int frameHeight, Node node, DataModelObserverIFace[] observers,
						 AddToAudioPlayerIFace addToAudioPlayerIFace) {
		this.setMinimumSize(new Dimension(frameWidth, frameHeight));
		this.setSize(frameWidth, frameHeight);	
		wholeWindowPanel = new SongInfoPanel(previousWindow, this, node, observers, addToAudioPlayerIFace);
		this.add(wholeWindowPanel);
		
		JFrame frame = this;
		this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
            	previousWindow.setVisible(true);
            	frame.setVisible(false);
            	frame.dispose();
            	
            }
        });
	}
}
