package dialogs;

import RocnikovyProjektIFace.plugin.JTextFieldWithBounds;
import RocnikovyProjektIFace.plugin.SetFieldIFace;
import Rocnikovy_Projekt.MyLogger;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

public class GetLengthDialog extends JPanel implements SetFieldIFace {
    public GetLengthDialog() {
        lenInSeconds = 60;
        createPanel();
    }

    public GetLengthDialog(int defaultLengthInSeconds) {
        lenInSeconds = defaultLengthInSeconds;
        createPanel();
    }


    public static final int MAX_LEN = 1800;
    private int lenInSeconds;

    /**
     * Returns length in seconds
     * @return
     */
    public int getLength() {
        return lenInSeconds;
    }
    private JLabel lenInSecondsLabel;
    private JTextFieldWithBounds lenInSecondsTextField;
    private JLabel unitLabel;

    private void createPanel() {
        this.setLayout(new GridLayout(0,3));
        lenInSecondsLabel = new JLabel("Write length of wave");


        try {
            Class<?> thisClass = GetLengthDialog.class;
            Field[] fields = thisClass.getDeclaredFields();
            Field field = null;
            for(Field f : fields) {
                if("lenInSeconds".equals(f.getName())) {
                    field = f;
                    break;
                }
            }
            field.setAccessible(true);

            lenInSecondsTextField = new JTextFieldWithBounds(false, 0, MAX_LEN,
                "WAVE LENGTH", field, this, this);
        }
        catch (IllegalAccessException e) {
            MyLogger.logException(e);
            System.exit(444);
        }


        unitLabel = new JLabel("seconds");
        this.add(lenInSecondsLabel);
        this.add(lenInSecondsTextField);
        this.add(unitLabel);
    }

    @Override
    public void setField(Field field, String value) {
        int val;
        if("".equals(value)) {
            val = 0;
        }
        else {
            val = Integer.parseInt(value);
        }
        lenInSeconds = val;
    }
}
