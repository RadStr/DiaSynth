package util.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public abstract class BooleanButton extends JButton implements ActionListener {
    protected boolean boolVar;

    public boolean getBoolVar() {
        return boolVar;
    }

    public void setBoolVar(boolean val) {
        boolVar = val;
        setButtonVisuals();
    }

    private List<ActionListener> listeners;

    private static final long serialVersionUID = 1L;

    BooleanButton() {
        listeners = new ArrayList<>();
        super.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolVar = !boolVar;
        setButtonVisuals();
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).actionPerformed(e);
        }
    }


    abstract protected void setButtonVisuals();

    @Override
    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        listeners.remove(l);
    }

    @Override
    public ActionListener[] getActionListeners() {
        return listeners.toArray(new ActionListener[0]);
    }

}
