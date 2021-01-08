package str.rad.player.popup;

public interface WavePanelPopupMenuActionsIFace {
    void copyWave();

    void removeWave();

    void cutWave();

    void pasteWave(int copyCount);

    void pasteWaveWithOverwriting(int copyCount);

    void moveWave();

    void cleanWave();
}