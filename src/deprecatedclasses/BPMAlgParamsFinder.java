package deprecatedclasses;

import analyzer.AnalyzerPanel;
import analyzer.bpm.SubbandSplitter;
import analyzer.bpm.SubbandSplitterIFace;
import analyzer.bpm.SubbandSplitterLinear;
import test.ProgramTest;
import util.Pair;
import util.audio.AudioUtilities;
import util.audio.wave.ByteWave;
import util.logging.MyLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class BPMAlgParamsFinder {
    // Then we take this list, for each bpm algorithm we sum the differences (note: the differences are always positive.)
    // and put the results to new list of Pair<String, Integer>, where the first value is the name of the algorithm
    // and the second is the sum of differences.
    // Based on that we choose the result with the smallest difference, which will be the first one in the sorted array.
    public static final int BPM_DIF_MULT_FACTOR = 5;

    public static void main(String[] args) {
        List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> list = createAndPrintDifList();
    }

    public static List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> createAndPrintDifList() {
        List<Pair<String, Pair<String, Integer>>> bpmList = new ArrayList<>();
        String[] paths = new String[0];         // Not distributing the test files
        for (int i = 0; i < paths.length; i++) {
            File dir = new File(paths[i]);
            if (dir.isDirectory()) {
                for (File f : dir.listFiles()) {
                    String path = f.getAbsolutePath();
                    findCoefs(path, bpmList);
                }
            }
            else {
                findCoefs(paths[i], bpmList);
            }
        }

        List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> difList = createDifList(bpmList);
        sortDifList(difList);
        printDifList(difList, 4);
        return difList;
    }

    public static void findCoefs(String filename, List<Pair<String, Pair<String, Integer>>> bpmList) {
        ProgramTest.debugPrint("Currently working with:", filename);
        ByteWave byteWave;

        try {
            byteWave = ByteWave.loadSong(filename, true);
            if (byteWave == null) {
                MyLogger.logWithoutIndentation("Error in method analyze(String filename) in AnalyzerPanel\n" +
                                               filename + "\n" + AudioUtilities.LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE);
                return;
            }
        }
        catch (IOException e) {
            MyLogger.logException(e);
            return;
        }

        try {
            byteWave.convertToMono();
        }
        catch (IOException e) {
            return;
        }

        addSongBPMToList(byteWave, bpmList);
    }

    private static void findBestCoefsAdvancedFullLinear(ByteWave byteWave, List list) {
        int referenceBPM = -1;
        int subbandCount = 64;
        double coef = 2.3;
        while (coef < 2.9) {
            for (double varianceLimit = 0; varianceLimit < 0.19; varianceLimit += 0.02) {
                for (int windowsBetweenBeats = 4; windowsBetweenBeats < 9; windowsBetweenBeats++) {
                    for (int i = 0; i < 5; i++) {
                        switch (i) {
                            case 0:
                                subbandCount = 6;
                                break;
                            case 1:
                                subbandCount = 8;
                                break;
                            case 2:
                                subbandCount = 16;
                                break;
                            case 3:
                                subbandCount = 32;
                                break;
                            case 4:
                                subbandCount = 64;
                                break;
                        }


                        SubbandSplitterIFace splitter = new SubbandSplitterLinear(subbandCount);
                        int bpm = byteWave.computeBPMSimpleWithFreqBands(subbandCount, splitter, coef, windowsBetweenBeats, varianceLimit);
                        String name = "BPMAdvancedFullLinear" + subbandCount + "Coef" + (int) Math.round(100 * coef) + "Win" + windowsBetweenBeats;
                        name += "Var" + (int) Math.round(100 * varianceLimit);

                        referenceBPM = addBPMToList(byteWave, name, list, bpm, referenceBPM);
                    }
                }
            }

            coef += 0.08;
            ProgramTest.debugPrint("Coeficient:", coef);
        }
    }

    private static void findBestCoefsAdvancedFullLogarithmic(ByteWave byteWave, List list) {
        int referenceBPM = -1;
        int subbandCount = 64;
        double coef = 2;
        while (coef < 3) {
            for (double varianceLimit = 0; varianceLimit < 1.4; varianceLimit += 0.16) {
                for (int windowsBetweenBeats = 0; windowsBetweenBeats < 5; windowsBetweenBeats++) {
                    for (int i = 0; i < 5; i++) {
                        switch (i) {
                            case 0:
                                subbandCount = 6;
                                break;
                            case 1:
                                subbandCount = 8;
                                break;
                            case 2:
                                subbandCount = 16;
                                break;
                            case 3:
                                subbandCount = 32;
                                break;
                            case 4:
                                subbandCount = 64;
                                break;
                        }


                        SubbandSplitterIFace splitter;
                        splitter = new SubbandSplitter(byteWave.getSampleRate(), 0, subbandCount);
                        int bpm = byteWave.computeBPMSimpleWithFreqBands(subbandCount, splitter, coef,
                                                                         windowsBetweenBeats, varianceLimit);

                        String name = "BPMAdvancedFullLog" + subbandCount + "Coef" + (int) Math.round(100 * coef) +
                                      "Win" + windowsBetweenBeats;
                        name += "Var" + (int) Math.round(100 * varianceLimit);

                        referenceBPM = addBPMToList(byteWave, name, list, bpm, referenceBPM);
                    }
                }
            }

            coef += 0.08;
            ProgramTest.debugPrint("Coeficient:", coef);
        }
    }

    public static void addSongBPMToList(ByteWave byteWave, List<Pair<String, Pair<String, Integer>>> list) {
        findBestCoefsAdvancedFullLinear(byteWave, list);
//        findBestCoefsAdvancedFullLogarithmic(prog, list);
    }

    /**
     * @param byteWave
     * @param algName
     * @param list
     * @param calculatedBPM is the bpm of the currently compared algorithm
     * @param referenceBPM  is used if bpm > 0
     * @return Returns the calculated BPM reference value
     */
    public static int addBPMToList(ByteWave byteWave, String algName,
                                   List<Pair<String, Pair<String, Integer>>> list,
                                   int calculatedBPM, int referenceBPM) {
        int difference;
        int bpm;

        if (referenceBPM > 0) {
            bpm = referenceBPM;
            difference = calculateDif(bpm, calculatedBPM);
        }
        else {
            if (byteWave.getFileName().toUpperCase().contains("BPM")) {
                bpm = getBPMFromName(byteWave.getFileName());
                difference = BPM_DIF_MULT_FACTOR * calculateDif(bpm, calculatedBPM);
            }
            else {
                Pair<String, String> tmpPair;
                tmpPair = AnalyzerPanel.analyzeBPMAllPart(byteWave);
                int bpmAll = Integer.parseInt(tmpPair.getValue());

                tmpPair = AnalyzerPanel.analyzeBPMBarycenterPart(byteWave);
                int bpmBarycenter = Integer.parseInt(tmpPair.getValue());
                bpm = bpmAll + bpmBarycenter;
                bpm /= 2;

                difference = calculateDif(bpm, calculatedBPM);
            }
        }


        Pair<String, Integer> valuePair = new Pair<>(byteWave.getFileName(), difference);
        Pair<String, Pair<String, Integer>> retPair = new Pair<>(algName, valuePair);
        list.add(retPair);

        return bpm;
    }

    public static int calculateDif(int referenceBPM, int calculatedBPM) {
        int dif = referenceBPM - calculatedBPM;
        dif = Math.abs(dif);
        return dif;
    }

    public static int getBPMFromName(String name) {
        int bpm;
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isDigit(c)) {
                if (startIndex < 0) {
                    startIndex = i;
                }
            }
            else {
                if (startIndex >= 0) {
                    endIndex = i - 1;
                    break;
                }
            }
        }

        String bpmString = name.substring(startIndex, endIndex + 1);
        bpm = Integer.parseInt(bpmString);
        return bpm;
    }

    public static List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> createDifList(List<Pair<String, Pair<String, Integer>>> list) {
        final List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> difList = new ArrayList<>();
        final List<Pair<String, Pair<String, Integer>>> currAlgPairs = new ArrayList<>();
        int count = 1;
        String firstName = list.get(0).getKey();
        for (int i = 1; i < list.size(); i++) {
            Pair<String, Pair<String, Integer>> currPair = list.get(i);
            if (firstName.equals(currPair.getKey())) {
                count++;
            }
        }
        final double[] difs = new double[count];


        for (int i = 0; i < list.size(); i++) {
            if (difListContainsName(difList, list.get(i).getKey())) {
                continue;
            }
            String name = null;
            int dif = 0;
            currAlgPairs.clear();
            for (int j = i; j < list.size(); j++) {
                // TODO: DEBUG
//                if(name != null) {
//                    ProgramTest.debugPrint("ALG:", name, dif);
//                }
                // TODO: DEBUG
                Pair<String, Pair<String, Integer>> currPair = list.get(j);
                if (name == null) {
                    if (!difListContainsName(difList, currPair.getKey())) {
                        name = currPair.getKey();
                        dif = currPair.getValue().getValue();
                        currAlgPairs.add(currPair);
                    }
                }
                else {
                    if (name.equals(currPair.getKey())) {
                        dif += currPair.getValue().getValue();
                        currAlgPairs.add(currPair);
                    }
                }
            }


            double avg = dif / (double) currAlgPairs.size();
            double variance = calculateVariance(avg, currAlgPairs);
            for (int k = 0; k < currAlgPairs.size(); k++) {
                Pair<String, Pair<String, Integer>> p = currAlgPairs.get(k);
                difs[k] = Math.abs(p.getValue().getValue() - avg);
            }
            Arrays.sort(difs);


            Pair<String, Integer> resultKey = new Pair<>(name, dif);
            Pair<Pair<String, Integer>, double[]> pair = new Pair(resultKey, difs);
            difList.add(new Pair(pair, variance));
        }

        return difList;
    }

    private static double calculateVariance(double avg, List<Pair<String, Pair<String, Integer>>> vals) {
        double variance = 0;
        for (Pair<String, Pair<String, Integer>> p : vals) {
            int val = p.getValue().getValue();
            double tmp = val - avg;
            variance += tmp * tmp;
        }

        return variance / vals.size();
    }

    private static boolean difListContainsName(List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> difList, String name) {
        for (int i = 0; i < difList.size(); i++) {
            Pair<String, Integer> p = difList.get(i).getKey().getKey();
            if (p.getKey().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static void sortDifList(List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> difList) {
        difList.sort(new Comparator<Pair<Pair<Pair<String, Integer>, double[]>, Double>>() {
            @Override
            public int compare(Pair<Pair<Pair<String, Integer>, double[]>, Double> o1,
                               Pair<Pair<Pair<String, Integer>, double[]>, Double> o2) {
                int val1 = o1.getKey().getKey().getValue();
                int val2 = o2.getKey().getKey().getValue();
                return Integer.compare(val1, val2);
            }
        });
    }

    public static void printDifList(List<Pair<Pair<Pair<String, Integer>, double[]>, Double>> difList, int difPrintCount) {
        for (int i = 0; i < difList.size(); i++) {
            MyLogger.log(difList.get(i).getKey().toString() +
                         "\t" + difList.get(i).getValue().toString(), 0);
            double[] arr = difList.get(i).getKey().getValue();
            for (int j = 0; j < difPrintCount; j++) {
                MyLogger.log(String.format("%.2f", arr[arr.length - j - 1]), 0);
            }
            MyLogger.log("----", 0);
        }
    }
}
