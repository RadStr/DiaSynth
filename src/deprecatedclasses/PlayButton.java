package deprecatedclasses;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Pause/play button
 */
@Deprecated // I use images instead - BooleanButtonWithImages
public class PlayButton extends JButton implements ActionListener {
    public boolean isPaused;

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public PlayButton() {
        isPaused = false;
        this.setText("Playing");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        isPaused = !isPaused;
        if (isPaused) {
            this.setText("Paused");
        }
        else {
            this.setText("Playing");
        }
    }
}
