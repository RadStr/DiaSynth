package util.audio;

/**
 * Contains measure together with the frequency
 * When sorting the array with elements represented by this class, then the first element is the element with
 * the lowest measure, next one has higher or the same measure, etc.
 */
@Deprecated
public class FrequencyWithMeasure implements Comparable<FrequencyWithMeasure> {
    public int frequency;
    public double measure;

    public FrequencyWithMeasure(int frequency, double measure) {
        this.frequency = frequency;
        this.measure = measure;
    }

    @Override
    public int compareTo(FrequencyWithMeasure arg0) {
        double result = this.measure - arg0.measure;
        if (result > 0) {
            return 1;
        }
        else if (result == 0) {
            return 0;
        }
        else {
            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        FrequencyWithMeasure o = (FrequencyWithMeasure) obj;
        if (this.frequency == o.frequency && this.measure == o.measure) {
            return true;
        }
        return false;
    }
}

