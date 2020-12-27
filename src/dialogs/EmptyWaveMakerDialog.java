package dialogs;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmptyWaveMakerDialog extends LengthDialog {
    public EmptyWaveMakerDialog() {
        super();
        numberOfWaves = 1;
        createPanel();
    }

    public EmptyWaveMakerDialog(int defaultLengthInSeconds) {
        super(defaultLengthInSeconds);
        numberOfWaves = 1;
        createPanel();
    }

    private int numberOfWaves;

    public int getNumberOfWaves() {
        return numberOfWaves;
    }

    private JLabel waveCountLabel;
    private JComboBox waveCountComboBox;
    private JLabel unitLabel;


    private void createPanel() {
        waveCountLabel = new JLabel("Choose number of waves");
        waveCountLabel.setToolTipText("Set number of waves to be added from toolbox");


        String[] values = new String[10];
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.toString(i + 1);
        }
        waveCountComboBox = new JComboBox(values);
        waveCountComboBox.setToolTipText("Number of waves");
        waveCountComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String selectedItem = (String) cb.getSelectedItem();
                numberOfWaves = Integer.parseInt(selectedItem);
            }
        });


        unitLabel = new JLabel("waves");

        this.add(waveCountLabel);
        this.add(waveCountComboBox);
        this.add(unitLabel);
    }
}
