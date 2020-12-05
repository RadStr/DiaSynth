package DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage;

import RocnikovyProjektIFace.AudioFormatChooserPackage.AudioFormatWithSign;

import java.awt.*;

/**
 * Used by DiagramPanel to communicate with SynthesizerMainPanel
 */
public interface SynthesizerMainPanelIFace extends AudioControlPanelGetterIFace {
    void clickRealTimeRecordingCheckbox();
    void putRecordedWaveToPlayer(byte[] record, int len, AudioFormatWithSign outputFormat,
                                 boolean shouldConvertToPlayerOutputFormat);
    Dimension getSize();
    int getTopButtonsPreferredHeight();
}
