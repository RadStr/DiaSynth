package deprecatedclasses;

import player.experimental.DoubleDrawPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ProgramExperimental {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                runProgram();
            }
        });
    }

    public static void runProgram() {
        JFrame f = new JFrame();
        JPanel p = new JPanel();
        
      
        p.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;
        
        
        
        f.setContentPane(p);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        DoubleDrawPanel drawPanel = new DoubleDrawPanel();
        LabelAndTextField timePart = new LabelAndTextField("Time:", 1000, null);
        
        p.add(drawPanel, c);
           
        c.gridy = 1;
        c.weighty = 0.000001;
        p.add(timePart, c);
       
        p.setPreferredSize(new Dimension(1600, 500));
        f.pack();
        f.setVisible(true);
    }
}