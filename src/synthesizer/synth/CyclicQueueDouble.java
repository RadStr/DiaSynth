package synthesizer.synth;

/**
 * NOTE: !!! This class doesn't work if there was pushed more than Integer.MAX_VALUE values (overflow occurred).
 * (For the use we use it, it is 13 hours of playing audio at 44.1 kHz)
 * Class works if 1 thread is consumer and other producer.
 * (Both consumer and producer can run on only 1 thread, but it isn't recommended).
 * To be maximally efficient use the push/pop variants with arrays (if asking for more than 1 value of course).
 *
 */
// NOTE 2: We could implement it to just modulo all the values when certain push limit is reached
// (we could check that using timer, for example).
// But it may introduce many problems which might be quite difficult for debugging.
public class CyclicQueueDouble {
    /**
     *
     * @param lenExponent the real length of the queue will be 2^lenExponent
     */
    public CyclicQueueDouble(int lenExponent) {
        queue = new double[1 << lenExponent];
        MOD_LEN = queue.length - 1;
        popIndex = 0;
        pushIndex = 0;
    }


    private final int MOD_LEN;
    private final double[] queue;
    // Needs to be volatile, because getLen is called from both the producer and consumer thread
    private volatile int popIndex;
    private int getPopIndexMod() {
        return getIndexMod(popIndex);
    }
    private volatile int pushIndex;
    private int getPushIndexMod() {
        return getIndexMod(pushIndex);
    }
    public int getIndexMod(int index) {
        return index & MOD_LEN;
    }

    /**
     * Returns current length of queue
     * @return
     */
    public int getLen() {
        return pushIndex - popIndex;
    }

    /**
     * Returns maximum capacity of queue.
     * @return
     */
    public int getTotalQueueCapacity() {
        return queue.length;
    }

    /**
     * Returns number of values which would be popped when pop with corresponding values would be called
     * @param startIndex
     * @param endIndex
     * @return
     */
    public int getPopLength(int startIndex, int endIndex) {
        return Math.min(endIndex - startIndex, getLen());
    }

    public int getPushLength(int startIndex, int endIndex) {
        int availableLen = queue.length - getLen();
        int len = Math.min(endIndex - startIndex, availableLen);
        return len;
    }


    public int push(double value) {
        if(pushIndex - popIndex < queue.length) {
            queue[getPushIndexMod()] = value;
            pushIndex++;
            return 1;
        }
        else {
            return 0;
        }
    }
    public int push(double[] values, int startIndex, int endIndex) {
        int len = getPushLength(startIndex, endIndex);
        for(int index = 0; index < len; index++, startIndex++, pushIndex++) {
            queue[getPushIndexMod()] = values[startIndex];
        }

        return len;
    }

    public double pop() {
        double retVal = 0;
        if(pushIndex > popIndex) {
            retVal = queue[getPopIndexMod()];
            popIndex++;
        }
        return retVal;
    }
    public int pop(double[] values, int startIndex, int endIndex) {
        int len = getPopLength(startIndex, endIndex);
        for(int index = 0; index < len; index++, startIndex++, popIndex++) {
            values[startIndex] = queue[getPopIndexMod()];
        }

        return len;
    }

    /**
     * Pops n elements from queue without storing them anywhere
     * @param n
     * @return
     */
    public int pop(int n) {
        int len = getPopLength(0, n);
        popIndex += len;
        return len;
    }


    public double peek() {
        return queue[popIndex % queue.length];
    }
    public int peek(double[] values, int startIndex, int endIndex) {
        int peekIndex = popIndex;
        int len = Math.min(endIndex - startIndex, pushIndex - peekIndex);
        for(int index = 0; index < len; index++, startIndex++, peekIndex++) {
            values[startIndex] = queue[getIndexMod(peekIndex)];
        }

        return len;
    }

    /**
     * Removes n last indices from queue, have to take into consideration the chunks, which are popped and
     * what thread is pushing, etc.
     * Since easily you can call push and while pushing call remove. That breaks everything. Another break case is
     * when popping the remove is called. So in result some values may be popped even when they are already "removed".
     * So this method should be called only when audio thread is stopped, otherwise it may be quite difficult.
     * @param n
     */
    public int remove(int n) {
        int len = getLen();
        len = Math.min(len, n);
        pushIndex -= len;
        return len;
    }


    public int reset() {
        int len = getLen();
        popIndex = 0;
        pushIndex = 0;
        return len;
    }
}
