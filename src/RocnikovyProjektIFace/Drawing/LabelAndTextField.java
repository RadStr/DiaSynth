package RocnikovyProjektIFace.Drawing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextField;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class LabelAndTextField extends JPanel {
	public LabelAndTextField(String labelText, double defaultValue, TextFieldCallbackInterface callbackClass) {
		this.callbackClass = callbackClass;
		this.label = new JLabel(labelText);
		this.textField = new JTextField(Double.toString(defaultValue));
		
		
		setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.1;
        c.weighty = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;
		
		
		this.add(label, c);
		
		c.weightx = 1;
		c.gridx = 1;
		this.add(textField, c);
	}
	
	private JLabel label;
	private JTextField textField;
	private TextFieldCallbackInterface callbackClass;
}