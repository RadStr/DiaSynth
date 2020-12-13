package synthesizer.gui.diagram;

import synthesizer.gui.diagram.ShapedPanels.ShapedPanel;
import synthesizer.synth.Unit;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;


/**
 * add(int index, Unit element) works as add(Unit u) to not break the promise that it is sorted by Y (secondary sorted by x).
 */
public class ListSortedByY extends ArrayList<Unit> {
    private Object lock = new Object();
    private volatile boolean hasChanged = false;
    public boolean getHasChanged() {
        return hasChanged;
    }
    public void setHasChanged(boolean value) {
        synchronized (lock) {
            hasChanged = value;
        }
    }


    @Override
    public boolean add(Unit u) {
        Point unitRelativePos = u.getShapedPanel().getRelativePosToReferencePanel();
        int i = 0;
        for(; i < size(); i++) {
            ShapedPanel sp = get(i).getShapedPanel();
            Point listElemRelativePos = sp.getRelativePosToReferencePanel();
            if(unitRelativePos.y < listElemRelativePos.y ||
                    (unitRelativePos.y == listElemRelativePos.y && unitRelativePos.x <= listElemRelativePos.x)) {
                break;
            }
        }

        super.add(i, u);
        setHasChanged(true);
        return true;
    }

    @Override
    public void add(int index, Unit element) {
        add(element);
    }

    @Override
    public boolean addAll(Collection<? extends Unit> c) {
        for(Unit u : c) {
            add(u);
        }

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Unit> c) {
        addAll(c);
        return true;
    }



    @Override
    public boolean remove(Object o) {
        boolean retVal = super.remove(o);
        if(retVal) {
            setHasChanged(true);
        }
        return retVal;
    }
    @Override
    public Unit remove(int index) {
        Unit removedObject = super.remove(index);
        setHasChanged(true);
        return removedObject;
    }

    @Override
    public boolean removeAll(Collection c) {
        boolean retVal = super.removeAll(c);
        if(retVal) {
            setHasChanged(true);
        }
        return retVal;
    }
    @Override
    public boolean removeIf(Predicate filter) {
        boolean retVal = super.removeIf(filter);
        if(retVal) {
            setHasChanged(true);
        }
        return retVal;
    }
    @Override
    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        setHasChanged(true);
    }

    @Override
    public void clear() {
        super.clear();
        setHasChanged(true);
    }


    public Point getMaxY() {
        int lastIndex = size() - 1;
        if(lastIndex >= 0) {
            Unit u = get(lastIndex);
            return u.getShapedPanel().getRelativePosToReferencePanel();
        }
        else {
            return null;
        }
    }


    public void repairUnitPosition(Unit u) {
        remove(u);
        add(u);
    }
}
