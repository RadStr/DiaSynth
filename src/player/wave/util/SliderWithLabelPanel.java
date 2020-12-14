package player.wave.util;

import player.wave.WaveMixPanelUpdaterIFace;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class SliderWithLabelPanel extends JPanel {
    public JLabel label;
    public JSlider slider;

    private double normalizedValue;
    public double getNormalizedValue() {
        return normalizedValue;
    }
    private void setNormalizedValue(int value) {
        int min = slider.getMinimum();
        int max = slider.getMaximum();
        int range = max - min;
        double normVal = value - min;
        normalizedValue = normVal / range;
    }

    public SliderWithLabelPanel(int orientation, int minVal, int maxVal, int defVal, String sliderName,
                                WaveMixPanelUpdaterIFace mainPanel, int index, boolean isLabelOnLeft) {
        this.label = new JLabel(sliderName);
        slider = new JSlider(orientation, minVal, maxVal, defVal);


        if(orientation == SwingConstants.VERTICAL) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }
        else {      // It must be horizontal, else there would be exception on creation of JSlider
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        }
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                setNormalizedValue(source.getValue());
                mainPanel.update(index, normalizedValue);
            }
        });

        if(isLabelOnLeft) {
            this.add(label);
            this.add(slider);
        }
        else {
            this.add(slider);
            this.add(label);
        }
        setNormalizedValue(slider.getValue());
    }
}
