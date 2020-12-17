package deprecatedclasses;

@Deprecated
public class Leftovers {
    // Saving audio to wav in c++ style
    //    // This code about creating wav file is modified example of this http://www.cplusplus.com/forum/beginner/166954/
//    public static void writeWord(OutputStreamWriter outs, int value, int size) throws IOException {
//        for (; size > 0; --size, value >>= 8) {
//            outs.write((value & 0xFF));
//        }
//    }

    // TODO: Works in c++ but not in java
//        // Example for reading wav file http://www.cplusplus.com/forum/beginner/166954/
//        // Wav file audioFormat reference page http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
//     public static void createWavFile(String path, byte[] samples, int numberOfChannels,
//        int samplingRate, int sampleSize, boolean isBigEndian) throws IOException {
//
//
//         boolean needsPadding = false;
//         int headerSize = 44;        // TODO:
//         int dataLength = samples.length;
//
//         if (samples.length % 2 == 1) {
//             needsPadding = true;
//             dataLength++; // Add padding
//         }
//         int fileLength = dataLength + headerSize;
//
//         try (OutputStreamWriter writer =
//                  new OutputStreamWriter(new FileOutputStream(path + ".wav"), StandardCharsets.US_ASCII)) {
//
//             writer.write("RIFF");
//             writeWord(writer, fileLength - 8, 4);        // RIFF chunk size, which is (file size - 8) bytes
//             writer.write("WAVEfmt ");
//             writeWord(writer, 16, 4);
//             writeWord(writer, 1, 2);
//             writeWord(writer, numberOfChannels, 2);
//             writeWord(writer, samplingRate, 4);
//
//
//             int frameLengthInBytes = sampleSize * numberOfChannels;
//             // Byte size of one second is calculated as (Sample Rate * BitsPerSample * Channels) / 8
//             int byteSizeOfOneSecond = samplingRate * frameLengthInBytes;
//             writeWord(writer, byteSizeOfOneSecond, 4);
//             writeWord(writer, frameLengthInBytes, 2); // data block size ... size of audio frame in bytes
//             int sampleSizeInBits = sampleSize * 8;
//             writeWord(writer, sampleSizeInBits, 2);  // number of bits per sample (use a multiple of 8)
//             writer.write("data");
//             writeWord(writer, dataLength, 4);
//
//             // Now write all samples to the file, in little endian audioFormat
//             int i = 0;
//             while (i < samples.length) {
//                 for (int j = 0; j < numberOfChannels; j++) {
//                     int sampleIndex = i;
//                     for (int k = 0; k < sampleSize; k++) {
//                         if (isBigEndian) {        // Big endian, it is needed to write bytes in opposite direction
//                             writer.write(samples[sampleIndex + sampleSize - k - 1]);
//                         } else {
//                             writer.write(samples[i]);
//                         }
//
//                         i++;
//                     }
//                 }
//             }
//
//             if (needsPadding) {    // If odd number of bytes in chunk then add padding byte
//                 writer.write(0);
//             }
//         }
//     }






}
