package RocnikovyProjektIFace.AudioWavePanelOnlyWavePopupMenuPackage;

public interface AudioWavePanelOnlyWavePopupMenuCommunicationIFace {
    void copyWave();
    void removeWave();
    void cutWave();
    void pasteWave(int copyCount);
    void pasteWaveWithOverwriting(int copyCount);
    void moveWave();
    void cleanWave();
}
