package player.SpecialSwingClasses;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Button which closes the program.
 */
public class ExitButton extends JButton {
	private static final long serialVersionUID = 1L;

	public ExitButton() {
		this.setText("EXIT");
		this.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
		});
	}
}
