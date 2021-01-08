package str.rad.util.audio.io;

import java.io.IOException;
import java.io.InputStream;

public class AudioReader {
    private AudioReader() { }        // Allow only static access



    /**
     * Reads bytes from input stream to the array given in parameter,
     * until either the end of the stream is reached or arr.length bytes are read.
     *
     * @param audioStream is the stream with samples.
     * @param arr         is the array to read the bytes to.
     * @return Returns number of bytes read.
     * @throws IOException is thrown when error with input stream occurred.
     */
    public static int readNSamples(InputStream audioStream, byte[] arr) throws IOException {
        int bytesRead = 0;
        int bytesReadSum = 0;
        int freeIndexesCount = arr.length;
        while (bytesReadSum != arr.length && bytesRead != -1) {
            bytesRead = audioStream.read(arr, bytesReadSum, freeIndexesCount);
            bytesReadSum = bytesReadSum + bytesRead;
            freeIndexesCount = freeIndexesCount - bytesRead;
        }

        return bytesReadSum;
    }

    /**
     * Skips n samples from input stream.
     *
     * @param samples    is the input stream with samples.
     * @param sampleSize is the size of one sample.
     * @param n          is the number of samples to be skipped.
     * @return Returns the number of read bytes or -1 if end of the stream was reached.
     * @throws IOException is thrown when error with input stream occurred.
     */
    public static int readNotNeededSamples(InputStream samples, int sampleSize, int n) throws IOException {
        byte[] arr = new byte[4096];
        int bytesRead = 0;
        int bytesReadSum = 0;
        int freeIndexesCount = n * sampleSize;

        while (freeIndexesCount != 0) {
            if (freeIndexesCount > arr.length) {
                bytesRead = samples.read(arr, 0, arr.length);
            }
            else {
                bytesRead = samples.read(arr, 0, freeIndexesCount);
            }
            bytesReadSum = bytesReadSum + bytesRead;
            freeIndexesCount = freeIndexesCount - bytesRead;
            if (bytesRead == -1) {
                return -1;
            }
        }

        return bytesReadSum;
    }


    // In future it may be better to return long.

    /**
     * Returns -1 if exception ocurred otherwise returns the length of input stream.
     *
     * @param samples
     */
    public static int getLengthOfInputStream(InputStream samples) {
        int bytesRead = 0;
        int bytesReadSum = 0;

        try {
            byte[] arr = new byte[Math.min(4096, samples.available())];
            if (arr.length <= 0) {       // available returned incorrect value
                arr = new byte[4096];
            }
            while (bytesRead != -1) {
                bytesRead = samples.read(arr, 0, arr.length);
                bytesReadSum = bytesReadSum + bytesRead;
            }
        }
        catch (IOException e) {
            return -1;
        }
        bytesReadSum++;        // Because I added -1

        return bytesReadSum;
    }
}
