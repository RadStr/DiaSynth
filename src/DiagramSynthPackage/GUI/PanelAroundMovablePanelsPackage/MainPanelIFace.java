package DiagramSynthPackage.GUI.PanelAroundMovablePanelsPackage;

import RocnikovyProjektIFace.AudioFormatChooserPackage.AudioFormatWithSign;

import javax.sound.sampled.AudioFormat;
import java.awt.*;

/**
 * Used by JPanelWithMovableJPanels to communicate with MainPanelWithEverything
 */
public interface MainPanelIFace extends PlayerButtonPanelGetterIFace {
    void clickRealTimeRecordingCheckbox();
    void putRecordedWaveToPlayer(byte[] record, int len, AudioFormatWithSign outputFormat,
                                 boolean shouldConvertToPlayerOutputFormat);
    Dimension getSize();
    int getTopButtonsPreferredHeight();
}
