package DiagramSynthPackage.Synth.Operators.UnaryOperations;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.Synth.Unit;

public abstract class Delay extends UnaryOperator {
    public Delay(Unit u) {
        super(u);
    }

    public Delay(JPanelWithMovableJPanels panelWithUnits) {
        super(panelWithUnits);
    }

    public static class DelayArray {
        public DelayArray(int sampleRate, double delayTimeInMs) {
            changeDelay(sampleRate, delayTimeInMs);
        }

        private double[] delayArr;
        private int currIndex;

        public void changeDelay(int sampleRate, double delayTimeInMs) {
            int arrLen = (int)(sampleRate * delayTimeInMs / 1000);
            if(delayArr == null || delayArr.length < arrLen) {
                delayArr = new double[arrLen];
            }
            currIndex = 0;
        }

        /**
         * Storage and replacement, can be the same array
         * @param storage
         * @param replacement
         * @param startIndex
         * @param endIndex
         * @return
         */
        public int getAndReplace(double[] storage, double[] replacement, int startIndex, int endIndex) {
            int len = endIndex - startIndex;
            if(len > delayArr.length) {
                endIndex = startIndex + delayArr.length;
                len = delayArr.length;
            }

            for(int index = startIndex; startIndex < endIndex; index++, currIndex++) {
                if(currIndex >= delayArr.length) {
                    currIndex = 0;
                }
                double value = delayArr[currIndex];
                delayArr[currIndex] = replacement[index];
                storage[index] = value;
            }

            return len;
        }

        public int push(double[] pushValues, int startIndex, int endIndex) {
            int len = endIndex - startIndex;
            if(len > delayArr.length) {
                endIndex = startIndex + delayArr.length;
                len = delayArr.length;
            }
            int index = currIndex;
            for(int i = startIndex; i < endIndex; i++, index++) {
                if(index >= delayArr.length) {
                    index = 0;
                }
                delayArr[index] = pushValues[i];
            }

            return len;
        }

        public int pushDefaultValues(int len) {
            len = Math.min(len, delayArr.length);
            for(int i = 0, index = currIndex; i < len; i++, index++) {
                if(index >= delayArr.length) {
                    index = 0;
                }
                delayArr[index] = 0;
            }

            return len;
        }
    }
}