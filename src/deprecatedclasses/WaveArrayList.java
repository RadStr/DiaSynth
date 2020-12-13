package deprecatedclasses;

import player.WaveMainPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;


/**
 * Only supported class is WaveMainPanel.
 */
@Deprecated // I wanted to have dynamic resizing of the label with the index of wave (on the left side), but
            // Java was resizing the whole wave when the number of digits in label changed, so I just dropped that feature.
            // Now we have 2(3) digits constantly.
public class WaveArrayList<T> extends ArrayList<T> {
    @Override
    public boolean add(T o) {
        boolean retVal = super.add(o);
        updateWaveIndexTextFields();
        return retVal;
    }

    @Override
    public void add(int index, T element) {
        super.add(index, element);
        updateWaveIndexTextFields();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean retVal = super.addAll(c);
        updateWaveIndexTextFields();
        return retVal;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean retVal = super.addAll(index, c);
        updateWaveIndexTextFields();
        return retVal;
    }

    @Override
    public boolean remove(Object o) {
        boolean retVal = super.remove(o);
        updateWaveIndexTextFields();
        return retVal;
    }
    @Override
    public T remove(int index) {
        T removedObject = super.remove(index);
        updateWaveIndexTextFields();
        return removedObject;
    }

    @Override
    public boolean removeAll(Collection c) {
        boolean retVal = super.removeAll(c);
        updateWaveIndexTextFields();
        return retVal;
    }
    @Override
    public boolean removeIf(Predicate filter) {
        boolean retVal = super.removeIf(filter);
        updateWaveIndexTextFields();
        return retVal;
    }
    @Override
    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        updateWaveIndexTextFields();
    }

    @Override
    public void clear() {
        super.clear();
        updateWaveIndexTextFields();
    }



    /**
     * Needs to be called to every time wave count is changed. Respectively every time it gets/loses new digit.
     * Upgrades the size of the text labels.
     */
    private void updateWaveIndexTextFields() {
        int digitCount = getDigitCount(this.size());
        updateWaveIndexTextFields(digitCount);
    }

    private void updateWaveIndexTextFields(int digitCount) {
        int len = this.size();
        if(len > 0) {
            WaveMainPanel wave;
            wave = (WaveMainPanel) this.get(0);
            Dimension newSize = wave.upgradeWaveIndexTextFieldPreferredSize(digitCount);
            for(int i = 1; i < len; i++) {
                wave = (WaveMainPanel) this.get(i);
                wave.upgradeWaveIndexTextFieldPreferredSize(newSize);
            }
        }
    }

//    public void updateWaveIndexTextFields(Dimension newPreferredSize) {
//        int len = this.size();
//        if(len > 0) {
//            WaveMainPanel wave;
//            for(int i = 0; i < len; i++) {
//                wave = (WaveMainPanel) this.get(i);
//                wave.upgradeWaveIndexTextFieldPreferredSize(newPreferredSize);
//            }
//        }
//    }



    public static int getDigitCount(int number) {
        int digitCount = 1;
        while(number >= 10) {
            digitCount++;
            number /= 10;
        }
        return digitCount;
    }
}
