package analyzer.util;

import player.util.ErrorFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Button which makes current panel dispose and some other visible
 */
public class FrameChangeWithDisposeButton extends JButton {
	private static final long serialVersionUID = 1L;	
	
	/**
	 * Constructor
	 * @param oldFrame is the frame which will be disposed.
	 * @param newFrame is the frame which will be made visible.
	 * @param name is the name of the button
	 */
	public FrameChangeWithDisposeButton(JFrame oldFrame, JFrame newFrame, String name) {	
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
				oldFrame.dispose();
			}
		});
	}
}
