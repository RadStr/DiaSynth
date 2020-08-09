package RocnikovyProjektIFace;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AudioWavePanelOnlyButtons extends JPanel {
    private JCheckBox includeInMixing;
    public boolean shouldIncludeInMixing() {
        return includeInMixing.isSelected();
    }

    private JCheckBox includeInOperations;
    public boolean shouldIncludeInOperations() {
        return includeInOperations.isSelected();
    }


    // Main panel (AudioWavePanelEverything) needs to be passed to repaint the wave if checkbox is pressed
    public AudioWavePanelOnlyButtons(AudioWavePanelEverything awpe) {

        includeInMixing = new JCheckBox("Include in mixing");
        includeInMixing.setSelected(true);
        includeInMixing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                awpe.updateWavesForMixing();
            }
        });




        includeInOperations = new JCheckBox("Include in operations");
        includeInOperations.setSelected(true);
        includeInOperations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                awpe.repaint();     // Repaint because checkbox changes the wave visualisation
            }
        });



        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(includeInMixing);
        this.add(includeInOperations);
    }

}
