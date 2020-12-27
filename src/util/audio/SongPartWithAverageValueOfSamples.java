package util.audio;

/**
 * Contains 1D byte array, which represents samples, together with the averageAmplitude
 * (or possibly the value stored in property averageAmplitude could represent some other property of the song part).
 * When sorting the array with elements represented by this class, then the first element is the element with
 * the lowest average amplitude, next one has higher or the same amplitude, etc.
 */
@Deprecated
public class SongPartWithAverageValueOfSamples implements Comparable<SongPartWithAverageValueOfSamples> {
    public int averageAmplitude;
    public byte[] songPart;

    public SongPartWithAverageValueOfSamples(int averageAmplitude, byte[] songPart, boolean makeNewArray) {
        this.averageAmplitude = averageAmplitude;
        if (makeNewArray) {
            this.songPart = new byte[songPart.length];
            for (int i = 0; i < songPart.length; i++) {
                this.songPart[i] = songPart[i];
            }
        }
        else {
            this.songPart = songPart;
        }
    }

    @Override
    public int compareTo(SongPartWithAverageValueOfSamples o) {
        return this.averageAmplitude - o.averageAmplitude;
    }
}
