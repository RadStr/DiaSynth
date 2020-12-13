package player;

import player.util.ErrorFrame;
import analyzer.AnalyzerPanel;
import analyzer.AnalyzerXML;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// TODO: Probably useless, I solved this problem by observer pattern

/**
 * Button which makes current panel invisible and some other visible. And also update the xmlDoc property in XML.
 */
@Deprecated
public class FrameChangeButtonUpdateXML extends JButton {	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * @param oldFrame is the frame which will be made invisible.
	 * @param newFrame is the frame which will be made visible.
	 * @param name is the name of the button
	 */
	public FrameChangeButtonUpdateXML(JFrame oldFrame, JFrame newFrame, String name) {	
		this.setText(name);
		// TODO:
		this.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				if(newFrame == null) {
					new ErrorFrame(oldFrame, "Problem with button changing windows");
					return;
				}
				newFrame.setVisible(true);
				oldFrame.setVisible(false);
				AnalyzerXML.setXMLDoc(AnalyzerPanel.ANALYZED_AUDIO_XML_FILENAME, oldFrame, "songs");
			}
		});
	}
}
