package RocnikovyProjektIFace;

import analyzer.AnalyzerMainPanel;

import javax.swing.*;
import java.awt.*;


/**
 * This panel represents the menu of game.
 * It contains play button, exit button.
 */
@Deprecated		// Not used anymore, this was just for testing - Alsi tak a look at the LAMBDA comment
public class MenuFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private AnalyzerMainPanel wholeWindowPanel;
	public AnalyzerMainPanel getWholeWindowPanel() {
	    return wholeWindowPanel;
    }
	
	public MenuFrame(int frameWidth, int frameHeight) {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSizeInternal(frameWidth, frameHeight);
        // I have to call setMinimumSize even when I had overridden the getMinimumSize in the frame, because
        // java needs some impulse to take the minimum size into consideration
        // (I could set it to random min size here and it would still have the frameWidth, frameHeight min size)
        // But it changes the start size if it is bigger than the min size
		this.setMinimumSize(new Dimension(frameWidth, frameHeight));
		this.setTitle("Menu");
		// LAMBDA - this lambda was added because it is needed when I want to have feature to add analyzed song to the audio player
		// And since MenuFrame isn't using audio player (it is used when I am testing analyzer separate) then I don't do anything in that method
		wholeWindowPanel = new AnalyzerMainPanel(this, null);
		this.add(wholeWindowPanel);
	}


    private Dimension minSize;
    private void setMinimumSizeInternal(int minFrameWidth, int minFrameHeight) {
        minSize = new Dimension(minFrameWidth, minFrameHeight);
    }

    @Override
    public Dimension getMinimumSize() {
        return minSize;
    }
}
