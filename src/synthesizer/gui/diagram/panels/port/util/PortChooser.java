package synthesizer.gui.diagram.panels.port.util;

import synthesizer.gui.diagram.panels.port.InputPort;
import synthesizer.gui.diagram.InputPortsGetterIFace;
import synthesizer.gui.diagram.ShapedPanels.PortFilterIFace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PortChooser extends JPanel {
    private PortChooser(InputPortsGetterIFace portsGetter, PortFilterIFace filter) {
        portButtons = new ArrayList<>();
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));


        InputPort[] inputPorts = portsGetter.getInputPorts();

        for(InputPort inputPort : inputPorts) {
            String portName = inputPort.getPortLabel().FULL_NAME;
            JButton portButton = new JButton(portName);
            if(!filter.isInputPortValid(inputPort)) {
                portButton.setEnabled(false);
            }
            portButtons.add(portButton);
            this.add(portButton);
            portButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chosenPort = inputPort;
                    // This presses ok on dialog and the behavior on what will depends on the dialog:
                    // 1 dialog is when connecting, so all that will do it will just connect to the certain input
                    // 2nd dialog is when removing, then I will return to the panelWithDiagrams and highlight -
                    // which is remove all the panels and cables, which are not connecting to this input port
                    // Now that I am thinking about it deleting maybe shouldn't be called on the input port but on the whole panel
                    // And I will just show on the panels to which input they are connected and by clicking on that panel if it will be connected to more than 1
                    // input port this dialog will appear
                    // Question - how to disable the deleting? - just by clicking on non-panel

                    PortChooser.closeDialog();
                }
            });
        }
    }

    private List<JButton> portButtons;

    private InputPort chosenPort = null;
    private InputPort getChosenPort() {
        return chosenPort;
    }



    // Combination of this https://stackoverflow.com/questions/18105598/closing-a-joptionpane-programmatically
    // and this            https://stackoverflow.com/questions/22417113/how-to-auto-click-ok-on-joptionpane-when-testing
    private static void pressOkOnDialog() {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof JDialog) {
                JDialog dialog = (JDialog) window;
                if (dialog.getContentPane().getComponentCount() == 1
                        && dialog.getContentPane().getComponent(0) instanceof JOptionPane) {
                    ((JOptionPane) dialog.getContentPane().getComponent(0)).setValue(JOptionPane.OK_OPTION);
                }
            }
        }
    }


    // Taken from: https://stackoverflow.com/questions/9860731/how-to-close-message-dialog-programmatically
    private static void closeDialog() {
        closeDialog(portChooser);
    }
    public static void closeDialog(Component dialogPanel) {
        Window win = SwingUtilities.getWindowAncestor(dialogPanel);
        win.dispose();
    }


    private static PortChooser portChooser;
    public static InputPort choosePort(InputPortsGetterIFace portsGetter, PortFilterIFace filter) {
        portChooser = new PortChooser(portsGetter, filter);
        Component panelContainingPort = (Component) portsGetter.getInputPorts()[0].getPanelWhichContainsPort();
        JOptionPane.showOptionDialog(panelContainingPort, portChooser,
                "Choose input port dialog", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{}, null);

        InputPort chosenPort;
        chosenPort = portChooser.getChosenPort();

        return chosenPort;
    }
}
