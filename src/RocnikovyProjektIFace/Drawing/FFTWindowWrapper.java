package RocnikovyProjektIFace.Drawing;

import java.awt.*;

public class FFTWindowWrapper extends DrawWrapperClass {
    public FFTWindowWrapper(double[] audio, Color backgroundColor, double minValue, double maxValue) {
        super(new FFTWindowPanel(audio, true, backgroundColor), minValue, maxValue);
        this.drawnFunctionPanel = drawnFunctionPanel;
    }

    private FunctionWaveDrawPanel drawnFunctionPanel;

    @Override
    public double[] getOutputValues() {
        return drawnFunctionPanel.getDrawnWave();
    }
}
