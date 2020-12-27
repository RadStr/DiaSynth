package util.audio;

/**
 * Contains 1D double array, which represents normalized samples (values of the samples are in between -1 and 1), together with the averageAmplitude
 * (or possibly other int which represents some other property of the song part).
 * When sorting the array with elements represented by this class, then the first element is the element with
 * the lowest average amplitude, next one has higher or the same amplitude, etc.
 */
@Deprecated
public class NormalizedSongPartWithAverageValueOfSamples implements Comparable<NormalizedSongPartWithAverageValueOfSamples> {
    public int averageAmplitude;
    public double[] songPart;

    public NormalizedSongPartWithAverageValueOfSamples(int averageAmplitude, double[] songPart, boolean makeNewArray) {
        this.averageAmplitude = averageAmplitude;

        if (makeNewArray) {
            this.songPart = new double[songPart.length];
            for (int i = 0; i < songPart.length; i++) {
                this.songPart[i] = songPart[i];
            }
        }
        else {
            this.songPart = songPart;
        }
    }

    @Override
    public int compareTo(NormalizedSongPartWithAverageValueOfSamples o) {
        return this.averageAmplitude - o.averageAmplitude;
    }
}
