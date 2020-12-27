package util.audio.wave;

import util.Utilities;
import util.audio.AudioConverter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class DoubleWave {
    public static final String HOME_DIRECTORY = new File("").getAbsolutePath();
    public static final String DOUBLE_WAVE_DIRECTORY = HOME_DIRECTORY + File.separator +
            "WaveCacheDirectory" + File.separator;
    public static final String DOUBLE_WAVE_EXTENSION = ".dwav";
    public static final int WAVE_LEN = 0;
    public static final int SAMPLE_RATE_POS = 1;
    public static final int NUMBER_OF_CHANNELS_POS = 2;
    public static final int SAMPLES_POS = 3;


    /**
     *
     * @param filename
     * @param buffer
     * @param sampleRate
     * @param numberOfChannels
     * @return Returns the length of written samples to file, or -1 when error with creating the file occurs.
     * -2 when error with writing to file occurs
     */
    public static int createDoubleWaveFile(String filename, double[] buffer, int sampleRate, int numberOfChannels) {
        String path = getFullPath(filename);
        int[] prefix = new int[] { buffer.length, sampleRate, numberOfChannels };
        return storeDoubleArray(buffer, 0, buffer.length, path, prefix);
    }


    /**
     *
     * @param filename
     * @return Returns the length of written samples to file, or -1 when error with creating the file occurs.
     * -2 when error with writing to file occurs
     */
    public int createDoubleWaveFile(String filename) {
        String path = getFullPath(filename);
        int[] prefix = createPrefix();
        return storeDoubleArray(getSong(), 0, getSong().length, path, prefix);
    }


    public static int storeDoubleArray(double[] doubles, int startIndex, int len, String path) {
        return storeDoubleArray(doubles, startIndex, len, path, new int[] { len });
    }

    // Partly taken from https://stackoverflow.com/questions/4358875/fastest-way-to-write-an-array-of-integers-to-a-file-in-java
    // Fixed using https://stackoverflow.com/questions/40599842/java-non-writable-channel-exception
    // Note programming the reading is basically the same, I just use getDouble instead of putDouble
    // I used that inside DrawValuesSupplierIFace class
    /**
     *
     * @param doubles
     * @param path
     * @param prefix
     * @return Returns the length of written samples to file, or -1 when error with creating the file occurs.
     * -2 when error with writing to file occurs
     */
    public static int storeDoubleArray(double[] doubles, int startIndex, int len, String path, int[] prefix) {
        int retVal = doubles.length;

        RandomAccessFile file = clearOrCreateFileRW(path);
        if (file == null) {
            return -1;
        }

        try {
            FileChannel fileChannel = file.getChannel();
            ByteBuffer buf = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0,
                Double.BYTES * len + prefix.length * Integer.BYTES);
            for (int i : prefix) {
                buf.putInt(i);
            }
            for (int i = startIndex; i < startIndex + len; i++) {
                buf.putDouble(doubles[i]);
            }
        } catch (IOException e) {
            retVal = -2;
        } finally {
            safeClose(file);
        }
        // I also first should set the file to null so it can be correctly garbage collected
        // From comment in https://stackoverflow.com/questions/11316289/cannot-delete-file-even-after-closing-audioinputstream
        // https://stackoverflow.com/questions/991489/file-delete-returns-false-even-though-file-exists-file-canread-file-canw
        file = null;
        System.gc();

        return retVal;
    }


    public static void safeClose(RandomAccessFile file) {
        try {
            if (file != null) {
                file.close();
            }
        }
        catch (IOException e) {
            // do nothing
        }
    }


    public static RandomAccessFile clearOrCreateFileRW(String filename) {
        RandomAccessFile raf;

        File f = new File(filename);
        try {
            f.getParentFile().mkdirs();
            if (!f.createNewFile()) {        // If the file exist, then delete it
                //f.delete();
                try(PrintWriter pw = new PrintWriter(f)) { }
            }
            raf = new RandomAccessFile(filename, "rw");

        } catch (Exception e) {
            raf = null;
        }
        return raf;
    }


    /**
     * Is the load method for storeDoubleArray if prefix.length == 1 and prefix[0] = length in samples.
     * @param fileChannel
     * @return
     */
    public static double[] getStoredDoubleArray(FileChannel fileChannel) {
        double[] storedArr = null;
        try {
            ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, Integer.BYTES);
            int len = byteBuffer.getInt();
            if(len > 0) {
                storedArr = new double[len];
                byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, Integer.BYTES, Double.BYTES * len);
                for (int i = 0; i < storedArr.length; i++) {
                    storedArr[i] = byteBuffer.getDouble();
                }
            }
        } catch (EOFException e) {
            storedArr = null;
        } catch (IOException e) {
            storedArr = null;
        }

        return storedArr;
    }


    private boolean isFullSongLoaded = true;
    public boolean getIsFullSongLoaded() {
        return isFullSongLoaded;
    }

    private double[] song;
    public double[] getSong() {
        return song;
    }
    public void setSong(int newLen) {
        double[] newSong = DoubleWave.createArrayWithZerosAtEnd(this.song, newLen);
        setSong(newSong);
    }
    private void setSong(double[] song) {
        repairEverythingAssociatedWithBuffer(this.song, song);
        this.song = song;
    }

    private void repairEverythingAssociatedWithBuffer(double[] oldSong, double[] newSong) {
        // EMPTY
        // Here should be mostly the repairing of the file which contains the wave
    }


    public int getSongLength() {
        return song.length;
    }


    public int[] createPrefix() {
        int[] prefix = new int[SAMPLES_POS];
        prefix[WAVE_LEN] = song.length;
        prefix[SAMPLE_RATE_POS] = getSampleRate();
        prefix[NUMBER_OF_CHANNELS_POS] = getNumberOfChannels();
        return prefix;
    }

    private int sampleRate;
    public int getSampleRate() {
        return sampleRate;
    }

    private int numberOfChannels;
    public int getNumberOfChannels() {
        return numberOfChannels;
    }

    /**
     * filenameWithoutExtension without the extension
     */
    private String filenameWithoutExtension;
    public String getFilenameWithoutExtension() {
        return filenameWithoutExtension;
    }
    public String getFilenameWithExtension() {
        return getFilenameWithExtension(filenameWithoutExtension);
    }
    public static String getFilenameWithExtension(String filename) {
        return filename + DoubleWave.DOUBLE_WAVE_EXTENSION;
    }
    public String getFullPath() {
        return getFullPath(filenameWithoutExtension);
    }
    public static String getFullPath(String filename) {
        filename = getFilenameWithExtension(filename);
        return addDirectoryToFilename(filename);
    }

    public static String addDirectoryToFilename(String filename) {
        return DOUBLE_WAVE_DIRECTORY + filename;
    }



    /**
     * If the normalizing fails (throws exception) then all values are set to "default" values (null, -1, empty string)
     * @param filenameWithoutExtension is the file name without the extension which will be created
     */
    public DoubleWave(String filenameWithoutExtension, ByteWave byteWave, boolean shouldCreateDoubleWaveFile) {
        this(filenameWithoutExtension, byteWave, shouldCreateDoubleWaveFile, -1);
    }

    /**
     * If the normalizing fails (throws exception) then all values are set to "default" values (null, -1, empty string)
     * @param filenameWithoutExtension is the file name without the extension which will be created
     * @param byteWave
     * @param shouldCreateDoubleWaveFile
     * @param newSampleRate if < 0 then don't perform sample rate conversion
     */
    public DoubleWave(String filenameWithoutExtension, ByteWave byteWave,
                      boolean shouldCreateDoubleWaveFile, int newSampleRate) {
        doubleWaveFileExists = shouldCreateDoubleWaveFile;
        this.filenameWithoutExtension = filenameWithoutExtension;

        this.sampleRate = byteWave.getSampleRate();
        this.numberOfChannels = byteWave.getNumberOfChannels();
        try {
            song = byteWave.normalizeToDoubles();
            if(newSampleRate >= 0) {
                song = AudioConverter.convertSampleRate(song, byteWave.getNumberOfChannels(),
                        byteWave.getSampleRate(), newSampleRate, true);
                this.sampleRate = newSampleRate;
            }
            if(shouldCreateDoubleWaveFile) {
                createDoubleWaveFile();
            }
        }
        catch(IOException e) {
            song = null;
            sampleRate = -1;
            numberOfChannels = -1;
            this.filenameWithoutExtension = "";
        }
    }

    /**
     * If the normalizing fails (throws exception) then all values are set to "default" values (null, -1, empty string)
     */
    public DoubleWave(ByteWave byteWave, boolean shouldCreateDoubleWaveFile) {
        this(Utilities.getNameWithoutExtension(byteWave.getFileName()), byteWave, shouldCreateDoubleWaveFile);
    }

    /**
     * If the normalizing fails (throws exception) then all values are set to "default" values (null, -1, empty string)
     * @param byteWave
     * @param shouldCreateDoubleWaveFile
     * @param newSampleRate is the new sample rate to which we should convert,
     *                      < 0 if we want to keep the old audioFormat
     */
    public DoubleWave(ByteWave byteWave, boolean shouldCreateDoubleWaveFile, int newSampleRate) {
        this(Utilities.getNameWithoutExtension(byteWave.getFileName()), byteWave,
                                               shouldCreateDoubleWaveFile, newSampleRate);
    }

    public DoubleWave(double[] wave, DoubleWave oldWave, boolean shouldCreateDoubleWaveFile) {
        this(wave, oldWave.getSampleRate(), oldWave.getNumberOfChannels(),
            oldWave.getFilenameWithoutExtension(), shouldCreateDoubleWaveFile);
    }

    public DoubleWave(double[] wave, int sampleRate, int numberOfChannels,
                      String filenameWithoutExtension, boolean shouldCreateDoubleWaveFile) {
        song = wave;
        this.sampleRate = sampleRate;
        this.numberOfChannels = numberOfChannels;
        this.filenameWithoutExtension = filenameWithoutExtension;
        doubleWaveFileExists = shouldCreateDoubleWaveFile;

        if(shouldCreateDoubleWaveFile) {
            createDoubleWaveFile();
        }
    }


    private boolean doubleWaveFileExists = false;
    public boolean getDoubleWaveFileExists() {
        return doubleWaveFileExists;
    }

    /**
     * Instance variant for the static method.
     * @return Returns the length of written samples to file, or -1 when FileNotFoundException occurs.
     * -2 when IOException occurs
     */
    public int createDoubleWaveFile() {
        return createDoubleWaveFile(getFilenameWithoutExtension());
    }


    public int convertSampleToSecs(int sample) {
        return convertSampleToSecs(sample, getSampleRate());
    }

    public static int convertSampleToSecs(int sample, int sampleRate) {
        return sample / sampleRate;
    }

    public int convertSampleToMillis(int sample) {
        return convertSampleToMillis(sample, getSampleRate());
    }

    public static int convertSampleToMillis(int sample, int sampleRate) {
        return (int)(1000 * (double)sample / sampleRate);
    }


    public static double[] createArrayWithZerosAtEnd(double[] oldArr, int newArrLen) {
        double[] newArr = new double[newArrLen];
        int len = Math.min(newArrLen, oldArr.length);
        System.arraycopy(oldArr, 0, newArr, 0, len);
        // The zeros at end are there by default
        return newArr;
    }
}
