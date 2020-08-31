package RocnikovyProjektIFace.Drawing;

import java.awt.*;

public class FunctionWaveDrawPanel extends WaveDrawPanel {
    public FunctionWaveDrawPanel(int binCount, boolean isEditable, Color backgroundColor) {
        super(binCount, "Input Value", isEditable, backgroundColor);
        setLabels();
        normalizeAndSetDrawValues();
        setLastPartOfTooltip();
    }


    /**
     * Isn't called anywhere it is just marker, that the labels needs to be set in deriving class.
     */
    @Override
    protected void setLabels() {
        createLabels(drawValues.length);
    }

    private void createLabels(int len) {
        labels = new String[len];
        double val = -1;
        double valJump = 2.0d / (len - 1);           // 2 Because I have range -1 to 1 I need to split it to the labels and -1 because the first doesn't count
        for(int i = 0; i < labels.length; i++, val += valJump) {
            labels[i] = String.format("%.2f", val);
        }
    }
}
