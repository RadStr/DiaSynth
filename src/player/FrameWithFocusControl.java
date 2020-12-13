package player;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FrameWithFocusControl extends JFrame {
    private boolean hasFocus = false;
    public boolean getHasFocus() {
        return hasFocus;
    }

    public FrameWithFocusControl(String name) {
        super(name);
        addWindowListener();
    }

    public FrameWithFocusControl() {
        super();
        addWindowListener();
    }

    private void addWindowListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
                hasFocus = false;
                System.out.println(hasFocus);
            }
            @Override
            public void windowDeiconified(WindowEvent e) {
                hasFocus = true;
                System.out.println(hasFocus);
            }


            @Override
            public void windowActivated(WindowEvent e) {
                hasFocus = true;
                System.out.println(hasFocus);
            }
            @Override
            public void windowDeactivated(WindowEvent e) {
                hasFocus = false;
                System.out.println(hasFocus);
            }
        });
    }
}
