package Rocnikovy_Projekt;
// When I talk about compiler I mean JVM
// TODO: Remove the next 2 lines after clean up
// When I didn't have much knowledge I did copy-pasting to make the code faster, but now after 2 years I see that
// it was very bad decision and also to compiler optimizes it anyways


// TODO: Copy pasted - REMOVE ALL THESE, sometimes can be found under:
// TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient

// TODO: !!!!!!!!!!!!!!!!!!! Prepsat veskery kod kde se kopiruje na System.arraycopy


///// This is example of tagging part of code for better code clarity

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Audio format conversion methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// public void convertAudio1() {}
// public void convertAudio2() {}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Audio format conversion methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///// This is end of example


// TEMPLATE TO COPY:

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    ///////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    ///////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// END OF TEMPLATE TO COPY


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* -------------------------------------------- [START] -------------------------------------------- */
/////////////////// FFT NOTES
/* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Full FFT on n complex values produces FFT result with n complex values

// FFT EXAMPLE: Let's say we have window size of 4 real numbers. When we perform FFT on 4 complex numbers,
// with imaginary part being 0 and the real part being the real numbers of the window. And with sample rate == 100Hz
// Then we get 4 bins [0] == 0Hz, [1] == 25Hz, [2] == 50Hz, [3] == 75Hz, where [0,1,2] are unique values and [3] is [1] mirrored,
// which means the real_part[1] == real_part[3] and imag_part[1] == -imag_part[3].
// So that is WINDOW_SIZE / 2 + 1 are unique values

// If we have only 3 real numbers then it is [0] == 0Hz, [1] == 33.33Hz, [2] == 66.66Hz
// Here values [0,1] are unique, and [2] is [1] mirrored.
// So now we have again WINDOW_SIZE / 2 + 1 unique values.

// If input is even, then there is the middle value which isn't mirrored.
// If input is odd,  then there isn't the middle value.


// Result of real forward FFT by library method:
// From documentation:
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* --------------------------------------------- [END] --------------------------------------------- */
/////////////////// FFT NOTES
/* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// The length of the window is in number of complex numbers not total length of array



import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.swing.*;

import Rocnikovy_Projekt.math.ArithmeticOperation;
import analyzer.bpm.SubbandSplitterIFace;
import debug.DEBUG_CLASS;
import synthesizer.synth.generators.classic.phase.SineGeneratorWithPhase;
import main.DiasynthTabbedPanel;
import player.format.AudioFormatWithSign;
import org.jtransforms.fft.DoubleFFT_1D;

// TODO: Vsude mit gettery a settery

// TODO: running average filter se chova jinak nez nerekurzivni filtr pro prvnich n samplu, kde n je velikost okenka.


/* // TODO:
public class Program {
    public static void main(String[] args) throws Exception {
    }
}*/


// TODO: not enough time - must remove some configs and try them later - such as weight inits etc. - best solution try it for some small parts and choose the best performing on these small samples
// TODO: Nekde se skore nemeni kdyz je tohle (TODO: nemeni) nekde tak to vymazat to je urceni na to u kterych se to nemeni

// TODO: przc - vymazat je to ted jen na zkouseni - jestli to hledani vhodne konfigurace funguje bez chyby

// TODO: Dropout will try different values later - for that uncomment everything where is TODO: Dropout

// TODO: Napsat metodu co zkontroluje jestli ma pole spravnou delku
//  (Tj. ze tam jsou vsechny framy cely, tedy jestli je tam ten posledni frame cely (Tj. delka pole % frameSize == 0))
public class Program {
//    static List<String> names;
//
//    static Evaluation eval;
//
//    static {
//        names = new ArrayList<>();
//        names.add("METAL");
//        names.add("POP");
//        names.add("ROCK");
//        names.add("CLASSICAL");
//        names.add("RAP");
//        eval = new Evaluation(Genre.values().length);
//        eval.setLabelsList(names);
//    }
//
//    Writer writerWithEvals;                 // TODO: Remove later
//
//    static String directoryWithEvals;       // TODO: remove later
//    static String directoryWithEvalsOver50; // TODO: remove later
//
//    static {
//        directoryWithEvals = "D:\\Hudba\\Evaluations";
//        directoryWithEvalsOver50 = "D:\\Hudba\\Evaluations\\over50";
//    }
//
//
//    DataSet trainingData;       // TODO: kdyz mam model tak je uz muzu vyhodit protoze to nepotrebuju
//    DataSet testingData;        // TODO: totez
//    int numberOfSecondsInInputSong; // TODO: nevim, skoro totez

//    /**
//     * Sets and returns trainingData variable.
//     *
//     * @param numberOfSecondsInInput number of seconds in each input sound.
//     * @return Returns set trainingData variable
//     * @throws IOException is thrown when there was problem with processing the training data from files.
//     */
//    public DataSet setTrainingData(int numberOfSecondsInInput) throws IOException {
//        String parentDir = "D:\\Hudba\\ZanrySmall";
//        String metalDir = "D:\\Hudba\\ZanrySmall\\MetalSmall";
//        String popDir = "D:\\Hudba\\ZanrySmall\\PopSmall";
//        String rockDir = "D:\\Hudba\\ZanrySmall\\RockSmall";
//        String classicalDir = "D:\\Hudba\\ZanrySmall\\ClassicalSmall";
//        String rapDir = "D:\\Hudba\\ZanrySmall\\RapSmall";
///*
//        String parentDir = "D:\\Hudba\\Zanry\\trainingAll";
//        String metalDir = "D:\\Hudba\\Zanry\\trainingAll\\Metal";
//        String popDir = "D:\\Hudba\\Zanry\\trainingAll\\Pop";
//        String rockDir = "D:\\Hudba\\Zanry\\trainingAll\\Rock";
//        String classicalDir = "D:\\Hudba\\Zanry\\trainingAll\\Classical";
//        String rapDir = "D:\\Hudba\\Zanry\\trainingAll\\Rap";
//*/
//        trainingData = prepareData(parentDir, metalDir, popDir, rockDir, classicalDir, rapDir, numberOfSecondsInInput, true);
//        return trainingData;
//    }
//
//    /**
//     * Sets and returns testingData variable.
//     *
//     * @param numberOfSecondsInInput number of seconds in each input sound.
//     * @return Returns set testingData variable
//     * @throws IOException is thrown when there was problem with processing the testing data from files.
//     */
//    public DataSet setTestingData(int numberOfSecondsInInput) throws IOException {
//        String parentDir = "D:\\Hudba\\Zanry\\testing";
//        String metalDir = "D:\\Hudba\\Zanry\\testing\\Metal";
//        String popDir = "D:\\Hudba\\Zanry\\testing\\Pop";
//        String rockDir = "D:\\Hudba\\Zanry\\testing\\Rock";
//        String classicalDir = "D:\\Hudba\\Zanry\\testing\\Classical";
//        String rapDir = "D:\\Hudba\\Zanry\\testing\\Rap";
//        testingData = prepareData(parentDir, metalDir, popDir, rockDir, classicalDir, rapDir, numberOfSecondsInInput, false);
//        return testingData;
//    }
//
//    private DataSet inputData;

    public byte[] song;         // TODO: Bylo static

    private int mask;                   // TODO: At to zbytecne nepocitam pro kazdou metodu zvlast (i kdyz to je lehkej vypocet)
    public int getMask() {
        return mask;
    }

    private File soundFile;
    public AudioInputStream decodedAudioStream;
    private SourceDataLine sourceLine;

    public int numberOfChannels;
    public int sampleRate;
    public int sampleSizeInBits;
    public int sampleSizeInBytes;

    public int wholeFileSize;
    private int onlyAudioSizeInBytes;
    public int getOnlyAudioSizeInBytes() {
        return onlyAudioSizeInBytes;
    }
    private float frameRate;
    public int frameSize;
    public boolean isBigEndian;
    private int kbits;

    public Encoding encoding;
    public boolean isSigned;

    private int headerSize;

    public int lengthOfAudioInSeconds;

    private String fileName;
    public String getFileName() {
        return fileName;
    }
    private String path;
    public String getPath() {
        return path;
    }

    private AudioFileFormat originalAudioFileFormat = null;
    private AudioFormat originalAudioFormat = null;
    private AudioFormat decodedAudioFormat = null;
    private Type type;
    private AudioType audioType;
    private AudioInputStream originalAudioStream;

    public static String fileWithModel = "fileWithTheModel";

    private int maxAbsoluteValue;

    private int sizeOfOneSecInFrames;
    public int getSizeOfOneSecInFrames() {
        return sizeOfOneSecInFrames;
    }

    private int sizeOfOneSecBytes;
    public int getSizeOfOneSecInBytes() {
        return sizeOfOneSecBytes;
    }
    private void setSizeOfOneSec() {
        sizeOfOneSecBytes = calculateSizeOfOneSec();
        sizeOfOneSecInFrames = sampleRate;
    }
    public int calculateSizeOfOneSec() { return calculateSizeOfOneSec(this.sampleRate, this.frameSize); }
    public static int calculateSizeOfOneSec(int sampleRate, int frameSize) {
        return sampleRate * frameSize;
    }

    public static int calculateFrameSize(AudioFormat format) {
        return format.getChannels() * format.getSampleSizeInBits() / 8;
    }


//    private double learningRate;
//    private double momentum;            // Only used in Nesterovs
//    private int seed;
//    private int[] numberOfNodesInLayer;
//    private WeightInit[] weightInits;
//    private Activation[] activations;
//    private IUpdater updater;
//    private Updater updaterFromEnum;
//    private LossFunction lossFunction;
//
//    private long currentConfNumber;
//    private IDropout[] dropouts;          // TODO: Dropout
//
//
//    private void writeNNConfigToWriter(int totalHiddenLayersCount, Writer writer, Updater updater) throws IOException {
//        writer.write("NN config number " + currentConfNumber + ": " + totalHiddenLayersCount + " " + learningRate);
//        writer.write(System.lineSeparator());
//        for (int i = 0; i < numberOfNodesInLayer.length; i++) {
//            writer.write(numberOfNodesInLayer[i] + " ");
//        }
//        writer.write(System.lineSeparator());
//        for (int i = 0; i < activations.length; i++) {
//            writer.write(activations[i] + " ");
//        }
//        writer.write(System.lineSeparator());
//        for (int i = 0; i < weightInits.length; i++) {
//            writer.write(weightInits[i] + " ");
//        }
//        writer.write(System.lineSeparator());
//        for (int i = 0; i < dropouts.length; i++) {                    // TODO: Dropout
//            writer.write(dropouts[i] + " ");
//        }
//
//        writer.write(lossFunction.toString() + " " + updater.toString());
//    }
//
//
//    private final double defaultDropoutValue = 0.1;
//    private final Activation defaultActivation = Activation.SOFTMAX;
//    private final WeightInit defaultWeightInit = WeightInit.RELU_UNIFORM;
//    private final LossFunction defaultLossFunction = LossFunction.SQUARED_LOSS;
//    private final Updater defaultUpdater = Updater.ADAMAX;
//
//    // currentFor could be considered as current layer.
//    public void infiniteNestedFors(int lowerBound, int upperBound, int currentFor, int maxFor,
//                                   boolean tryWeightInits, boolean tryActivations, boolean tryDropouts,
//                                   boolean tryUpdaters, boolean tryLossFunctions, boolean tryMomentumForNesterovs) throws IOException {
//        int numberOfWeightInitsTried = 0;
//        int numberOfActivationsTried = 0;
//        int numberOfDropoutsTried = 0;
//        int numberOfUpdatersTried = 0;
//        int numberOfLossFunctionsTried = 0;
//
//
//        if (currentFor == maxFor) {                     // TODO: put this if in one method
//            MultiLayerConfiguration networkConf;
//            if (tryLossFunctions) {
//                for (LossFunction lf : LossFunction.values()) {
//                    if (lf == LossFunction.CUSTOM || lf == LossFunction.EXPLL || lf == LossFunction.RMSE_XENT) {
//                        continue;
//                    }
//                    lossFunction = lf;
//                    if (tryUpdaters) {
//                        for (Updater u : Updater.values()) {               // TODO: updatery
//                            updaterFromEnum = u;
//                            if (u == Updater.CUSTOM) {
//                                continue;
//                            }
//                            if (u != Updater.NESTEROVS) {
//                                writeNNConfigToWriter(maxFor, writerWithEvals, updaterFromEnum);
//                                //     writeNNConfigToWriter(maxFor, writerWithEvals, u);
//                                updater = u.getIUpdaterWithDefaultConfig();
//                                if (updater.hasLearningRate()) {
//                                    updater.setLrAndSchedule(learningRate, null);
//                                }
//                                networkConf = setNNGeneral(trainingData.getFeatures().columns(), learningRate, seed, numberOfNodesInLayer, weightInits, activations, updater, lossFunction, true);
//                                trainAndTestNN(networkConf);
//                                currentConfNumber++;
//                            } else {    // Also set momentum, because it is Nesterovs
//                                momentum = 0.1;
//                                if (tryMomentumForNesterovs) {
//                                    for (int i = 0; i < 10; i++) {    //TODO: przc - ale vyzkouset az prozdeji - prida zbytecne mnoho casu
//                                        writeNNConfigToWriter(maxFor, writerWithEvals, updaterFromEnum);
//                                        writerWithEvals.write(" " + momentum);
//                                        updater = new Nesterovs(learningRate, momentum);
//                                        networkConf = setNNGeneral(trainingData.getFeatures().columns(), learningRate, seed, numberOfNodesInLayer, weightInits, activations, updater, lossFunction, true);
//                                        trainAndTestNN(networkConf);
//                                        momentum = momentum + 0.1;          // Won't add exactly 0.1 since 0.1 is infinite in binary representation }
//                                        currentConfNumber++;
//                                    }
//                                } else {
//                                    writeNNConfigToWriter(maxFor, writerWithEvals, updaterFromEnum);
//                                    writerWithEvals.write(" " + momentum);
//                                    updater = new Nesterovs();                          // TODO: przc
//                                    networkConf = setNNGeneral(trainingData.getFeatures().columns(), learningRate, seed, numberOfNodesInLayer, weightInits, activations, updater, lossFunction, true);
//                                    trainAndTestNN(networkConf);
//                                    currentConfNumber++;
//                                }
//                            }
//                        }
//                    } else {
//                        updaterFromEnum = defaultUpdater;
//                        writeNNConfigToWriter(maxFor, writerWithEvals, updaterFromEnum);
//                        networkConf = setNNGeneral(trainingData.getFeatures().columns(), learningRate, seed, numberOfNodesInLayer, weightInits, activations, updater, lossFunction, true);
//                        trainAndTestNN(networkConf);
//                        currentConfNumber++;
//                    }
//                }
//            } else {
//                lossFunction = defaultLossFunction;
//                if (tryUpdaters) {
//                    for (Updater u : Updater.values()) {               // TODO: updatery
//                        updaterFromEnum = u;
//                        if (u == Updater.CUSTOM) {
//                            continue;
//                        }
//                        if (u != Updater.NESTEROVS) {
//                            writeNNConfigToWriter(maxFor, writerWithEvals, updaterFromEnum);
//                            updater = u.getIUpdaterWithDefaultConfig();
//                            if (updater.hasLearningRate()) {
//                                updater.setLrAndSchedule(learningRate, null);
//                            }
//                            networkConf = setNNGeneral(trainingData.getFeatures().columns(), learningRate, seed, numberOfNodesInLayer, weightInits, activations, updater, lossFunction, true);
//                            trainAndTestNN(networkConf);
//                            currentConfNumber++;
//                        } else {    // Also set momentum, because it is Nesterovs
//                            momentum = 0.1;
//                            if (tryMomentumForNesterovs) {
//                                for (int i = 0; i < 10; i++) {    //TODO: przc - ale vyzkouset az prozdeji - prida zbytecne mnoho casu
//                                    writeNNConfigToWriter(maxFor, writerWithEvals, updaterFromEnum);
//                                    writerWithEvals.write(" " + momentum);
//                                    updater = new Nesterovs(learningRate, momentum);
//                                    networkConf = setNNGeneral(trainingData.getFeatures().columns(), learningRate, seed, numberOfNodesInLayer, weightInits, activations, updater, lossFunction, true);
//                                    trainAndTestNN(networkConf);
//                                    momentum = momentum + 0.1;          // Won't add exactly 0.1 since 0.1 is infinite in binary representation }
//                                    currentConfNumber++;
//                                }
//                            } else {
//                                writeNNConfigToWriter(maxFor, writerWithEvals, updaterFromEnum);
//                                writerWithEvals.write(" " + momentum);
//                                updater = new Nesterovs();                          // TODO: przc
//                                networkConf = setNNGeneral(trainingData.getFeatures().columns(), learningRate, seed, numberOfNodesInLayer, weightInits, activations, updater, lossFunction, true);
//                                trainAndTestNN(networkConf);
//                                currentConfNumber++;
//                            }
//                        }
//                    }
//                } else {
//                    updaterFromEnum = defaultUpdater;
//                    writeNNConfigToWriter(maxFor, writerWithEvals, updaterFromEnum);
//                    networkConf = setNNGeneral(trainingData.getFeatures().columns(), learningRate, seed, numberOfNodesInLayer, weightInits, activations, updater, lossFunction, true);
//                    trainAndTestNN(networkConf);
//                    currentConfNumber++;
//                }
//            }
//        } else if (currentFor < maxFor - 1) {
//            for (int i = lowerBound; i <= upperBound; i++) {
//                numberOfNodesInLayer[currentFor] = i;
//                callMethodRecursivelyBasedOnBooleanArgs(lowerBound, i, currentFor, maxFor,
//                    tryWeightInits, tryActivations, tryDropouts, tryUpdaters, tryLossFunctions, tryMomentumForNesterovs);
//            }
//        } else {        // currentFor = maxFor - 1 ... output layer
//            callMethodRecursivelyBasedOnBooleanArgs(lowerBound, upperBound, currentFor, maxFor,
//                tryWeightInits, tryActivations, tryDropouts, tryUpdaters, tryLossFunctions, tryMomentumForNesterovs);
//        }
//    }
//
//
//    private void callMethodRecursivelyBasedOnBooleanArgs(int lowerBound, int currentUpperBound, int currentFor, int maxFor,
//                                                         boolean tryWeightInits, boolean tryActivations, boolean tryDropouts,
//                                                         boolean tryUpdaters, boolean tryLossFunctions, boolean tryMomentumForNesterovs) throws IOException {
//        if (tryActivations) {
//            for (Activation activation : Activation.values()) {
//                activations[currentFor] = activation;
//                if (tryWeightInits) {
//                    for (WeightInit weightInit : WeightInit.values()) {
//                        weightInits[currentFor] = weightInit;
//                        if (tryDropouts) {
//                            double dropoutValue = defaultDropoutValue;
//                            for (int j = 0; j < 8; j++) {           // TODO: Dropout
//                                dropouts[currentFor] = new Dropout(dropoutValue);          // TODO: Dropout
//                                dropoutValue = dropoutValue + 0.1;                  // TODO: Dropout
//                                infiniteNestedFors(lowerBound, currentUpperBound, currentFor + 1, maxFor,
//                                    tryWeightInits, tryActivations, tryDropouts,
//                                    tryUpdaters, tryLossFunctions, tryMomentumForNesterovs);
//                            }                                     // TODO: Dropout
//                        } else {
//                            dropouts[currentFor] = new Dropout(defaultDropoutValue);
//                            infiniteNestedFors(lowerBound, currentUpperBound, currentFor + 1, maxFor,
//                                tryWeightInits, tryActivations, tryDropouts,
//                                tryUpdaters, tryLossFunctions, tryMomentumForNesterovs);
//                        }
//                    }
//                } else {
//                    weightInits[currentFor] = WeightInit.NORMAL;
//                    if (tryDropouts) {
//                        double dropoutValue = defaultDropoutValue;
//                        for (int j = 0; j < 8; j++) {           // TODO: Dropout
//                            dropouts[currentFor] = new Dropout(dropoutValue);          // TODO: Dropout
//                            dropoutValue = dropoutValue + 0.1;                  // TODO: Dropout
//                            infiniteNestedFors(lowerBound, currentUpperBound, currentFor + 1, maxFor,
//                                tryWeightInits, tryActivations, tryDropouts,
//                                tryUpdaters, tryLossFunctions, tryMomentumForNesterovs);
//                        }                                     // TODO: Dropout
//                    } else {
//                        dropouts[currentFor] = new Dropout(defaultDropoutValue);
//                        infiniteNestedFors(lowerBound, currentUpperBound, currentFor + 1, maxFor,
//                            tryWeightInits, tryActivations, tryDropouts,
//                            tryUpdaters, tryLossFunctions, tryMomentumForNesterovs);
//                    }
//                }
//            }
//        } else {
//            activations[currentFor] = defaultActivation;
//            if (tryWeightInits) {
//                for (WeightInit weightInit : WeightInit.values()) {
//                    weightInits[currentFor] = weightInit;
//                    if (tryDropouts) {
//                        double dropoutValue = defaultDropoutValue;
//                        for (int j = 0; j < 8; j++) {           // TODO: Dropout
//                            dropouts[currentFor] = new Dropout(dropoutValue);          // TODO: Dropout
//                            dropoutValue = dropoutValue + 0.1;                  // TODO: Dropout
//                            infiniteNestedFors(lowerBound, currentUpperBound, currentFor + 1, maxFor,
//                                tryWeightInits, tryActivations, tryDropouts,
//                                tryUpdaters, tryLossFunctions, tryMomentumForNesterovs);
//                        }                                     // TODO: Dropout
//                    } else {
//                        dropouts[currentFor] = new Dropout(defaultDropoutValue);
//                        infiniteNestedFors(lowerBound, currentUpperBound, currentFor + 1, maxFor,
//                            tryWeightInits, tryActivations, tryDropouts,
//                            tryUpdaters, tryLossFunctions, tryMomentumForNesterovs);
//                    }
//                }
//            } else {
//                weightInits[currentFor] = WeightInit.NORMAL;
//                if (tryDropouts) {
//                    double dropoutValue = defaultDropoutValue;
//                    for (int j = 0; j < 8; j++) {           // TODO: Dropout
//                        dropouts[currentFor] = new Dropout(dropoutValue);          // TODO: Dropout
//                        dropoutValue = dropoutValue + 0.1;                  // TODO: Dropout
//                        infiniteNestedFors(lowerBound, currentUpperBound, currentFor + 1, maxFor,
//                            tryWeightInits, tryActivations, tryDropouts,
//                            tryUpdaters, tryLossFunctions, tryMomentumForNesterovs);
//                    }                                     // TODO: Dropout
//                } else {
//                    dropouts[currentFor] = new Dropout(defaultDropoutValue);
//                    infiniteNestedFors(lowerBound, currentUpperBound, currentFor + 1, maxFor,
//                        tryWeightInits, tryActivations, tryDropouts,
//                        tryUpdaters, tryLossFunctions, tryMomentumForNesterovs);
//                }
//            }
//        }
//    }
//
//
//    //lowerBoundNeurons should be at least 1
//    public void tryAllNN(int upperBoundSeconds, int upperBoundHiddenLayers, int upperBoundNeurons, int numberOfSecondsInInput, int lowerBoundHiddenLayers,
//                         int lowerBoundNeurons, boolean tryWeightInits, boolean tryActivations, boolean tryDropouts, boolean tryLearningRates,
//                         boolean tryUpdaters, boolean tryLossFunctions, boolean tryMomentumForNesterovs) throws IOException {
////        if(Genre.values().length > 5) {
////            throw new IOException("There is more genres than there should be");
//        // is thrown because in future we may want to recognize other genres, if so then we need to change this method, so this warning, that we should change it
////        }
//        seed = 1337;
//        currentConfNumber = 0;
//        for (; numberOfSecondsInInput < upperBoundSeconds; numberOfSecondsInInput++) {
//            writerWithEvals = new FileWriter(directoryWithEvals + "\\" + numberOfSecondsInInput);
//            numberOfSecondsInInputSong = numberOfSecondsInInput;
//            setTrainingData(numberOfSecondsInInput);
//            setTestingData(numberOfSecondsInInput);
////            trainingData.normalize();             // TODO: replaced by my own implementation
//            for (; lowerBoundHiddenLayers < upperBoundHiddenLayers; lowerBoundHiddenLayers++) {
//                numberOfNodesInLayer = new int[lowerBoundHiddenLayers];
//                weightInits = new WeightInit[lowerBoundHiddenLayers + 1];
//                activations = new Activation[lowerBoundHiddenLayers + 1];
//                dropouts = new Dropout[lowerBoundHiddenLayers + 1];             // TODO: Dropout
//                learningRate = 0.1;
////                for(int i = 0; i < 5; i++) {    // TODO: przc
//                infiniteNestedFors(lowerBoundNeurons, upperBoundNeurons, 0, lowerBoundHiddenLayers + 1,
//                    tryWeightInits, tryActivations, tryDropouts, tryUpdaters, tryLossFunctions, tryMomentumForNesterovs);      // + 1 because of outputLayer
//                learningRate = learningRate / 10;
////                }                               // TODO: przc
//            }
//        }
//
//    }
//
//
//    /**
//     * Train neural network and return the model.
//     *
//     * @param numberOfSecondsInInput represents the number of seconds, which will be from each song given to input of nn,
//     * @return Returns the trained model of neural network.
//     * @throws IOException is thrown as warning - In future when new genre is added, then we need to also change the code in this method.
//     *                     So exception is thrown to remind us of changing the code.
//     */
//    public MultiLayerNetwork trainNN(int numberOfSecondsInInput) throws IOException {
//        if (Genre.values().length > 5) {
//            throw new IOException("There is more genres than there should be");
//            // is thrown because in future we may want to recognize other genres, if so then we need to change this method, so this warning, that we should change it
//        }
//        // training data
//        DataSet dataset = setTrainingData(numberOfSecondsInInput);
////        dataset.normalize();                          TODO: replaced by my own implementation
//        MultiLayerConfiguration conf = setNN(dataset.getFeatures().columns());
//        MultiLayerNetwork modelNN = trainNN(dataset, conf);
//        // TODO: Was like this before without early stopping
///*        MultiLayerNetwork modelNN = new MultiLayerNetwork(conf);
//        modelNN.setListeners(new ScoreIterationListener(10));  //Print score every 10 parameter updates
//        modelNN.init();
//        int nEpochs = 100000;
//        for ( int n = 0; n < nEpochs; n++) {
//            modelNN.fit(dataset);
//        }
//*/
//
//        List<util.Pair<String, String>> pairs = getInfoPairsForModel();    // TODO: Tenhle Pair je dost nebezpecnej ... jsem uplne zapomnel ze v ty knihovne uz je pair
//        ModelInfo.createModelInfoFile(fileWithModel + ".xml", pairs);                                           // TODO: Nebezpecnej ve smyslu, ze si je zpletu
//        ModelSerializer.writeModel(modelNN, fileWithModel, true);
//
//        return modelNN;
//    }
//
//    private List<util.Pair<String, String>> getInfoPairsForModel() {
//        List<util.Pair<String, String>> pairs = new ArrayList<>();
//        util.Pair<String, String> pair =
//            new util.Pair<String, String>("inputAudioLength", ((Integer) numberOfSecondsInInputSong).toString());
//        pairs.add(pair);        // TODO: For now only this statistic, but in future it would be nice to add number of training/test data and maybe even name of songs
//        return pairs;
//    }
//
//    // Expects the trainingData variable to be set
//    public void trainAndTestNN(MultiLayerConfiguration configNN) throws IOException {
//        MultiLayerNetwork modelNN = trainNN(trainingData, configNN);
//        testNN(modelNN, numberOfSecondsInInputSong, writerWithEvals);
//    }
//
//
//    // doesn't serialize model
//    public MultiLayerNetwork trainNN(DataSet input, MultiLayerConfiguration configNN) throws IOException {
//        try {
//            ArrayList<Pair<INDArray, INDArray>> list = new ArrayList<Pair<INDArray, INDArray>>();
//            // Version 1 everything in 1 data set (1 row is 1 example)
//            Pair<INDArray, INDArray> pair = new Pair<INDArray, INDArray>(input.getFeatures(), input.getLabels());
//            list.add(pair);                                     // TODO: Like this or just add pair of every row (example)
//            INDArrayDataSetIterator dsi = new INDArrayDataSetIterator(list, 1);
//            //
//            // Version 2: Version with pair for every row:
////        ArrayList<Pair<INDArray,INDArray>> list = new ArrayList<Pair<INDArray,INDArray>>();
////        INDArray features = input.getFeatures();
////        INDArray labels = input.getLabels();
////        for(int i = 0; i < input.numExamples(); i++) {
////            Pair<INDArray, INDArray> pair = new Pair<INDArray,INDArray>(features.getRow(i), labels.getRow(i));
////            list.add(pair);
////        }
////        INDArrayDataSetIterator dsi = new INDArrayDataSetIterator(list, input.numExamples());
//            //
//
////        DataSetIterator dsi = new MyDatasetIterator(testingData);     // TODO: REMOVE MyDatasetIterator
//
//            EarlyStoppingConfiguration esConf = new EarlyStoppingConfiguration.Builder()
//                .epochTerminationConditions(new MaxEpochsTerminationCondition(250))
//                .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES))
////            .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(3, TimeUnit.MINUTES))      // TODO: przc
//                .scoreCalculator(new DataSetLossCalculator(dsi, true))
//                .evaluateEveryNEpochs(1)
////            .modelSaver(new LocalFileModelSaver(directory))
//                .build();
//
//            EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, configNN, dsi);
//            trainer.setListener(null);
//            EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
//            System.out.println("aaaaaaaaaaaaa");
//            MultiLayerNetwork modelNN = result.getBestModel();
//            System.out.println("aaaaaaaaaaaaa\t" + (modelNN == null));
//
//            // TODO: Remove - for optimalization
//            //Print out the results:
////            writerWithEvals.write(System.lineSeparator());                                                  // TODO: just debugging - remove later
////            writerWithEvals.write("Score at best epoch: " + ((Double) result.getBestModelScore()).toString());       // TODO: just debugging - rem
////            writerWithEvals.write(System.lineSeparator());
////            writerWithEvals.write("Best epoch number: " + result.getBestModelEpoch());  // TODO: nemeni ... remove later
////            writerWithEvals.write(System.lineSeparator());                                  // TODO: nemeni ... remove later
//            System.out.println("Termination reason: " + result.getTerminationReason());
//            System.out.println("Termination details: " + result.getTerminationDetails());
//            System.out.println("Total epochs: " + result.getTotalEpochs());
//            System.out.println("Best epoch number: " + result.getBestModelEpoch());
//            System.out.println("Score at best epoch: " + result.getBestModelScore());
////        MultiLayerNetwork modelNN = new MultiLayerNetwork(configNN);
////        modelNN.setListeners(new ScoreIterationListener(10));  //Print score every 10 parameter updates
////        modelNN.init();
////        int nEpochs = 100000;
////        for ( int n = 0; n < nEpochs; n++) {
////            modelNN.fit(input);
////        }
//            return modelNN;
//        } catch (Exception e) {             // TODO: remove try, catch blocks from this method
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            System.out.println(e.getMessage());
//            return null;
//        }
//    }
//
//
//    /**
//     * Tests the model given as paramater, where the data for 1 song are first numberOfSecondsInInput seconds. Writes statistics to writer
//     *
//     * @param trainedModelNN         is the model to be tested (evaluated).
//     * @param numberOfSecondsInInput is the length of 1 input sound.
//     * @param writer                 is the writer, where will be written the evaluation. If is null, then doesn't write.
//     * @throws IOException is thrown when we fail to write to the writer.
//     */
//    public void testNN(MultiLayerNetwork trainedModelNN, int numberOfSecondsInInput, Writer writer) throws IOException {
//        INDArray output = trainedModelNN.output(testingData.getFeatures());
//        eval.eval(testingData.getLabels(), output);
//        if (writer != null) {
//            writer.write(eval.stats());
//        }
//        writer.write(System.lineSeparator());
//        writer.write(((Double) eval.accuracy()).toString());
//        if (eval.accuracy() > 0.5) {
//            boolean fileCreated = false;
//            try {
//                File f = new File(directoryWithEvalsOver50 + "\\" + currentConfNumber + "-" + eval.accuracy() + ".txt");
//                if (!f.exists()) {
//                    fileCreated = f.createNewFile();
//                }
//                Writer w = new FileWriter(f);
//                writeNNConfigToWriter(trainedModelNN.getLayers().length, w, updaterFromEnum);
//                w.write(System.lineSeparator());
//                w.write(eval.stats());
//                w.close();
//            } catch (Exception ex) {
//                System.out.println(ex.getMessage() + "\tFile created:" + fileCreated);
//            }
//            writer.write(" !");
//        }
//        if (eval.accuracy() > 0.6) {
//            writer.write("!");
//        }
//        if (eval.accuracy() > 0.7) {
//            writer.write("!");
//        }
//        if (eval.accuracy() > 0.8) {
//            writer.write("!");
//        }
//        if (eval.accuracy() > 0.9) {
//            writer.write("!");
//        }
//
//        for (int numberOfEmptyLines = 0; numberOfEmptyLines < 2; numberOfEmptyLines++) {
//            writer.write(System.lineSeparator());
//        }
//        writer.write("End of neural network evaluation");
//        // ty testovaci data budou furt stejny tak ty uz budu mit predpripraveny proto nejsou jako parametry
//    }
//
//
//    public DataSet prepareData(String parentDir, String metalDir, String popDir, String rockDir, String classicalDir, String rapDir, int numberOfSecondsInInput, boolean isTrainingData) throws IOException {
//        FileWriter writer;
//        if (isTrainingData) {
//            writer = new FileWriter("TrainingData.txt");
//        } else {
//            writer = new FileWriter("TestingData.txt");
//        }
//        int totalNumberOfInputs = fileCountInDir(parentDir, numberOfSecondsInInput);
//        //
//        INDArray input = Nd4j.zeros(totalNumberOfInputs, numberOfSecondsInInput);
//        // 5 output neurons - for each genre 1, if the neuron gives 1 then it is that genre.
//        INDArray labels = Nd4j.zeros(totalNumberOfInputs, Genre.values().length);
//
//        int currentRow = 0;
//
//        currentRow = processDirectory(metalDir, Genre.METAL, 0, currentRow, input, labels, writer, numberOfSecondsInInput);
//        currentRow = processDirectory(popDir, Genre.POP, 1, currentRow, input, labels, writer, numberOfSecondsInInput);
//        currentRow = processDirectory(rockDir, Genre.ROCK, 2, currentRow, input, labels, writer, numberOfSecondsInInput);
//        currentRow = processDirectory(classicalDir, Genre.CLASSICAL, 3, currentRow, input, labels, writer, numberOfSecondsInInput);
//        currentRow = processDirectory(rapDir, Genre.RAP, 4, currentRow, input, labels, writer, numberOfSecondsInInput);
//        writer.close();
//
//        DataSet ds = new DataSet(input, labels);
//        return ds;
//    }
//
//
//    public MultiLayerConfiguration setNNGeneral(int numberOfInputs, double learningRate, int seed, int[] numberOfNodesInLayer, WeightInit[] weightInits, Activation[] activations, IUpdater updater, LossFunction lossFunction, boolean setSeed) {
//        NeuralNetConfiguration.Builder b = new NeuralNetConfiguration.Builder();
//        if (setSeed) {
//            b = b.seed(seed);            // TODO: nebo b = b.seed(100) ??? asi by tam melo byt to = ... preci jen tam neni to ze to vraci ten typ jen tak (ale typicky to vraci this)
//        }
//        b = b.updater(updater);
//        NeuralNetConfiguration.ListBuilder listBuilder = b.list();
////        b.updater(new Nesterovs(learningRate, 0.9));
//
//        Layer hiddenLayer;
//        hiddenLayer = new DenseLayer.Builder().nIn(numberOfInputs).nOut(numberOfNodesInLayer[0])
//            .weightInit(weightInits[0])
//            .activation(activations[0])
////                .dropOut(dropouts[0])                          // TODO: Dropout // TODO: Is another hypermparameter - that means do it the same way as for the layers and nodes, etc.
//            .dropOut(new Dropout(0.5))
//            .build();
//        listBuilder.layer(0, hiddenLayer);
//
//        for (int i = 0; i < numberOfNodesInLayer.length - 1; i++) {      // Create hidden layers which neighbour with at least 1 hidden layer
//            hiddenLayer = new DenseLayer.Builder().nIn(numberOfNodesInLayer[i]).nOut(numberOfNodesInLayer[i + 1])
//                .weightInit(weightInits[i])
//                .activation(activations[i])
////                .dropOut(dropouts[i])                          // TODO: Dropout // TODO: Is another hypermparameter - that means do it the same way as for the layers and nodes, etc.
//                .dropOut(new Dropout(0.5))
//                .build();
//
//            listBuilder.layer(i + 1, hiddenLayer);                  // TODO: bylo i
//        }
//        System.out.println("Node numbers in layers:");
//        for (int i = 0; i < numberOfNodesInLayer.length; i++) {
//            System.out.println(numberOfNodesInLayer[i]);
//        }
//
////        b.layer(hiddenLayer);
//        Layer outputLayer = new OutputLayer.Builder(lossFunction)
//            .weightInit(weightInits[weightInits.length - 1])
//            .activation(activations[activations.length - 1])
//            .lossFunction(lossFunction)
////            .dropOut(dropouts[i])                          // TODO: Dropout
//            .nIn(numberOfNodesInLayer[numberOfNodesInLayer.length - 1]).nOut(Genre.values().length).build();
//
//        listBuilder.layer(numberOfNodesInLayer.length, outputLayer);        // TODO: bylo numberOfNodesInLayer.length - 1 Ale ted po zmene to je takhle, to je dobre protoze delka tohoto pole odpovida poctu skrytych vrstev a protoze indexujeme od 0 a neni tam zapocitana vystupni vrstva tak to je takhle
//
//
//        // TODO: musim to volat na tom .list ty metody s layer jinak to nevrati multilayer - to dava smysl - ono to vraci list builder - a do neho jen pridavam ty jednotlivy vrstvy !!!!!
//
////        listBuilder.layer(0, hiddenLayer);
////        listBuilder.layer(1, outputLayer);
//
//
//        // TODO!!!!!!!!!!!!!!!
//        //return listBuilder.pretrain(false).backprop(true).build();     // TODO: Tohle driv fungovalo, ale ted uz listBuilder nema metody pretrain a backprop
//        return listBuilder.build();
//        // TODO!!!!!!!!!!!!!!!
//
//
////        MultiLayerConfiguration conf1 = listBuilder.build();                    // asi zhruba takhle
//        // URÄŚITÄš TO MĂ� BĂťT b =  ... protoĹľe to vĹľdycky volĂˇm na vĂ˝sledku kdyĹľ tam je ta .
//    }
//
//    /**
//     * Returns configurated neural network, which expects numberOfInputs as number of inputs.
//     *
//     * @param numberOfInputs
//     * @return Returns configurated neural network
//     */
//    public MultiLayerConfiguration setNN(int numberOfInputs) {
////        double learningRate = 0.1;
////        int hiddenNodesCount = 4;
////        int numOutputs = Genre.values().length;
////        int seed = 100;
////        int[] numberOfNodes = new int[]{numberOfInputs, hiddenNodesCount, numOutputs};     // For each layer
////        WeightInit[] weightInits = new WeightInit[]{WeightInit.XAVIER, WeightInit.XAVIER};
////        Activation[] activations = new Activation[]{Activation.RELU, Activation.SOFTMAX};
////        Updater[] updaters = Updater.values();
////
////
////        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
////            .seed(seed)
////            .updater(new Nesterovs(learningRate, 0.9))
////            .list()
////            .layer(0, new DenseLayer.Builder().nIn(numberOfInputs).nOut(hiddenNodesCount)
////                .weightInit(WeightInit.XAVIER)
////                .activation(Activation.RELU)
////                .dropOut(new Dropout(0.5))
////                .build())
////            .layer(1, new OutputLayer.Builder(LossFunction.MSE)
////                .weightInit(WeightInit.XAVIER)
////                .activation(Activation.SOFTMAX)
////                .nIn(hiddenNodesCount).nOut(numOutputs).build())
////            // TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!
////            //.pretrain(false).backprop(true).build();// TODO: Tohle driv fungovalo, ale ted uz to nema metody pretrain a backprop
////                .build();
////            // TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!
////
////        NeuralNetConfiguration.Builder b = new NeuralNetConfiguration.Builder();
////        b = b.seed(100);            // TODO: nebo b = b.seed(100) ??? asi by tam melo byt to = ... preci jen tam neni to ze to vraci ten typ jen tak (ale typicky to vraci this)
////        b = b.updater(new Nesterovs(learningRate, 0.9));
////        Layer hiddenLayer = new DenseLayer.Builder().nIn(numberOfInputs).nOut(hiddenNodesCount)
////            .weightInit(WeightInit.XAVIER)
////            .activation(Activation.RELU)
////            .build();
////// TODO: davat to b = radsi
////     // TODO: Ted jsem odstranil   b.layer(hiddenLayer);
////        Layer outputLayer = new OutputLayer.Builder(LossFunction.MSE)
////            .weightInit(WeightInit.XAVIER)
////            .activation(Activation.SOFTMAX)
////            .lossFunction(LossFunction.MSE)
////            .nIn(hiddenNodesCount).nOut(numOutputs).build();
////
////
////        // TODO: musim to volat na tom .list ty metody s layer jinak to nevrati multilayer - to dava smysl - ono to vraci list builder - a do neho jen pridavam ty jednotlivy vrstvy !!!!!
////
////        NeuralNetConfiguration.ListBuilder listBuilder = b.list();
////
////        listBuilder.layer(0, hiddenLayer);
////        listBuilder.layer(1, outputLayer);
////        MultiLayerConfiguration conf1 = listBuilder.build();                    // asi zhruba takhle
//////        return conf1;           // TODO: Ted to delam takhle
////        // URÄŚITÄš TO MĂ� BĂťT b =  ... protoĹľe to vĹľdycky volĂˇm na vĂ˝sledku kdyĹľ tam je ta .
////
////        return conf;
//
//
//
//
//
//
//
//
//
//
//
//
//        ////////////////////////////////////////////////////////////----------------------------------------------------
//        NeuralNetConfiguration.Builder b = new NeuralNetConfiguration.Builder();
//        b = b.seed(100);            // TODO: nebo b = b.seed(100) ??? asi by tam melo byt to = ... preci jen tam neni to ze to vraci ten typ jen tak (ale typicky to vraci this)
//        b = b.updater(new Sgd());
//        NeuralNetConfiguration.ListBuilder listBuilder = b.list();
////        b.updater(new Nesterovs(learningRate, 0.9));
//
//        int numberOfHiddenNeurons =  5000;
//        Layer hiddenLayer;
//        hiddenLayer = new DenseLayer.Builder().nIn(numberOfInputs).nOut(numberOfHiddenNeurons)
//            .weightInit(WeightInit.NORMAL)
//            .activation(Activation.SIGMOID)
////                .dropOut(dropouts[0])                          // TODO: Dropout // TODO: Is another hypermparameter - that means do it the same way as for the layers and nodes, etc.
//            .dropOut(new Dropout(0.5))
//            .build();
//        listBuilder.layer(0, hiddenLayer);
//
//
////        b.layer(hiddenLayer);
//        Layer outputLayer = new OutputLayer.Builder(LossFunction.MSE)
//            .weightInit(WeightInit.NORMAL)
//            .activation(Activation.SIGMOID)
//            .lossFunction(LossFunction.MSE)
////            .dropOut(dropouts[i])                          // TODO: Dropout
//            .nIn(numberOfHiddenNeurons).nOut(Genre.values().length).build();
//
//        listBuilder.layer(1, outputLayer);        // TODO: bylo numberOfNodesInLayer.length - 1 Ale ted po zmene to je takhle, to je dobre protoze delka tohoto pole odpovida poctu skrytych vrstev a protoze indexujeme od 0 a neni tam zapocitana vystupni vrstva tak to je takhle
//
//
//        // TODO: musim to volat na tom .list ty metody s layer jinak to nevrati multilayer - to dava smysl - ono to vraci list builder - a do neho jen pridavam ty jednotlivy vrstvy !!!!!
//
////        listBuilder.layer(0, hiddenLayer);
////        listBuilder.layer(1, outputLayer);
//
//
//        // TODO!!!!!!!!!!!!!!!
//        //return listBuilder.pretrain(false).backprop(true).build();     // TODO: Tohle driv fungovalo, ale ted uz listBuilder nema metody pretrain a backprop
//        return listBuilder.build();
//        // TODO!!!!!!!!!!!!!!!
//
//
//    }
//
//
//    // Used for choosing the right label
//    public static INDArray[] labelReferenceArrs;
//
//    static {
//        labelReferenceArrs = new INDArray[Genre.values().length];
//        for (int i = 0; i < labelReferenceArrs.length; i++) {
//            INDArray arr = Nd4j.zeros(labelReferenceArrs.length);
//            arr.putScalar(new int[]{i}, 1);
//            labelReferenceArrs[i] = arr;
//        }
//    }
//
//
//    /**
//     * @param dirName         is the name of the directory with the inputs.
//     * @param genre           is the genre.
//     * @param label           is the index of the neuron which value should be 1 for certain genre.
//     * @param currentRow      is currentRow in the input (1 row is 1 input for neural network)
//     * @param input           is the array containing all the inputs
//     * @param labels          is the array containing labels for corresponding input.
//     * @param writer          is the writer, which writes to file what songs were processed and if they were processed without error.
//     * @param numberOfSeconds is the number of seconds of 1 input fed to the neural netowrk
//     * @return Returns the current row in input (total number of processed inputs without error)
//     * @throws IOException is thrown when writer fails.
//     */
//    public int processDirectory(String dirName, Genre genre, int label, int currentRow, INDArray input, INDArray labels, Writer writer, int numberOfSeconds) throws IOException {
//        Path dir = Paths.get(dirName);
//        File[] fileList = dir.toFile().listFiles();
//        writer.write(dirName);
//        writer.write(System.lineSeparator());
//        for (int i = 0; i < fileList.length; i++) {
//            if (fileList[i].isDirectory()) {
//                currentRow = processDirectory(fileList[i].getAbsolutePath(), genre, label, currentRow, input, labels, writer, numberOfSeconds);
//            } else {
//                try {
//                    System.out.println("Processing\t" + genre.toString() + " - " + fileList[i].getName());
//// TODO: Tyhle koncovky jsou pokruty uvnitr setVariables                    if (fileList[i].getPath().toLowerCase().endsWith(".mp3") || fileList[i].getPath().toLowerCase().endsWith(".wav")) {
//                        if(!setVariables(fileList[i].getAbsolutePath(), false)) {
//                            writer.write("FAILED\t" + genre.toString() + " - " + fileList[i].getName() + " - is not audio file");
//                            writer.write(System.lineSeparator());
//                            return currentRow;
//                        }
//                        byte[][] song = getEveryXthTimePeriodWithLength(decodedAudioStream, sampleRate * numberOfSeconds, Integer.MAX_VALUE, frameSize, 0);
//                        if (song.length != 0) {
//                            byte[] first3MinsOfSong = song[0];
////                        if(song.length != 0) {                              // TODO:
//                            //        byte[] first3MinsOfSong = song[0];
//                            ByteArrayInputStream bais = new ByteArrayInputStream(first3MinsOfSong);
//                            SongPartWithAverageValueOfSamples[] result = takeSongPartsAndAddAggregation(bais, sampleRate, frameSize, isBigEndian, isSigned, sampleSizeInBytes, false, Aggregations.AVG);
//                            double[] values = takeValuesFromSongParts(result);
//                            normalizeToDoubles(values, sampleSizeInBits, isSigned);     // normalization
//                            INDArray arr = Nd4j.create(values);
//                            input.putRow(currentRow, arr);
//                            labels.putRow(currentRow, labelReferenceArrs[label]);
//                            currentRow++;
//                            writer.write("OK\t" + genre.toString() + " - " + fileList[i].getName());
//                            writer.write(System.lineSeparator());
//                        } else {
//                            System.out.println("TOO SHORT\t" + genre.toString() + " - " + fileList[i].getName());
//                            writer.write("TOO SHORT\t" + genre.toString() + " - " + fileList[i].getName() + " - " + fileList[i].getPath());
//                            writer.write(System.lineSeparator());
//                        }
//// TODO: Koncova zavorka od toho                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    writer.write("FAILED\t" + genre.toString() + " - " + fileList[i].getName());
//                    writer.write(System.lineSeparator());
//                }
//            }
//        }
//
//        return currentRow;
//    }
//
//
//    // TODO: can add exception if minLengthInSeconds is < 0
//
//    /**
//     * Counts total number of files (of type .wav and .mp3)in directory, goes through nested directories.
//     *
//     * @param dir is the directory to count the number of files in.
//     * @return Returns number of files with .mp3 or .wav suffix in directory.
//     * @throws IOException is thrown when error in one of the files occurred.
//     */
//    public int fileCountInDir(String dir, int minLengthInSeconds) throws IOException {
//        return fileCountInDirInternal(dir, 0, minLengthInSeconds);
//    }
//
//    private int fileCountInDirInternal(String dirName, int count, int minLengthInSeconds) throws IOException {
//        Path dir = Paths.get(dirName);
//        File[] fileList = dir.toFile().listFiles();
//        for (int i = 0; i < fileList.length; i++) {
//            if (fileList[i].isDirectory()) {
//                count = fileCountInDirInternal(fileList[i].getAbsolutePath(), count, minLengthInSeconds);
//            } else {
//// TODO: Zase pryc protoze to je pokryty v setVariables                if (fileList[i].getPath().toLowerCase().endsWith(".mp3") || fileList[i].getPath().toLowerCase().endsWith(".wav")) {
//                    if(setVariables(fileList[i].getAbsolutePath(), false)) {
//                        byte[][] song = getEveryXthTimePeriodWithLength(decodedAudioStream, sampleRate * minLengthInSeconds, Integer.MAX_VALUE, frameSize, 0);
//                        // TODO: v urcitych pripadech to neni efektivni ... treba kdyz 2x delam totez
//                        if (song.length != 0) {
//                            count++;
//                        }
//                    }
//// TODO: Koncova zavorka                }
//            }
//        }
//
//        return count;
//    }



    /**
     * Takes the int values representing some property (min or max or avg or rms)of each song part and returns them in 1D double array.
     *
     * @param songParts are the samples of the song part together with int, which represents some property of the song part.
     * @return Returns 1D double array containing the int values which are in the SongPartWithAverageValueOfSamples as int property.
     */
    public static double[] takeValuesFromSongParts(SongPartWithAverageValueOfSamples[] songParts) {
        double[] values = new double[songParts.length];

        for (int i = 0; i < values.length; i++) {
            values[i] = songParts[i].averageAmplitude;
        }

        return values;
    }

    /**
     * Converts given stream to byte array,
     * setVariables needs to be called before calling this method because
     * onlyAudioSizeInBytes variable needs to be set to correct byte length of audio
     * @param stream is the stream to convert
     * @return returns the converted stream
     * @throws IOException if error with stream occurred
     */
    public byte[] convertStreamToByteArray(InputStream stream) throws IOException {
        byte[] converted = new byte[onlyAudioSizeInBytes];
        int readCount = 0;
        int totalLen = 0;
        int readLen = stream.available();
        if(readLen <= 0) {
            readLen = 4096;
        }
        else {
            readLen = Math.min(readLen, 4096);
        }
        while(readCount != -1) {
            readCount = stream.read(converted, totalLen, readLen);
            totalLen += readCount;
        }

        return converted;
    }


    /**
     * Reads n samples from stream.
     *
     * @param audioStream is the input stream with samples.
     * @param n           represents number of samples to be read.
     * @param sampleSize  represents size of one sample.
     * @return Returns bytes read with the array containing the read bytes (size of the array is always n * sampleSize)
     * @throws IOException is thrown when error with input stream occurred.
     */
    public static BytesReadWithArr readNSamples(InputStream audioStream, int n, int sampleSize) throws IOException {
        int bytesRead = 0;
        int bytesReadSum = 0;
        byte[] arr = new byte[n * sampleSize];
        int freeIndexesCount = arr.length;
        while (bytesReadSum != arr.length && bytesRead != -1) {
            bytesRead = audioStream.read(arr, bytesReadSum, freeIndexesCount);
            bytesReadSum = bytesReadSum + bytesRead;
            freeIndexesCount = freeIndexesCount - bytesRead;
        }

        return new BytesReadWithArr(arr, bytesReadSum);
    }


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
     * This method takes every x-th read part of length length from input stream
     * Starts at startFame
     * Example: if length = 3, x = 2, frameSize = 4 (bytes), startFrame = 1
     * then the first byte is discarded, then the next 3 * 4 bytes are taken, then 3 * 4 bytes are skipped and then
     * the next 3 * 4 bytes are taken, this continues until the end of the stream is reached
     * Only the parts of size length are returned (that means if the last part isn't long enough, then it won't be added to the output)
     *
     * @param audioStream is the stream containing the audio
     * @param length      is the length of one part (in frames)
     * @param x           - Every x-th part of length length is taken, if x = MAX_VALUE, then only the first part of length length is taken
     * @param frameSize   is the size of one frame
     * @param startFrame  is the first frame to be processed, that means frames from 0 to startFrame - 1 will be discarded
     * @return Returns 2 dimensional array, containing all the x-th parts of size length
     * @throws IOException is thrown when error with the input stream occurred.
     */
    public static byte[][] getEveryXthTimePeriodWithLength(InputStream audioStream, int length, int x, int frameSize, int startFrame) throws IOException {
        int currentTime = 0;
        int bytesRead = 0;
        byte[] songPart = new byte[length * frameSize];
        ArrayList<byte[]> list = new ArrayList<>();

        // Skip first startFrame * frameSize bytes ... the library skip method doesn't skip exactly the number of bytes it should
        bytesRead = readNotNeededSamples(audioStream, frameSize, startFrame);

        int bytesReadSum = 0;

        // The algorithm
        while (bytesRead != -1) {
            bytesReadSum = readNSamples(audioStream, songPart);
            if (bytesReadSum != songPart.length) {
                break;
            }
            // it's x-th part
            if (currentTime % x == 0) {
                // It has exactly length size
                if (bytesReadSum == songPart.length) {        // > 0 if we want to also get the last part, even if it isn't full
                    byte[] arr = new byte[songPart.length];
                    for (int k = 0; k < arr.length; k++) {
                        arr[k] = songPart[k];
                    }
                    list.add(arr);
                }
            }
            currentTime++;

            // take only the first part
            if (x == Integer.MAX_VALUE) {
                break;
            }
        }


        byte[][] bArr = new byte[list.size()][];
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = list.get(i);
        }

        return bArr;
    }


    /**
     * This method takes every x-th read part of length length from byte array
     * Starts at startFame
     * Example: if length = 3, x = 2, frameSize = 4 (bytes), startFrame = 1
     * then the first byte is discarded, then the next 3 * 4 bytes are taken, then 3 * 4 bytes are skipped and then
     * the next 3 * 4 bytes are taken, this continues until the end of the array is reached
     * Only the parts of size length are returned (that means if the last part isn't long enough, then it won't be added to the output)
     *
     * @param audio      is byte array containing audio
     * @param length     is the length of one part (in frames)
     * @param x          - Every x-th part of length length is taken, if x = MAX_VALUE, then only the first part of length length is taken
     * @param frameSize  is the size of one frame
     * @param startFrame is the first frame to be processed, that means frames from 0 to startFrame - 1 will be discarded
     * @return Returns 2 dimensional array, containing all the x-th parts of size length
     */
    public static byte[][] getEveryXthTimePeriodWithLength(byte[] audio, int length, int x, int frameSize, int startFrame) {

        int frameCount = audio.length / frameSize;
        int x1 = x - 1;
        int arrLen = 0;
        int remainingFrames = frameCount - startFrame;
        // Calculate how
        if (x == Integer.MAX_VALUE && remainingFrames >= length) {
            arrLen = 1;
        } else if (x == Integer.MAX_VALUE && remainingFrames < length) {
            arrLen = 0;
        } else {
            while (remainingFrames >= length) {
                arrLen++;
                remainingFrames = remainingFrames - length;
                remainingFrames = remainingFrames - length * x1;
            }
        }
        byte[][] result = new byte[arrLen][];
        int secondDimIndex = 0;
        int firstDimIndex = 0;
        int oldArrIndex = startFrame * frameSize;
        arrLen = length * frameSize;

        while (firstDimIndex < result.length) {
            secondDimIndex = 0;
            if (secondDimIndex + arrLen <= audio.length) {
                byte[] newSamples = new byte[arrLen];
                for (int i = 0; i < length; i++) {
                    for (int j = 0; j < frameSize; j++) {
                        newSamples[secondDimIndex] = audio[oldArrIndex];
                        oldArrIndex++;
                        secondDimIndex++;
                    }
                }
                result[firstDimIndex] = newSamples;
                oldArrIndex = oldArrIndex + x1 * frameSize * length;
            }
            firstDimIndex++;
        }

        return result;
    }


    /**
     * Returns 1D array containing every nth sample of size sampleSize.
     * If the result of this method wants to be played in some audio player, then it is important to notice, that to
     * play the song in original tempo we need to divide the sample rate and frame rate by n given in argument.
     * Important info: sampleSize and startSample needs to be equal to c * (original frame rate), where c > 0
     * That's limitation of java.
     *
     * @param samples     is the input stream with samples
     * @param sampleSize  is the size of one sample
     * @param n           - Every nth sample is taken
     * @param frameSize   is the size of one frame (= sampleSize * number of channels)
     * @param startSample is the number of sample to start at
     * @return Returns 1D array containing every nth sample of size sampleSize
     * @throws IOException is thrown when error with InputStream occurred or if the sampleSize is not multiple of the
     *                     original sampleSize or if the startSample is not multiple of the original sampleSize
     */
    public static byte[] takeEveryNthSampleOneChannel(InputStream samples, int sampleSize, int n, int frameSize, int startSample) throws IOException {
        int bytesRead = 0;
        byte[] arr = new byte[sampleSize * n];

        if (arr.length % frameSize != 0 || startSample % frameSize != 0) {        // limitation of java library
            throw new IOException("Not supported yet");                        // it's not possible to read smaller
        }                                                                    // chunks than size of frame
        ArrayList<Byte> sampleList = new ArrayList<>();
        byte[] newSamples;
        int bytesReadSum = 0;

        // skip samples until the startSample is reached
        bytesRead = readNotNeededSamples(samples, sampleSize, startSample);

        while (bytesRead != -1) {
            bytesReadSum = readNSamples(samples, arr);
            if (bytesReadSum >= sampleSize) {
                for (int i = 0; i < sampleSize; i++) {
                    sampleList.add(arr[i]);
                }
            } else {
                break;
            }
            if (bytesReadSum != arr.length) {
                bytesRead = -1;
            }
        }

        newSamples = new byte[sampleList.size()];
        for (int i = 0; i < newSamples.length; i++) {
            newSamples[i] = sampleList.get(i);
        }
        return newSamples;
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
    private static int readNotNeededSamples(InputStream samples, int sampleSize, int n) throws IOException {
        byte[] arr = new byte[4096];
        int bytesRead = 0;
        int bytesReadSum = 0;
        int freeIndexesCount = n * sampleSize;

        while (freeIndexesCount != 0) {
            if (freeIndexesCount > arr.length) {
                bytesRead = samples.read(arr, 0, arr.length);
            } else {
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

    // TODO: LONG - mozna bych mel vracet long
    /**
     * Returns -1 if exception ocurred otherwise returns the length of input stream
     * @param samples
     */
    public static int getLengthOfInputStream(InputStream samples) {
        int bytesRead = 0;
        int bytesReadSum = 0;

        try {
            byte[] arr = new byte[Math.min(4096, samples.available())];
            if(arr.length <= 0) {       // available returned incorrect value
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



    /**
     * Returns 1D array containing every nth sample of size sampleSize.
     * If the result of this method wants to be played in some audio player, then it is important to notice, that to
     * play the song in original tempo we need to divide the sample rate and frame rate by n given in argument
     *
     * @param samples     is the byte array containing the samples
     * @param sampleSize  is the size of one sample
     * @param n           - Every nth sample is taken
     * @param startSample is the number of sample to start at
     * @return Returns 1D array containing every nth sample of size sampleSize
     */
    public static byte[] takeEveryNthSampleOneChannel(byte[] samples, int sampleSize, int n, int startSample) {
        // Solved by calling more general method
        byte[][] newSamples = getEveryXthTimePeriodWithLength(samples, 1, n, sampleSize, startSample);

        return convertTwoDimArrToOneDim(newSamples);
    }


    /**
     * Converts the 2D array to 1D array by stacking the labelReferenceArrs
     *
     * @param arr is the 2D array to be converted to 1D array
     * @return Returns 1D array
     */
    private static byte[] convertTwoDimArrToOneDim(byte[][] arr) {
        int length = 0;
        for (int i = 0; i < arr.length; i++) {
            length = length + arr[i].length;
        }

        byte[] result = new byte[length];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                result[index] = arr[i][j];
                index++;
            }
        }

        return result;
    }


    /**
     * Takes the input stream and returns the samples of channels in byte labelReferenceArrs (1 array = 1 channel).
     *
     * @param samples    is the input stream containing samples
     * @param sampleSize is the size of 1 sample in a channel
     * @param totalAudioLength is the total length of the samples input in bytes.
     * @return Returns 2D byte array, where each byte array corresponds to 1 channel.
     * @throws IOException is thrown when error with input
     */
    public static byte[][] separateChannels(InputStream samples, int numberOfChannels, int sampleSize,
                                            int totalAudioLength) throws IOException {
        // TODO: PROGRAMO
        //return takeEveryNthSampleMoreChannels(samples, numberOfChannels, sampleSize, 1, 0);
        return takeEveryNthSampleMoreChannels(samples, numberOfChannels, sampleSize, 1, 0, totalAudioLength);
        // TODO: PROGRAMO
    }


    /**
     * Takes the input stream and returns the samples of channels in double[][] (1 array = 1 channel).
     *
     * @param samples    is the input stream containing samples
     * @param sampleSize is the size of 1 sample in a channel
     * @return Returns 2D byte array, where each double array corresponds to 1 channel.
     * @throws IOException is thrown when error with input
     */
    public static double[][] separateChannelsDouble(InputStream samples, int numberOfChannels, int sampleSize,
                                                    boolean isBigEndian, boolean isSigned, int totalAudioLength) throws IOException {
        return takeEveryNthSampleMoreChannelsDouble(samples, numberOfChannels, sampleSize, 1,
            0, isBigEndian, isSigned, totalAudioLength);
    }

// TODO: PROGRAMO
//    /**
//     * This method basically splits the array to channels and from each channel takes the n-th sample.
//     * Internally it is performed a bit different, but the result is the same.
//     *
//     * @param samples          is the input stream containing samples
//     * @param numberOfChannels represents number of channels
//     * @param sampleSize       is the size of 1 sample in a channel
//     * @param n                - Every n-th sample is taken from all channels separately
//     * @param startSample      - The first sample to be taken from each channel
//     * @return Returns 2D byte array, where each array represents the channels where only every n-th sample is taken
//     * @throws IOException is thrown when the error in input stream occurred
//     */
//    @Deprecated // Slow variant - Was creating too large objects on heap which were immediately deleted
//    public static byte[][] takeEveryNthSampleMoreChannels(InputStream samples, int numberOfChannels, int sampleSize, int n, int startSample) throws IOException {
//        byte[][] arr = new byte[numberOfChannels][];
//        int frameSize = sampleSize * numberOfChannels;
//        byte[] oneFrame = new byte[frameSize];
//
//        ArrayList<ArrayList<Byte>> listList = new ArrayList<>();
//        for (int i = 0; i < numberOfChannels; i++) {
//            listList.add(new ArrayList<>());
//        }
//
//        int bytesRead = 0;
//        int arrIndex;
//        int count = 0;
//        int bytesReadSum = 0;
//
//        bytesRead = readNotNeededSamples(samples, sampleSize * numberOfChannels, startSample);
//        while (bytesRead != -1) {
//            arrIndex = 0;
//            bytesReadSum = readNSamples(samples, oneFrame);
//            if (bytesReadSum < oneFrame.length) {
//                break;
//            }
//            if (count % n == 0) {
//                arrIndex = 0;
//                for (int i = 0; i < numberOfChannels; i++) {
//                    for (int j = 0; j < sampleSize; j++) {
//                        listList.get(i).add(oneFrame[arrIndex]);
//                        arrIndex++;
//                    }
//                }
//            }
//            count++;
//        }
//
//        for (int i = 0; i < numberOfChannels; i++) {
//            arr[i] = new byte[listList.get(i).size()];
//            for (int j = 0; j < arr[i].length; j++) {
//                arr[i][j] = listList.get(i).get(j);
//            }
//        }
//
//        return arr;
//    }






    /**
     * This method basically splits the array to channels and from each channel takes the n-th sample.
     * Internally it is performed a bit different, but the result is the same.
     * Doesn't work correctly if startSample isn't multiple of frameSize
     *
     * @param samples          is the input stream containing samples
     * @param numberOfChannels represents number of channels
     * @param sampleSize       is the size of 1 sample in a channel
     * @param n                - Every n-th sample is taken from all channels separately
     * @param startSample      - The first sample to be taken from each channel
     * @param totalAudioLength is the total length of the samples input in bytes.
     * @return Returns 2D byte array, where each array represents the channels where only every n-th sample is taken
     * @throws IOException is thrown when the error in input stream occurred
     */
    public static byte[][] takeEveryNthSampleMoreChannels(InputStream samples, int numberOfChannels, int sampleSize,
                                                          int n, int startSample, int totalAudioLength) throws IOException {
        int channelLen = getLengthOfOneChannelInSamplesForSampleSkipping(totalAudioLength, startSample, n,
            numberOfChannels, sampleSize);
        channelLen *= sampleSize;
        byte[][] arr = new byte[numberOfChannels][channelLen];
        int frameSize = sampleSize * numberOfChannels;
        byte[] oneFrame = new byte[frameSize];

        int bytesRead = 0;
        int arrIndex;
        int count = 0;
        int bytesReadSum = 0;
        int outputIndex = 0;

        bytesRead = readNotNeededSamples(samples, sampleSize, startSample);
        while (bytesRead != -1) {
            arrIndex = 0;
            bytesReadSum = readNSamples(samples, oneFrame);
            if (bytesReadSum < oneFrame.length) {
                break;
            }
            if (count % n == 0) {
                arrIndex = 0;
                for (int i = 0; i < numberOfChannels; i++) {
                    int channelOutputIndex = outputIndex;
                    for (int j = 0; j < sampleSize; j++, channelOutputIndex++, arrIndex++) {
                        arr[i][channelOutputIndex] = oneFrame[arrIndex];
                    }
                }

                outputIndex += sampleSize;
            }
            count++;
        }

        return arr;
    }

// TODO: PROGRAMO



    /**
     * This method basically splits the array to channels and from each channel takes the n-th sample.
     * Internally it is performed a bit different, but the result is the same.
     * Doesn't work correctly if startSample isn't multiple of frameSize
     *
     * @param samples          is the input stream containing samples
     * @param numberOfChannels represents number of channels
     * @param sampleSize       is the size of 1 sample in a channel
     * @param n                - Every n-th sample is taken from all channels separately
     * @param startSample      - The first sample to be taken from each channel
     * @param totalAudioLength is the total length of the samples input in bytes.
     * @return Returns 2D double array, where each array represents the channels where only every n-th sample is taken
     * @throws IOException is thrown when the error in input stream occurred
     */
    @Deprecated     // The buffer it uses is too small - only of frameSize
    public static double[][] takeEveryNthSampleMoreChannelsDoubleOldAndSlow(InputStream samples, int numberOfChannels,
                                                                            int sampleSize, int n, int startSample,
                                                                            boolean isBigEndian, boolean isSigned,
                                                                            int totalAudioLength) throws IOException {
        int channelLen = getLengthOfOneChannelInSamplesForSampleSkipping(totalAudioLength, startSample, n,
            numberOfChannels, sampleSize);
        double[][] outputArr = new double[numberOfChannels][channelLen];

        int frameSize = sampleSize * numberOfChannels;
        byte[] oneFrame = new byte[frameSize];

        int bytesRead = 0;
        int arrIndex;
        int count = 0;
        int bytesReadSum = 0;
        int outputIndex = 0;

        bytesRead = readNotNeededSamples(samples, sampleSize, startSample);
        while (bytesRead != -1) {
            arrIndex = 0;
            bytesReadSum = readNSamples(samples, oneFrame);
            if (bytesReadSum < oneFrame.length) {
                break;
            }
            if (count % n == 0) {
                arrIndex = 0;
                for (int i = 0; i < numberOfChannels; i++, arrIndex += sampleSize) {
                    Program.normalizeToDoubles(oneFrame, outputArr[i], sampleSize, sampleSize * 8,
                                               arrIndex, outputIndex, 1, isBigEndian, isSigned);
                }

                outputIndex++;
            }
            count++;
        }

        return outputArr;
    }

    /**
     * This method basically splits the array to channels and from each channel takes the n-th sample.
     * Internally it is performed a bit different, but the result is the same.
     * Doesn't work correctly if startSample isn't multiple of frameSize
     *
     * @param samples          is the input stream containing samples
     * @param numberOfChannels represents number of channels
     * @param sampleSize       is the size of 1 sample in a channel
     * @param n                - Every n-th sample is taken from all channels separately
     * @param startSample      - The first sample to be taken from each channel
     * @param totalAudioLength is the total length of the samples input in bytes.
     * @return Returns 2D double array, where each array represents the channels where only every n-th sample is taken
     * @throws IOException is thrown when the error in input stream occurred
     */
    public static double[][] takeEveryNthSampleMoreChannelsDouble(InputStream samples, int numberOfChannels,
                                                                  int sampleSize, int n, int startSample,
                                                                  boolean isBigEndian, boolean isSigned,
                                                                  int totalAudioLength) throws IOException {
        int channelLen = getLengthOfOneChannelInSamplesForSampleSkipping(totalAudioLength, startSample, n,
            numberOfChannels, sampleSize);
        double[][] outputArr = new double[numberOfChannels][channelLen];

        int frameSize = sampleSize * numberOfChannels;
        int FRAME_COUNT = 2048;
        byte[] buffer = new byte[frameSize * FRAME_COUNT];

        int mask = calculateMask(sampleSize);
        int maxAbsoluteValue = Program.getMaxAbsoluteValueSigned(8 * sampleSize);

        int bytesRead = 0;
        int bytesReadSum = 0;
        int outputIndex = 0;

        int nextNByteIndex = 0;
        int nextTotalIndex = 0;

        bytesRead = readNotNeededSamples(samples, sampleSize, startSample);
        while (bytesRead != -1) {
            bytesReadSum = readNSamples(samples, buffer);
            if (bytesReadSum == -1) {
                break;
            }

            nextTotalIndex += bytesReadSum;
            while(nextNByteIndex < nextTotalIndex && outputIndex < outputArr[0].length) {
                for (int i = 0, arrIndex = nextNByteIndex % buffer.length; i < numberOfChannels; i++, arrIndex += sampleSize) {
                    int sample = Program.convertBytesToInt(buffer, sampleSize, mask, arrIndex, isBigEndian, isSigned);
                    outputArr[i][outputIndex] = Program.normalizeToDoubleBetweenMinusOneAndOne(sample, maxAbsoluteValue, isSigned);
                }

                outputIndex++;
                nextNByteIndex += n * frameSize;
            }
        }

        return outputArr;
    }


    /**
     * Returns int which is the length of channel 0 (if the startSample wasn't multiple of frameSize then some next channels may have length of the 0th channel - 1)
     * @param totalAudioLength
     * @param startSample
     * @param skip
     * @param numberOfChannels
     * @param sampleSize
     * @return
     */
    private static int getLengthOfOneChannelInSamplesForSampleSkipping(int totalAudioLength, int startSample,
                                                                       int skip, int numberOfChannels, int sampleSize) {
        int channelLen;
        int totalByteSize = totalAudioLength - (startSample * sampleSize);
        int frameSize = numberOfChannels * sampleSize;
        int samplesPerChannel = totalByteSize / frameSize;
        // Again the thing I wrote to TODO - solving the problem that I want to count the channel if samplesPerChannel == 1 as 1
        // It is in todo under the tag: PROBLEM_KTEREJ_JSEM_UZ_RESIL_V_NEKOLIKA_PROGRAMECH
        channelLen = (skip - 1 + samplesPerChannel) / skip;


        return channelLen;
    }



    /**
     * Takes the byte array with samples and returns the samples of channels in byte labelReferenceArrs (1 array = 1 channel).
     *
     * @param samples    is the byte array containing samples
     * @param sampleSize is the size of 1 sample in a channel
     * @return Returns 2D byte array, where each byte array corresponds to 1 channel.
     * @throws IOException is thrown when error with input
     */
    public static byte[][] separateChannels(byte[] samples, int numberOfChannels, int sampleSize) throws IOException {
        return takeEveryNthSampleMoreChannels(samples, numberOfChannels, sampleSize, 1, 0);
    }


    /**
     * Takes nth sample from each channel.
     * This method basically splits the array to channels and from each channel takes the n-th sample.
     * Internally it is performed a bit different, but the result is the same.
     *
     * @param samples          is the byte array containing samples
     * @param numberOfChannels represents number of channels
     * @param sampleSize       is the size of 1 sample in a channel
     * @param n                - Every n-th sample is taken from all channels separately
     * @param startSample      - The first sample to be taken from each channel
     * @return Returns 2D byte array, where each array represents the channels where only every n-th sample is taken
     */
    public static byte[][] takeEveryNthSampleMoreChannels(byte[] samples, int numberOfChannels, int sampleSize, int n, int startSample) {
        byte[][] arr = new byte[numberOfChannels][];

        for (int i = 0; i < numberOfChannels; i++) {
            arr[i] = takeEveryNthSampleOneChannel(samples, sampleSize, n * numberOfChannels, i + startSample);
        }

        return arr;
    }


    // TODO: Nejak nebere k uvahu pocet kanalu ale tvari se ze jo protoze bere frameSize
    /**
     * Splits the input stream to parts of size numberOfFramesInOneSongPart * frameSize and calculates the aggregation agg for each part
     * The output is sorted by the int value (which depends on the agg argument), if the output is sorted depends on the value of variable returnSorted.
     * If the song is multi-channel (for example stereo), then the agg int is calculated as it was mono
     *
     * @param audioStream                 is the input stream with the audio samples.
     * @param numberOfFramesInOneSongPart is the total number of frames in 1 song part
     * @param frameSize                   is the size of 1 frame, which equals numberOfChannels * sampleSize, unless the method is used differently
     * @param isBigEndian                 is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned                    is boolean variable, which is true if the samples are signed, false if unsigned.
     * @param sampleSize                  is the size of 1 sample
     * @param returnSorted                if true, then the output is sorted by the int value, which is based on the agg, else the output is not sorted, that
     *                                    means if we connected all the song parts from the result in the order as they are in the array, then it would be
     *                                    same as the original input stream
     * @param agg                         represents the aggregation - what double will be added to each song part.
     * @return Returns the array of type SongPartWithAverageValueOfSamples which contains the song parts with the int based on the agg argument.
     * @throws IOException is thrown when error in reading the input stream occurred, or when the method is called with invalid agg.
     */
    public static SongPartWithAverageValueOfSamples[] takeSongPartsAndAddAggregation(InputStream audioStream,
                                                                                     int numberOfFramesInOneSongPart, int frameSize,
                                                                                     boolean isBigEndian, boolean isSigned, int sampleSize,
                                                                                     boolean returnSorted, Aggregations agg) throws IOException {
        ArrayList<SongPartWithAverageValueOfSamples> songParts = new ArrayList<>();
        int size = numberOfFramesInOneSongPart * frameSize;            // size of the song part
        byte[] songPart = new byte[size];
        int bytesRead = 0;

        int bytesReadSum = 0;
        while (bytesRead != -1) {
            bytesReadSum = readNSamples(audioStream, songPart);
            if (bytesReadSum < sampleSize) {
                break;
            }
            double specialValue;
            specialValue = performAggregation(songPart, sampleSize, isBigEndian, isSigned, agg);
            if (bytesReadSum != songPart.length) {// TODO: !!!!!!!!!!!!!!!!!!
                // TODO: Here I take the last window i nthe other cases I don't so I guess that I should just drop it
   /*
                byte[] arr = new byte[bytesReadSum];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = songPart[i];
                }
                songParts.add(new SongPartWithAverageValueOfSamples((int) specialValue, arr, false));
                bytesRead = -1;

    */
            } else {
                songParts.add(new SongPartWithAverageValueOfSamples((int) specialValue, songPart, true));
            }
        }

        SongPartWithAverageValueOfSamples[] arr = new SongPartWithAverageValueOfSamples[songParts.size()];
        arr = songParts.toArray(arr);
        if (returnSorted) {
            Arrays.sort(arr);
        }
        return arr;
    }


    /**
     * Splits the input stream to parts of size numberOfFramesInOneSongPart * frameSize and calculates the aggregation mod for each part.
     * The output is sorted by the int value (which depends on the mod argument).
     * If the output is sorted depends on the value of variable returnSorted.
     * The mod value is calculated of the original (non-normalized) values.
     * If the song is multi-channel (for example stereo), then the average value is calculated as it was mono
     *
     * @param audioStream                 is the input stream with the audio samples.
     * @param numberOfFramesInOneSongPart is the total number of frames in 1 song part
     * @param frameSize                   is the size of 1 frame, which equals numberOfChannels * sampleSize, unless the method is used differently
     * @param isBigEndian                 is boolean variable, which is true if the samples are big endian and false if little endian
     * @param sampleSize                  is the size of 1 sample
     * @param isSigned                    is boolean variable, which is true if the samples are signed and false if unsigned
     * @param returnSorted                if true, then the output is sorted by the int value, which depends on the mod, else the output is not sorted, that
     *                                    means if we connected all the song parts from the result in the order as they are in the array, then it would be
     *                                    same as the original input stream
     * @param mod                         represents the aggregation - what int will be added to each song part.
     * @return Returns the array of type NormalizedSongPartWithAverageValueOfSamples which contains the song parts in form of normalized samples, which are stored in 1D double array.
     * Together with the int based on the mod argument.
     * @throws IOException is thrown when error in reading the input stream occurred, or if the value in argument mod is invalid
     */
    @Deprecated
    public static NormalizedSongPartWithAverageValueOfSamples[] takeNormalizedSongPartsAndAddMod(InputStream audioStream,
                                                                                                 int numberOfFramesInOneSongPart,
                                                                                                 int frameSize,
                                                                                                 boolean isBigEndian,
                                                                                                 int sampleSize,
                                                                                                 boolean isSigned,
                                                                                                 boolean returnSorted,
                                                                                                 Aggregations mod) throws IOException {
        ArrayList<NormalizedSongPartWithAverageValueOfSamples> songParts = new ArrayList<>();
        int size = numberOfFramesInOneSongPart * frameSize;            // size of the song part
        byte[] songPart = new byte[size];
        double[] normalizedSongPart = new double[size / sampleSize];
        int bytesRead = 0;

        int bytesReadSum = 0;
        while (bytesRead != -1) {
            bytesReadSum = readNSamples(audioStream, songPart);
            if (bytesRead < sampleSize) {
                break;
            }

            int[] intArr;
            intArr = convertBytesToSamples(songPart, sampleSize, isBigEndian, isSigned);

            double songPartValue = 0;
            switch (mod) {            // TODO: If the compiler doesn't optimize the cases outside the loop, then it is really inefficient
                case RMS:
                    for (int i = 0; i < intArr.length; i++) {
                        songPartValue = songPartValue + (double) (intArr[i] * intArr[i]) / intArr.length;
                    }
                    songPartValue = Math.sqrt(songPartValue);
                    break;
                case AVG:
                    for (int i = 0; i < intArr.length; i++) {
                        songPartValue = songPartValue + (double) intArr[i] / intArr.length;
                    }
                    break;
                case MIN:
                    int min = Integer.MAX_VALUE;
                    for (int i = 0; i < intArr.length; i++) {
                        if (intArr[i] < min) {
                            min = intArr[i];
                        }
                    }
                    songPartValue = min;
                    break;
                case MAX:
                    int max = Integer.MIN_VALUE;
                    for (int i = 0; i < intArr.length; i++) {
                        if (intArr[i] > max) {
                            max = intArr[i];
                        }
                    }
                    songPartValue = max;
                    break;
                default:
                    throw new IOException();
            }
            normalizedSongPart = normalizeToDoubles(intArr, sampleSize * 8, isSigned);

            if (bytesReadSum != songPart.length) {
                double[] arr = new double[bytesReadSum / sampleSize];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = normalizedSongPart[i];
                }
                songParts.add(new NormalizedSongPartWithAverageValueOfSamples((int) songPartValue, arr, false));
                bytesRead = -1;
            } else {
                songParts.add(new NormalizedSongPartWithAverageValueOfSamples((int) songPartValue, normalizedSongPart, true));
            }
        }

        NormalizedSongPartWithAverageValueOfSamples[] arr = new NormalizedSongPartWithAverageValueOfSamples[songParts.size()];
        arr = songParts.toArray(arr);
        if (returnSorted) {
            Arrays.sort(arr);
        }
        return arr;
    }


    public static double performAggregation(double val1, double val2, Aggregations agg) {
        switch(agg) {
            case ABS_MAX:
                return Math.max(Math.abs(val1), Math.abs(val2));
            case ABS_MIN:
                return Math.min(Math.abs(val1), Math.abs(val2));
            case MAX:
                return Math.max(val1, val2);
            case MIN:
                return Math.min(val1, val2);
            case RMS:
                return Math.sqrt((val1 * val1 + val2 * val2) / 2);
            case AVG:
                return (val1 + val2) / 2;
            case SUM:
                return val1 + val2;
            default:
                return 0;
        }
    }


    public static double performAggregation(double[] arr, Aggregations agg) {
        return performAggregation(arr, 0, arr.length, agg);
    }


    /**
     * Compresses the audio:
     * (1) if agg = MIN then by taking the sample with the lowest amplitude of n given samples.
     * (2) if agg = MAX then by taking the sample with the highest amplitude of n given samples
     * (3) if agg = AVG then by taking the average value of samples
     * (4) if agg = RMS then by taking the RMS of samples
     * (5) if agg = SUM then by taking the sum of samples
     * Expects the samples to be from one channel (so if working with stereo, either convert stereo to mono
     * or call this method for each channel's samples respectively).
     *
     * @param samples     is double array with samples from one channel
     * @param startIndex
     * @param len
     * @param agg         represents the aggregation which will be performed on the len samples
     * @return Returns double which is result of the performed operation on len samples.
     */
    public static double performAggregation(double[] samples, int startIndex, int len, Aggregations agg) {
        double specialValue = agg.defaultValueForMod();

        int endIndex = startIndex + len;
        for (int i = startIndex; i < endIndex; i++) {
            switch(agg) {               // TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient
                case ABS_MAX:
                    {
                        double abs = Math.abs(samples[i]);
                        if (specialValue < abs) {
                            specialValue = abs;
                        }
                        break;
                    }
                case ABS_MIN:
                    {
                        double abs = Math.abs(samples[i]);
                        if (specialValue > abs) {
                            specialValue = abs;
                        }
                        break;
                    }

                case MAX:
                    if (specialValue < samples[i]) {
                        specialValue = samples[i];
                    }
                    break;
                case MIN:
                    if (specialValue > samples[i]) {
                        specialValue = samples[i];
                    }
                    break;
                case RMS:
                    specialValue += (samples[i] * samples[i]);
                    break;
                case AVG:
                case SUM:
                    specialValue += samples[i];
                    break;
            }
        }


        switch(agg) {
            case RMS:
                specialValue /= len;
                specialValue = Math.sqrt(specialValue);
                break;
            case AVG:
                specialValue /= len;
                break;
        }

        return specialValue;
    }

    // TODO: Note - can be implemented using class from WavePanel with the extremes but,
    //  it is way too general and slower, and it is simple enough to implement it again here
    /**
     * Finds the min and max in the samples array at range [startIndex, endIndex]
     * Expects the samples to be from one channel (so if working with stereo, either convert stereo to mono
     * or call this method for each channel's samples respectively).
     * If the startIndex is out of bounds crashes. If len == 0 puts the samples[startIndex] into output[0] and output[1].
     * @param samples     is double array with samples from one channel
     * @param startIndex
     * @param endIndex
     * @param output puts min at index 0 and max at index 1
     */
    public static void convertNSamplesToMinAndMax(double[] samples, int startIndex, int endIndex, double[] output) {
        double min = samples[startIndex];
        double max = samples[startIndex];

        for (int i = startIndex + 1; i < endIndex; i++) {
            if (max < samples[i]) {
                max = samples[i];
            }
            else if (min > samples[i]) {
                min = samples[i];
            }
        }

        output[0] = min;
        output[1] = max;
    }




    // TODO: zbytecne opakujici se kod staci 1 - jediny co je jiny je big a little nedian pro konverzi
    // TODO:     convertBytesToInt(samples, sampleSize, mask, index, isBigEndian,isSigned);
    // TODO: Akorat neivm jestli to pak nebude pomalejsi
    /**
     * Compresses the audio:
     * (1) if agg = MIN then by taking the sample with the lowest amplitude of n given samples.
     * (2) if agg = MAX then by taking the sample with the highest amplitude of n given samples
     * (3) if agg = AVG then by taking the average value of samples
     * (4) if agg = RMS then by taking the RMS of samples
     * (5) if agg = SUM then by taking the sum of samples
     * Expects the samples to be from one channel (so if working with stereo, either convert stereo to mono
     * or call this method for each channel's samples respectively).
     *
     * @param samples     is byte array with samples from one channel
     * @param sampleSize  is the size of one sample
     * @param isBigEndian is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned    is boolean variable, which is true if samples are signed, false if unsigned
     * @param agg         represents the aggregation which will be performed on the n samples
     * @return Returns double which is result of the performed operation on n samples.
     * @throws IOException is thrown when the method calculateMask fails - invalid sample size
     */
    public static double performAggregation(byte[] samples, int sampleSize, boolean isBigEndian,
                                            boolean isSigned, Aggregations agg) throws IOException {
        int n = samples.length / sampleSize;

        int mask = calculateMask(sampleSize);
        double specialValue = agg.defaultValueForMod();

        int sample;
        int index = 0;

        // TODO: Copy-pasted - probably to make it easier for compiler, but it should probably recognize it, the code is just too old
        if (isBigEndian) {
            for (int j = 0; j < n; j++) {
                sample = convertBytesToIntBigEndian(samples, sampleSize, mask, index, isSigned);
                switch(agg) {           // TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient
                    case MAX:
                        if (specialValue < sample) {
                            specialValue = sample;
                        }
                        break;
                    case MIN:
                        if (specialValue > sample) {
                            specialValue = sample;
                        }
                        break;
                    case RMS:
                        specialValue += sample * (double)sample;
                        break;
                    case SUM:
                    case AVG:
                        specialValue += sample;
                        break;
                }
                index = index + sampleSize;
            }
        } else {
            for (int j = 0; j < n; j++) {
                sample = convertBytesToIntLittleEndian(samples, sampleSize, mask, index, isSigned);
                switch(agg) {           // TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient
                    case MAX:
                        if (specialValue < sample) {
                            specialValue = sample;
                        }
                        break;
                    case MIN:
                        if (specialValue > sample) {
                            specialValue = sample;
                        }
                        break;
                    case RMS:
                        specialValue += sample * (double)sample;
                        break;
                    case SUM:
                    case AVG:
                        specialValue += sample;
                        break;
                }
                index = index + sampleSize;
            }
        }

        double maxAbsVal = (double)getMaxAbsoluteValue(sampleSize * 8, isSigned);
        specialValue /= maxAbsVal;
        if (agg == Aggregations.RMS) {
            specialValue /= maxAbsVal;
            specialValue = Math.sqrt(specialValue / n);
        }
        else if(agg == Aggregations.AVG) {
            specialValue /= n;
        }

        return specialValue;
    }


    /**
     * Compresses the audio:
     * (1) if agg = MIN then by taking the sample with the lowest amplitude of n given samples.
     * (2) if agg = MAX then by taking the sample with the highest amplitude of n given samples
     * (3) if agg = AVG then by taking the average value of samples
     * (4) if agg = RMS then by taking the RMS of samples
     * (5) if agg = SUM then by taking the sum of samples
     * @param stream           is the input stream containing samples.
     * @param numberOfChannels represents number of channels.
     * @param sampleSize       is the size of one sample in bytes.
     * @param isBigEndian      is true if the samples are big endian, false otherwise.
     * @param isSigned         true if the samples are signed numbers, false otherwise.
     * @param byteLength       is the total length of the input stream. (The value is the same as onlyAudioSizeInBytes property in the class)
     * @return Returns double value which represents the result of the aggregation performed on samples given in input stream.
     * @throws IOException is thrown where error with input stream occurred, or the argument sampleSize is invalid.
     */
    public static double performAggregation(InputStream stream, int numberOfChannels, int sampleSize,
                                            boolean isBigEndian, boolean isSigned, int byteLength,
                                            Aggregations agg) throws IOException {
        int n = byteLength / sampleSize;
        double specialValue = agg.defaultValueForMod();
        int bytesRead = 0;
        int sample;

        int mask = calculateMask(sampleSize);

        byte[] arr = new byte[sampleSize * numberOfChannels * 16];

        if (isBigEndian) {                // TODO: Again 2 same codes ... maybe can be done better ... currently for optimalization
            while (bytesRead != -1) {
                bytesRead = readNSamples(stream, arr);
                int arrIndex = 0;
                while (arrIndex < bytesRead) {
                    sample = convertBytesToIntBigEndian(arr, sampleSize, mask, arrIndex, isSigned);
                    // TODO: Copy pasted
                    switch(agg) {           // TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient
                        case MAX:
                            if (specialValue < sample) {
                                specialValue = sample;
                            }
                            break;
                        case MIN:
                            if (specialValue > sample) {
                                specialValue = sample;
                            }
                            break;
                        case RMS:
                            specialValue += sample * (double)sample;
                            break;
                        case AVG:
                        case SUM:
                            specialValue += (double) sample;
                            break;
                    }
                    arrIndex = arrIndex + sampleSize;
                }
            }
        } else {
            while (bytesRead != -1) {
                bytesRead = readNSamples(stream, arr);
                int arrIndex = 0;
                while (arrIndex < bytesRead) {
                    sample = convertBytesToIntLittleEndian(arr, sampleSize, mask, arrIndex, isSigned);
                    switch(agg) {           // TODO: If the compiler doesn't optimize the if outside the loop, then it is really inefficient
                        case MAX:
                            if (specialValue < sample) {
                                specialValue = sample;
                            }
                            break;
                        case MIN:
                            if (specialValue > sample) {
                                specialValue = sample;
                            }
                            break;
                        case RMS:
                            specialValue += sample * (double)sample;
                            break;
                        case AVG:
                        case SUM:
                            specialValue += (double) sample;
                            break;
                    }
                    arrIndex = arrIndex + sampleSize;
                }
            }
        }


        double maxAbsVal = (double)getMaxAbsoluteValue(sampleSize * 8, isSigned);
        specialValue /= maxAbsVal;
        if (agg == Aggregations.RMS) {
            specialValue /= maxAbsVal;
            specialValue = Math.sqrt(specialValue / n);
        }
        else if(agg == Aggregations.AVG) {
            specialValue /= n;
        }

        return specialValue;
    }




    /**
     * Mask for top 8 bits in int
     */
    private static final int TOP_8_BITS_MASK = 0xFF_00_00_00;

    /**
     * Creates mask used for converting the byte array to number of size sampleSize bytes, which must fit to int.
     * The mask has the top sampleSize * 8 bits set to 1, the rest is set to 0
     *
     * @param sampleSize is the size of 1 sample in bytes
     * @return returns the mask which is used for converting the byte array to int
     * @throws IOException is thrown when the sample size > 4, because then the samples can't fit to int, or when it is <= 0
     */
    public static int calculateMask(int sampleSize) throws IOException {
        // TODO: Tyhle kontroly asi můžu dát pryč
        if (sampleSize <= 0) {
            throw new IOException("Sample size is <= 0 bytes");
        }
        else  if (sampleSize > 4) {
            throw new IOException("SampleSize is > 4 bytes");
        }

        if (sampleSize == 4) {
            return 0x00000000;
        }
        int mask = TOP_8_BITS_MASK;
        for (int k = 0; k < Integer.BYTES - sampleSize - 1; k++) {
            mask = mask >> 8;
            mask = mask | TOP_8_BITS_MASK;
        }

        return mask;
    }


    /**
     * Creates mask used for converting the byte array to to number of size sampleSize bytes, which must fit to int.
     * The mask has the top sampleSize * 8 bits set to 0, the rest is set to 1. So the result is binary negation of the
     * result of method calculateMask, if it was called with the same parameter.
     *
     * @param sampleSize is the size of 1 sample in bytes
     * @return returns the mask which is used for converting the byte array to int
     * @throws IOException is thrown when the sample size > 4, because then the samples can't fit to int, or when it is <= 0
     */
    public static int calculateInverseMask(int sampleSize) throws IOException {
        if (sampleSize > 4 || sampleSize <= 0) {
            throw new IOException();
        }
        if (sampleSize == 4) {
            return 0xFFFFFFFF;
        }
        int inverseMaskTop8Bits = ~TOP_8_BITS_MASK;
        int inverseMask = inverseMaskTop8Bits;
        for (int k = 0; k < Integer.BYTES - sampleSize - 1; k++) {
            inverseMask = inverseMask >> 8;
            inverseMask = inverseMask & inverseMaskTop8Bits;
        }

        return inverseMask;
    }


    /**
     * Binary negates the argument mask.
     *
     * @return Returns binary negation of argument mask.
     */
    public static int calculateInverseMaskFromMask(int mask) {
        return (~mask);
    }


    public void convertToMono() throws IOException {
        this.song = convertToMono(this.song, this.frameSize, this.numberOfChannels, this.sampleSizeInBytes,
            this.isBigEndian, this.isSigned);
        this.numberOfChannels = 1;
        this.frameSize = sampleSizeInBytes;
        this.decodedAudioFormat = new AudioFormat(decodedAudioFormat.getEncoding(),
                                                  decodedAudioFormat.getSampleRate(),
                                                  decodedAudioFormat.getSampleSizeInBits(), 1,
                                                  this.frameSize, decodedAudioFormat.getFrameRate(),
                                                  decodedAudioFormat.isBigEndian());
        setSizeOfOneSec();
    }

    /**
     * Converts the audio from samples to mono signal by averaging the samples in 1 frame.
     *
     * @param samples          is the input array with samples
     * @param frameSize        is the size of 1 frame
     * @param numberOfChannels represents the number of channels
     * @param sampleSize       is the size of one sample
     * @param isBigEndian      true if the samples are in big endian, false otherwise.
     * @param isSigned         true if the samples are signed numbers, false otherwise.
     * @param monoSong         is the arraz in which will be stored the resulting mono song.
     * @throws IOException is thrown when method calculateMask failed - fails if the sampleSize is invalid.
     */
    public static void convertToMono(byte[] samples, int frameSize, int numberOfChannels, int sampleSize,
                                     boolean isBigEndian, boolean isSigned, byte[] monoSong) throws IOException {
        int sample = 0;
        int monoSample = 0;

        int mask = calculateMask(sampleSize);

        byte[] monoSampleInBytes = new byte[sampleSize];

        for (int index = 0, monoSongIndex = 0; index < samples.length;) {
            // We take the bytes from end, but it doesn't matter, since we take just the average value
            monoSample = 0;
            for (int i = 0; i < numberOfChannels; i++) {
// TODO: Tenhle for tu podle me nema byt                       for(int j = 0 ; j < sampleSize; j++) {
                sample = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
// TODO:                        }
                monoSample = monoSample + sample;
                index += sampleSize;
            }

            monoSample = monoSample / numberOfChannels;
            convertIntToByteArr(monoSampleInBytes, monoSample, isBigEndian);
            for (int i = 0; i < monoSampleInBytes.length; i++, monoSongIndex++) {
                monoSong[monoSongIndex] = monoSampleInBytes[i];
            }
        }

    }


    /**
     * Converts the audio from samples to mono signal by averaging the samples in 1 frame.
     *
     * @param samples          is the input array with samples
     * @param frameSize        is the size of 1 frame
     * @param numberOfChannels represents the number of channels
     * @param sampleSize       is the size of one sample
     * @param isBigEndian      true if the samples are in big endian, false otherwise.
     * @param isSigned         true if the samples are signed numbers, false otherwise.
     * @return Returns 1D byte array which is represents the mono audio gotten from the input array by averaging
     * the samples in frame
     * @throws IOException is thrown when method calculateMask failed - fails if the sampleSize is invalid.
     */
    public static byte[] convertToMono(byte[] samples, int frameSize, int numberOfChannels,
                                       int sampleSize, boolean isBigEndian, boolean isSigned) throws IOException {
        byte[] monoSong = new byte[samples.length / numberOfChannels];
        convertToMono(samples, frameSize, numberOfChannels, sampleSize, isBigEndian, isSigned, monoSong);

// TODO: 60 BPM stereo - not both channels are the same
/*
// TODO:
// TODO: Tohle je dobrej test, kdyz jsou oba kanaly stejny
        for(int i = 0, monoIndex = 0; i < samples.length; i += (numberOfChannels - 1) * sampleSize) {
            for(int j = 0; j < sampleSize; j++, i++, monoIndex++) {
                if(monoSong[monoIndex] != samples[i]) {
                    System.out.println(monoSong[monoIndex] + "\t" + samples[i]);
                    System.exit(1);
                }
            }
        }
// TODO:
*/
        return monoSong;
    }

// TODO: Nahrazeno volanim pres referenci
//    /**
//     * Converts the audio from samples to mono signal by averaging the samples in 1 frame.
//     *
//     * @param samples          is the input array with samples
//     * @param frameSize        is the size of 1 frame
//     * @param numberOfChannels represents the number of channels
//     * @param sampleSize       is the size of one sample
//     * @param isBigEndian      true if the samples are in big endian, false otherwise.
//     * @param isSigned         true if the samples are signed numbers, false otherwise.
//     * @return Returns 1D byte array which is represents the mono audio gotten from the input array by averaging
//     * the samples in frame
//     * @throws IOException is thrown when method calculateMask failed - fails if the sampleSize is invalid.
//     */
//    public static byte[] convertToMono(byte[] samples, int frameSize, int numberOfChannels,
//                                                   int sampleSize, boolean isBigEndian, boolean isSigned) throws IOException {
//        int sample = 0;
//        int monoSample = 0;
//
//        int mask = calculateMask(sampleSize);
//
//        byte[] monoSong = new byte[samples.length / numberOfChannels];
//        byte[] monoSampleInBytes = new byte[sampleSize];
//
//        for (int index = 0, monoSongIndex = 0; index < samples.length;) {
//            // We take the bytes from end, but it doesn't matter, since we take just the average value
//            monoSample = 0;
//            for (int i = 0; i < numberOfChannels; i++) {
//// TODO: Tenhle for tu podle me nema byt                       for(int j = 0 ; j < sampleSize; j++) {
//                sample = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
//// TODO:                        }
//                monoSample = monoSample + sample;
//                index = index + sampleSize;
//            }
//
//            monoSample = monoSample / numberOfChannels;
//            convertIntToByteArr(monoSampleInBytes, monoSample, isBigEndian);
//            for (int i = 0; i < monoSampleInBytes.length; i++, monoSongIndex++) {
//                monoSong[monoSongIndex] = monoSampleInBytes[i];
//            }
//        }
//
//
//        return monoSong;
//    }

    // TODO: Tohle je nova verze konverze do mona
    /**
     * Converts the audio from audioStream to mono signal by averaging the samples in 1 frame.
     * @param audioStream is the InputStream with samples
     * @param frameSize is the size of 1 frame
     * @param numberOfChannels represents the number of channels
     * @param sampleSize is the size of one sample
     * @param isBigEndian true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @return Returns 1D byte array which is represents the mono audio gotten from the input stream by averaging
     * the samples in frame
     * @throws IOException is thrown when method calculateMask failed - fails if the sampleSize is invalid.
     */
    public static byte[] convertToMono(InputStream audioStream, int frameSize, int numberOfChannels,
                                       int sampleSize, boolean isBigEndian, boolean isSigned) throws IOException {

        int sample = 0;
        int monoSample = 0;

        int mask = calculateMask(sampleSize);

        ArrayList<Byte> monoSong = new ArrayList<>();
        int bytesRead = 0;
        byte[] frame = new byte[frameSize];
        byte[] monoSampleInBytes = new byte[sampleSize];

        while (bytesRead != -1) {
            try {
                bytesRead = readNSamples(audioStream, frame);
                int index = 0;
                // We take the bytes from end, but it doesn't matter, since we take just the average value
                monoSample = 0;
                for(int i = 0; i < numberOfChannels; i++) {
// TODO: Tenhle for tu podle me nema byt                       for(int j = 0 ; j < sampleSize; j++) {
                    sample = convertBytesToInt(frame, sampleSize, mask, index, isBigEndian, isSigned);
// TODO:                        }
                    monoSample = monoSample + sample;
                    index = index + sampleSize;
                }

                monoSample = monoSample / numberOfChannels;
                convertIntToByteArr(monoSampleInBytes, monoSample, isBigEndian);
                for(int i = 0; i < monoSampleInBytes.length; i++) {
                    monoSong.add(monoSampleInBytes[i]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] arr = new byte[monoSong.size()];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = monoSong.get(i);
        }

        return arr;
    }
//    /**
//     * Converts the audio from audioStream to mono signal by averaging the samples in 1 frame.
//     * @param audioStream is the InputStream with samples
//     * @param frameSize is the size of 1 frame
//     * @param frameRate is the frame rate, which is the same as sample rate
//     * @param numberOfChannels represents the number of channels
//     * @param sampleSize is the size of one sample
//     * @return Returns 1D byte array which is represents the mono audio gotten from the input stream by averaging
//     * the samples in frame
//     * @throws IOException is thrown when method calculateMask failed - fails if the sampleSize is invalid.
//     */
//    public static byte[] convertToMono(InputStream audioStream,
//                                                   int frameSize, int frameRate, int numberOfChannels, int sampleSize, boolean isBigEndian) throws IOException {
//
//        int sample = 0;
//        int monoSample = 0;
//
//        int mask = calculateMask(sampleSize);
//        int inverseMask = calculateInverseMaskFromMask(mask);
//
//        ArrayList<Byte> monoSong = new ArrayList<>();
//        int bytesRead = 0;
//        byte[] frame = new byte[frameSize];
//        byte[] monoSampleInBytes = new byte[sampleSize];
//        if(isBigEndian) {				// TODO: Here i have 2 same codes, maybe it can be done better, but right now it is for optimalization
//            while (bytesRead != -1) {
//                try {
//                    bytesRead = readNSamples(audioStream, frame);
//                    int index = 0;
//                    // We take the bytes from end, but it doesn't matter, since we take just the average value
//                    for(int i = 0; i < numberOfChannels; i++) {
//                        sample = 0;
//                        monoSample = 0;
//                        for(int j = 0 ; j < sampleSize; j++) {
//                            sample = convertBytesToIntBigEndian(frame, sampleSize, mask, inverseMask, index);
//                        }
//                        monoSample = monoSample + sample;
//                        index = index + sampleSize;
//                    }
//                    monoSample = monoSample / numberOfChannels;
//                    for(int j = 0; j < sampleSize; j++) {
//                        monoSampleInBytes[j] = (byte) (monoSample >> (j * 8));
//                    }
//
//
//                    for(int i = 0; i < monoSampleInBytes.length; i++) {
//                		monoSong.add(monoSampleInBytes[i]);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            while (bytesRead != -1) {
//                try {
//                    bytesRead = readNSamples(audioStream, frame);
//                    int index = 0;
//                    // We take the bytes from end, but it doesn't matter, since we take just the average value
//                    for(int i = 0; i < numberOfChannels; i++) {
//                        sample = 0;
//                        monoSample = 0;
//                        for(int j = 0 ; j < sampleSize; j++) {
//                            sample = convertBytesToIntLittleEndian(frame, sampleSize, mask, inverseMask, index);
//                        }
//                        monoSample = monoSample + sample;
//                        index = index + sampleSize;
//                    }
//                    monoSample = monoSample / numberOfChannels;
//                    for(int j = 0; j < sampleSize; j++) {
//                        monoSampleInBytes[j] = (byte) (monoSample >> (j * 8));
//                    }
//
//                    for(int i = 0; i < monoSampleInBytes.length; i++) {
//                        monoSong.add(monoSampleInBytes[i]);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        byte[] arr = new byte[monoSong.size()];
//        for(int i = 0; i < arr.length; i++) {
//            arr[i] = monoSong.get(i);
//        }
//
//        return arr;
//    }


    /**
     * Converts the sizeInBytes least significant bytes of int given in parameter numberToConvert to byte array of size sizeInBytes.
     * @param sizeInBytes is the size of the number in bytes.
     * @param numberToConvert is the number to be converted.
     * @param convertToBigEndian is boolean variable, if true, then the first byte in array contains the most significant
     * byte of the number, if false, then it contains the least significant
     * @return Returns byte array of size sizeInBytes, which contains the converted number.
     */
    public static byte[] convertIntToByteArr(int sizeInBytes, int numberToConvert, boolean convertToBigEndian) {
        byte[] converted = new byte[sizeInBytes];

        if(convertToBigEndian) {
            for (int i = sizeInBytes - 1; i >= 0; i--) {
                converted[i] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        } else {
            for (int i = 0; i < sizeInBytes; i++) {
                converted[i] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        }

        return converted;
    }

    // TODO: Can be solved by calling the general convertIntToByteArr method, this should be a bit faster, but it doesn't matter
    /**
     * Fills given array with int given in parameter numberToConvert.
     * @param arr is the array to be filled with bytes of numberToConvert in given endianity.
     * @param numberToConvert is the number to be converted.
     * @param convertToBigEndian is boolean variable, if true, then the first byte in array contains the most significant
     * byte of the number, if false, then it contains the least significant
     */
    public static void convertIntToByteArr(byte[] arr, int numberToConvert, boolean convertToBigEndian) {
        if(convertToBigEndian) {
            for (int i = arr.length - 1; i >= 0; i--) {
                arr[i] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        } else {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        }
    }


    /**
     * Converts given number to bytes and put those bytes in the byte array starting at startIndex.
     * @param arr is the byte array.
     * @param numberToConvert is the array to be converted.
     * @param sampleSize is the number of bytes to be converted.
     * @param startIndex is the starting index, where should be put the first byte.
     * @param convertToBigEndian tells if we should convert to big endian or not.
     */
    public static void convertIntToByteArr(byte[] arr, int numberToConvert, int sampleSize,
                                           int startIndex, boolean convertToBigEndian) {   // TODO: Nova metoda
        int endIndex = startIndex + sampleSize;                                         // TODO: Predchozi metodu lze prepsat touto
        if(convertToBigEndian) {
            endIndex--;
            for (; endIndex >= startIndex; endIndex--) {
                arr[endIndex] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        } else {
            for (; startIndex < endIndex; startIndex++) {
                arr[startIndex] = (byte) numberToConvert;
                numberToConvert = numberToConvert >> 8;
            }
        }
    }


    public static void main(String[] args) throws Exception {
        ProgramTest test = new ProgramTest();
        test.testAll();
    }



    /**
     * Reverses the samples. First sample is last, last is first etc.
     * @param arr samples to be reversed.
     * @param sampleSize is the size of 1 sample.
     */
    public static void reverseArr(byte[] arr, int sampleSize) {
        int sampleCount = arr.length / sampleSize;
        int index = 0;
        int index2 = 0;
        for(int i = 0; i < sampleCount / 2; i++)
        {
            index2 = index2 + sampleSize;
            for(int j = 0; j < sampleSize; j++) {
                byte temp = arr[index];
                arr[index] = arr[arr.length - index2 + j];
                arr[arr.length - index2 + j] = temp;
                index++;
            }
        }
    }


    /**
     * Reverses the samples. First sample is last, last is first etc. Isn't tested
     * @param arr samples to be reversed.
     * @param numberOfChannels is the number of channels.
     */
    public static void reverseArr(double[] arr, int numberOfChannels) {
        for(int index = 0, index2 = arr.length - 1; index < arr.length / 2; index2 -= numberOfChannels) {
            int upperBound = index + numberOfChannels;
            for(; index < upperBound; index++, index2++) {
                double tmp = arr[index];
                arr[index] = arr[index2];
                arr[index2] = tmp;
            }
        }
    }



    /**
     * Plays the audio given in the 1D array song in audio audioFormat given as parameter.
     * @param song is the audio with the samples to be played.
     * @param audioFormat is the audio audioFormat.
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when error with playing the song occurred.
     */
    public void playSong(byte[] song, AudioFormat audioFormat, boolean playBackwards) throws LineUnavailableException {
        int bytesWritten;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(audioFormat);
        line.start();
        if(playBackwards) {
            reverseArr(song, audioFormat.getSampleSizeInBits() / 8);
        }
        // because number of frames needs to be integer, so if some last bytes doesn't fit in the last frame,
        // we don't play them
        int bytesToWrite = song.length - (song.length % frameSize);
        bytesWritten = line.write(song, 0, bytesToWrite);
        line.drain();
    }


    /**
     * Plays the audio given in the input stream in audio audioFormat given as parameter.
     * @param song is the input stream with the samples to be played.
     * @param audioFormat is the audio audioFormat.
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when error with playing the song occurred.
     */
    public void playSong(InputStream song, AudioFormat audioFormat, boolean playBackwards) throws LineUnavailableException, IOException {
        if(playBackwards) {
            byte[] songArr = convertStreamToByteArray(song);
            playSong(songArr, audioFormat, playBackwards);
        } else {
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
            line.start();
            int bytesRead = 0;
            byte[] buffer = new byte[frameSize * 256];
            while(bytesRead != -1) {
                bytesRead = song.read(buffer, 0, buffer.length);
                line.write(buffer, 0, bytesRead);
            }
            line.drain();
        }
    }


    /**
     * Plays the audio given in the 1D array song, other parameters of this method describe the audioFormat in which will be the audio played.
     * @param song is 1D byte array which contains the samples, which will be played.
     * @param encoding is the encoding of the audio data.
     * @param sampleRate is the sample rate of the audio data.
     * @param sampleSizeInBits is the size of 1 sample in bits.
     * @param numberOfChannels represents the number of channels.
     * @param frameSize is the size of one frame.
     * @param frameRate is the frame rate of the audio.
     * @param isBigEndian is true if the samples are in big endian, false if in little endian
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when there is problem with feeding the data to the SourceDataLine.
     */
    public void playSong(byte[] song, Encoding encoding, int sampleRate, int sampleSizeInBits, int numberOfChannels, int frameSize, float frameRate, boolean isBigEndian, boolean playBackwards) throws LineUnavailableException {
        AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, numberOfChannels, frameSize, frameRate, isBigEndian);
        playSong(song, audioFormat, playBackwards);
    }


    /**
     * Plays the song given in the input stream song. Other parameters of this method describe the audioFormat in which will be the audio played.
     * Playing the audio backwards may be too slow, the input stream has to be transformed to byte array first.
     * @param song is the input stream containing samples, which will be played.
     * @param encoding is the encoding of the audio data.
     * @param sampleRate is the sample rate of the audio data.
     * @param sampleSizeInBits is the size of 1 sample in bits.
     * @param numberOfChannels represents the number of channels.
     * @param frameSize is the size of one frame.
     * @param frameRate is the frame rate of the audio.
     * @param isBigEndian is true if the samples are in big endian, false if in little endian
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * @throws LineUnavailableException is thrown when there is problem with feeding the data to the SourceDataLine.
     * @throws IOException is thrown when error with the input stream occurred.
     */
    public void playSong(InputStream song, Encoding encoding, int sampleRate, int sampleSizeInBits, int numberOfChannels, int frameSize, float frameRate, boolean isBigEndian, boolean playBackwards) throws LineUnavailableException, IOException {
        AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, numberOfChannels, frameSize, frameRate, isBigEndian);
        playSong(song, audioFormat, playBackwards);
    }


    /**
     * Plays song parts given in the songParts parameter.
     * @param songParts contains the song parts together with the average value of the song part.
     * @param audioFormat is the audio audioFormat to play the song parts in.
     * @param ascending is true if we want to play the song parts in ascending order (first play part at the 0th index, then 1st, etc.)
     * if it is set to false, then play in descending order (last index, last - 1 index, etc.)
     * This is important if the songParts array is sorted, if so then the ascending order (when ascending = true)
     * plays the songParts with lowest
     * average amplitude first. Otherwise first the ones with the highest and then continue in descending order.
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * Also if we want to play the song backwards (from finish to start), then we should call it with
     * specific values: ascending = false and the song parts should't be sorted* @throws LineUnavailableException
     * @throws LineUnavailableException is thrown when there is problem with feeding the data to the SourceDataLine.
     */
    public void playSongParts(SongPartWithAverageValueOfSamples[] songParts, AudioFormat audioFormat, boolean ascending, boolean playBackwards) throws LineUnavailableException {
        int bytesWritten;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(audioFormat);
        line.start();

        if(playBackwards) {
            for(int i = 0; i < songParts.length; i++) {
                reverseArr(songParts[i].songPart, audioFormat.getSampleSizeInBits() / 8);
            }
            if(ascending) {
                for(int i = 0; i < songParts.length; i++) {
                    // Number of frames needs to be an integer,
                    // so we don't play that part (if the number of frames in that part is not an integer)
                    // because the reverse method couldn't produce correct output, so the output is probably noise
                    if(songParts[i].songPart.length % audioFormat.getFrameSize() == 0) {
                        bytesWritten = line.write(songParts[i].songPart, 0, songParts[i].songPart.length);
                    }
                }
            } else {
                for(int i = songParts.length - 1; i >= 0; i--) {
                    // Number of frames needs to be an integer,
                    // so we don't play that part (if the number of frames in that part is not an integer)
                    // because the reverse method couldn't produce correct output, so the output is probably noise
                    if(songParts[i].songPart.length % audioFormat.getFrameSize() == 0) {
                        bytesWritten = line.write(songParts[i].songPart, 0, songParts[i].songPart.length);
                    }
                }
            }
        } else {
            if(ascending) {
                for(int i = 0; i < songParts.length; i++) {
                    // Because number of frames needs to be integer
                    int bytesToWrite = songParts[i].songPart.length - (songParts[i].songPart.length % audioFormat.getFrameSize());
                    bytesWritten = line.write(songParts[i].songPart, 0, bytesToWrite);
                }
            } else {
                for(int i = songParts.length - 1; i >= 0; i--) {
                    // Because number of frames needs to be integer
                    int bytesToWrite = songParts[i].songPart.length - (songParts[i].songPart.length % audioFormat.getFrameSize());
                    bytesWritten = line.write(songParts[i].songPart, 0, bytesToWrite);
                }
            }
        }
        line.drain();
    }


    /**
     * Plays song parts given in the songParts parameter.
     * @param songParts contains the song parts together with the average value of the song part.
     * @param encoding is the encoding of the audio data.
     * @param sampleRate is the sample rate of the audio data.
     * @param sampleSizeInBits is the size of 1 sample in bits.
     * @param numberOfChannels represents the number of channels.
     * @param frameSize is the size of one frame.
     * @param frameRate is the frame rate of the audio.
     * @param isBigEndian is true if the samples are in big endian, false if in little endian
     * @param ascending is true if we want to play the song parts in ascending order (first play part at the 0th index, then 1st, etc.)
     * if it is set to false, then play in descending order (last index, last - 1 index, etc.)
     * This is important if the songParts array is sorted, if so then the ascending order play the songParts with lowest
     * average amplitude first. Otherwise first the ones with the highest and then continue in descending order.
     * @param playBackwards if true, then the song will be played from last sample to first, otherwise will be played normally from start to finish.
     * Also if we want to play the song backwards (from finish to start), then we should call it with
     * specific values: ascending = false and the song parts should't be sorted
     * @throws LineUnavailableException is thrown when there is problem with feeding the data to the SourceDataLine.
     */
    public void playSongParts(SongPartWithAverageValueOfSamples[] songParts, Encoding encoding,
                              int sampleRate, int sampleSizeInBits, int numberOfChannels, int frameSize, float frameRate,
                              boolean isBigEndian, boolean ascending, boolean playBackwards) throws LineUnavailableException {

        AudioFormat audioFormat = new AudioFormat(encoding, sampleRate, sampleSizeInBits, numberOfChannels, frameSize, frameRate, isBigEndian);
        playSongParts(songParts, audioFormat, ascending, playBackwards);
    }




    /**
     * Sets the properties of this class and writes them to output together with some additional info.
     * @param file is the file with the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     * if it is false, then doesn't set the variable, which may save a lot of memory and time.
     * @throws IOException is thrown if there was problem with reading the info from the file.
     * @return Returns boolean based on the output of setVariables
     */
    public boolean setVariablesAndWriteValues(File file, boolean setSong) throws IOException {
        if(setVariables(file, setSong)) {
            writeVariables();
            return true;
        }
        return false;
    }

    /**
     * Sets the properties of this class and writes them to output together with some additional info.
     * @param path is the path to the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     * if it is false, then doesn't set the variable, which may save a lot of memory and time.
     * @throws IOException is thrown if there was problem with reading the info from the file.
     * @return Returns boolean based on the output of setVariables
     */
    public boolean setVariablesAndWriteValues(String path, boolean setSong) throws IOException {
        if(setVariables(path, setSong)) {
            writeVariables();
            return true;
        }
        return false;
    }


    /**
     * Sets the properties of this class.
     * @param path is the path to the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     * if it is false, then doesn't set the variable, which may save a lot of memory and time.
     * @throws IOException is thrown if there was problem with reading the info from the file.
     * @return Returns true if there was no problem with file, false if it couldn't be read (for example it wasn't audio).
     */
    public boolean setVariables(String path, boolean setSong) throws IOException {
        setNameVariables(path);
        if(!setFormatAndStream(path)) {
            return false;
        }
        setVariables();


        if(setSong) {
            setSong();
        }
        else {
            setTotalAudioLength();
        }


        if(!setFormatAndStream(path)) {     // If there was some problem, then something unexpected happened
            return false;                   // Because the previous call succeeded
        }
        return true;
    }

    /**
     * Sets the properties of this class.
     * @param file is the file with the song
     * @param setSong is true, if we want to write the whole song to 1D byte array song (It is property of this class).
     * if it is false, then doesn't set the variable, which may save a lot of memory and time.
     * @throws IOException is thrown if there was problem with reading the info from the file.
     * @return Returns true if there was no problem with file, false if it couldn't be read (for example it wasn't audio).
     */
    public boolean setVariables(File file, boolean setSong) throws IOException {
        setNameVariables(file);
        if(!setFormatAndStream(file)) {
            return false;
        }
        setVariables();

        if(setSong) {
            setSong();
        }
        else {
            setTotalAudioLength();
        }


        if(!setFormatAndStream(file)) {     // If there was some problem, then something unexpected happened
            return false;                   // Because the previous call succeeded
        }
        return true;
    }

    private boolean setTotalAudioLength() throws IOException {
        // TODO: PROGRAMO
        onlyAudioSizeInBytes = Program.getLengthOfInputStream(decodedAudioStream);
        headerSize = wholeFileSize - onlyAudioSizeInBytes;
        decodedAudioStream.close();
        // TODO: PROGRAMO
        return true;
    }

    private void setSong() throws IOException  {
        setTotalAudioLength();
        setFormatAndStream(this.path);
        song = convertStreamToByteArray(decodedAudioStream);
        onlyAudioSizeInBytes = song.length;
        headerSize = wholeFileSize - onlyAudioSizeInBytes;
        decodedAudioStream.close();
    }




    private void setNameVariables(File file) {
        this.fileName = file.getName();
        this.path = file.getPath();
    }

    private void setNameVariables(String path) {
        this.path = path;
        this.fileName = getFileNameFromPath(path);
    }


    // Sets variables if there is already valid decodedAudioFormat
    private void setVariables() throws IOException {
        isBigEndian = decodedAudioFormat.isBigEndian();
        numberOfChannels = decodedAudioFormat.getChannels();
        encoding = decodedAudioFormat.getEncoding();
        frameRate = decodedAudioFormat.getFrameRate();
        sampleSizeInBits = decodedAudioFormat.getSampleSizeInBits();
        sampleSizeInBytes = sampleSizeInBits / 8;
        frameSize = sampleSizeInBytes * numberOfChannels;
        sampleRate = (int)decodedAudioFormat.getSampleRate();
        setSizeOfOneSec();
        mask = calculateMask(sampleSizeInBytes);
        maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);

        wholeFileSize = originalAudioFileFormat.getByteLength();
        kbits = ((numberOfChannels * sampleRate * sampleSizeInBits) / 1000);

        // That is the number of frames that means total number of samples is numberOfChannels * numberOfFrames
        if(this.audioType == AudioType.MP3) {
            // TODO: This MP3 framecount - since here we call frames some different thing
            int frameCount = Integer.parseInt(originalAudioFileFormat.properties().get("mp3.length.frames").toString());
            lengthOfAudioInSeconds = (int)(frameCount * 0.026);        // 0.026s is size of 1 frame
        }
        else {
            int totalNumberOfFrames = originalAudioFileFormat.getFrameLength();
            lengthOfAudioInSeconds = (totalNumberOfFrames / sampleRate);        // Works for wav
        }

        isSigned = AudioFormatWithSign.getIsSigned(encoding);

        if(frameSize != decodedAudioFormat.getFrameSize()) {
            throw new IOException();
        }
    }


    public static int getMaxAbsoluteValue(int sampleSizeInBits, boolean isSigned) {
        if(isSigned) {
            return getMaxAbsoluteValueSigned(sampleSizeInBits);
        }
        else {
            return getMaxAbsoluteValueUnsigned(sampleSizeInBits);
        }
    }


    public static int getMaxAbsoluteValueSigned(int sampleSizeInBits) {
        return (1 << (sampleSizeInBits - 1)) - 1;
    }
    public static int getMaxAbsoluteValueUnsigned(int sampleSizeInBits) {
        return (1 << sampleSizeInBits) - 1;
    }



    public static final String LOG_MESSAGE_WHEN_SET_VARIABLES_RETURN_FALSE =
            "Probably invalid audioFormat or the file wasn't audio or the path was invalid";

    /**
     * Gets the audioFormat of the decoded audio and also gets the audio stream for the decoded audio and sets corresponding properties.
     * For the decoding of mp3 files is used library. If false is returnes, then there is some problem and song should
     * be invalidated.
     * @param path is the path to the file with audio.
     * @return Returns true if all was set correctly, false if there was some problem.
     */
    private boolean setFormatAndStream(String path) {
        try {
            soundFile = new File(path);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return setFormatAndStream();
    }

    public boolean setFormatAndStream(File f) {
        soundFile = f;
        return setFormatAndStream();
    }

    private boolean setFormatAndStream() {
        try {
            if(originalAudioStream != null) {
                originalAudioStream.close();
            }
            originalAudioFileFormat = AudioSystem.getAudioFileFormat(soundFile);
            type = originalAudioFileFormat.getType();
            originalAudioStream = AudioSystem.getAudioInputStream(soundFile);
            originalAudioFormat = originalAudioStream.getFormat();

            if("mp3".equals(type.getExtension())) {
                audioType = AudioType.MP3;
                decodedAudioFormat = new AudioFormat(Encoding.PCM_SIGNED,
                    originalAudioFormat.getSampleRate(),
                    16,
                    originalAudioFormat.getChannels(),
                    originalAudioFormat.getChannels() * 2,
                    originalAudioFormat.getSampleRate(),
                    false);
                // TODO: I should probably later close the original inputStream
                decodedAudioStream = AudioSystem.getAudioInputStream(decodedAudioFormat, originalAudioStream);
            }
            else {
                audioType = AudioType.OTHER;
                decodedAudioFormat = originalAudioFormat;
                decodedAudioStream = originalAudioStream;
            }
        } catch (Exception e) {
            originalAudioStream = null;
            originalAudioFormat = null;
            decodedAudioFormat = null;
            decodedAudioStream = null;
            audioType = AudioType.NOTSUPPORTED;
            return false;
        }

        return true;
    }


    public String getFileFormatType() {
        if(audioType == AudioType.MP3) {
            return "MP3 (.mp3)";
        }
        else {
            return type.toString() + " (." + type.getExtension() + ")";
        }
    }

    /**
     * Writes the contents of the properties together with some additional info.
     */
    private void writeVariables() {
        // TODO: at mp3 files writes some good properties
        for (int i = 0; i < 5; i++) {
            System.out.println();
        }
        System.out.println("Audio info:");
        System.out.println("AudioFileFormat properties:");
        System.out.println("Number of properties:\t" + originalAudioFileFormat.properties().size());
        for(Map.Entry<String, Object> property : originalAudioFileFormat.properties().entrySet()) {
            System.out.println("Property name:\t" + property.getKey() + "\nValue of property:\t" + property.getValue());
        }
        System.out.println();

        // TODO: mostly doesn't write anything
        System.out.println("AudioFormat properties:");
        System.out.println("Number of properties:\t" + decodedAudioFormat.properties().size());
        for(Map.Entry<String, Object> property : decodedAudioFormat.properties().entrySet()) {
            System.out.println("Property name:\t" + property.getKey() + "\nValue of property:\t" + property.getValue());
        }
        System.out.println();

        System.out.println("Extension:\t" + type.getExtension());
        System.out.println("Filetype (mostly WAVE):\t" + audioType);
        System.out.println(decodedAudioFormat);
        System.out.println("Number of channels:\t" + numberOfChannels);
        System.out.println("Type of encoding to waves (mostly PCM):\t" + encoding);
        System.out.println("Frame rate:\t" + frameRate);
        System.out.println("Size of frame:\t" + frameSize); // Size of 1 frame
        // frameSize = numberOfChannels * sampleSize
        // TODO: Zase nefunguje u mp3 - tam je frame ten mp3 frame to jsou samply co majĂ­ 0.23 sekund
        System.out.println("Sample(Sampling) rate (in Hz):\t" + sampleRate);
        System.out.println("Size of sample (in bits):\t" + sampleSizeInBits); // Size of 1 sample
        System.out.println("Is big endian: " + isBigEndian);
        System.out.println("Size of entire audio file (not just the audio data):\t" + wholeFileSize);

        System.out.println("Size of header:\t" + headerSize);

        System.out.printf("kbit/s:\t%d\n", ((numberOfChannels * sampleRate * sampleSizeInBits) / 1000));	// /1000 because it's kbit/s
        if(song != null) {
            System.out.println("song length in bytes:\t" + song.length);	// size of song in bytes
        }

        System.out.println("audio length in seconds:\t" + lengthOfAudioInSeconds);
        System.out.println("Audio lengths (in audioFormat hours:mins:secs):\t" + Program.convertSecondsToTime(lengthOfAudioInSeconds, -1));
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Time algorithms
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final StringBuilder timeStringBuilder = new StringBuilder();

    private static final int[] convertMinutesTimeDivArray = new int[] { 60, 60 };
    private static final int[] convertMinutesTimeModArray = new int[] { 10, 10 };
    /**
     * This variant can be used when I want to do parallel processing. Resets the stringbuilder.
     */
    public static String convertMinutesToTime(int mins, StringBuilder timeSB, int alignmentRecursionLevel) {
        String res = convertTimeUniversal(mins, convertMinutesTimeDivArray,
                convertMinutesTimeModArray, timeSB, alignmentRecursionLevel);
        return res;
    }

    /**
     * Doesn't work work for parallel processing
     */
    public static String convertMinutesToTime(int mins, int alignmentRecursionLevel) {
        return convertMinutesToTime(mins, timeStringBuilder, alignmentRecursionLevel);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int[] convertSecondsTimeDivArray = new int[] { 60, 60, 60 };
    private static final int[] convertSecondsTimeModArray = new int[] { 10, 10, 10 };
    /**
     * This variant can be used when I want to do parallel processing. Resets the stringbuilder.
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                than the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 001 for alignmentRecursionDepth == 0.
     *
     */
    public static String convertSecondsToTime(int seconds, StringBuilder timeSB, int alignmentRecursionDepth) {
        String res = convertTimeUniversal(seconds, convertSecondsTimeDivArray,
                convertSecondsTimeModArray, timeSB, alignmentRecursionDepth);
        return res;
    }
    /**
     * Doesn't work work for parallel processing
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                than the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 001 for alignmentRecursionDepth == 0.
     */
    public static String convertSecondsToTime(int seconds, int alignmentRecursionDepth) {
        return convertSecondsToTime(seconds, timeStringBuilder, alignmentRecursionDepth);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static final int[] convertMillisecondsTimeDivArray = new int[] { 1000, 60, 60, 60 };
    private static final int[] convertMillisecondsTimeModArray = new int[] { 100, 10, 10, 10 };
    /**
     * This variant can be used when I want to do parallel processing. Resets the stringbuilder.
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                than the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 0:001 when calling it with alignmentRecursionDepth == -1 or
     *                                001 when calling it with alignmentRecursionDepth == 0.
     */
    public static String convertMillisecondsToTime(int millis, StringBuilder timeSB, int alignmentRecursionDepth) {
        if(DEBUG_CLASS.DEBUG) {
            ProgramTest.debugPrint("MILLIS in convertMillisecondsToTime", millis);
        }
        String res = "";
        if(alignmentRecursionDepth < 0) {
            if (millis < 1000) {
                res = "0:";
            }
        }
        res += convertTimeUniversal(millis, convertMillisecondsTimeDivArray,
                convertMillisecondsTimeModArray, timeSB, alignmentRecursionDepth);
        return res;
    }
    /**
     * Doesn't work work for parallel processing
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                than the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 0:001 when calling it with alignmentRecursionDepth == -1 or
     *      *                         001 when calling it with alignmentRecursionDepth == 0.
     */
    public static String convertMillisecondsToTime(int millis, int alignmentRecursionDepth) {
        return convertMillisecondsToTime(millis, timeStringBuilder, alignmentRecursionDepth);
    }



    /**
     *
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                than the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 001 for alignmentRecursionDepth == 0.
     */
    private static String convertTimeUniversal(int timeAmount, int[] timeAmountToNextTimeLevel, int[] modLimitsToAddZero,
                                               StringBuilder timeSB, int alignmentRecursionDepth) {
        timeSB.setLength(0);
        convertTimeUniversal(timeAmount, timeAmountToNextTimeLevel, modLimitsToAddZero,
                0, timeSB, alignmentRecursionDepth);
        return timeSB.toString();
    }

    /**
     * Expects the StringBuilder to be of length 0.
     * @param timeAmount
     * @param timeAmountToNextTimeLevel
     * @param modLimitsToAddZero
     * @param depth
     * @param timeSB
     * @param alignmentRecursionDepth is the depth to which should be the time aligned. To say it easy - The parameter controls number of colons.
     *                                To get the best results with minimal alignment just call the method with argument -1.
     *                                If == 0 then no alignment is performed.
     *                                If < 0 then based on the method called in some special case alignment may be used - for example
     *                                for milliseconds there will always be at least one ':'. When calling it on other
     *                                that the millisecond method, it is the same as == 0 case.
     *                                Otherwise it is performed to the given depth (if the depth > modLimitsToAddZero.length)
     *                                then modLimitsToAddZero.length - 1 is used.
     *                                Example when alignmentRecursionDepth == 2 then the value is 00:00:001
     *                                instead of just 001 for alignmentRecursionDepth == 0.
     */
    private static void convertTimeUniversal(int timeAmount, int[] timeAmountToNextTimeLevel, int[] modLimitsToAddZero,
                                             int depth, StringBuilder timeSB, int alignmentRecursionDepth) {
        if(timeAmount != 0) {
            if (depth < timeAmountToNextTimeLevel.length) {
                int nextTimeMeasurementCount = timeAmount / timeAmountToNextTimeLevel[depth];
                convertTimeUniversal(nextTimeMeasurementCount, timeAmountToNextTimeLevel, modLimitsToAddZero,
                    depth + 1, timeSB, alignmentRecursionDepth);
                if(timeSB.length() != 0) {
                    timeSB.append(':');
                }
                int mod = timeAmount % timeAmountToNextTimeLevel[depth];
                int modChanging = mod;
                if(mod == 0) {
                    timeSB.append('0');
                }
                else {
                    for (; modChanging < modLimitsToAddZero[depth]; modChanging *= 10) {
                        timeSB.append('0');
                    }
                }

                timeSB.append(mod);
            }
        }
        else {
            if(alignmentRecursionDepth < 0) {
                if (depth == 0) {
                    timeSB.append('0');
                }
            }
            else {
                if(timeSB.length() > 0) {
                    timeSB.append(':');
                }

                alignmentRecursionDepth = Math.min(alignmentRecursionDepth, modLimitsToAddZero.length - 1);
                for (int i = alignmentRecursionDepth; i >= depth; i--) {
                    for (int mod = 1; mod <= modLimitsToAddZero[i]; mod *= 10) {
                        timeSB.append('0');
                    }
                    if(i != depth) {
                        timeSB.append(':');
                    }
                }
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Time algorithms
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Puts all the samples together. For example if the audio is stereo, then result 1D array looks like this
     * Puts 1st sample from the 1st channel, then 1st sample from the 2nd channel, then 2nd sample from 1st channel then
     * 2nd sample from 2nd channel, etc. (do that for all the samples).
     * @param channels is 2D byte array. Each byte array represents 1 channels.
     * @return Returns 1D byte array.
     */
    public byte[] createSongFromChannels(byte[][] channels) {
        byte[] song;
        ArrayList<Byte> songList = new ArrayList<>();
        int len;
        byte sample;
        if(channels.length == 1) {		// it is mono
            return channels[0];
        } else {
            // Putting channels together to make original song
            len = channels[0].length / sampleSizeInBytes;
            for(int i = 0; i < len; i++) {		// All have same size
                for(int j = 0; j < channels.length; j++) {
                    for(int k = 0; k < sampleSizeInBytes; k++) {
                        sample = channels[j][i * sampleSizeInBytes + k];
                        songList.add(sample);
                    }
                }
            }

            song = new byte[songList.size()];
            for(int i = 0; i < song.length; i++) {
                song[i] = songList.get(i);
            }
            return song;
        }
    }



    /**
     * Performs aggregation (compression) agg to all channels. For all channels do respectively, take n samples
     * perform the agg action on them, add the given number to the result, continue until the end of the channel is reached.
     * @param channels is 2D byte array, where 1 array corresponds to 1 channel.
     * @param n is the number of samples to perform 1 aggregation to.
     * @param sampleSize is the size of 1 sample in bytes.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @param agg is the type of aggregation to be performed.
     * @return Returns 2D array which corresponds to the modified channels.
     * @throws IOException is thrown when the given agg is not supported.
     */
    @Deprecated
    public static byte[][] forEachChannelModifySamplesMoreChannels(byte[][] channels, int n, int sampleSize,
                                                                   boolean isBigEndian, boolean isSigned,
                                                                   Aggregations agg) throws IOException {
        byte[][] modChannels = new byte[channels.length][];
        ArrayList<Byte> moddedChannel = new ArrayList<>();
        byte[] samples = new byte[n * sampleSize];
        for(int i = 0; i < channels.length; i++) {
            int index = 0;
            while(index + samples.length <= channels[i].length) {
                for(int j = 0; j < samples.length; j++) {
                    samples[j] =  channels[i][index];
                    index++;
                }
                int newSample = (int) performAggregation(samples, sampleSize, isBigEndian, isSigned, agg);

                byte[] arr = convertIntToByteArr(sampleSize, newSample, isBigEndian);
                for(int k = 0; k < arr.length; k++) {
                    moddedChannel.add(arr[k]);
                }
            }
            byte[] arr = new byte[moddedChannel.size()];
            for(int k = 0; k < arr.length; k++) {
                arr[k] = moddedChannel.get(k);
            }
            modChannels[i] = arr;
        }

        return modChannels;
    }


    /**
     * Performs aggregation (compression) agg to channel. Take n samples
     * perform the agg action on them, add the given number to the result, continue until the end of the channel is reached.
     * @param mono is 1D byte array with samples of the mono audio.
     * @param n is the number of samples to perform 1 aggregation to.
     * @param sampleSize is the size of 1 sample in bytes.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @param agg is the type of aggregation to be performed.
     * @return Returns the modified 1D byte array.
     * @throws IOException
     */
    @Deprecated
    public static byte[] forEachChannelModifySamplesOneChannel(byte[] mono, int n, int sampleSize,
                                                               boolean isBigEndian, boolean isSigned,
                                                               Aggregations agg) throws IOException {
        byte[][] channel = new byte[1][];
        channel[0] = mono;
        byte[][] result = forEachChannelModifySamplesMoreChannels(channel, n, sampleSize, isBigEndian, isSigned, agg);
        return result[0];
    }


    /**
     * Takes the 1D byte array (parameter samples) and split it to parts of size n * frameSize.
     * @param samples is the 1D byte array with the samples.
     * @param n is the size of the 1 song part.
     * @param frameSize is the size of 1 frame (= numberOfChannels * sampleSize).
     * @return Returns the 2D array where 1 byte array represents the part of size n * frameSize.
     */
    public byte[][] convertSongPartToMultipleSongPartsOfSizeNFrames(byte[] samples, int n, int frameSize) {
        byte[][] result = getEveryXthTimePeriodWithLength(samples, n, 1, frameSize, 0);
        return result;
    }


    /**
     * Takes the input stream (parameter samples) and split it to parts of size n * frameSize.
     * @param samples is the input stream with the samples.
     * @param n is the size of the 1 song part.
     * @param frameSize is the size of 1 frame (= numberOfChannels * sampleSize).
     * @return Returns the 2D array where 1 byte array represents the part of size n * frameSize.
     */
    public byte[][] convertWholeSongToMultipleSongPartsOfSizeNFrames(InputStream samples, int n, int frameSize) {
        byte[][] result = getEveryXthTimePeriodWithLength(song, n, 1, frameSize, 0);

        return result;
    }





// From documentation:
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]

    // TODO !!!!!!!!!!!!!!!!!!!!!!
    // TODO: !!!!!!!!!!!!!!!!!!!!! DOST ZASADNI CHYBA ASI ... PRO TU LICHOU FFT ma predposledni cislo tu a[1] jako imag
    // TODO: ale za nim je asi jeste jedno cislo ktery ma jen realnou slozku ...
    // TODO: Ja si myslel ze to co ma imaginarni slozku na pozici [1] uz je posledni
    // TODO: Tohle byla ta stara verze kdyz jsem mel jen tuhle metodu, ted to resim ze to volam pres tu resim ze to volam pres tu referencni variantu
//    /**
//     * Transforms the real and imaginary part to real by taking the distance of the complex number from zero,
//     * which is calculated as realPart * realPart + imagPart * imagPart.
//     * @param fftResult is 1D double array gotten from fft. On even indexes contains real parts, on odd imaginary ones.
//     * @return Returns 1D double array of half length of the original array. The new array contains the distances of complex numbers from zero.
//     */
//    public static double[] convertResultsOfFFTToRealRealForward(double[] fftResult) {
//        double[] result;
//        if(fftResult.length % 2 == 0) {			// It's even
//            result = new double[fftResult.length / 2 + 1];
//            result[0] = fftResult[0] * fftResult[0];
//            int index = 1;
//            for(int i = 2; i < fftResult.length; i = i + 2) {
//                result[index] = fftResult[i] * fftResult[i] + fftResult[i + 1] * fftResult[i + 1];
//                index++;
//            }
//            result[result.length - 1] = fftResult[1] * fftResult[1];
//        } else {
//            result = new double[(fftResult.length + 1) / 2];
//            result[0] = fftResult[0] * fftResult[0];
//            int index = 1;
//            for(int i = 2; i < fftResult.length - 1; i = i + 2) {
//                result[index] = fftResult[i] * fftResult[i] + fftResult[i + 1] * fftResult[i + 1];
//                index++;
//            }
//            result[result.length - 1] = fftResult[fftResult.length - 1] * fftResult[fftResult.length - 1] + fftResult[1] * fftResult[1];
//        }
//
//        return result;
//    }

    /**
     * Transforms the real and imaginary part to real by taking the distance of the complex number from zero,
     * which is calculated as realPart * realPart + imagPart * imagPart.
     * @param fftResult is 1D double array gotten from fft. On even indexes contains real parts, on odd imaginary ones.
     * @return Returns 1D double array of half length of the original array. The new array contains the distances of complex numbers from zero.
     */
    public static double[] convertResultsOfFFTToRealRealForward(double[] fftResult) {
        double[] result;
        int binCount = getBinCountRealForward(fftResult.length);
        result = new double[binCount];
        convertResultsOfFFTToRealRealForward(fftResult, result);
        return result;
    }

    // TODO: Napsat dokumentaci
    public static void convertResultsOfFFTToRealRealForward(double[] fftResult, double[] result) {
        if(fftResult.length % 2 == 0) {			// It's even;
            result[0] = calculateComplexNumMeasure(fftResult[0], 0);
            int index = 1;
            for(int i = 2; i < fftResult.length; i = i + 2) {
                result[index] = calculateComplexNumMeasure(fftResult[i], fftResult[i + 1]);
                index++;
            }
            result[result.length - 1] = calculateComplexNumMeasure(0, fftResult[1]);
        } else {
            result[0] = calculateComplexNumMeasure(fftResult[0], 0);
            int index = 1;
            for(int i = 2; i < fftResult.length - 1; i = i + 2) {
                result[index] = calculateComplexNumMeasure(fftResult[i], fftResult[i + 1]);
                index++;
            }

            result[result.length - 1] = calculateComplexNumMeasure(fftResult[fftResult.length - 1], fftResult[1]);
        }
    }


    // From documentation (this are the values we want convert to):
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]
    /**
     * This method is inverse to convertResultsOfFFTToRealRealForward, but it doesn't preserve imaginary values,
     * it just puts the measures to the real parts of the array and sets the imaginary part to 0.
     * @param fftMeasures
     * @param result
     * @return
     */
    public static double[] convertFFTAmplitudesToClassicFFTArr(double[] fftMeasures, double[] result) {
        if(result.length % 2 == 0) {			// It's even
            result[0] = Math.sqrt(fftMeasures[0]);
            result[1] = Math.sqrt(fftMeasures[fftMeasures.length - 1]);
            for(int i = 2, j = 2; i < fftMeasures.length; i++, j++) {
                result[j] = Math.sqrt(fftMeasures[i]);
                j++;
                result[j] = 0;
            }
        } else {
            result[0] = Math.sqrt(fftMeasures[0]);
            for(int i = 1, j = 2; i < fftMeasures.length; i++, j++) {
                result[j] = Math.sqrt(fftMeasures[i]);
                if(i == fftMeasures.length - 2) {
                    result[1] = 0;
                }
                else {
                    j++;
                    result[j] = 0;
                }
            }
        }

        return result;


//        if(result.length % 2 == 0) {			// It's even
//            result[0] = fftMeasures[0];
//            result[1] = fftMeasures[fftMeasures.length - 1];
//            for(int i = 2, j = 2; i < fftMeasures.length; i++, j++) {
//                result[j] = fftMeasures[i];
//                j++;
//                result[j] = 0;
//            }
//        } else {
//            for(int i = 0, j = 0; i < fftMeasures.length; i++, j++) {
//                result[j] = fftMeasures[i];
//                j++;
//                result[j] = 0;
//            }
//        }
//
//        return result;
    }



    // From documentation (this are the values we want convert to):
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]
    /**
     * This method is inverse to convertResultsOfFFTToRealRealForward, but instead of the previous variant, it has
     * both real and imaginary part set in such a way that real part is set by random and imaginary is the remainder
     * @param fftMeasures
     * @param result
     * @return
     */
    public static double[] convertFFTAmplitudesToClassicFFTArrRandom(double[] fftMeasures, double[] result) {
        if(result.length % 2 == 0) {			// It's even
            result[0] = Math.sqrt(fftMeasures[0]);
            result[1] = Math.sqrt(fftMeasures[fftMeasures.length - 1]);
            for(int i = 2, j = 2; i < fftMeasures.length; i++, j++) {
                double real = Math.random() * fftMeasures[i];
                double imag = fftMeasures[i] - real;
                result[j] = Math.sqrt(real);
                j++;
                result[j] = Math.sqrt(imag);
            }
        } else {
            result[0] = Math.sqrt(fftMeasures[0]);
            for(int i = 1, j = 2; i < fftMeasures.length; i++, j++) {
                double real = Math.random() * fftMeasures[i];
                double imag = fftMeasures[i] - real;
                result[j] =  Math.sqrt(real);
                if(i == fftMeasures.length - 2) {
                    result[1] = Math.sqrt(imag);
                }
                else {
                    j++;
                    result[j] = Math.sqrt(imag);
                }
            }
        }

        return result;
    }


    /**
     * To understand the result take a look at FFT NOTES. Also it is important to understand, that this operation is
     * irreversible because of the integer division.
     * @param windowSize
     * @return
     */
    public static int getBinCountRealForward(int windowSize) {
        int binCount;
        binCount = windowSize / 2 + 1;      // Take a look at the FFT notes to understand why it is windowSize / 2 + 1.
        return binCount;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert complex number to real number methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static interface ComplexToRealInterface {
        double complexToReal(double real, double imag);
    }

    /**
     * Calculates the distance of complex number from 0.
     * @param realPart is the real part of complex number.
     * @param imagPart is the imaginary part of complex number.
     * @return Returns the distance of complex number from 0.
     */
    public static double calculateComplexNumMeasure(double realPart, double imagPart) {
        double result = Math.sqrt(realPart * realPart + imagPart * imagPart);

        return result;
    }

    public static double calculateComplexNumPower(double real, double imag) {
        return real * real + imag * imag;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert complex number to real number methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert real numbers to complex numbers methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void realToComplexRealOnly(double[] real, double[] complex, final boolean shouldSetOtherPartToZero) {
        realToComplexRealOnly(real, 0, real.length, complex, 0, shouldSetOtherPartToZero);
    }

    /**
     * Converts array with real values to array with comples values, which as at [% 2 == 0] real values and everywhere else 0.
     */
    public static void realToComplexRealOnly(double[] real, int realArrLen, double[] complex,
                                             final boolean shouldSetOtherPartToZero) {
        realToComplexRealOnly(real, 0, realArrLen, complex, 0, shouldSetOtherPartToZero);
    }

    public static void realToComplexRealOnly(double[] real, int realStartIndex, int realArrLen,
                                             double[] complex, int complexStartIndex,
                                             final boolean shouldSetOtherPartToZero) {
        for(int r = realStartIndex, c = complexStartIndex; r < realArrLen; r++, c++) {
            complex[c] = real[r];
            c++;
            if(shouldSetOtherPartToZero) {
                complex[c] = 0;
            }
        }
    }


    public static void realToComplexImagOnly(double[] real, double[] complex,
                                             final boolean shouldSetOtherPartToZero) {
        realToComplexImagOnly(real, 0, real.length, complex, 0, shouldSetOtherPartToZero);
    }

    public static void realToComplexImagOnly(double[] real, int realArrLen, double[] complex,
                                             final boolean shouldSetOtherPartToZero) {
        realToComplexImagOnly(real, 0, realArrLen, complex, 0, shouldSetOtherPartToZero);
    }

    public static void realToComplexImagOnly(double[] real, int realStartIndex, int realArrLen,
                                             double[] complex, int complexStartIndex,
                                             final boolean shouldSetOtherPartToZero) {
        for(int r = realStartIndex, c = complexStartIndex; r < realArrLen; r++, c++) {
            if(shouldSetOtherPartToZero) {
                complex[c] = 0;
            }
            c++;
            complex[c] = real[r];
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert real numbers to complex numbers methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Convert complex numbers to real numbers methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void complexToRealPowers(double[] real, int realArrLen, double[] complex) {
        complexToRealPowers(real, 0, realArrLen, complex, 0);
    }

    // These 2 methods could be solved by using interface method, but as micro micro optimization it is called straight.
    /**
     * Converts complex array to real array by taking power
     * @param real
     * @param realStartIndex
     * @param realArrLen
     * @param complex
     * @param complexStartIndex
     */
    public static void complexToRealPowers(double[] real, int realStartIndex, int realArrLen,
                                           double[] complex, int complexStartIndex) {
        for(int r = realStartIndex, c = complexStartIndex; r < realArrLen; r++, c += 2) {
            real[r] = Program.calculateComplexNumPower(complex[c], complex[c + 1]);
        }
    }

    public static void complexToRealMeasures(double[] real, int realArrLen, double[] complex) {
        complexToRealMeasures(real, 0, realArrLen, complex, 0);
    }

    /**
     * Converts complex array to real array by taking measure
     * @param real
     * @param realStartIndex
     * @param realArrLen
     * @param complex
     * @param complexStartIndex
     */
    public static void complexToRealMeasures(double[] real, int realStartIndex, int realArrLen,
                                             double[] complex, int complexStartIndex) {
        for(int r = realStartIndex, c = complexStartIndex; r < realArrLen; r++, c += 2) {
            real[r] = Program.calculateComplexNumMeasure(complex[c], complex[c + 1]);
        }
    }

    public static void complexToReal(double[] real, int realArrLen, double[] complex,
                                     ComplexToRealInterface action) {
        complexToReal(real, 0, realArrLen, complex, 0, action);
    }



    public static void complexToReal(double[] real, int realStartIndex, int realArrLen,
                                     double[] complex, int complexStartIndex,
                                     ComplexToRealInterface action) {
        for(int r = realStartIndex, c = complexStartIndex; r < realArrLen; r++, c += 2) {
            real[r] = action.complexToReal(complex[c], complex[c + 1]);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Convert complex numbers to real numbers methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Puts the real and imag part to result array in such way that [i % 2 == 0] contains real part and odd indices imaginary part.
     * So the result array should be at least 2 * real.length. And also the result will have real.length complex numbers.
     * And imaginary part should be the same size as real part.
     * That means that on the results needs to be performed full fft. If the imaginary part is == 0, then the second
     * part is just the mirror of the first one.
     * Note: the result array is always even because in order for it to contain both real and imaginary parts it needs to be even.
     * But the number of complex numbers in result doesn't have to be even.
     * Take look at FFT NOTES to understand how many bins are for each window size.
     */
    public static void connectRealAndImagPart(double[] real, double[] imag, double[] result) {
        int i = 0;
        int partIndex = 0;
        for(; partIndex < real.length; i++, partIndex++) {
            result[i] = real[partIndex];
            i++;
            result[i] = imag[partIndex];
            // TODO: DEBUG
//            ProgramTest.debugPrint("Index connect", i, partIndex, real.length, imag.length);
            // TODO: DEBUG
        }
        partIndex--;

        // If the windowSize is even, then the middle element is there only one time, so we don't mirror it.
        if((result.length / 2) % 2 == 0) {
            partIndex--;
        }
        // > 0 because the 0-th frequency isn't copied
        for(; partIndex > 0; i++, partIndex--) {
            result[i] = real[partIndex];
            i++;
            result[i] = -imag[partIndex]; // - because mirrored
            // TODO: DEBUG
//            ProgramTest.debugPrint("Index connect mirror", i, partIndex, real.length, imag.length);
            // TODO: DEBUG
        }
    }


    // From documentation (this are the values we want convert to):
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]
    // TODO: Nebo aspon myslim ze by to melo byt (realForwardFFTArr.length + 1) / 2
    /**
     * Takes realForward fft array and divides it to real and imaginary part. The real and imaginary part should be the
     * same length and the length should be (realForwardFFTArr.length + 1) / 2
     * @param real
     * @param imag
     * @param realForwardFFTArr
     * @param fftArrLen because the realForwardFFTArr can be longer than the result of fft
     */
    public static void separateRealAndImagPart(double[] real, double[] imag, double[] realForwardFFTArr, int fftArrLen) {
        if(fftArrLen % 2 == 0) {			// It's even
            real[0] = realForwardFFTArr[0];
            imag[0] = 0;
            int index = 1;
            for(int i = 2; i < fftArrLen; i++, index++) {
                real[index] = realForwardFFTArr[i];
                i++;
                imag[index] = realForwardFFTArr[i];
            }

            real[real.length - 1] = realForwardFFTArr[1];
            imag[imag.length - 1] = 0;
        } else {
            real[0] = realForwardFFTArr[0];
            imag[0] = 0;
            int index = 1;
            for (int i = 2; i < fftArrLen - 1; i++, index++) {
                real[index] = realForwardFFTArr[i];
                i++;
                imag[index] = realForwardFFTArr[i];
            }

            real[real.length - 1] = realForwardFFTArr[fftArrLen - 1];
            imag[imag.length - 1] = realForwardFFTArr[1];
        }
    }

    // Basically separateRealAndImagPart method without setting the imaginary part
    public static void separateOnlyRealPart(double[] real, double[] realForwardFFTArr, int fftArrLen) {
        if(fftArrLen % 2 == 0) {			// It's even
            real[0] = realForwardFFTArr[0];
            int index = 1;
            for(int i = 2; i < fftArrLen; i += 2, index++) {
                real[index] = realForwardFFTArr[i];
            }

            real[real.length - 1] = realForwardFFTArr[1];
        } else {
            real[0] = realForwardFFTArr[0];
            int index = 1;
            for (int i = 2; i < fftArrLen - 1; i += 2, index++) {
                real[index] = realForwardFFTArr[i];
            }

            real[real.length - 1] = realForwardFFTArr[fftArrLen - 1];
        }
    }


    // Basically separateRealAndImagPart method without setting the real part
    public static void separateOnlyImagPart(double[] imag, double[] realForwardFFTArr, int fftArrLen) {
        if(fftArrLen % 2 == 0) {			// It's even
            imag[0] = 0;
            int index = 1;
            for(int i = 3; i < fftArrLen; i += 2, index++) {
                imag[index] = realForwardFFTArr[i];
            }

            imag[imag.length - 1] = 0;
        } else {
            imag[0] = 0;
            int index = 1;
            for (int i = 3; i < fftArrLen - 1; i += 2, index++) {
                imag[index] = realForwardFFTArr[i];
            }

            imag[imag.length - 1] = realForwardFFTArr[1];
        }
    }




    /**
     * Method takes the results of FFT, converts them to real number by taking the distances of the complex numbers.
     * Then just adds frequencies to corresponding complex numbers.
     * Calculating the frequency for complex number at index indexOfTheComplexNumber:
     * indexOfTheComplexNumber * sampleRate / totalNumberOfComplexNumbers is the frequency
     * and the distance from 0 of the complex number on that index is the measure.
     * @param fftResult is 1D double array which contains the result of FFT.
     * @param sampleRate is the sample rate of the data which were transformed to fftResult parameter.
     * @param returnSortedByMeasure is true if we want the output to be sorted by measure, false otherwise.
     * @return Returns the FrequencyWithMeasure[] which contains the frequencies with corresponding measures.
     * Is sorted if returnSortedByMeasure = true
     */
    public static FrequencyWithMeasure[] convertImagPartToRealReturnArrWithFrequenciesRealForward(double[] fftResult, int sampleRate, boolean returnSortedByMeasure) {
        double[] realResult = convertResultsOfFFTToRealRealForward(fftResult);			// TODO: not really effective, because new double array needs to be created, it can be calculated in place without creating the array
        return returnArrWithFrequenciesRealForward(realResult, sampleRate, returnSortedByMeasure);
    }
//

    /**
     * Expects the fftResult to be the distances of complex numbers. So it just adds the frequencies to the distances.
     * Calculating the frequency for complex number at index indexOfTheComplexNumber:
     * indexOfTheComplexNumber * sampleRate / totalNumberOfComplexNumbers is the frequency
     * and the distance from 0 of the complex number on that index is the measure.
     * @param fftResult is 1D double array which contains the result of FFT.
     * @param sampleRate is the sample rate of the data which were transformed to fftResult parameter.
     * @param returnSortedByMeasure is true if we want the output to be sorted by measure, false otherwise.
     * @return Returns the FrequencyWithMeasure[] which contains the frequencies with corresponding measures.
     * Is sorted if returnSortedByMeasure = true
     */
    public static FrequencyWithMeasure[] returnArrWithFrequenciesRealForward(double[] fftResult, int sampleRate, boolean returnSortedByMeasure) {
        FrequencyWithMeasure[] arr = new FrequencyWithMeasure[fftResult.length];
//        System.out.println("arr:\t" + arr.length);                                        // TODO: remove - is just for debugging
//        System.out.println("Sample rate:\t" + sampleRate);
//        System.out.println("The division:\t" + (sampleRate / arr.length));
        int number = arr.length - 1;                          //TODO: ta -1
        int spaceSize = sampleRate / (number * 2);                // TODO: * 2 jsem pridal ja
        /*if(sampleRate % arr.length != 0) {
            spaceSize++;
        }*/
        for(int i = 0; i < arr.length; i++) {
            int frequency = i * spaceSize;

           // System.out.println("Freq:" + frequency + "----------------------------------------------");
            arr[i] = new FrequencyWithMeasure(frequency, fftResult[i]);
        }

        if(returnSortedByMeasure) {
            Arrays.sort(arr);
        }

        return arr;
    }

    // TODO: Ty jmena jsou trochu divny !!!!!!!!!!!!!!!!!!!!!!!
// TODO: Napsat komentare
    public static void calculateIFFTRealForward(double[] samples, boolean scale) {
        DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);
        calculateIFFTRealForward(samples, fft, scale);
    }

    // TODO: na tohle ani nepotrebuju metodu
    public static void calculateIFFTRealForward(double[] samples, DoubleFFT_1D fft, boolean scale) {
        fft.realInverse(samples, scale);            // TODO: Nevim co scale dela, ale rekl bych ze by to melo byt false
    }

    public static double[] calculateIFFTRealForwardCopyVariant(double[] samples, DoubleFFT_1D fft, boolean scale) {
        double[] copy = Arrays.copyOf(samples, samples.length);
        calculateIFFTRealForward(copy, fft, scale);
        return copy;
    }

    public static double[] calculateIFFTRealForwardCopyVariant(double[] samples, boolean scale) {
        double[] copy = Arrays.copyOf(samples, samples.length);
        calculateIFFTRealForward(copy, scale);
        return copy;
    }


    // TODO: Napsat komentare
    public static void calculateIFFTComplexForward(double[] samples, boolean scale) {
        DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);
        calculateIFFTComplexForward(samples, fft, scale);
    }

    // TODO: na tohle ani nepotrebuju metodu
    public static void calculateIFFTComplexForward(double[] samples, DoubleFFT_1D fft, boolean scale) {
        fft.complexInverse(samples, scale);            // TODO: Nevim co scale dela, ale rekl bych ze by to melo byt false
    }

    public static double[] calculateIFFTComplexForwardCopyVariant(double[] samples, DoubleFFT_1D fft, boolean scale) {
        double[] copy = Arrays.copyOf(samples, samples.length);
        calculateIFFTComplexForward(copy, fft, scale);
        return copy;
    }

    public static double[] calculateIFFTComplexForwardCopyVariant(double[] samples, boolean scale) {
        double[] copy = Arrays.copyOf(samples, samples.length);
        calculateIFFTComplexForward(copy, scale);
        return copy;
    }


    // TODO: Hloupost ... tohle dava smysl u FFT ... ale tak je mozny ze to nekdy vyuziju, takze muzu nechat
    public static double[] calculateIFFTComplexForwardProlongArray(double[] samples, boolean scale) {
        double[] result = Arrays.copyOf(samples, samples.length * 2);
        DoubleFFT_1D fft = new DoubleFFT_1D(samples.length);        // TODO: asi by to melo mit tuhle velikost tj. samples.length (... dalsi moznost je samples.length * 2 ale to nedava moc smysl)
        calculateIFFTComplexForward(result, fft, scale);
        return result;
    }
    // TODO: Hloupost ... tohle dava smysl u FFT ... ale tak je mozny ze to nekdy vyuziju, takze muzu nechat
    public static double[] calculateIFFTComplexForwardProlongArray(double[] samples, DoubleFFT_1D fft, boolean scale) {
        double[] result = Arrays.copyOf(samples, samples.length * 2);
        calculateIFFTComplexForward(result, fft, scale);
        return result;
    }




    public static double[] calculateFFTRealForward(double[] samples, int startIndex, int len, int numberOfChannels,
                                                   int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, len, numberOfChannels, result, fftSize);
        return result;
    }
    public static double[] calculateFFTRealForward(double[] samples, int startIndex, int len, int numberOfChannels, int fftSize) {
        return calculateFFTRealForward(samples, startIndex, len, numberOfChannels, fftSize, fftSize);
    }

    public static double[] calculateFFTRealForward(double[] samples, int startIndex, int len, int numberOfChannels,
                                                   DoubleFFT_1D fft, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, len, numberOfChannels, fft, result);
        return result;
    }
    ////////////////////
    public static void calculateFFTRealForward(double[] samples, int startIndex, int len, int numberOfChannels,
                                               double[] result, int fftSize) {
        if(result.length < fftSize) {       // TODO: Mel bych tu provadet check nebo ne???
            return;
        }
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);
        calculateFFTRealForward(samples, startIndex, len, numberOfChannels, fft, result);
    }
    public static void calculateFFTRealForward(double[] samples, int startIndex, int len,
                                               int numberOfChannels, DoubleFFT_1D fft, double[] result) {
        for (int i = 0; i < len; i++, startIndex += numberOfChannels) {
            result[i] = samples[startIndex];
        }
        fft.realForward(result);
    }

    //////////////////////
    //////////////////////
    // MaxAbsoluteValue is result of getMaxAbsoluteValueSigned

    public static double[] calculateFFTRealForward(int[] samples, int startIndex, int numberOfChannels, int maxAbsoluteValue,
                                                   boolean isSigned, int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, numberOfChannels, result, maxAbsoluteValue, isSigned, fftSize);
        return result;
    }
    public static double[] calculateFFTRealForward(int[] samples, int startIndex, int numberOfChannels, int maxAbsoluteValue,
                                                   boolean isSigned, int fftSize) {
        return calculateFFTRealForward(samples, startIndex, numberOfChannels, maxAbsoluteValue, isSigned, fftSize, fftSize);
    }

    public static double[] calculateFFTRealForward(int[] samples, int startIndex, int numberOfChannels, DoubleFFT_1D fft,
                                                   int maxAbsoluteValue, boolean isSigned, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, numberOfChannels, fft, result, maxAbsoluteValue, isSigned);
        return result;
    }

    public static void calculateFFTRealForward(int[] samples, int startIndex, int numberOfChannels, double[] result, int maxAbsoluteValue, boolean isSigned,
                                               int fftSize) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);           // TODO: Mel bych tu provadet check nebo ne??? jako u calculateFFTRealForward
        calculateFFTRealForward(samples, startIndex, numberOfChannels, fft, result, maxAbsoluteValue, isSigned);
    }
    public static void calculateFFTRealForward(int[] samples, int startIndex, int numberOfChannels, DoubleFFT_1D fft,
                                               double[] result, int maxAbsoluteValue, boolean isSigned) {
        for(int i = 0; i < result.length; i++, startIndex += numberOfChannels) {
            result[i] = normalizeToDoubleBetweenMinusOneAndOne(samples[startIndex], maxAbsoluteValue, isSigned);
        }
        fft.realForward(result);
    }

    //////////////////////
    //////////////////////
    public static double[] calculateFFTRealForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize, int frameSize, int mask,
                                                   int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, result, maxAbsoluteValue,
            isBigEndian, isSigned, fftSize);
        return result;
    }
    public static double[] calculateFFTRealForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize, int frameSize, int mask,
                                                   int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int fftSize) {
        return calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, maxAbsoluteValue,
            isBigEndian, isSigned, fftSize, fftSize);
    }

    public static double[] calculateFFTRealForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize,
                                                   int frameSize, int mask,
                                                   DoubleFFT_1D fft,
                                                   int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, fft, result, maxAbsoluteValue,
            isBigEndian, isSigned);
        return result;
    }
    public static void calculateFFTRealForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize, int frameSize, int mask,
                                               double[] result, int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int fftSize) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);           // TODO: Mel bych tu provadet check nebo ne??? jako u calculateFFTRealForward
        calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, fft, result, maxAbsoluteValue,
                                    isBigEndian, isSigned);
    }
    public static void calculateFFTRealForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize,
                                               int frameSize, int mask,
                                               DoubleFFT_1D fft,
                                               double[] result, int maxAbsoluteValue, boolean isBigEndian, boolean isSigned) {
        int valInt;
        for(int i = 0; i < result.length; i++, startIndex += numberOfChannels * frameSize) {
            valInt = convertBytesToInt(samples, sampleSize, mask, startIndex, isBigEndian, isSigned);
            result[i] = normalizeToDoubleBetweenMinusOneAndOne(valInt, maxAbsoluteValue, isSigned);
        }

        fft.realForward(result);
    }


    public static void calculateFFTRealForward(double[] samples, int startIndex, double[] result, int fftLen) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftLen);         // TODO: Mel bych tu provadet check nebo ne??? jako u calculateFFTRealForward
        calculateFFTRealForward(samples, startIndex, fft, result);
    }
    public static void calculateFFTRealForward(double[] samples, int startIndex, DoubleFFT_1D fft, double[] result) {
        System.arraycopy(samples, startIndex, result, 0, result.length);
        fft.realForward(result);
    }

    // TODO: !!!!!!!!!!!!!!! Ta complexni varianta je jen copy pasta s tim ze budu zpracovavat 2 kanaly najednou,
    // TODO: a ted si nejsem moc jistej jestli to je spravne ... rozhodne ty vysledky nejsou stejny, ale mozna to pro ty energii tak moc nevadi ... a hlavni jen bude ze obe jsou rostouci
    // TODO: I kdyz nejsou rostouci uplne stejne
    // TODO: Ted ale nema cenu to implementovat kdyz nevim, jestli to bude potreba a navic jak rikam tak to je jen copy pasta
    // TODO: !!!!!!!!!!!!!!! Dava to smysl, protoze kdyz pak udelam IFFT tak dostanu ten puvodni vysledek, takze muze provest operace jakoby na 2 kanalech najendou
    public static double[] calculateFFTComplexForward(double[] samples, int startIndex, int numberOfChannels, int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, result, fftSize);
        return result;
    }
    public static double[] calculateFFTComplexForward(double[] samples, int startIndex, int numberOfChannels, int fftSize) {
        return calculateFFTComplexForward(samples, startIndex, numberOfChannels, 2*fftSize, fftSize);
    }

    public static double[] calculateFFTComplexForward(double[] samples, int startIndex, int numberOfChannels, DoubleFFT_1D fft, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, fft, result);
        return result;
    }
    ////////////////////
    public static void calculateFFTComplexForward(double[] samples, int startIndex, int numberOfChannels, double[] result, int fftSize) {
        if(result.length < fftSize) {       // TODO: Mel bych tu provadet check nebo ne???
            return;
        }
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, fft, result);
    }
    public static void calculateFFTComplexForward(double[] samples, int startIndex, int numberOfChannels, DoubleFFT_1D fft, double[] result) {
        for (int i = 0; i < result.length; i++, startIndex += numberOfChannels - 1) {
            result[i++] = samples[startIndex++];
            result[i] = samples[startIndex];
        }
        fft.realForward(result);
    }

    //////////////////////
    //////////////////////
    public static double[] calculateFFTComplexForward(int[] samples, int startIndex, int numberOfChannels, int maxAbsoluteValue,
                                                   boolean isSigned, int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, result, maxAbsoluteValue, isSigned, fftSize);
        return result;
    }
    public static double[] calculateFFTComplexForward(int[] samples, int startIndex, int numberOfChannels, int maxAbsoluteValue,
                                                   boolean isSigned, int fftSize) {
        return calculateFFTComplexForward(samples, startIndex, numberOfChannels, maxAbsoluteValue, isSigned, 2*fftSize, fftSize);
    }

    public static double[] calculateFFTComplexForward(int[] samples, int startIndex, int numberOfChannels, DoubleFFT_1D fft,
                                                   int maxAbsoluteValue, boolean isSigned, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, fft, result, maxAbsoluteValue, isSigned);
        return result;
    }
    public static void calculateFFTComplexForward(int[] samples, int startIndex, int numberOfChannels, double[] result, int maxAbsoluteValue, boolean isSigned,
                                               int fftSize) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);           // TODO: Mel bych tu provadet check nebo ne??? jako u calculateFFTRealForward
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, fft, result, maxAbsoluteValue, isSigned);
    }
    public static void calculateFFTComplexForward(int[] samples, int startIndex, int numberOfChannels, DoubleFFT_1D fft,
                                               double[] result, int maxAbsoluteValue, boolean isSigned) {
        for(int i = 0; i < result.length; i++, startIndex += numberOfChannels - 1) {
            result[i++] = normalizeToDoubleBetweenMinusOneAndOne(samples[startIndex], maxAbsoluteValue, isSigned);
            startIndex++;
            result[i] = normalizeToDoubleBetweenMinusOneAndOne(samples[startIndex], maxAbsoluteValue, isSigned);
        }
        fft.realForward(result);
    }

    //////////////////////
    //////////////////////
    public static double[] calculateFFTComplexForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize, int frameSize, int mask,
                                                      int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int resultArrayLen, int fftSize) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, result, maxAbsoluteValue,
            isBigEndian, isSigned, fftSize);
        return result;
    }
    public static double[] calculateFFTComplexForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize, int frameSize, int mask,
                                                        int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int fftSize) {
        return calculateFFTComplexForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, maxAbsoluteValue,
            isBigEndian, isSigned, 2*fftSize, fftSize);
    }

    public static double[] calculateFFTComplexForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize,
                                                   int frameSize, int mask,
                                                   DoubleFFT_1D fft,
                                                   int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int resultArrayLen) {
        double[] result = new double[resultArrayLen];
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, fft, result, maxAbsoluteValue,
            isBigEndian, isSigned);
        return result;
    }
    public static void calculateFFTComplexForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize, int frameSize, int mask,
                                               double[] result, int maxAbsoluteValue, boolean isBigEndian, boolean isSigned, int fftSize) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftSize);           // TODO: Mel bych tu provadet check nebo ne??? jako u calculateFFTRealForward
        calculateFFTComplexForward(samples, startIndex, numberOfChannels, sampleSize, frameSize, mask, fft, result, maxAbsoluteValue,
            isBigEndian, isSigned);
    }
    public static void calculateFFTComplexForward(byte[] samples, int startIndex, int numberOfChannels, int sampleSize,
                                               int frameSize, int mask,
                                               DoubleFFT_1D fft,
                                               double[] result, int maxAbsoluteValue, boolean isBigEndian, boolean isSigned) {
        int valInt;
        for(int i = 0; i < result.length; i++, startIndex += (numberOfChannels - 1) * frameSize) {
            valInt = convertBytesToInt(samples, sampleSize, mask, startIndex, isBigEndian, isSigned);
            result[i++] = normalizeToDoubleBetweenMinusOneAndOne(valInt, maxAbsoluteValue, isSigned);
            startIndex += frameSize;
            valInt = convertBytesToInt(samples, sampleSize, mask, startIndex, isBigEndian, isSigned);
            result[i] = normalizeToDoubleBetweenMinusOneAndOne(valInt, maxAbsoluteValue, isSigned);
        }
        fft.realForward(result);
    }


    public static void calculateFFTComplexForward(double[] samples, int startIndex, double[] result, int fftLen) {
        DoubleFFT_1D fft = new DoubleFFT_1D(fftLen);         // TODO: Mel bych tu provadet check nebo ne??? jako u calculateFFTRealForward
        calculateFFTComplexForward(samples, startIndex, fft, result);
    }
    public static void calculateFFTComplexForward(double[] samples, int startIndex, DoubleFFT_1D fft, double[] result) {
        System.arraycopy(samples, startIndex, result, 0, result.length);
        fft.realForward(result);
    }

    /**
     * Takes song parts performs fft on them, converts the complex numbers from result to real numbers by taking the distance from 0. Connects these numbers to corresponding frequencies.
     * Distance is measure of that frequency in the song part.
     * Returns null if the input songParts array is null, or if any of the song part if the array is null
     * @param songParts are the song parts together with average amplitude, song part is represented by byte array.
     * @param sampleSize is the size of one sample
     * @param sampleRate is the sample rate of the audio
     * @param isBigEndian true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @return Returns 2D array containing the measures with frequencies. 1 array = measures and frequencies for 1 song part.
     * @throws IOException is thrown when sample size is invalid
     */
    public static FrequencyWithMeasure[][] calculateFFTRealForward(SongPartWithAverageValueOfSamples[] songParts, int sampleSize, int sampleRate, boolean isBigEndian, boolean isSigned) throws IOException {
        FrequencyWithMeasure[][] results = new FrequencyWithMeasure[songParts.length][];
        if(songParts == null || songParts[0].songPart == null) {
            return null;
        }
        DoubleFFT_1D fft = new DoubleFFT_1D(songParts[0].songPart.length / sampleSize);
        for(int i = 0; i < songParts.length; i++) {
            if(songParts[i].songPart == null) {
                return null;
            }
            int[] intSamples = convertBytesToSamples(songParts[i].songPart, sampleSize, isBigEndian, isSigned);

            /*for(int j = 0; j < songParts[i].songPart.length; j++) {
                System.out.println(intSamples[j]);
                if(songParts[i].songPart[j] != (byte)intSamples[j]) {
                    System.out.println("Chyba");
                }
            }*/
            double[] normalizedSongPart = normalizeToDoubles(intSamples, sampleSize * 8, isSigned);
//            for(int j = 0; j < normalizedSongPart.length; j++) {
//                System.out.println(normalizedSongPart[j]);
//            }

            //DoubleFFT_1D fft = new DoubleFFT_1D(normalizedSongPart.length);
            fft.realForward(normalizedSongPart);
            // TODO: odsud dolu remove
//            double[] copiedArr = new double[normalizedSongPart.length];
////            for(int k = 0; k < intSamples.length; k++) {
////                normalizedSongPart[k] = intSamples[k];
////            }
//            for(int k = 0; k < copiedArr.length; k++) {
//                copiedArr[k] = normalizedSongPart[k];
//            }
//            fft.realForward(normalizedSongPart);
//            fft.realInverse(normalizedSongPart, true);
//            System.out.println("Original arr length:\t" + normalizedSongPart.length);
//            for(int k = 0; k < normalizedSongPart.length; k++) {     // TODO: Testing jestli vraci stejny vysledky
//                System.out.println("Original:\t" + copiedArr[k] + "\tNew:\t" + normalizedSongPart[k]);
//            }
            // TODO: tedy az sem


/*            fft.realForward(normalizedSongPart);
            double[] fullArr = convertGivenRealForwardFFTToClassic(normalizedSongPart);
            fft.realInverse(normalizedSongPart, false);
            System.out.println("Original arr length:\t" + normalizedSongPart.length + "\tNew arr length:\t" + fullArr.length);
            fft.complexInverse(fullArr, true);
            int index = 0;
            for(int k = 0; k < fullArr.length; k = k + 2) {     // TODO: Testing jestli vraci stejny vysledky
                System.out.println("Original:\t" + normalizedSongPart[index] + "\tNew:\t" + fullArr[k]);
                index++;
            }
*/
            FrequencyWithMeasure[] frequenciesWithMeasures = convertImagPartToRealReturnArrWithFrequenciesRealForward(normalizedSongPart, sampleRate, false);
            results[i] = frequenciesWithMeasures;
        }

        return results;
    }



    /**
     * Calculates FFT for audio with multiple channels. For each channels calculates the fft separately, the complex numbers in result
     * are then transformed to real numbers by taking the distances of complex numbers from 0. Then the results of fft of each channel are put together by averaging.
     * At the end connects frequencies with corresponding measures (the averages).
     * @param songParts is 2D array with song parts, 1 array is one channel. First dim are channels.
     * Second dim are the samples (song parts) of channels. The song parts are 1D double labelReferenceArrs, where each element is normalized sample (value between -1 and 1).
     * @param sampleRate is the sample rate of the audio.
     * @return Returns FrequencyWithMeasure[][] where 1 FrequencyWithMeasure[] represents the measures and frequencies of the song part.
     */
    public FrequencyWithMeasure[][] calculateFFTRealForward(NormalizedSongPartWithAverageValueOfSamples[][] songParts, int sampleRate) {
        FrequencyWithMeasure[][] results = new FrequencyWithMeasure[songParts[0].length][];
        double[][] values = new double[songParts.length][];
        for(int k = 0; k < songParts[0].length; k++) {
            for(int i = 0; i < songParts.length; i++) {
                double[] normalizedSongPart = new double[songParts[i][k].songPart.length];
                DoubleFFT_1D fft = new DoubleFFT_1D(normalizedSongPart.length);
                for(int j = 0; j < normalizedSongPart.length; j++) {
                    normalizedSongPart[j] = songParts[i][k].songPart[j];
                }
                fft.realForward(normalizedSongPart);
                values[i] = convertResultsOfFFTToRealRealForward(normalizedSongPart);
            }

            for(int f = 0; f < values[0].length; f++) {
                for(int l = 1; l < values.length; ) {
                    values[0][f] += values[l][f];
                }
                values[0][f] = values[0][f] / values.length;
            }
            FrequencyWithMeasure[] frequenciesWithMeasures = returnArrWithFrequenciesRealForward(values[0], sampleRate, false);
            results[k] = frequenciesWithMeasures;
        }

        return results;
    }


    /**
     * Calculates fft for each song part, the complex numbers in result are then transformed to real numbers by taking the distances of complex numbers from 0.
     * Then connects frequencies with corresponding measures (the distances from 0).
     * @param songParts is 1D array with song parts. The song parts are 1D double labelReferenceArrs, where each element is normalized sample (value between -1 and 1).
     * @param sampleRate is the sample rate of the audio.
     * @return Returns FrequencyWithMeasure[][] where 1 FrequencyWithMeasure[] represents the measures and frequencies of the song part.
     * @throws IOException is thrown when some song part is not divisible by 2 (that way it can't store complex numbers)
     */
    public FrequencyWithMeasure[][] calculateFFTRealForward(NormalizedSongPartWithAverageValueOfSamples[] songParts, int sampleRate) throws IOException {
        FrequencyWithMeasure[][] results = new FrequencyWithMeasure[songParts.length][];		// because real and imag part

        for(int i = 0; i < songParts.length; i++) {
            double[] normalizedSongPart = new double[songParts[i].songPart.length];
            DoubleFFT_1D fft = new DoubleFFT_1D(normalizedSongPart.length);
            for(int j = 0; j < normalizedSongPart.length; j++) {
                normalizedSongPart[j] = songParts[i].songPart[j];
            }

            fft.realForward(normalizedSongPart);
            FrequencyWithMeasure[] frequenciesWithMeasures = convertImagPartToRealReturnArrWithFrequenciesRealForward(normalizedSongPart, sampleRate, false);
            results[i] = frequenciesWithMeasures;
        }

        return results;
    }


    // TODO: Pro tuhle metodu kdyztak napsat IFFT variantu, jestli to bude potreba ale rekl bych ze ne
    /**
     * Input samples are expected in mono (1 channel).
     * Memory efficient variant, we convert byte samples right in to the FFT output.
     * If last part of samples doesn't fill whole window then return that part is not added to the output!!!
     * @param samples is the array with input samples. Audio is expected to be mono.
     * @param sampleSize is the size of 1 sample in bytes.
     * @param sampleSizeInBits is the size of 1 sample in bits
     * @param sampleRate is the sample rate of input samples.
     * @param windowSize is the size of windows to perform FFT
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns frequencies with measures 2D array.
     * @throws IOException if sampleSize is <=0 or >4
     */
    public static FrequencyWithMeasure[][] calculateFFTRealForward(byte[] samples, int sampleSize, int sampleSizeInBits,
                                                                   int sampleRate, int windowSize,
                                                                   boolean isBigEndian, boolean isSigned) throws IOException {
        int windowSizeInBytes = windowSize * sampleSize;
        FrequencyWithMeasure[][] results = new FrequencyWithMeasure[samples.length / windowSizeInBytes][];
        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);            // TODO: jestli beru spravnou velikost

        for(int index = 0, i = 0; i < results.length; i++, index += windowSizeInBytes) {
//            System.out.println(i + ":" + results.length + ":" + samples.length + ":" + windowSizeInBytes + ":" + windowSize);
            double[] arr = normalizeToDoubles(samples, sampleSize, sampleSizeInBits,
                                              index, windowSize, isBigEndian, isSigned);
            fft.realForward(arr);
            FrequencyWithMeasure[] frequenciesWithMeasures = convertImagPartToRealReturnArrWithFrequenciesRealForward(arr,
                sampleRate, false);
            results[i] = frequenciesWithMeasures;
        }

        return results;
    }



    // TODO: dava smysl mit pro tuhle metodu a tu pod ni (tu s measurama co ale asi neni potreba) aby tam byl i shiftSize ... kdyz budu delat okna co se prekryvaji
    // TODO: A taky dava smysl aby to dostavalo v parametru koeficienty okna ktery si na to aplikuje (treba hanning atd)
    /**
     * Input samples are expected in mono (1 channel).
     * Memory efficient variant, we convert byte samples right in to the FFT output.
     * If last part of samples doesn't fill whole window then return that part is not added to the output!!!
     * @param samples is the array with input samples. Audio is expected to be mono.
     * @param sampleSize is the size of 1 sample in bytes.
     * @param sampleSizeInBits is the size of 1 sample in bits
     * @param windowSize is the size of windows to perform FFT
     *                        * @param startIndex is the index where should the fft calculations start.
     *      * @param endIndex is the where should the fft calculations end.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns the fft results of windowSize in 2D double array ... that means we lose information about frequencies.
     * @throws IOException if sampleSize is <=0 or >4
     */
    public static double[][] calculateFFTRealForward(byte[] samples, int sampleSize, int sampleSizeInBits,
                                                     int windowSize, int startIndex, int endIndex,
                                                     boolean isBigEndian, boolean isSigned,
                                                     DoubleFFT_1D fft) throws IOException {
        int windowSizeInBytes = windowSize * sampleSize;
        int len = endIndex - startIndex;
        double[][] results = new double[len / windowSizeInBytes][];
// TODO:        System.out.println("len / windowSizeInBytes\t" + len / (double)windowSizeInBytes);

        for(int index = startIndex, i = 0; i < results.length; i++) {
            double[] arr = new double[windowSize];
// TODO:            System.out.println("calculateFFTRealForward:" + index + "\t" + windowSize + "\t" + sampleSize);
            index = normalizeToDoubles(samples, arr, sampleSize, sampleSizeInBits, index, isBigEndian, isSigned);
            for(int l = 0; l < arr.length; l++) {
// TODO:                System.out.println(l + "\t" + arr[l]);
            }
            fft.realForward(arr);
//            arr = Program.convertResultsOfFFTToRealRealForward(arr);    // TODO:
            results[i] = arr;
        }

        return results;
    }


    // TODO: tahle metoda asi nebude potreba ... staci mi spocitat ty fft nepotebuju measury
    /**
     * Input samples are expected in mono (1 channel).
     * Memory efficient variant, we convert byte samples right in to the FFT output.
     * If last part of samples doesn't fill whole window then return that part is not added to the output!!!
     * @param samples is the array with input samples. Audio is expected to be mono.
     * @param sampleSize is the size of 1 sample in bytes.
     * @param sampleSizeInBits is the size of 1 sample in bits
     * @param windowSize is the size of windows to perform FFT
     * @param startIndex is the index where should the fft calculations start.
     * @param endIndex is the where should the fft calculations end.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns only measures in 2D double array ... that means we lose information about frequencies.
     * @throws IOException if sampleSize is <=0 or >4
     */
    public static double[][] calculateFFTRealForwardOnlyMeasures(byte[] samples, int sampleSize, int sampleSizeInBits,
                                                                 int windowSize, int startIndex, int endIndex,
                                                                 boolean isBigEndian,
                                                                 boolean isSigned) throws IOException {
        int windowSizeInBytes = windowSize * sampleSize;
        int len = endIndex - startIndex;
        double[][] results = new double[len / windowSizeInBytes][];
        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);            // TODO: jestli beru spravnou velikost
        double[] arr = new double[windowSize];

        for(int index = startIndex, i = 0; i < results.length; i++) {
            index = normalizeToDoubles(samples, arr, sampleSize, sampleSizeInBits, index, isBigEndian, isSigned);
            fft.realForward(arr);
            results[i] = convertResultsOfFFTToRealRealForward(arr);
        }

        return results;
    }

    // This method is used only in testing, because it is kind of useless, since we don't have information about frequencies.
    public static double[] getNHighestMeasures(double[] arr, int n) {
        if(n > arr.length) {
            return null;
        }
        double[] result = new double[n];
        Arrays.sort(arr);
        int index = arr.length - 1;
        for(int i = 0; i < result.length; i++, index--) {
            result[i] = arr[index];
        }

        return result;
    }


    /**
     * Takes n frequencies with highest measures. The result is in descending order - first is the frequency with highest measure
     * and then is followed by frequency with lower measure, etc.
     * @param arr is 1D array where each elements contains the frequency and measure of that frequency.
     * @param n is the number of frequencies to be returned.
     * @param arrIsSorted is true if the array arr is already sorted, otherwise is false, so it needs to be sorted.
     * @return Returns n frequencies with highest measure. In descending order.
     * @throws IOException is thrown if the n is larger than size of given array.
     */
    public static FrequencyWithMeasure[] takeNFreqsWithHighestMeasure(FrequencyWithMeasure[] arr, int n,
                                                                      boolean arrIsSorted) throws IOException {
        if(n > arr.length) {
            throw new IOException();
        }
        if(!arrIsSorted) {
            Arrays.sort(arr);
        }

        FrequencyWithMeasure[] result = new FrequencyWithMeasure[n];

        int index = arr.length - 1;
        for(int i = 0; i < result.length; i++) {
            result[i] = arr[index];
            index--;
        }

        return result;
    }


    /**
     * For all song parts: Takes n frequencies with highest measures of that song part. The result is in song part descending order - first is the frequency with highest measure
     * and then is followed by frequency with lower measure, etc.
     * @param arr is 2D array, where each array represents the frequencies and measures for 1 song part.
     * @param n is the number of frequencies to be returned for 1 song part.
     * @param arrIsSorted is true if ale the labelReferenceArrs in arr are already sorted, otherwise is false, so they need to be sorted.
     * @return Returns for all song parts n frequencies with highest measures (The measures for each song part are in ascending order).
     * @throws IOException is thrown if the n is larger than size of given array.
     */
    public FrequencyWithMeasure[][] takeNFreqsWithHighestMeasureForAllSongParts(FrequencyWithMeasure[][] arr, int n, boolean arrIsSorted) throws IOException {
        FrequencyWithMeasure[][] result = new FrequencyWithMeasure[arr.length][n];
        for(int i = 0; i < arr.length; i++) {
            result[i] = takeNFreqsWithHighestMeasure(arr[i], n, arrIsSorted);
        }

        return result;
    }


    /**
     * For all song parts: Takes n frequencies with highest measures of that song part. The result is in song part ascending order - first is the frequency with lowest measure
     * and then is followed by frequency with higher measure, etc.
     * @param arr is 2D array, where each array represents the frequencies and measures for 1 song part.
     * @param n is the number of frequencies to be returned for 1 song part.
     * @param arrIsSorted is true if ale the labelReferenceArrs in arr are already sorted, otherwise is false, so they need to be sorted.
     * @return Returns for all song parts n frequencies with highest measures (The measures for each song part are in ascending order).
     * @throws IOException is thrown if the n is larger than size of given array.
     */
    public FrequencyWithMeasure[][] takeNFreqsWithLowestMeasureForAllSongParts(FrequencyWithMeasure[][] arr, int n, boolean arrIsSorted) throws IOException {
        FrequencyWithMeasure[][] result = new FrequencyWithMeasure[arr.length][n];
        for(int i = 0; i < arr.length; i++) {
            result[i] = takeNFreqsWithLowestMeasure(arr[i], n, arrIsSorted);
        }

        return result;
    }


    /**
     * Takes n frequencies with lowest measures. The result is in ascending order - first is the frequency with lowest measure
     * and then is followed by frequency with higher measure, etc.
     * @param arr is 1D array where each elements contains the frequency and measure of that frequency.
     * @param n is the number of frequencies to be returned.
     * @param arrIsSorted is true if the array arr is already sorted, otherwise is false, so it needs to be sorted.
     * @return Returns n frequencies with lowest measures. In ascending order.
     * @throws IOException is thrown if the n is larger than size of given array.
     */
    public FrequencyWithMeasure[] takeNFreqsWithLowestMeasure(FrequencyWithMeasure[] arr, int n, boolean arrIsSorted) throws IOException {
        if(n > arr.length) {
            throw new IOException();
        }
        if(!arrIsSorted) {
            Arrays.sort(arr);
        }

        FrequencyWithMeasure[] result = new FrequencyWithMeasure[n];
        for(int i = 0; i < result.length; i++) {
            result[i] = arr[i];
        }

        return result;
    }



    // TODO: verze se signed/unsigned
    // TODO: Taky zbytecne 2 vetve - ale zase kvuli optimalizaci pro ted necham, nevim jestli se to dobre prelozi ... TODO: Zkontrolovat
    /**
     * Converts byte array to int samples of size sampleSize.
     * @param byteSamples are the samples in 1D byte array.
     * @param sampleSize is the size of one sample in bytes.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned tells if the converted samples are signed or unsigned
     * @return Returns the samples as 1D array of ints.
     * @throws IOException is thrown when the sample size is invalid.
     */
    public static int[] convertBytesToSamples(byte[] byteSamples, int sampleSize, boolean isBigEndian, boolean isSigned) throws IOException {
        int[] result = new int[byteSamples.length / sampleSize];

        int arrIndex;
        int mask = calculateMask(sampleSize);
        if(isBigEndian) {
            arrIndex = 0;
            for(int i = 0; i < result.length; i++) {
                result[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, isSigned);
                arrIndex = arrIndex + sampleSize;
            }
        } else {
            arrIndex = 0;
            for(int i = 0; i < result.length; i++) {
                result[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, isSigned);
                arrIndex = arrIndex + sampleSize;
            }
        }

        return result;
    }

//    // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Neresim Signed, Unsigned
//    /**
//     * Converts byte array to int samples of size sampleSize.
//     * @param byteSamples are the samples in 1D byte array.
//     * @param sampleSize is the size of one sample in bytes.
//     * @param isBigEndian is true if the samples are in big endian, false otherwise.
//     * @return Returns the samples as 1D array of ints.
//     * @throws IOException is thrown when the sample size is invalid.
//     */
//    public static int[] convertBytesToSamples(byte[] byteSamples, int sampleSize, boolean isBigEndian) throws IOException {
//        int[] result = new int[byteSamples.length / sampleSize];
//
//        int arrIndex;
//        int mask = calculateMask(sampleSize);
//        int inverseMask = calculateInverseMaskFromMask(mask);
//        if(isBigEndian) {
//            arrIndex = 0;
//            for(int i = 0; i < result.length; i++) {
//                result[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, inverseMask, arrIndex);
//                arrIndex = arrIndex + sampleSize;
//            }
//        } else {
//            arrIndex = 0;
//            for(int i = 0; i < result.length; i++) {
//                result[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, inverseMask, arrIndex);
//                arrIndex = arrIndex + sampleSize;
//            }
//        }
//
//        return result;
//    }


    /**
     * Converts byte array to int samples, which are then normalized to double number between -1 and 1 which are returned.
     * @param byteSamples are the samples in 1D byte array.
     * @param sampleSize is the size of one sample.
     * @param sampleSizeInBits is the size of one sample in ibts.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns normalized samples in form of double[]
     * @throws IOException is thrown when the sample size is invalid.
     */
    public static double[] normalizeToDoubles(byte[] byteSamples, int sampleSize, int sampleSizeInBits,
                                              boolean isBigEndian, boolean isSigned) throws IOException {
        double[] result = new double[byteSamples.length / sampleSize];
        int maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);
        int arrIndex = 0;
        int mask = calculateMask(sampleSize);

        if(isSigned) {
            if(isBigEndian) {
                for(int i = 0; i < result.length; i++) {
                    result[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    result[i] = result[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = 0; i < result.length; i++) {
                    result[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    result[i] = result[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            if(isBigEndian) {
                for(int i = 0; i < result.length; i++) {
                    result[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    result[i] = result[i] - convertUnsignedToSigned;
                    result[i] = result[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = 0; i < result.length; i++) {
                    result[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    result[i] = result[i] - convertUnsignedToSigned;
                    result[i] = result[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        }

        return result;
    }


    /**
     * Converts byte array to int samples, which are then normalized to double number between -1 and 1 and put in outputArr.
     * Ends when outputArr is full
     * @param byteSamples are the samples in 1D byte array.
     * @param outputArr is the array which will contain the normalized samples
     * @param sampleSize is the size of one sample.
     * @param sampleSizeInBits is the size of one sample in bits.
     * @param arrIndex is index in byteSamples array where is the first sample to be normalized.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns the current index in byteSamples after performing normalization
     * @throws IOException is thrown when the sample size is invalid.
     */
    public static int normalizeToDoubles(byte[] byteSamples, double[] outputArr, int sampleSize,
                                         int sampleSizeInBits, int arrIndex,
                                         boolean isBigEndian, boolean isSigned) throws IOException {
        return normalizeToDoubles(byteSamples, outputArr, sampleSize, sampleSizeInBits,
                                  arrIndex, 0, outputArr.length, isBigEndian, isSigned);
    }


    /**
     * Converts byte array to int samples, which are then normalized to double number between -1 and 1 and put in outputArr.
     * Ends when outputArr is full
     * @param byteSamples are the samples in 1D byte array.
     * @param outputArr is the array which will contain the normalized samples
     * @param sampleSize is the size of one sample.
     * @param sampleSizeInBits is the size of one sample in bits.
     * @param arrIndex is index in byteSamples array where is the first sample to be normalized.
     * @param outputStartIndex is the index to which we should start give output values
     * @param outputLen is the length of the output - how many samples should be taken
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns the current index in byteSamples after performing normalization
     * @throws IOException is thrown when the sample size is invalid.
     */
    public static int normalizeToDoubles(byte[] byteSamples, double[] outputArr, int sampleSize,
                                         int sampleSizeInBits, int arrIndex, int outputStartIndex, int outputLen,
                                         boolean isBigEndian, boolean isSigned) throws IOException {
        int maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);
        int mask = calculateMask(sampleSize);
        int outputEndIndex = outputStartIndex + outputLen;

        if(isSigned) {
            if(isBigEndian) {
                for(int i = outputStartIndex; i < outputEndIndex; i++) {
                    outputArr[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = outputStartIndex; i < outputEndIndex; i++) {
                    outputArr[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            if(isBigEndian) {
                for(int i = outputStartIndex; i < outputEndIndex; i++) {
                    outputArr[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    outputArr[i] = outputArr[i] - convertUnsignedToSigned;
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = outputStartIndex; i < outputEndIndex; i++) {
                    outputArr[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    outputArr[i] = outputArr[i] - convertUnsignedToSigned;
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        }

        return arrIndex;
    }


    /**
     * Converts byte array to int samples, which are then normalized to double number between -1 and 1 and put in outputArr.
     * Ends when outputArr is full
     * @param byteSamples are the samples in 1D byte array.
     * @param sampleSize is the size of one sample.
     * @param sampleSizeInBits is the size of one sample in bits.
     * @param arrIndex is index in byteSamples array where is the first sample to be normalized.
     * @param windowSizeInSamples is the size of the double array.
     * @param isBigEndian is true if the samples are in big endian, false otherwise.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns normalized samples in form of double[]
     * @throws IOException is thrown when the sample size is invalid.
     */
    public static double[] normalizeToDoubles(byte[] byteSamples, int sampleSize,
                                              int sampleSizeInBits, int arrIndex, int windowSizeInSamples,
                                              boolean isBigEndian, boolean isSigned) throws IOException {
        double[] outputArr = new double[windowSizeInSamples];
        normalizeToDoubles(byteSamples, outputArr, sampleSize, sampleSizeInBits, arrIndex, isBigEndian, isSigned);

        // TODO: Zakomentovano protoze to bylo nahrazeno volanim referencni varianty
/*
        int maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);
        int mask = calculateMask(sampleSize);

        if(isSigned) {
            if(isBigEndian) {
                for(int i = 0; i < outputArr.length; i++) {
                    outputArr[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = 0; i < outputArr.length; i++) {
                    outputArr[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, true);
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            if(isBigEndian) {
                for(int i = 0; i < outputArr.length; i++) {
                    outputArr[i] = convertBytesToIntBigEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    outputArr[i] = outputArr[i] - convertUnsignedToSigned;
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            } else {
                for(int i = 0; i < outputArr.length; i++) {
                    outputArr[i] = convertBytesToIntLittleEndian(byteSamples, sampleSize, mask, arrIndex, false);
                    outputArr[i] = outputArr[i] - convertUnsignedToSigned;
                    outputArr[i] = outputArr[i] / maxAbsoluteValue;
                    arrIndex = arrIndex + sampleSize;
                }
            }
        }
*/

        return outputArr;
    }


    public static double normalizeToDoubleBetweenMinusOneAndOne(int sample, int maxAbsoluteValue, boolean isSigned) {
        double result;

        if (isSigned) {
            result = sample / (double) maxAbsoluteValue;
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            result = sample - convertUnsignedToSigned;
            result = result / (double) maxAbsoluteValue;
        }

        return result;
    }

     /**
     Takes int[] which represents samples converts them to double[] which are normalized samples (values between -1 and 1).
     * @param samples is 1D int array with samples.
     * @param sampleSizeInBits is the size of 1 sample in bits in the samples array.
     * @param isSigned is true if the samples are signed, is false otherwise.
     * @return Returns normalized samples in form of double[]
     */
    public static double[] normalizeToDoubles(int[] samples, int sampleSizeInBits, boolean isSigned) {
        double[] result = new double[samples.length];
//        System.out.println("sample size in bits:\t" + sampleSizeInBits);          // TODO: remove debug prints
        int maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);
//        System.out.println("Max absolute value:\t" + maxAbsoluteValue);
        if(isSigned) {
            for (int i = 0; i < result.length; i++) {
                result[i] = samples[i] / (double)maxAbsoluteValue;
//                System.out.println("Original sample:\t" + samples[i] + "\tnormalized sample:\t" + result[i]);
            }
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            for (int i = 0; i < result.length; i++) {
                result[i] = samples[i] - convertUnsignedToSigned;
                result[i] = result[i] / (double)maxAbsoluteValue;
            }
        }

        return result;
    }



    // TODO: i think that converting to int before dividing may help for receiving better results
    /**
     * Takes double[] which represents samples (or for example average value) - First converts them to int (since the double values are in these case expected to be ints). Performs normalization on these samples and returns them.
     * This class exists for optimalization (saving copying and creating array).
     * @param samples is 1D int array with samples.
     * @param sampleSizeInBits is the size of 1 sample in bits in the samples array.
     * @param isSigned is true if the samples are signed, is false otherwise.
     */
    public static void normalizeToDoubles(double[] samples, int sampleSizeInBits, boolean isSigned) {
//        System.out.println("sample size in bits:\t" + sampleSizeInBits);          // TODO: remove debug prints
        int maxAbsoluteValue = getMaxAbsoluteValueSigned(sampleSizeInBits);
//        System.out.println("Max absolute value:\t" + maxAbsoluteValue);
        if(isSigned) {
            for (int i = 0; i < samples.length; i++) {
                samples[i] = (int)samples[i];                               // TODO: maybe remove
                samples[i] = samples[i] / maxAbsoluteValue;
//                System.out.println("Original sample:\t" + samples[i] + "\tnormalized sample:\t" + result[i]);
            }
        } else {
            int convertUnsignedToSigned = maxAbsoluteValue;
            for (int i = 0; i < samples.length; i++) {
                samples[i] = (int)samples[i];
                samples[i] = samples[i] - convertUnsignedToSigned;
                samples[i] = samples[i] / maxAbsoluteValue;
            }
        }
    }


    /**
     * Converts sample in byte array of size sampleSize to int. With given endianity.
     * @param bytes is byte array with sample(s).
     * @param mask is mask used to mask certain bytes, so that the value in int is same as the value in the sample.
     * @param isBigEndian says if the given array is in big endian.
     * @param isSigned says if the given array has signed samples.
     * @return Returns the sample starting at index arrIndex.
     */
    public static int convertBytesToInt(byte[] bytes, int mask, boolean isBigEndian, boolean isSigned) {
        return convertBytesToInt(bytes, bytes.length, mask, 0, isBigEndian, isSigned);
    }
        // TODO: tahle metoda je nove pridana
    // TODO: Ted jsem tam dopsal isSigned, ale to by melo byt ... i v tom prevodu mona by to melo byt
    /**
     * Converts sample starting at index arrIndex in byte array bytes of size sampleSize to int. With given endianity.
     * @param bytes is byte array with sample(s).
     * @param sampleSize is the size of 1 sample.
     * @param mask is mask used to mask certain bytes, so that the value in int is same as the value in the sample.
     * @param arrIndex is index in the array, where starts the sample to be converted
     * @param isBigEndian says if the given array is in big endian.
     * @param isSigned says if the given array has signed samples.
     * @return Returns the sample starting at index arrIndex.
     */
    public static int convertBytesToInt(byte[] bytes, int sampleSize, int mask, int arrIndex, boolean isBigEndian, boolean isSigned) {
        if(isBigEndian) {
            return convertBytesToIntBigEndian(bytes, sampleSize, mask, arrIndex, isSigned);
        }
        else {
            return convertBytesToIntLittleEndian(bytes, sampleSize, mask, arrIndex, isSigned);
        }
    }

    // TODO: maybe it is better performace wise to write it all explicitly
    // TODO: in switch for each sample size (1..4) then having it in general if
    /**
     * The sample is expected to be in big endian audioFormat.
     * Converts sample starting at index arrIndex in byte array bytes of size sampleSize to int.
     * @param bytes is byte array with sample(s).
     * @param sampleSize is the size of 1 sample.
     * @param mask is mask used to mask certain bytes, so that the value in int is same as the value in the sample.
     * @param arrIndex is index in the array, where starts the sample to be converted
     * @param isSigned tells if the converted sample is signed or unsigned
     * @return Returns the sample starting at index arrIndex.
     */
    public static int convertBytesToIntBigEndian(byte[] bytes, int sampleSize, int mask, int arrIndex, boolean isSigned) {
        int result = 0;
        arrIndex = arrIndex + sampleSize - 1;
        for (int i = 0; i < sampleSize; i++) {
            result = result | (((int) bytes[arrIndex] & 0x00_00_00_FF) << (i * 8));
            arrIndex--;
        }

        // TODO: old variant with if
//        if(isSigned) {
//            if (((result >> (((sampleSize - 1) * 8) + 7)) & 1) == 1) {  //If true, then copy sign bit
//                result = result | mask;
//            }
//        }

        // TODO: New variant without if
        if (isSigned) {
            int sign = (result >> (((sampleSize - 1) * 8) + 7)) & 1;  //If == 1 then there is sign bit, if == 0 then no sign bit
            mask *= sign;       // mask will be 0 if the number >= 0 (no sign bit); mask == mask if sign == 1
            result = result | mask;
        }

        return result;
    }



    // TODO: maybe it is better performace wise to write it all explicitly
    // TODO: in switch for each sample size (1..4) then having it in general if
    /**
     * The sample is expected to be in little endian audioFormat.
     * Converts sample starting at index arrIndex in byte array bytes of size sampleSize to int.
     * @param bytes is byte array with sample(s).
     * @param sampleSize is the size of 1 sample.
     * @param mask is mask used to mask certain bytes, so that the value in int is same as the value in the sample.
     * @param arrIndex is index in the array, where starts the sample to be converted
     * @param isSigned tells if the converted sample is signed or unsigned
     * @return Returns the sample starting at index arrIndex.
     */
    public static int convertBytesToIntLittleEndian(byte[] bytes, int sampleSize, int mask, int arrIndex, boolean isSigned) {
        int result = 0;
        for (int i = 0; i < sampleSize; i++) {
            result = result | (((int) bytes[arrIndex] & 0x00_00_00_FF) << (i * 8));
            arrIndex++;
        }
// TODO: old variant with if
//        if (isSigned) {
//            if (((result >> (((sampleSize - 1) * 8) + 7)) & 1) == 1) {  //If true, then copy sign bit
//                result = result | mask;
//            }
//        }

        // TODO: New variant without if
        if (isSigned) {
            int sign = (result >> (((sampleSize - 1) * 8) + 7)) & 1;  //If == 1 then there is sign bit, if == 0 then no sign bit
            mask *= sign;       // mask will be 0 if the number >= 0 (no sign bit); mask == mask if sign == 1
            result = result | mask;
        }

        return result;
    }


    /**
     * Expects the double to be between -1 and 1
     * @param sampleDouble
     * @param maxAbsoluteValue
     * @param isSigned
     * @return
     */
    public static int convertDoubleToInt(double sampleDouble, int maxAbsoluteValue, boolean isSigned) {
        int sampleInt = (int)(sampleDouble * maxAbsoluteValue); // TODO: Maybe Math.ceil or something more advanced will have better result
        if(!isSigned) {
            sampleInt += maxAbsoluteValue;
        }

        return sampleInt;
    }

    public static byte[] convertDoubleToByteArr(double sampleDouble, int sampleSize, int maxAbsoluteValue,
                                                boolean isBigEndian, boolean isSigned) {
        byte[] resultArr = new byte[sampleSize];
        convertDoubleToByteArr(sampleDouble, sampleSize, maxAbsoluteValue, isBigEndian,  isSigned,0, resultArr);
        return resultArr;
    }

    public static void convertDoubleToByteArr(double sampleDouble, int sampleSize, int maxAbsoluteValue,
                                              boolean isBigEndian, boolean isSigned, int startIndex, byte[] resultArr) {
        int sampleInt = convertDoubleToInt(sampleDouble, maxAbsoluteValue, isSigned);
        convertIntToByteArr(resultArr, sampleInt, sampleSize, startIndex, isBigEndian);
    }

///////////

    public static int[] convertDoubleArrToIntArr(double[] doubleArr, int maxAbsoluteValue, boolean isSigned) {
        int[] intArr = new int[doubleArr.length];

        for(int i = 0; i < doubleArr.length; i++) {
            intArr[i] = convertDoubleToInt(doubleArr[i], maxAbsoluteValue, isSigned);
        }

        return intArr;
    }

    // TODO: Not sure about effectivity, maybe I could I just all the more advanced variant.
    public static void convertDoubleArrToIntArr(double[] doubleArr, int[] intArr, int maxAbsoluteValue, boolean isSigned) {
        for(int i = 0; i < doubleArr.length; i++) {
            intArr[i] = convertDoubleToInt(doubleArr[i], maxAbsoluteValue, isSigned);
        }
    }


    public static void convertDoubleArrToIntArr(double[] doubleArr, int[] intArr, int doubleStartInd, int intStartInd,
                                                int len, int maxAbsoluteValue, boolean isSigned) {
        for(int i = 0; i < len; i++, doubleStartInd++, intStartInd++) {
            intArr[intStartInd] = convertDoubleToInt(doubleArr[doubleStartInd], maxAbsoluteValue, isSigned);
        }
    }


    public static void convertDoubleArrToByteArr(double[] doubleArr, byte[] byteArr, int doubleStartInd,
                                                 int byteStartInd, int len, int sampleSize,
                                                 int maxAbsoluteValue, boolean isBigEndian, boolean isSigned) {
        for(int i = 0; i < len; i++, doubleStartInd++, byteStartInd += sampleSize) {
            convertDoubleToByteArr(doubleArr[doubleStartInd], sampleSize, maxAbsoluteValue, isBigEndian, isSigned, byteStartInd, byteArr);
        }
    }

    public static byte[] convertDoubleArrToByteArr(double[] doubleArr, int doubleStartInd, int len, int sampleSize,
                                                 int maxAbsoluteValue, boolean isBigEndian, boolean isSigned) {
        byte[] arr = new byte[len * sampleSize];
        convertDoubleArrToByteArr(doubleArr, arr, doubleStartInd, 0, len, sampleSize,
        maxAbsoluteValue, isBigEndian, isSigned);
        return arr;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// TODO: Nove pridany veci, ktery se hodi - ziskani informaci o skladbe.
    /**
     * Returns all operations from Aggregations performed on given array.
     * @param samples is the given array with samples.
     * @param sampleSize is the size of 1 sample.
     * @param isBigEndian true if the given samples are in big endian, false if in little endian.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @return Returns array with mods in Aggregations order (given by calling Aggregations.values()).
     * @throws IOException is thrown when the sample size is <= 0 or > 4
     */
    public static double[] calculateAllAggregations(byte[] samples, int sampleSize, boolean isBigEndian, boolean isSigned) throws IOException {
        double[] arr = new double[Aggregations.values().length];
        int index = 0;
        for (Aggregations agg : Aggregations.values()) {
            arr[index] = performAggregation(samples, sampleSize, isBigEndian, isSigned, agg);
            index++;
        }
        return arr;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// TODO: Nove pridany veci, ktery se hodi - prevody sample rate

    public void convertSampleRate(int newSampleRate) throws IOException {
        this.song = convertSampleRate(this.song, this.sampleSizeInBytes, this.frameSize,
                this.numberOfChannels, this.sampleRate, newSampleRate,
                this.isBigEndian, this.isSigned, false);
        this.sampleRate = newSampleRate;
    }

    /**
     * Input array isn't changed.
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate"
     * @param samples          is input array with samples
     * @param sampleSize       is the size of one sample in bytes
     * @param frameSize        is the size of one frame in bytes
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned         is boolean variable, which is true if the samples are signed, false if unsigned.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalit (<0 or >4)
     */
    public static byte[] convertSampleRate(byte[] samples, int sampleSize, int frameSize,
                                           int numberOfChannels, int oldSampleRate, int newSampleRate,
                                           boolean isBigEndian, boolean isSigned,
                                           boolean canChangeInputArr) throws IOException {
        byte[] retArr = null;
        if (oldSampleRate > newSampleRate) {
//            retArr = convertToLowerSampleRate(samples, sampleSize, numberOfChannels, oldSampleRate, newSampleRate, isBigEndian, isSigned);
            retArr = convertToLowerSampleRateByUpSampling(samples, sampleSize, frameSize, numberOfChannels,
                    oldSampleRate, newSampleRate, isBigEndian, isSigned, canChangeInputArr);
        }
        else if (oldSampleRate < newSampleRate) {
            retArr = convertToHigherSampleRate(samples, sampleSize, numberOfChannels, oldSampleRate, newSampleRate, isBigEndian, isSigned);
        }
        else {
            retArr = samples;        // The sampling rates are the same
        }
        return retArr;
    }


    /**
     * Input array isn't changed.
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate < newSampleRate
     * @param samples          is input array with samples
     * @param sampleSize       is the size of one sample in bytes
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned         is boolean variable, which is true if the samples are signed, false if unsigned.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalid (<0 or >4)
     */
    private static byte[] convertToHigherSampleRate(byte[] samples, int sampleSize, int numberOfChannels, int oldSampleRate,
                                                    int newSampleRate, boolean isBigEndian, boolean isSigned) throws IOException {
        return convertSampleRateImmediateVersion(samples, sampleSize, numberOfChannels, oldSampleRate, newSampleRate, isBigEndian, isSigned);
    }


    public static double[] convertSampleRate(double[] samples, int numberOfChannels, int oldSampleRate,
                                             int newSampleRate, boolean canChangeInputArr) throws IOException {
        double[] retArr;
        if (oldSampleRate > newSampleRate) {
            //retArr = convertToLowerSampleRateByUpSampling(samples, numberOfChannels, oldSampleRate, newSampleRate, canChangeInputArr);
            retArr = convertToLowerSampleRateByImmediate(samples, numberOfChannels, oldSampleRate, newSampleRate, canChangeInputArr);
        }
        else if (oldSampleRate < newSampleRate) {
            retArr = convertToHigherSampleRate(samples, numberOfChannels, oldSampleRate, newSampleRate);
        }
        else {
            retArr = samples;        // The sampling rates are the same
        }
        return retArr;
    }

    /**
     * Input array isn't changed.
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate < newSampleRate
     * @param samples          is input array with samples
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalid (<0 or >4)
     */
    private static double[] convertToHigherSampleRate(double[] samples, int numberOfChannels, int oldSampleRate,
                                                    int newSampleRate) throws IOException {
        return convertSampleRateImmediateVersion(samples, numberOfChannels, oldSampleRate, newSampleRate);
    }


    /**
     * Expects the samples to be filtered if converting to lower sample rate
     * @param samples
     * @param sampleSize
     * @param numberOfChannels
     * @param oldSampleRate
     * @param newSampleRate
     * @param isBigEndian
     * @param isSigned
     * @return
     * @throws IOException
     */
    private static byte[] convertSampleRateImmediateVersion(byte[] samples, int sampleSize, int numberOfChannels,
                                                            int oldSampleRate, int newSampleRate,
                                                            boolean isBigEndian, boolean isSigned) throws IOException {
        int frameSize = numberOfChannels * sampleSize;
        if (samples == null || samples.length <= frameSize) {
            return samples;
        }
        double ratio = ((double) oldSampleRate) / newSampleRate;
        ArrayList<Byte> retList = new ArrayList<>();
        int mask = calculateMask(sampleSize);
/*
		int secs = samples.length / oldSampleRate;
		if(samples.length % oldSampleRate != 0) {	// If the last chunk of data doesn't represent whole second
			secs++;									// We just add 1 more second to the new data
		}

		byte[] retArr = new byte[(secs) * newSampleRate];
		for(int i = 0; i < samples.length; i++) {
			for(int j = 0; j < )
		}
*/

        double currRatio = 0;
        int[][] currentSamples = new int[numberOfChannels][2];  // for each channel we will have left and right sample
        int bytesNeededToInitArr = currentSamples.length * currentSamples[0].length * sampleSize;
        int index = 0;
//        for (int j = 0; j < 2; j++) {
//            for (int i = 0; i < numberOfChannels; i++) {
//                currentSamples[i][j] = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
//                index += sampleSize;
//            }
//        }
        index = setLeftAndRightSamples(currentSamples, samples, sampleSize, numberOfChannels, mask, index, isBigEndian, isSigned);
        int val = 0;
        byte[] valByte = new byte[sampleSize];
        while(index < samples.length || currRatio+ratio <= 1) {      // The second part of or is for case when we are working with the last samples
            for(int j = 0; j < currentSamples.length; j++) {
                val = (int) (currentSamples[j][0] * (1 - currRatio) + currentSamples[j][1] * currRatio);
                convertIntToByteArr(valByte, val, isBigEndian);
// TODO:                System.out.println("val: " + val);
// TODO:                System.out.println("Index:\t" + index + ":" + j + ":" + currentSamples[j][0] + ":" +
// TODO:                    currentSamples[j][1] + ":" + (val == currentSamples[j][0]) + ":" + currRatio);
                for(int ind = 0; ind < valByte.length; ind++) {
                    retList.add(valByte[ind]);					// TODO: tohle uz chci delat pro ty intovy (pripadne doublovy) hodnoty, rozhodne to nechci delat pro byte hodnoty
                }
            }

// TODO:            System.out.println(TODO++ + ":\t" + index + "\t:\t" + currRatio);
            currRatio += ratio;
//            System.out.println("A" + ":\t" + ratio + ":" + currRatio);
            if(currRatio > 1) {
                if(ratio <= 1) {         // Should be optimized by compiler ... perform the if branching only once
                    for (int j = 0; j < currentSamples.length; j++) {
                        currentSamples[j][0] = currentSamples[j][1];
                        currentSamples[j][1] = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                        index += sampleSize;
                    }
                }
                else {
                    if(currRatio >= 3) {
                        index += ((int)currRatio - 2) * frameSize;
                    }

                    if(index > samples.length - bytesNeededToInitArr) {           // We skipped too much // TODO: not sure if in this case I should add the last right samples
                        break;
                    }
                    index = setLeftAndRightSamples(currentSamples, samples, sampleSize, numberOfChannels, mask, index, isBigEndian, isSigned);
                    // TODO: tohle je v te metode
//                    for (int j = 0; j < currentSamples.length; j++) {
//                        currentSamples[j][0] = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
//                        index += sampleSize;
//                        currentSamples[j][1] = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
//                        index += sampleSize;
//                    }

                }

                currRatio %= 1;
            }
        }									// TODO: nemel by byt problem, staci jen udelat metodu co prevadi byty na normalni hodnoty

        for(int i = 0; i < currentSamples.length; i++) {        // Not sure if I always want to add the last frame, but it is just one last frame so it doesn't matter that much
            convertIntToByteArr(valByte, currentSamples[i][1], isBigEndian);        // currentSamples[i][0] if we want to pass the tests
            for(int ind = 0; ind < valByte.length; ind++) {
                retList.add(valByte[ind]);					// TODO: tohle uz chci delat pro ty intovy (pripadne doublovy) hodnoty, rozhodne to nechci delat pro byte hodnoty
            }
        }


        byte[] retArr = new byte[retList.size()];
        int i = 0;
        for(byte b : retList) {			// TODO: nevim jestli funguje
            retArr[i] = b;
            i++;
        }
        return retArr;
    }

    /**
     * Fills currentSamples array where for each channel we will fill 2 successive samples from the input samples array
     * starting at index index.
     * @param currentSamples   is double int array to be filled
     * @param samples          is input array with samples
     * @param sampleSize       is the size of one sample in bytes
     * @param numberOfChannels is number of channels in samples array.
     * @param mask             is the mask from calculateMask method
     * @param index            is the current index in samples array
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned         is boolean variable, which is true if the samples are signed, false if unsigned.
     * @return Returns the index of the sample behind the last touched sample (so the returned index = index + sampleSize * 2 * numberOfChannels).
     */
    private static int setLeftAndRightSamples(int[][] currentSamples, byte[] samples, int sampleSize, int numberOfChannels,
                                               int mask, int index, boolean isBigEndian, boolean isSigned) {
        // j == 0 means set the left value, j == 1 set the right value ... we first set all the left then all the right
        // - it makes since since this is how the audio data are stored in the array ...
        // in frames (samples 1 for all channels then samples 2 for all channels)
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < numberOfChannels; i++) {
                currentSamples[i][j] = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                index += sampleSize;
            }
        }

        return index;
    }


    /**
     * Expects the samples to be filtered if converting to lower sample rate
     * @param samples
     * @param numberOfChannels
     * @param oldSampleRate
     * @param newSampleRate
     *
     * @return
     * @throws IOException
     */
    private static double[] convertSampleRateImmediateVersion(double[] samples, int numberOfChannels, int oldSampleRate,
                                                              int newSampleRate) {
        double ratio = ((double)newSampleRate) / oldSampleRate;
        int newLen = Program.convertToMultipleUp((int)(samples.length * ratio), numberOfChannels);
        double[] convertedArr = new double[newLen];

        int i = 0;
        double currRatio = 0;
        int convertedArrIndex = 0;
        double indexJump = 1 / ratio * numberOfChannels;
        while(i < samples.length - 1) {
            for(int ch = 0; ch < numberOfChannels; ch++, convertedArrIndex++) {
                convertedArr[convertedArrIndex] = (samples[i] * (1 - currRatio) + samples[i + 1] * currRatio);
            }
//            ProgramTest.debugPrint("CONV", convertedArr[convertedArrIndex], convertedArrIndex, i,
//                    currRatio, samples[i], samples[i + 1]);
            currRatio += indexJump;
            if(currRatio >= 1) {
                i += (int) currRatio;
                currRatio %= 1;
            }
        }

        return convertedArr;
    }



    /**
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate > newSampleRate.
     * <br>
     * The conversion isn't immediate, we convert to some factor "n" of the newSampleRate which is bigger than the oldSampleRate.
     * And the we take every "n"-th sample of the upsampled array.
     * @param samples          is input array with samples
     * @param sampleSize       is the size of one sample in bytes
     * @param frameSize        is the size of one frame in bytes
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned         is boolean variable, which is true if the samples are signed, false if unsigned.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalid (<0 or >4)
     */
    @Deprecated
    private static byte[] convertToLowerSampleRateByUpSampling(byte[] samples, int sampleSize, int frameSize,
                                                               int numberOfChannels, int oldSampleRate, int newSampleRate,
                                                               boolean isBigEndian, boolean isSigned,
                                                               boolean canChangeInputArr) throws IOException {
        int upSampleRate = newSampleRate;
        int upSampleRateRatio = 1;
        while(upSampleRate < oldSampleRate) {
            upSampleRateRatio++;
            upSampleRate += newSampleRate;
        }
        int skipSize = (upSampleRateRatio - 1) * frameSize;       // Skip all the frames to downsample
// If I want the tests to return true        samples = runLowPassFilter(samples, newSampleRate / 2, 64, oldSampleRate);			// Low pass filter for the nyquist frequency of the new frequency
        byte[] upSampledArr = null;
        if(oldSampleRate % newSampleRate == 0) {
            if(canChangeInputArr) {
                upSampledArr = new byte[samples.length];
                System.arraycopy(samples, 0, upSampledArr, 0, upSampledArr.length);
            }
            else {
                upSampledArr = samples;
            }
        }
        else {
            upSampledArr = convertToHigherSampleRate(samples, sampleSize, numberOfChannels, oldSampleRate, upSampleRate, isBigEndian, isSigned);
        }                                                                        // TODO: nemelo by tu byt upSampleRate
        upSampledArr = runLowPassFilter(upSampledArr, newSampleRate / 2, 64, oldSampleRate,
            numberOfChannels, sampleSize, frameSize, isBigEndian, isSigned);			// Low pass filter for the nyquist frequency of the new frequency
        int len = frameSize;        // Get frame count
        //int frameCount = upSampledArr.length / (upSampleRateRatio * frameSize);
        int frameCount = upSampledArr.length / frameSize;
        if(frameCount % upSampleRateRatio == 0) {
            len = 0;
        }
        len += (frameCount / upSampleRateRatio) * frameSize;
        byte[] retArr = new byte[len];           // TODO: Ted nevim jestli tu nema byt jen upSampleRateRatio

        for(int retInd = 0, upSampleInd = 0; retInd < retArr.length; upSampleInd += skipSize) {
            for (int fs = 0; fs < frameSize; fs++, retInd++, upSampleInd++) {
                retArr[retInd] = upSampledArr[upSampleInd];
            }
        }

        return retArr;
    }


    /**
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate > newSampleRate.
     * <br>
     * The conversion isn't immediate, we convert to some factor "n" of the newSampleRate which is bigger than the oldSampleRate.
     * And the we take every "n"-th sample of the upsampled array.
     * @param samples          is input array with samples
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalid (<0 or >4)
     */
    private static double[] convertToLowerSampleRateByUpSampling(double[] samples, int numberOfChannels,
                                                                 int oldSampleRate, int newSampleRate,
                                                                 boolean canChangeInputArr) throws IOException {
        // First find the first multiple bigger than the old sample rate
        int upSampleRate = newSampleRate;
        int upSampleRateRatio = 1;
        while(upSampleRate < oldSampleRate) {
            upSampleRateRatio++;
            upSampleRate += newSampleRate;
        }
        int skipSize = (upSampleRateRatio - 1);       // Skip all the frames to downsample
        double[] upSampledArr = null;
        if(oldSampleRate % newSampleRate == 0) {      // Then the upSampleRate = oldSampleRate
            if(canChangeInputArr) {
                upSampledArr = samples;
            }
            else {
                upSampledArr = new double[samples.length];
                System.arraycopy(samples, 0, upSampledArr, 0, upSampledArr.length);
            }
        }
        else {
            upSampledArr = convertToHigherSampleRate(samples, numberOfChannels, oldSampleRate, upSampleRate);
        }
        // Low pass filter for the nyquist frequency of the new frequency
        runLowPassFilter(upSampledArr, 0, numberOfChannels, oldSampleRate,
                newSampleRate / 2,64, upSampledArr, 0, upSampledArr.length);
        int convertArrLen;
        convertArrLen = (upSampledArr.length / upSampleRateRatio);
        double[] retArr = new double[convertArrLen];

        for(int retInd = 0, upSampleInd = 0; retInd < retArr.length; upSampleInd += skipSize) {
            for (int ch = 0; ch < numberOfChannels; ch++, retInd++, upSampleInd++) {
                retArr[retInd] = upSampledArr[upSampleInd];
            }
        }

        return retArr;
    }

    /**
     * Input array isn't changed.
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate > newSampleRate
     * <br>
     * Performs the conversion immediately.
     * @param samples          is input array with samples
     * @param sampleSize       is the size of one sample in bytes
     * @param frameSize        is the size of one frame in bytes.
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @param isBigEndian      is boolean variable, which is true if the samples are big endian and false if little endian
     * @param isSigned         is boolean variable, which is true if the samples are signed, false if unsigned.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalit (<0 or >4)
     */
    @Deprecated
    private static byte[] convertToLowerSampleRate(byte[] samples, int sampleSize, int frameSize,
                                                   int numberOfChannels, int oldSampleRate, int newSampleRate,
                                                   boolean isBigEndian, boolean isSigned) throws IOException {
        byte[] filtered = runLowPassFilter(samples, newSampleRate / 2, 64, oldSampleRate,
            numberOfChannels, sampleSize, frameSize, isBigEndian, isSigned);
        return convertSampleRateImmediateVersion(filtered, sampleSize, numberOfChannels, oldSampleRate, newSampleRate, isBigEndian, isSigned);
    }


    /**
     * <br>
     * Converts input "samples" array which is supposed to have "oldSampleRate" to "newSampleRate", where oldSampleRate > newSampleRate
     * <br>
     * Performs the conversion immediately.
     * @param samples          is input array with samples
     * @param numberOfChannels is number of channels in samples array.
     * @param oldSampleRate    is the sampling rate of the given array.
     * @param newSampleRate    is the sampling rate to which we convert.
     * @return Returns input samples array but with sampling rate of newSampleRate.
     * @throws IOException is thrown when the sampleSize is invalit (<0 or >4)
     */
    private static double[] convertToLowerSampleRateByImmediate(double[] samples, int numberOfChannels,
                                                                int oldSampleRate, int newSampleRate,
                                                                boolean canChangeInputArr) throws IOException {
        double[] filtered;
        if(canChangeInputArr) {
            filtered = samples;
        }
        else {
            filtered = new double[samples.length];
            System.arraycopy(samples, 0, filtered, 0, filtered.length);
        }
        // Low pass filter for the nyquist frequency of the new frequency
        runLowPassFilter(samples, 0, numberOfChannels, oldSampleRate,
                         newSampleRate / 2, 64, filtered, 0, filtered.length);
        return convertSampleRateImmediateVersion(filtered, numberOfChannels, oldSampleRate, newSampleRate);
    }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// TODO: Veci pro modifikaci audia ktery ted nejsou nutny ... filtrovani, sum, atd.


    /**
     * Changes samples values based on type of operation and on size of changeValue.
     * Doesn't change given samples array. Creates new one and puts results in it
     * For op = PLUS we add the changeValue to all samples.
     * For op = MULTIPLY we multiply all samples by changeValue.
     * For op = LOG we take logarithm from the samples. The logarithm is of base changeValue.
     * For op = POWER we raise the samples to the changeValue-th power.
     * @param samples is the array containing samples.
     * @param startIndex is the start index from which to start perform operations on.
     * @param len is the number of samples to perform operation on
     * @param changeValue is the value which will be used on all elements in samples array.
     * @param op is the operation to be performed on the samples.
     * @return Returns copy of the samples array with corresponding changes.
     */
    public static double[] performOperationOnSamples(double[] samples, int startIndex, int outputStartIndex,
                                                     int len, double changeValue, ArithmeticOperation op) {
        double[] retArr = new double[samples.length];
        System.arraycopy(samples, 0, retArr, 0, retArr.length);
        performOperationOnSamples(samples, retArr, startIndex, outputStartIndex, len, changeValue, op);
        return retArr;
    }

    /**
     * Changes samples values based on type of operation and on size of changeValue.
     * Changes given outputArr array.
     * For op = PLUS we add the changeValue to all samples.
     * For op = MULTIPLY we multiply all samples by changeValue.
     * For op = LOG we take logarithm from the samples. The logarithm is of base changeValue.
     * For op = POWER we raise the samples to the changeValue-th power.
     * @param samples is the array containing samples.
     * @param outputArr is the array to put the results in (can be same as the samples array)
     * @param samplesStartIndex is the start index from which to start perform operations on.
     * @param len is the number of samples to perform operation on
     * @param changeValue is the value which will be used on all elements in samples array.
     * @param op is the operation to be performed on the samples.
     */
    public static void performOperationOnSamples(double[] samples, double[] outputArr, int samplesStartIndex, int outputStartIndex,
                                                 int len, double changeValue, ArithmeticOperation op) {
        int endIndex = samplesStartIndex + len;
        for(int i = samplesStartIndex, outIndex = outputStartIndex; i < endIndex; i++, outIndex++) {
            outputArr[outIndex] = performOperation(samples[i], changeValue, op);
        }
    }


    /**
     * Changes samples values based on type of operation and on size of changeValue.
     * Changes given samples array.
     * For op = PLUS we add the changeValue to all samples.
     * For op = MULTIPLY we multiply all samples by changeValue.
     * For op = LOG we take logarithm from the samples. The logarithm is of base changeValue.
     * For op = POWER we raise the samples to the changeValue-th power.
     * @param samples is the array containing samples.
     * @param startIndex is the start index in array
     * @param endIndex is the end index in array
     * @param changeValue is the value which will be used on all elements in samples array.
     * @param op is the operation to be performed on the samples.
     */
    public static void performOperationOnSamples(double[] samples, int startIndex, int endIndex, double changeValue, ArithmeticOperation op) {
        int len = endIndex - startIndex;
        performOperationOnSamples(samples, samples, startIndex, startIndex, len, changeValue, op);
    }



//    @Deprecated
//    public static double[] performOperationOnSamples(double[] samples, double[] changeValues,
//                                              int startSamplesIndex, int startChangeValuesIndex, int outputStartIndex,
//                                              int len, ArithmeticOperation op) {
//        double[] retArr = new double[samples.length];
//        performOperationOnSamples(samples, changeValues, retArr, startSamplesIndex, startChangeValuesIndex, outputStartIndex, len, op);
//        return retArr;
//    }


//    @Deprecated
//    public static void performOperationOnSamples(double[] samples, double[] changeValues, double[] outputArr,
//                                          int startSamplesIndex, int startChangeValuesIndex, int outputStartIndex,
//                                          int len, ArithmeticOperation op) {
//        int changeValuesEndIndex = startChangeValuesIndex + len;
//        for(int indexInChangeValues = startChangeValuesIndex, samplesIndex = startSamplesIndex, outputIndex = outputStartIndex;
//                indexInChangeValues < changeValuesEndIndex;
//                indexInChangeValues++, samplesIndex++, outputIndex++) {
//            outputArr[outputIndex] = Program.performOperation(samples[samplesIndex], changeValues[indexInChangeValues], op);
//        }
//    }

    /**
     * Not used, this is too general
     * @param input
     * @param changeValues
     * @param output
     * @param inputStartIndex
     * @param inputEndIndex
     * @param changeValuesStartIndex
     * @param changeValuesEndIndex
     * @param outputStartIndex
     * @param outputEndIndex
     * @param op
     */
    public static void performOperationOnSamples(double[] input, double[] changeValues, double[] output,
                                                 int inputStartIndex, int inputEndIndex,
                                                 int changeValuesStartIndex, int changeValuesEndIndex,
                                                 int outputStartIndex, int outputEndIndex, ArithmeticOperation op) {
        int inputLen = inputEndIndex - inputStartIndex;
        boolean isPowerOf2 = Program.testIfNumberIsPowerOfN(inputLen, 2) >= 0;

        if (isPowerOf2) {
            int changeValuesLen = changeValuesEndIndex - changeValuesStartIndex;
            boolean isPowerOf2CV = Program.testIfNumberIsPowerOfN(inputLen, 2) >= 0;
            if(isPowerOf2CV) {
                for (int oi = outputStartIndex, ii = inputStartIndex, cvi = changeValuesStartIndex; oi < outputEndIndex; oi++, ii++, cvi++) {
                    output[oi] = Program.performOperation(input[inputStartIndex + (ii % inputLen)],
                        changeValues[changeValuesStartIndex + (cvi % changeValuesLen)], op);
                }
            }
            else {
                for (int oi = outputStartIndex, ii = inputStartIndex, cvi = changeValuesStartIndex; oi < outputEndIndex; oi++, ii++, cvi++) {
                    if (cvi >= changeValuesEndIndex) {
                        cvi = changeValuesStartIndex;
                    }
                    output[oi] = Program.performOperation(input[inputStartIndex + (ii % inputLen)], changeValues[cvi], op);
                }
            }
        }
        else {
            for(int oi = outputStartIndex, ii = inputStartIndex, cvi = changeValuesStartIndex; oi < outputEndIndex; oi++, ii++, cvi++) {
                if (ii >= inputEndIndex) {
                    ii = inputStartIndex;
                }
                if(cvi >= changeValuesEndIndex) {
                    cvi = changeValuesStartIndex;
                }
                output[oi] = Program.performOperation(input[ii], changeValues[cvi], op);
            }
        }
    }

    /**
     * Less general variant, doesn't contain the change values
     */
    public static void performOperationOnSamples(double[] input, double[] output,
                                                 int inputStartIndex, int inputEndIndex,
                                                 int outputStartIndex, int outputEndIndex, ArithmeticOperation op) {
        int inputLen = inputEndIndex - inputStartIndex;
        boolean isPowerOf2 = Program.testIfNumberIsPowerOfN(inputLen, 2) >= 0;

        if (isPowerOf2) {
            for (int oi = outputStartIndex, ii = inputStartIndex; oi < outputEndIndex; oi++, ii++) {
                output[oi] = Program.performOperation(input[inputStartIndex + (ii % inputLen)], output[oi], op);
            }
        }
        else {
            for(int oi = outputStartIndex, ii = inputStartIndex; oi < outputEndIndex; oi++, ii++) {
                if (ii >= inputEndIndex) {
                    ii = inputStartIndex;
                }
                output[oi] = Program.performOperation(input[ii], output[oi], op);
            }
        }
    }



    public static double performOperation(double sample, double changeValue, ArithmeticOperation op) {
        switch(op) {
            case PLUS:
                sample += changeValue;
                break;
            case MULTIPLY:
                sample *= changeValue;
                break;
            case LOG:
                sample = logGeneral(sample, changeValue);      // changeValue is the base
                break;
            case POWER:
                sample = Math.pow(sample, changeValue);
                break;
        }

        return sample;
    }


    public static double logGeneral(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    /**
     * Fills given array with random numbers from range lowestRandom to highestRandom. Usually from -1 to 1.
     * Where 1 random generated number is used to set next repeatedNumbersCount samples.
     * Changes the values in array.
     * @param arr is the array to be filled.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     * @param repeatedNumbersCount is number of samples to be set with 1 random number.
     */
    public static void generateWhiteNoiseWithRepeatByRef(double[] arr, int repeatedNumbersCount, double lowestRandom, double highestRandom) {
        double random;
        for(int i = 0; i < arr.length;) {
            random = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);
            for(int j = 0; j < repeatedNumbersCount; j++, i++) {
                arr[i] = random;
            }
        }
    }



    /**
     * Fills given array with random numbers from range lowestRandom to highestRandom. Usually from -1 to 1.
     * Where 1 random generated number is used to set next repeatedNumbersCount samples.
     * @param len is the length of the array to be filled with random noise.
     * @param repeatedNumbersCount is number of samples to be set with 1 random number.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     * @return returns the array with white noise with repeat.
     */
    public static double[] generateWhiteNoiseWithRepeatByCopy(int len, int repeatedNumbersCount, double lowestRandom, double highestRandom) {
        double[] retArr = new double[len];
        generateWhiteNoiseWithRepeatByRef(retArr, repeatedNumbersCount, lowestRandom, highestRandom);
        return retArr;
    }


    /**
     * Fills given array with random numbers from range lowestRandom to highestRandom. Usually from -1 to 1.
     * 2 random numbers are generated. First sample gets the first random number, then n-1 samples
     * are linearly interpolated to the second random number (exclusive) and then follows the second random number.
     * Then the second random is taken as first and new second random number is generated, then we interpolate this, etc.
     * @param arr is the array to be filled.
     * @param n is number of samples after which will be next random number generated.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     * That means: Generate random number every nth sample.
     */
    public static void generateWhiteNoiseWithLinearInterpolationByRef(double[] arr, int n, double lowestRandom, double highestRandom) {
        double random1;
        double random2 = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);
        double jump;


        for(int i = 0; i < arr.length;) {
            random1 = random2;
            random2 = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);
            jump = (random2 - random1) / n;

            arr[i] = random1;
            i++;
            for(int j = 0; j < n; j++, i++) {
                arr[i] = random1;
                random1 += jump;
            }
            arr[i] = random2;
            i++;
        }
    }


    /**
     * Same as generateWhiteNoiseWithLinearInterpolationByRef but the array is returned and created internally.
     * @param len is the length of the array to be filled with random noise.
     * @param n is number of samples after which will be next random number generated.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     * @return Returns white noise with linear interpolation.
     */
    public static double[] generateWhiteNoiseWithLinearInterpolationByCopy(int len, int n, double lowestRandom, double highestRandom) {
        double[] retArr = new double[len];
        generateWhiteNoiseWithLinearInterpolationByRef(retArr, n, lowestRandom, highestRandom);
        return retArr;
    }


    // TODO: Jine interpolace podle me nemaji smysl


    /**
     * Probability 1 - probabilityToContinue is the probability that the method will end.
     * Until the method ends, it chooses random sample in each iteration which will be set with parameter number.
     * Changes the input array.
     * @param samples is the input array.
     * @param number is the number with which will be set randomly chosen sample.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     */
    public static void setRandomSamplesToNumberByRef(double[] samples, double number, double probabilityToContinue) {
        Random rand = new Random();
        int index;

        while(rand.nextDouble() < probabilityToContinue) {
            index = rand.nextInt(samples.length);
            samples[index] = number;
        }
    }

    /**
     * Same as setRandomSamplesToNumberByRef but doesn't change the input array.
     * @param samples is the input array.
     * @param number is the number with which will be set randomly chosen sample.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     * @return Returns copy of the samples array where random samples are set to number.
     */
    public static double[] setRandomSamplesToNumberByCopy(double[] samples, double number, double probabilityToContinue) {
        double[] retArr = new double[samples.length];
        for(int i = 0; i < samples.length; i++) {
            retArr[i] = samples[i];
        }
        setRandomSamplesToNumberByRef(retArr, number, probabilityToContinue);

        return retArr;
    }

    // TODO: Teoreticky lze pridat verzi co vybere random index a da na neho random cislo
    /**
     * Probability 1 - probabilityToContinue is the probability that the method will end.
     * Until the method ends, it chooses random sample in each iteration which will be set to random double number between
     * lowestRandom and highestRandom.
     * Changes the input array.
     * @param samples is the input array.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     */
    public static void setRandomSamplesToRandomNumberByRef(double[] samples, double probabilityToContinue, double lowestRandom, double highestRandom) {
        Random rand = new Random();
        int index;

        while(rand.nextDouble() < probabilityToContinue) {
            index = rand.nextInt(samples.length);
            samples[index] = ThreadLocalRandom.current().nextDouble(lowestRandom, highestRandom);;
        }
    }

    /**
     * Same as setRandomSamplesToNumberByRef but doesn't change the input array.
     * @param samples is the input array.
     * @param probabilityToContinue is the probability that we will set some next sample with parameter number.
     * @param lowestRandom is the lowest possible number to be generated. Usually -1.
     * @param highestRandom is the highest possible number to be generated. Usually 1.
     * @return Returns copy of the samples array where random samples are set to number.
     */
    public static double[] setRandomSamplesToRandomNumberByCopy(double[] samples, double probabilityToContinue, double lowestRandom, double highestRandom) {
        double[] retArr = new double[samples.length];
        for(int i = 0; i < samples.length; i++) {
            retArr[i] = samples[i];
        }
        setRandomSamplesToRandomNumberByRef(retArr, probabilityToContinue, lowestRandom, highestRandom);

        return retArr;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// TODO: Filtry

    // TODO: Not sure about this, maybe moving average is calculated from the already averaged values,
    // TODO: but this way it makes more sense.
    // TODO: !!!!!!!!!!!!! Running average is filter, so it should be implemented by using the filter method !!!!
    // TODO: !!!!!!!!!!!!! But it is specific filter, which can be implemented more efficiently than general filter
    /**
     * Performs moving window average on windows of size windowSize. Moving window average averages last
     * windowSize samples and the average is stored in the last sample of window.
     * Changes the input array.
     * @param samples is the input array to perform the window average on.
     * @param windowSize is the size of window on which the averaging will be performed.
     *                   For example if == 1 then we average just 1 sample in each channel, so the output won't change.
     *
     * @deprecated Old method works only for mono
     */
    public static void performMovingWindowAverageByRef(double[] samples, int windowSize) {
//		double oldSampleValue;
//		double windowSum = 0;
//
//		int i = 0;
//		int firstIndexInWindow = 0;
//		for(; i < windowSize; i++) {		// Sum of first window
//			windowSum += samples[i];
//		}
//
//
//		// Now we will just move the window (subtract first element of that window and add the last one)
//		for(; i < samples.length; i++, firstIndexInWindow++) {
//			oldSampleValue = samples[i];
//			samples[i] = windowSum / windowSize;
//			windowSum = windowSum - samples[firstIndexInWindow] + oldSampleValue;
//		}

        // Zmenena verze
        double[] oldSampleValues = new double[windowSize];
        double windowSum = 0;
        int i = 0;
        for(; i < windowSize; i++) {		// Sum of first window
            windowSum += samples[i];
            oldSampleValues[i] = samples[i];
        }
        i--;			// TODO: Zmena oproti minulemu, protoze mi prijde ze se ten prumer ma pocitat i z te soucasne hodnoty


        // Now we will just move the window (subtract first element of that window and add the last one)
        int firstIndexInWindow = 0;
        // 2 Variants because of optimization
        if(windowSize % 2 == 0)
        {
            for(; i < samples.length - 1; i++, firstIndexInWindow++) {
                samples[i] = windowSum / windowSize;
                windowSum = windowSum - oldSampleValues[firstIndexInWindow % windowSize] + samples[i+1];
                oldSampleValues[firstIndexInWindow % windowSize] = samples[i+1];
            }
        }
        else {
            for(; i < samples.length - 1; i++, firstIndexInWindow++) {
                samples[i] = windowSum / windowSize;
                if(firstIndexInWindow == windowSize) {
                    firstIndexInWindow = 0;
                }
                windowSum = windowSum - oldSampleValues[firstIndexInWindow] + samples[i+1];
                oldSampleValues[firstIndexInWindow] = samples[i+1];
            }
        }
        samples[samples.length - 1] = windowSum / windowSize;
    }

    /**
     * Performs moving window average on windows of size windowSize. Moving window average averages last
     * windowSize samples and the average is stored in the last sample of window.
     * Changes the input array.
     * @param samples is the input array to perform the window average on.
     * @param windowSize is the size of window on which the averaging will be performed.
     *                   For example if == 1 then we average just 1 sample in each channel, so the output won't change.
     * @param numberOfChannels is the number of channels.
     */
    public static void performMovingWindowAverageByRef(double[] samples, int windowSize, int numberOfChannels) {
//		double oldSampleValue;
//		double windowSum = 0;
//
//		int i = 0;
//		int firstIndexInWindow = 0;
//		for(; i < windowSize; i++) {		// Sum of first window
//			windowSum += samples[i];
//		}
//
//
//		// Now we will just move the window (subtract first element of that window and add the last one)
//		for(; i < samples.length; i++, firstIndexInWindow++) {
//			oldSampleValue = samples[i];
//			samples[i] = windowSum / windowSize;
//			windowSum = windowSum - samples[firstIndexInWindow] + oldSampleValue;
//		}

        // Zmenena verze
        double[][] oldSampleValues = new double[numberOfChannels][windowSize];
        double[] windowSum = new double[numberOfChannels];
        int sampleInd = 0;
        int indexCheck = windowSize * windowSum.length;
        if(indexCheck > samples.length) {
            return;
        }
        for(int i = 0; i < windowSize; i++) {		// Sum of first window
            for(int ch = 0; ch < windowSum.length; ch++, sampleInd++) {
                windowSum[ch] += samples[sampleInd];
                oldSampleValues[ch][i] = samples[sampleInd];
            }
        }
        sampleInd -= numberOfChannels;			// TODO: Zmena oproti minulemu, protoze mi prijde ze se ten prumer ma pocitat i z te soucasne hodnoty


        // Now we will just move the window (subtract first element of that window and add the last one)
        int firstIndexInWindow = 0;
        double oldVal;
        // 2 Variants because of optimization
        if(windowSize % 2 == 0)
        {
            for(; sampleInd < samples.length - numberOfChannels; firstIndexInWindow++) {
                for(int ch = 0; ch < numberOfChannels; ch++, sampleInd++) {
                    samples[sampleInd] = windowSum[ch] / windowSize;
                    windowSum[ch] = windowSum[ch] - oldSampleValues[ch][firstIndexInWindow % windowSize] + samples[sampleInd + numberOfChannels];
                    oldSampleValues[ch][firstIndexInWindow % windowSize] = samples[sampleInd + numberOfChannels];
                }
            }
        }
        else {
            for(; sampleInd < samples.length - numberOfChannels; firstIndexInWindow++) {
                for(int ch = 0; ch < numberOfChannels; ch++, sampleInd++) {
                    samples[sampleInd] = windowSum[ch] / windowSize;
                    if (firstIndexInWindow == windowSize) {
                        firstIndexInWindow = 0;
                    }
                    windowSum[ch] = windowSum[ch] - oldSampleValues[ch][firstIndexInWindow] + samples[sampleInd + numberOfChannels];
                    oldSampleValues[ch][firstIndexInWindow] = samples[sampleInd + numberOfChannels];
                }
            }
        }
        for(int i = 0; i < windowSum.length; i++, sampleInd++) {
            samples[sampleInd] = windowSum[i] / windowSize;
        }
    }



    // TODO: !!! Not sure if I should work with ints or doubles when multiplying with coeffecients - for example
    // for example in doubles 2/3 + 2/3 = 4/3 which will be converted to 1. But when working with ints it is 0+0=0
    // TODO: Udelat reference variantu
    /**
     * Performs non-recursive filter, result is returned in new array. Non-recursive filter is this (y[n] is n-th output sample and x[n] is n-th input sample):
     * <br>
     * y[n] = coef[0] * x[n - coef.length + 1] + ... + coef[coef.length] x[n]
     * @param samples is the input array. It isn't changed.
     * @param coef are the coefficients for the input samples. The last index contains index for the currently computed output. The first index is the (coef.length+1)-th before the current sample.
     * @param numberOfChannels represents the number of channels
     * @param sampleSize is the size of 1 sample
     * @param frameSize is the size of 1 frame
     * @param isBigEndian true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @return Returns new array gotten from input samples array by non-recursive filter.
     * @throws IOException is thrown by method calculateMask if the sampleSize is invalid.
     */
    @Deprecated
    public static byte[] performNonRecursiveFilter(byte[] samples, double[] coef, int numberOfChannels,
                                                   int sampleSize, int frameSize, boolean isBigEndian, boolean isSigned) throws IOException {
        byte[] retArr = new byte[samples.length];
        int[] vals = new int[numberOfChannels];
        int index;
        int startingCoefInd;
        int sample;
        byte[] sampleBytes = new byte[sampleSize];
        int mask = calculateMask(sampleSize);

        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
        // It's for optimization because we need to check if there are the preceding samples.
        startingCoefInd = -coef.length * frameSize + frameSize;		// +1 because the current sample can be used (Simple check of correctness is if we had just 1 coef)
        int resInd;
        int coefInd;
        for(resInd = 0, coefInd = 0; coefInd < coef.length - 1; startingCoefInd += frameSize, coefInd++) {
            // Covers the case when there is more coefficients than frames,
            // but sample.length is expected to be containing only full frames,
            // that is samples.length % frameSize == 0
            if(resInd >= retArr.length) {
                return retArr;
            }
            for(int ch = 0; ch < vals.length; ch++) {
                vals[ch] = 0;
            }
            index = startingCoefInd;
// TODO:            System.out.println(resInd + ":" + index + "\t:\t" + retArr.length + ":" + samples.length + ":\t" + numberOfChannels + ":\t" + sampleSize);
            for (int j = 0; j < coef.length; j++) {
                if (index >= 0) {
                    for (int ch = 0; ch < vals.length; ch++, index += sampleSize) {
                        sample = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                        vals[ch] += coef[j] * sample;
                        // TODO:                      System.out.println("SAMPLE:\t" + sample + "\t:\tMULTSAMPLE:\t" + vals[ch]);
// TODO:                        System.out.println(index);
                    }
                }
                else {
// TODO:                    System.out.println(":::::::::::" + index);
                    index += frameSize;
                }
            }
// TODO:            System.out.println("IND:\t" + index);

            for (int ch = 0; ch < vals.length; ch++) {
// TODO:                System.out.println("VAL:\t" + vals[ch]);
                convertIntToByteArr(sampleBytes, vals[ch], isBigEndian);
                for(int j = 0; j < sampleBytes.length; j++, resInd++) {
// TODO:                   System.out.println("VALBYTE:\t" + sampleBytes[j]);
                    retArr[resInd] = sampleBytes[j];
                }
            }
        }

        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
        // TODO:        System.out.println("------------------------------------------");

        for(; resInd < retArr.length; startingCoefInd += frameSize) {
            for(int ch = 0; ch < vals.length; ch++) {
                vals[ch] = 0;
            }
            index = startingCoefInd;
// TODO:            System.out.println(resInd + ":" + index + "\t:\t" + retArr.length + ":" + samples.length + ":\t" + numberOfChannels + ":\t" + sampleSize);
            for(int j = 0; j < coef.length; j++) {
                for (int ch = 0; ch < vals.length; ch++, index += sampleSize) {
                    sample = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                    vals[ch] += coef[j] * sample;
//TODO:                    System.out.println("SAMPLE:\t" + sample + "\t:\tMULTSAMPLE:\t" + vals[ch]);
                }
            }

            // TODO: the same for cycle as above (30 lines above)
            for (int ch = 0; ch < vals.length; ch++) {
                convertIntToByteArr(sampleBytes, vals[ch], isBigEndian);
//TODO:                System.out.println("VAL:\t" + vals[ch]);
                for(int j = 0; j < sampleBytes.length; j++, resInd++) {
//TODO:                    System.out.println("VALBYTE:\t" + sampleBytes[j]);
                    retArr[resInd] = sampleBytes[j];
                }
            }
        }

        return retArr;

//        byte[] retArr = new byte[samples.length];
//        byte val;
//        int index;
//        int startingCoefInd;
//
//        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
//        // It's for optimization because we need to check if there are the preceding samples.
//        startingCoefInd = -coef.length + 1;		// +1 because the current sample can be used (Simple check of correctness is if we had just 1 coef)
//        int i;
//        for(i = 0; i < coef.length; i++, startingCoefInd++) {
//            val = 0;
//            index = startingCoefInd;
//            for(int j = 0; j < coef.length; j++, index++) {
//                if(index >= 0) {
//                    val += coef[j] * samples[index];
//                }
//            }
//
//            retArr[i] = val;
//        }
//
//        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
//        startingCoefInd = 0;
//        for(; i < samples.length; i++, startingCoefInd++) {
//            val = 0;
//            index = startingCoefInd;
//            for(int j = 0; j < coef.length; j++, index++) {
//                val += coef[j] * samples[index];
//            }
//
//            retArr[i] = val;
//        }
//
//        return retArr;
    }



//// TODO: !!!!!!!!!!!!!!!!!!!! Jen ted na rychlo double varianta bez testu
//    // TODO: !!! Not sure if I should work with ints or doubles when multiplying with coeffecients - for example
//    // for example in doubles 2/3 + 2/3 = 4/3 which will be converted to 1. But when working with ints it is 0+0=0
//    // TODO: Udelat reference variantu
//    /**
//     * Performs non-recursive filter, result is returned in new array. Non-recursive filter is this (y[n] is n-th output sample and x[n] is n-th input sample):
//     * <br>
//     * y[n] = coef[0] * x[n - coef.length + 1] + ... + coef[coef.length] x[n]
//     * @param samples is the input array. It isn't changed.
//     * @param coef are the coefficients for the input samples. The last index contains index for the currently computed output. The first index is the (coef.length+1)-th before the current sample.
//     * @param numberOfChannels represents the number of channels
//     * @param retArr is he array which will contain the result of filter.
//     * @return Returns -1 if the output array was shorter than length of coefs array else returns 1. In both cases the result of filter is in retArr.
//     */
//    // TODO: Returns -1 if the input array was too short ... the function doesn't do anything
//    public static int performNonRecursiveFilter(double[] samples, double[] coef, int numberOfChannels, double[] retArr) {
//        double[] vals = new double[numberOfChannels];
//        int index;
//        int startingCoefInd;
//
//        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
//        // It's for optimization because we need to check if there are the preceding samples.
//        startingCoefInd = -coef.length * numberOfChannels + numberOfChannels;		// +1 because the current sample can be used (Simple check of correctness is if we had just 1 coef)
//        int resInd;
//        int coefInd;
//
//        if(numberOfChannels * coef.length >= retArr.length) {
//            return -1;
//        }
//
//        for(resInd = 0, coefInd = 0; coefInd < coef.length - 1; startingCoefInd += numberOfChannels, coefInd++) {
//            // Covers the case when there is more coefficients than frames,
//            // but sample.length is expected to be containing only full frames,
//            // that is samples.length % frameSize == 0
//            for(int ch = 0; ch < vals.length; ch++) {
//                vals[ch] = 0;
//            }
//            index = startingCoefInd;
//// TODO:            System.out.println(resInd + ":" + index + "\t:\t" + retArr.length + ":" + samples.length + ":\t" + numberOfChannels + ":\t" + sampleSize);
//            for (int j = 0; j < coef.length; j++) {
//                if (index >= 0) {
//                    for (int ch = 0; ch < vals.length; ch++, index++) {
//                        vals[ch] += coef[j] * samples[index];
//                        // TODO:                      System.out.println("SAMPLE:\t" + sample + "\t:\tMULTSAMPLE:\t" + vals[ch]);
//// TODO:                        System.out.println(index);
//                    }
//                }
//                else {
//// TODO:                    System.out.println(":::::::::::" + index);
//                    index += numberOfChannels;
//                }
//            }
//// TODO:            System.out.println("IND:\t" + index);
//
//            for (int ch = 0; ch < vals.length; ch++, resInd++) {
//// TODO:                System.out.println("VAL:\t" + vals[ch]);
//                retArr[resInd] = vals[ch];
//            }
//        }
//
//        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
//        // TODO:        System.out.println("------------------------------------------");
//
//        for(; resInd < retArr.length; startingCoefInd += numberOfChannels) {
//            for(int ch = 0; ch < vals.length; ch++) {
//                vals[ch] = 0;
//            }
//            index = startingCoefInd;
//// TODO:            System.out.println(resInd + ":" + index + "\t:\t" + retArr.length + ":" + samples.length + ":\t" + numberOfChannels + ":\t" + sampleSize);
//            for(int j = 0; j < coef.length; j++) {
//                for (int ch = 0; ch < vals.length; ch++, index++) {
//                    vals[ch] += coef[j] * samples[index];
//                    // TODO: PROGRAMO
////                    ProgramTest.debugPrint("low-pass filter:", j, coef[j], samples[index], coef[j] * samples[index], vals[ch]);
////                    // https://stackoverflow.com/questions/16098046/how-do-i-print-a-double-value-without-scientific-notation-using-java
////                    System.out.printf("v1: %f\n", coef[j]);
////                    System.out.printf("v2: %f\n", samples[index]);
////                    System.out.printf("v3: %f\n", coef[j] * samples[index]);
////                    System.out.printf("v4: %f\n", vals[ch]);
////                    Tady totiz jsou 2 problemy - 1) je to celkem pomaly protoze to delam jakoby po 1 prvku
////                    to ale az tak nevadi
////                        2) co ale vadi je ze kdyz retArr == samples which is the input arr, then it rewrites underlying
////                        samples so the values become invalid, so I will have to have so buffer and then when I am with
////                        the index far enough I will copy it - the buffer can be the vals[ch] just make it [][] and I am set
////                        so I will solve both problems at once
////                        3) the result has samples larger than 1 - This is fine I can just call set to max after that, or just user let do what he wants -
////                    to se da vyresit tak ze znormalizuju ty coeficienty
//                    // TODO: PROGRAMO
//                }
//            }
//
//            // TODO: the same for cycle as above (30 lines above)
//            for (int ch = 0; ch < vals.length; ch++, resInd++) {
////TODO:                System.out.println("VAL:\t" + vals[ch]);
//                retArr[resInd] = vals[ch];
//            }
//        }
//
//        return 1;
//    }


    // TODO: !!!!!!!!!!!!!!!!!!!! Jen ted na rychlo double varianta bez testu
    // TODO: !!! Not sure if I should work with ints or doubles when multiplying with coeffecients - for example
    // for example in doubles 2/3 + 2/3 = 4/3 which will be converted to 1. But when working with ints it is 0+0=0
    /**
     * Performs non-recursive filter on input array, result is returned in output array (Input and output array can be the same).
     * Non-recursive filter is this (y[n] is n-th output sample and x[n] is n-th input sample):
     * <br>
     * y[n] = coef[0] * x[n - coef.length + 1] + ... + coef[coef.length] x[n]
     * @param samples is the input array. It isn't changed.
     * @param coef are the coefficients for the input samples. The last index contains index for the currently computed output. The first index is the (coef.length+1)-th before the current sample.
     * @param numberOfChannels represents the number of channels
     * @param retArr is he array which will contain the result of filter.
     * @param retArrStartIndex is the start index in the output array (retArr) - inclusive
     * @param retArrEndIndex is the end index in the output array (retArr) - exclusive
     * @return Returns -1 if the output array was shorter than length of coefs array else returns 1.
     * Returns -2 if the input array isn't long enough. If 1 is returned the result of filter is in retArr. Else the retArr isn't changed in any way.
     */
    // Implementation note: since retArrEndIndex is exclusive,
    // we need to use retArrEndIndex - 1 when we are referring to valid indices
    public static int performNonRecursiveFilter(double[] samples, int samplesStartIndex,
                                                double[] coef, int numberOfChannels,
                                                double[] retArr, int retArrStartIndex, final int retArrEndIndex) {
        int bufferLen = 4096;
        bufferLen = Math.max(bufferLen, Program.getFirstPowerOfNAfterNumber(coef.length, 2));
        int indexToStopCopyFrom = bufferLen;
        int indexCountToWaitWithForNextIteration = coef.length - 1;
        bufferLen += indexCountToWaitWithForNextIteration;

        double[][] vals = new double[numberOfChannels][bufferLen];
        int index;
        int startingCoefInd;

        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
        // It's for optimization because we need to check if there are the preceding samples.
        startingCoefInd = samplesStartIndex + -indexCountToWaitWithForNextIteration * numberOfChannels;        // +1 because the current sample can be used (Simple check of correctness is if we had just 1 coef)
        int resInd = retArrStartIndex;

        if (retArrStartIndex + numberOfChannels * coef.length >= retArrEndIndex) {
            return -1;
        }
        if (samplesStartIndex + numberOfChannels * coef.length >= samples.length ||
            retArrEndIndex - retArrStartIndex > samples.length - samplesStartIndex) {
            return -2;
        }

        resetTwoDimArr(vals, 0, vals[0].length);
        for (int i = 0, coefInd = 0; i < vals[0].length;
             i++, startingCoefInd += numberOfChannels, coefInd++) {
            index = startingCoefInd;
            if (coefInd >= indexCountToWaitWithForNextIteration) {
                break;
            }
            for (int j = 0; j < coef.length; j++) {
                if (index >= 0) {
                    for (int ch = 0; ch < vals.length; ch++, index++) {
                        vals[ch][i] += coef[j] * samples[index];
                    }
                } else {
                    index += numberOfChannels;
                }
            }
        }
        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
        int firstInvalidIndexInChannel = -1;
        for ( ; resInd < retArrEndIndex - 1; ) {
            // This represents the current result index (where we are in the out array currently)
            int virtualResInd = resInd + indexCountToWaitWithForNextIteration * vals.length;

            for (int i = indexCountToWaitWithForNextIteration; i < vals[0].length;
                 i++, startingCoefInd += numberOfChannels, virtualResInd += numberOfChannels) {
                if (virtualResInd >= retArrEndIndex - 1) {
                    firstInvalidIndexInChannel = i;
                    break;
                }
                index = startingCoefInd;
                for (int j = 0; j < coef.length; j++) {
                    for (int ch = 0; ch < vals.length; ch++, index++) {
                        vals[ch][i] += coef[j] * samples[index];
                    }
                }
            }

            resInd = setRetArrInLowPassFilter(resInd, firstInvalidIndexInChannel, vals, indexToStopCopyFrom, retArr);
            for (int ch = 0; ch < vals.length; ch++) {
                System.arraycopy(vals[ch], indexToStopCopyFrom, vals[ch], 0, indexCountToWaitWithForNextIteration);
            }
            resetTwoDimArr(vals, indexCountToWaitWithForNextIteration, vals[0].length);
        }

        return 1;
    }


    private static int setRetArrInLowPassFilter(int resInd, int firstInvalidIndexInChannel, double[][] vals,
                                                int endIndex, double[] retArr) {
        int startResInd = resInd;
        final int resIndAndMethodStart = resInd;
        int len;
        if(firstInvalidIndexInChannel < 0) {
            len = endIndex;
        }
        else {
            len = firstInvalidIndexInChannel;
        }
        for (int ch = 0; ch < vals.length; ch++, startResInd++, resInd = startResInd) {
            for (int i = 0; i < len; i++, resInd += vals.length) {
// TODO:                System.out.println("VAL:\t" + vals[ch]);
                retArr[resInd] = vals[ch][i];
            }
        }

        return resIndAndMethodStart + len * vals.length;
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* -------------------------------------------- [START] -------------------------------------------- */
    /////////////////// Fill array with values methods
    /* -------------------------------------------- [START] -------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void resetTwoDimArr(double[][] arr, int startIndex, int endIndex) {
        setTwoDimArr(arr, startIndex, endIndex, 0);
    }

    public static void setTwoDimArr(double[][] arr, int startIndex, int endIndex, double value) {
        for (int ch = 0; ch < arr.length; ch++) {
            setOneDimArr(arr[ch], startIndex, endIndex, value);
        }
    }

    // Modified code from https://stackoverflow.com/questions/9128737/fastest-way-to-set-all-values-of-an-array
    /*
     * initialize a smaller piece of the array and use the System.arraycopy
     * call to fill in the rest of the array in an expanding binary fashion
     */
    public static void setOneDimArr(double[] array, int startIndex, int endIndex, double value) {
        int len = endIndex - startIndex;
        array[startIndex] = value;

        //Value of i will be [1, 2, 4, 8, 16, 32, ..., len]
        for (int i = 1, outIndex = startIndex + 1; i < len; outIndex += i, i += i) {
            System.arraycopy(array, startIndex, array, outIndex, ((len - i) < i) ? (len - i) : i);
        }
    }

    public static void setOneDimArrWithCheck(double[] array, int startIndex, int endIndex, double value) {
        if(endIndex > startIndex) {
            int len = endIndex - startIndex;
            array[startIndex] = value;

            //Value of i will be [1, 2, 4, 8, 16, 32, ..., len]
            for (int i = 1, outIndex = startIndex + 1; i < len; outIndex += i, i += i) {
                System.arraycopy(array, startIndex, array, outIndex, ((len - i) < i) ? (len - i) : i);
            }
        }
    }

    public static enum CURVE_TYPE {
        SINE {
            double[] createCurve(int len, double amp, double freq, int sampleRate, double phase) {
                return SineGeneratorWithPhase.createSine(len, amp, freq, sampleRate, phase);
            }
        },
        LINE {
            double[] createCurve(int len, double amp, double freq, int sampleRate, double phase) {
                double[] line = new double[len];
                Program.setOneDimArr(line,0, line.length, amp);
                return line;
            }
        },
        RANDOM {
            double[] createCurve(int len, double amp, double freq, int sampleRate, double phase) {
                double[] arr = new double[len];
                Program.fillArrWithRandomValues(arr, amp);
                return arr;
            }
        };

        /**
         * Fills array with values based on given parameters. Based on curve some parameters may be ignored.
         * @param len
         * @param amp
         * @param freq
         * @param sampleRate
         * @param phase
         * @return
         */
        abstract double[] createCurve(int len, double amp, double freq, int sampleRate, double phase);
    }

    public static void fillArrWithRandomValues(double[] arr, double amplitude) {
        Random r = new Random();

        for (int j = 0; j < arr.length; j++) {
            arr[j] = r.nextDouble();
            arr[j] *= amplitude;
            if (r.nextDouble() > 0.5) {
                arr[j] = -arr[j];
            }
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------- [END] --------------------------------------------- */
    /////////////////// Fill array with values methods
    /* --------------------------------------------- [END] --------------------------------------------- */
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    /**
     * Performs recursive filter, result is returned in new array. Recursive filter is this (y[n] is n-th output sample and x[n] is n-th input sample):
     * <br>
     * y[n] = coef[0] * x[n - coef.length + 1] + ... + coef[coef.length] x[n] + coefOutput[0] * y[n - coefOutput.length] + coefOutput[coefOutput.length] * y[n-1]
     * @param samples is the input array. It isn't changed.
     * @param coef are the coefficients for the input samples. The last index contains index for the currently computed output. The first index is the (coef.length+1)-th before the current sample.
     * @param coefOutput are the coefficients for the output samples. The last index contains index for the currently for the output before the currently calculated one. The first index is the coef.length -th before the current sample.
     * @return Returns new array gotten from input samples array by recursive filter.
     */
    public static byte[] performRecursiveFilter(byte[] samples, double[] coef, double[] coefOutput) {
        byte[] retArr = new byte[samples.length];
        byte val;
        int index;
        int startingCoefInd;
        int startingCoefOutputInd;
        int len = Math.max(coef.length, coefOutput.length);

        // Filter for the first indexes is a bit different, since they dont have all the preceding samples for the filtering.
        // It's for optimalization because we need to check if there are the preceding samples.
        startingCoefInd = -coef.length + 1;		// +1 because the current sample can be used (Simple check of correctness is if we had just 1 coef)
        startingCoefOutputInd = -coefOutput.length;
        int i;
        for(i = 0; i < len; i++, startingCoefInd++, startingCoefOutputInd++) {
            val = 0;
            index = startingCoefInd;
            for(int j = 0; j < coef.length; j++, index++) {
                if(index >= 0) {
                    val += coef[j] * samples[index];
                }
            }

            index = startingCoefOutputInd;
            for(int j = 0; j < coefOutput.length; j++, index++) {
                if(index >= 0) {
                    val += coefOutput[j] * retArr[index];
                }
            }

            retArr[i] = val;
        }

        // Now we just perform do filtering for the rest, we don't need to check for the preceding elements anymore.
        startingCoefInd = 0;
        for(; i < samples.length; i++, startingCoefInd++, startingCoefOutputInd++) {
            val = 0;
            index = startingCoefInd;
            for(int j = 0; j < coef.length; j++, index++) {
                val += coef[j] * samples[index];
            }

            index = startingCoefOutputInd;
            for(int j = 0; j < coefOutput.length; j++, index++) {
                if(index >= 0) {
                    val += coefOutput[j] * retArr[index];
                }
            }

            retArr[i] = val;
        }

        return retArr;
    }



/* TODO: // ono to je asi celkem zbytecny protoze samotnej filtr je by copy
	private static byte[] runLowPassFilterByCopy(byte[] samples, int cutoffFreq) {
		byte[] retArr = Arrays.copyOf(samples, samples.length);
		runLowPassFilterByRef(retArr, cutoffFreq);
		return retArr;
	}
*/

    /**
     * Performs low pass filtering with cutoffFreq on given samples, which are supposed to be sampled at sampleRate.
     * @param samples are the samples to perform the low pass filter on.
     * @param cutoffFreq is the cut-off frequency of the filter.
     * @param coefCount is the number of the coefficients used for filtering (How many last samples should be used for calculating the current one in the filter). Usually the more the better filter.
     * @param sampleRate is the sampling rate of the given samples
     * @param numberOfChannels represents the number of channels
     * @param sampleSize is the size of 1 sample
     * @param frameSize is the size of 1 frame
     * @param isBigEndian true if the samples are in big endian, false otherwise.
     * @param isSigned true if the samples are signed numbers, false otherwise.
     * @return Returns copy of the samples array on which was performed low pass filter.
     * @throws IOException is thrown by method calculateMask if the sampleSize is invalid.
     */
    @Deprecated
    public static byte[] runLowPassFilter(byte[] samples, double cutoffFreq, int coefCount, int sampleRate,
                                          int numberOfChannels, int sampleSize, int frameSize,
                                          boolean isBigEndian, boolean isSigned) throws IOException {
        double[] coef = calculateCoefForLowPass(cutoffFreq, coefCount, sampleRate);
        return performNonRecursiveFilter(samples, coef, numberOfChannels, sampleSize, frameSize, isBigEndian, isSigned);
    }


    public static int runLowPassFilter(double[] samples, int samplesStartIndex,
                                       int numberOfChannels, int sampleRate,
                                       double cutoffFreq, int coefCount,
                                       double[] retArr, int retArrStartIndex, int retArrEndIndex) {
        double[] coef = calculateCoefForLowPass(cutoffFreq, coefCount, sampleRate);
        int retVal = performNonRecursiveFilter(samples, samplesStartIndex,
            coef, numberOfChannels, retArr, retArrStartIndex, retArrEndIndex);
        return retVal;
    }


    /**
     * Caculates coefficients for low pass filter and returns them in array.
     * @param cutOffFreq is the cut-off frequency of the filter
     * @param coefCount is the number of the coefficients which will be returned
     * @param sampleRate is the sampling rate of the samples on which will be used filtering.
     * @return Returns double array containing the coefficients for non-recursive low pass filtering.
     */
    public static double[] calculateCoefForLowPass(double cutOffFreq, int coefCount, int sampleRate) {
        double[] coefForCalc = new double[coefCount];
        double[] coef = new double[coefCount];
        int jump = sampleRate / coefCount;
        int currFreq = 0;

        // Calculate values which are used for calculating the coefficients for filters
        int index = 0;
        while(currFreq < cutOffFreq) {
            coefForCalc[index] = 1;
            currFreq += jump;
            index++;
        }
        if(index < coefForCalc.length) {   // So the jump in filter isn't abrupt (Page 208 Dodge - Computer music)
            coefForCalc[index] = 1 / (double)2;
            index++;
        }
        for(; index < coefForCalc.length; index++) {
            coefForCalc[index] = 0;
        }

        // Calculate coefs for filter
        for(int k = 0; k < (coefCount - 1) / (double)2; k++) {      // From Page 206 Dodge
            double currCoef = 0;
            for(int i = 1; i < (coefCount - 1) / (double)2; i++) {
                double tmp = (2 * Math.PI * i / coefCount) * (k - (coefCount - 1) / (double)2);
                currCoef += Math.abs(coefForCalc[i]) * Math.cos(tmp);
            }
            coef[k] =  coefForCalc[0] + 2 * currCoef;
            coef[k] /= coefCount;
        }

        // The rest is symmetric
        for(int k = 0; k < (coefCount - 1) / (double)2; k++) {
            coef[coefCount - k - 1] = coef[k];
        }

        // Normalize the coefficients
        double sum = 0;
        for(int i = 0; i < coef.length; i++) {
            sum += coef[i];
        }
        if(sum > 1) {
            for (int i = 0; i < coef.length; i++) {
                coef[i] /= sum;
            }
        }
        // Now we reverse the array
        reverse(coef);
        return coef;
    }


    /**
     * Reverses given array
     * @param arr is the given array
     */
    private static void reverse(double[] arr) {
        double tmp;
        for (int i = 0; i < arr.length / 2; i++) {
            tmp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = tmp;
        }
    }



    // TODO: If I want to write stream, I would have to do some workaround - like write the header
    // Then after that write the samples, and then fix the header to correct size
    public void saveAudio(String path, Type type) throws IOException {
        Program.saveAudio(path, this.decodedAudioFormat, this.song, type);
    }


    public static void saveAudio(String path, float sampleRate,
                                 int sampleSizeInBits,
                                 int numberOfChannels, boolean isSigned,
                                 boolean isBigEndian, byte[] input, Type type) throws IOException
    {
        AudioFormat af = new AudioFormat(sampleRate, sampleSizeInBits, numberOfChannels, isSigned, isBigEndian);
        saveAudio(path, af, input, type);
    }
    public static void saveAudio(String path, AudioFormat format, byte[] input, Type type) throws IOException {
        InputStream is = new ByteArrayInputStream(input);
        long frameLen = input.length / format.getFrameSize();
        saveAudio(path, format, is, frameLen, type);
    }

    public static void saveAudio(String path, AudioFormat format, byte[] input,
                                 int startIndex, int endIndex, Type type) throws IOException {
        InputStream is = new ByteArrayInputStream(input, startIndex, endIndex);
        long frameLen = (endIndex - startIndex) / format.getFrameSize();
        saveAudio(path, format, is, frameLen, type);
    }


    public static void saveAudio(String path, float sampleRate, int sampleSizeInBits, int numberOfChannels,
                                 boolean isSigned, boolean isBigEndian,
                                 InputStream input, long len, Type type) throws IOException
    {
        AudioFormat af = new AudioFormat(sampleRate, sampleSizeInBits, numberOfChannels, isSigned, isBigEndian);
        saveAudio(path, af, input, len, type);
    }
    public static void saveAudio(String path, AudioFormat format, InputStream input, long len, Type type) throws IOException {
        AudioInputStream ais = new AudioInputStream(input, format, len);
        saveAudio(path, ais, type);
    }


    public static void saveAudio(String path, AudioInputStream audioInputStream, Type type) {
        File f = new File(path + "." + type.getExtension());
        try {
            AudioSystem.write(audioInputStream, type, f);    // TODO: Tohle nefunguje kdyz se to nevejde do pameti ... tak proste ten inputstream dam ze souboru kdyz se to nevejde
        }
        catch(Exception e) {
            MyLogger.logException(e);
        }
    }


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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// BPM DETECTION ALGORITHMS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////
    // BPM Algorithm 1
    ////////////////////////////////////////////////////
    public int calculateBPMSimple() {
        writeVariables();

        int windowsLen = 43;    // Because 22050 / 43 == 512 == 1 << 9 ... 44100 / 43 == 1024 etc.
        int windowSize = sampleRate / windowsLen;
        windowSize = Program.convertToMultipleDown(windowSize, this.frameSize);
        double[] windows = new double[windowsLen];                       // TODO: Taky bych mel mit jen jednou asi ... i kdyz tohle je vlastne sampleRate specific
        return calculateBPMSimple(this.song, windowSize, windows, this.numberOfChannels, this.sampleSizeInBytes, this.frameSize,
                                  this.sampleRate, this.mask, this.isBigEndian, this.isSigned, 4);
    }


// TODO: Podle me kdyz vezmu jen mono signal, tak ty energie mi budou vychazet stejne
// TODO: A bude to rychlejsi... hlavne u tech comb filtru - to je tak vypocetne narocny, ze se bere jen par sekund
// TODO: http://archive.gamedev.net/archive/reference/programming/features/beatdetection/page2.html
// TODO: Ten derivation filter se mi nejak nezda - proc to nasobi fs(samplovaci frekvenci) a bere ten nasledujici sample
// TODO: filtry vetsinou berou jen ty predchozi - ale tak asi proc ne - podle me podobnyho vysledku dosahnu
// TODO: Tim ze vezmu ten soucasny a ten predchozi a ty zprumeruju
// TODO: A proc u toho sterea bere z leveho kanalu realny hodnoty a z praveho imaginarni - co kdybych mel 5 kanalu
// TODO: On to dela, protoze vezme ten levej kanal jako realny koeficienty a pravej jako imaginarni a na to posle FFT.

// The subbands in the algorithm means that we take some frequency bandwidth (we take the frequency bins in that
// bandwidth and work with them as it is 1. We just have 2D array instead of 1D and treat bandwidths separately // TODO:

// TODO: V tom R22 to s znaci soucasny subband a ws znaci jeho sirku - kolik je v nem binu
// TODO: Ten barycenter - je prostě že vezmu průměr z těch subbandů vážený tím bpm
// TODO: Tj. vydeleny souctem energii a to co delim je suma kde spolu nasobim tu BPM a tu energii v tom BPM ...
// TODO: beru z tech BPM nejakou funcki g (asi aby to vyslo lip ... ale neni zmineno jaka je ta funkce g)

    // sampleRate / windowSize == windows.length
     public static int calculateBPMSimple(byte[] samples, int windowSize, double[] windows, int numberOfChannels,
                                          int sampleSize, int frameSize,
                                          int sampleRate, int mask, boolean isBigEndian, boolean isSigned,
                                          int windowsBetweenBeats) {
        // TODO: DEBUG
        double maxEnergy = Double.MIN_VALUE;
        double minCoef = Double.MAX_VALUE;
        double maxCoef = Double.MIN_VALUE;
        double maxVariance = Double.MIN_VALUE;
        // TODO: DEBUG

        final int maxAbsValSigned = getMaxAbsoluteValueSigned(8 * sampleSize);     // TODO: Signed and unsigned variant

        int beatCount = 0;
        int sampleIndex = 0;
        int i;
        int windowSizeInBytes = windowSize * frameSize;
        int nextSampleIndex = windowSizeInBytes;
        double energySum = 0;
        double energyAvg;
        for(i = 0; i < windows.length; i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
            if(nextSampleIndex < samples.length) {
                windows[i] = getEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
                    isBigEndian, isSigned, maxAbsValSigned);
                energySum += windows[i];
            }
        }



         double maxValueInEnergy = ((double)windowSize) * maxAbsValSigned * maxAbsValSigned;     // max energy
         double maxValueInVariance = 2 * maxValueInEnergy;           // the val - avg (since avg = -val then it is 2*)
         // TODO: It is way to strict (The max variance can be much lower), but I don't see how could I make it more accurate
         maxValueInVariance *= maxValueInVariance;                   // Finally the variance of 1 window (we don't divide by the windows.length since we calculated for just 1 window as I said)
         // Just took 10000 because it worked quite nicely, but not for every sample rate,
         // so we have to multiply it with some value based on that
         double varianceMultFactor = 10000 * Math.pow(3.75, 44100d / sampleRate - 1);

        int windowsFromLastBeat = windowsBetweenBeats;
        int oldestIndexInWindows = 0;
        double currEnergy;
        double variance;
        double coef;
        while(nextSampleIndex < samples.length) {
            energyAvg = energySum / windows.length;
            currEnergy = getEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
                    isBigEndian, isSigned, maxAbsValSigned);
            variance = getVariance(energyAvg, windows);
            variance /= maxValueInVariance;

            variance *= varianceMultFactor;

            coef = -variance / maxValueInVariance + 1.4;        // TODO: pryc
            coef = -0.0025714 * variance + 1.5142857;
//            coef = -0.0025714 * maxValueInVariance * variance + 1.5142857;            // TODO: NE
//            coef = -2.5714 * variance + 1.5142857;                                      // TODO: NE
//            coef = -2.5714 * variance * 128 + 1.5142857;                              // TODO: NE - zmeni to - ale smerem nahoru, takze vlastne kdyz nad tim preymslim tak tohle naopak zvetsuje BPM a ne snizuje
                                                                                        // TODO: Musel bych jeste posunout tu konstantu smerem vys
//            coef = -2.5714 * variance * 1024 + 1.5142857;             // TODO: Poskoci o 10
//            coef = -variance + 1.4;               // Gives a bit bigger results then the results should be
            coef = -0.0025714 * variance + 1.5142857;
            coef = -0.0025714 * variance + 1.8;

//            energyAvg = energyAvg / (windowSize * (1 << (sampleSize * 8)));
            // TODO: DEBUG
//            System.out.println("!!!!!!!!!!!!!!!!");
//            System.out.println(maxValueInEnergy);
//            System.out.println(":" + coef + ":\t" + maxValueInEnergy + ":\t" + (variance / (maxValueInEnergy * maxValueInEnergy)));
//            System.out.println(currEnergy + ":\t" + coef * energyAvg + ":\t" + variance);
//            System.out.println("!!!!!!!!!!!!!!!!");
            // TODO: DEBUG
// TODO:
            if(currEnergy > coef * energyAvg) {
                if(windowsFromLastBeat >= windowsBetweenBeats) {
                    beatCount++;
                    windowsFromLastBeat = -1;
                }

                // TODO: DEBUG
//                ProgramTest.debugPrint("TODO: TEST", currEnergy, coef, energyAvg, coef * energyAvg);
                // TODO: DEBUG
            }

            // TODO: DEBUG
            minCoef = Math.min(coef, minCoef);
            maxCoef = Math.max(coef, maxCoef);
            maxEnergy = Math.max(energySum, maxEnergy);
            maxVariance = Math.max(variance, maxVariance);
// TODO: DEBUG

            // Again optimize the case when windows.length is power of 2
            if(windows.length % 2 == 0) {
                energySum = energySum - windows[oldestIndexInWindows % windows.length] + currEnergy;
                windows[oldestIndexInWindows % windows.length] = currEnergy;
            }
            else {
                if(oldestIndexInWindows >= windows.length) {
                    oldestIndexInWindows = 0;
                }
                energySum = energySum - windows[oldestIndexInWindows] + currEnergy;
                windows[oldestIndexInWindows] = currEnergy;
            }
            // TODO: DEBUG
//            ProgramTest.debugPrint("Window in simple BPM:", windows[oldestIndexInWindows]);          // TODO: DEBUG
            // TODO: DEBUG
            oldestIndexInWindows++;
            sampleIndex = nextSampleIndex;
            nextSampleIndex += windowSizeInBytes;
            windowsFromLastBeat++;
        }

         int bpm = convertBPM(beatCount, samples.length, sampleSize, numberOfChannels, sampleRate);

        // TODO: DEBUG
//         MyLogger.log("END OF BPM SIMPLE:\t" + minCoef + "\t" + maxCoef + "\t" + maxEnergy + "\t" + maxVariance, 0);
        ProgramTest.debugPrint("END OF BPM SIMPLE:", minCoef, maxCoef, maxEnergy, maxVariance);
         // TODO: DEBUG
         return bpm;
     }

     public static int convertBPM(int beats, int sampleCount, int sampleSize, int numberOfChannels, int sampleRate) {
         int sizeOfOneSecond = sampleSize * numberOfChannels * sampleRate;
         int bpm = (int) (beats / ((double)sampleCount / (60 * sizeOfOneSecond)));
         return bpm;
     }




    private static double getEnergy(byte[] samples, int windowSize, int numberOfChannels, int sampleSize,
                                    int index, int mask, boolean isBigEndian, boolean isSigned,
                                    int maxAbsoluteValueSigned) {
        double energy = 0;

        for(int i = 0; i < windowSize; i++) {
            for(int j = 0; j < numberOfChannels; j++, index += sampleSize) {
                int val = convertBytesToInt(samples, sampleSize, mask, index, isBigEndian, isSigned);
                if(!isSigned) {     // Convert unsigned sample to signed
                    val -= maxAbsoluteValueSigned;
                }
                energy += val*(double)val;
            }
        }

        return energy;
     }

    // Currently is expected to run only on small labelReferenceArrs, so there is no need to parallelize this method.
     private static double getVariance(double average, double[] values) {
        double variance = 0;
        double val;
        for(int i = 0; i < values.length; i++) {
            val = values[i] - average;
            variance += val*val;
        }

        return variance / values.length;
     }






    ////////////////////////////////////////////////////
    // BPM Algorithm 2
    ////////////////////////////////////////////////////
    // TODO: Mozna vymazat tyhle 2 metody a volat to primo - nebo aspon zmenit jmeno
    public int getBPMSimpleWithFreqDomainsWithVarianceOld(SubbandSplitterIFace splitter) {
        return calculateBPMSimpleWithFreqBands(splitter.getSubbandCount(), splitter, 4.5, 4, 0);
    }

    public int getBPMSimpleWithFreqDomainsWithVarianceNew(SubbandSplitterIFace splitter) {
// TODO:
//        return calculateBPMSimpleWithFreqBands(subbandCount, splitter, 2.72, 4, 0.16);
//        return calculateBPMSimpleWithFreqBands(subbandCount, splitter, 3.72, 4, 0.0); NE
//        return calculateBPMSimpleWithFreqBands(subbandCount, splitter, 7.72, 4, 0.0); NE uz to je moc uz jsou ty veci co nejsou BPM na nule
//        return calculateBPMSimpleWithFreqBands(subbandCount, splitter, 2.72, 4, 2);
//        return calculateBPMSimpleWithFreqBands(subbandCount, splitter, 3.4, 4, 0.16);

//        return calculateBPMSimpleWithFreqBands(subbandCount, splitter, 2.72, 5, 0.16); Asi jo - lepsi vysledky o neco
        // This gives the best results
        return calculateBPMSimpleWithFreqBands(splitter.getSubbandCount(), splitter,
                2.72, 6, 0.16);
//        return calculateBPMSimpleWithFreqBands(subbandCount, splitter, 2.72, 7, 0.16);
// TODO:
    }


     public int calculateBPMSimpleWithFreqBands(int subbandCount, SubbandSplitterIFace splitter,
                                                double coef, int windowsBetweenBeats,
                                                double varianceLimit) {  // TODO: Bud predavat ty referenci nebo ne ... ono to nedava uplne smysl to predavat referenci
        // TODO: Dava smysl ze to vytvorim tady ... protoze to vyrabim v zavislosti na sample rate a tak


         int historySubbandsCount = 43;    // Because 22050 / 43 == 512 == 1 << 9 ... 44100 / 43 == 1024 etc.
         int windowSize = this.sampleRate / historySubbandsCount;
         int powerOf2After = getFirstPowerOfNAfterNumber(windowSize, 2);
         int powerOf2Before = powerOf2After / 2;
         int remainderBefore = windowSize - powerOf2Before;
         int remainderAfter = powerOf2After - windowSize;
         if(remainderAfter > remainderBefore) {       // Trying to get power of 2 closest to the number ... for fft efficiency
             windowSize = powerOf2Before;
         }
         else {
             windowSize = powerOf2After;
         }

         int mod = windowSize % this.frameSize;     // But not always is the power of 2 divisible by the frameSize
         // TODO: DEBUG
//         ProgramTest.debugPrint("window size (2nd bpm alg):", windowSize);        // TODO: remove
         // TODO: DEBUG
         windowSize += mod;
         DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);
         double[][] subbandEnergies = new double[historySubbandsCount][subbandCount];

         try {
             return calculateBPMSimpleWithFreqBands(this.song, this.sampleSizeInBytes, this.sampleRate,
                 windowSize, this.isBigEndian, this.isSigned, this.mask, this.maxAbsoluteValue, fft, splitter,
                     subbandEnergies, coef, windowsBetweenBeats, varianceLimit);
         }
         catch (IOException e) {
             return -1;             // TODO:
         }
     }




    public static int getFirstPowerOfNBeforeNumber(int startNumber, int num, int n) {
        int result = getFirstPowerOfNAfterNumber(startNumber, num, n);
        return result / n;
    }

    public static int getFirstPowerOfNAfterNumber(int startNumber, int num, int n) {
        int result = startNumber;

        while(result <= num) {
            result *= n;
        }

        return result;
    }


    public static int getFirstPowerExponentOfNBeforeNumber(int startNumber, int num, int n) {
        int e = getFirstPowerExponentOfNAfterNumber(startNumber, num, n);
        return e - 1;
    }

    public static int getFirstPowerExponentOfNAfterNumber(int startNumber, int num, int n) {
        int result = startNumber;
        int e = 0;

        while(result <= num) {
            result *= n;
            e++;
        }

        return e;
    }


    public static int getFirstPowerOfNBeforeNumber(int num, int n) {
        return getFirstPowerOfNBeforeNumber(1, num, n);
    }

     public static int getFirstPowerOfNAfterNumber(int num, int n) {
        return getFirstPowerOfNAfterNumber(1, num, n);
     }


    public static int getFirstPowerExponentOfNBeforeNumber(int num, int n) {
        return getFirstPowerExponentOfNBeforeNumber(1, num, n);
    }

    public static int getFirstPowerExponentOfNAfterNumber(int num, int n) {
        return getFirstPowerExponentOfNAfterNumber(1, num, n);
    }


    /**
     * Tests if number num is power of n.
     * @param num is the number to test.
     * @param n is the power.
     * @return Returns -1 if it num is not i-th power of n, returns i otherwise.
     */
    public static int testIfNumberIsPowerOfN(int num, int n) {
        int result = 1;

        int i = 0;
        while(result < num) {
            result *= n;
            i++;
        }
        if(result == num) {
            return i;
        }
        else {
            return -1;
        }
    }


    /**
     * Tests if number num is power of n.
     * @param num is the number to test.
     * @param n is the power.
     * @return Returns -1 if it num is not i-th power of n, returns i otherwise. Returns -2 if the number is not integer/
     */
    public static int testIfNumberIsPowerOfN(double num, int n) {
        if(num == Math.floor(num)) {
            return testIfNumberIsPowerOfN((int)num, n);
        }
        else {
            return -2;
        }
    }


    // TODO: Dont create new array in FFT only measures
    // TODO: Verze s tim ze se to bude delat po 2jicich ta FFT - s realnou i komplexni casti
    // TODO: THIS IS VERSION FOR MONO SIGNAL
    // TODO: double[][][] subbandEnergies in multiple channel case
//    public static int getBPMSimpleWithFreqDomains(byte[] samples, int sampleSize, int sampleSizeInBits,
//                                                  int windowSize, boolean isBigEndian, boolean isSigned,
//                                                  int mask, int maxAbsoluteValue, DoubleFFT_1D fft, SubbandSplitterIFace splitter,
//                                                  double[][] subbandEnergies // TODO: 1D are the past values, 2D are the subbands
//                                                  ) throws IOException { // TODO: Predpokladam ,ze subbandEnergies uz je alokovany pole o spravny velikosti
//
///*
//        int bpm = 0;
//        double fft;
//        int windowSizeInBytes = sampleSize * windowSize;        // TODO: * frameSize
//        for(i = 0; i < windows.length; i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
//            if(nextSampleIndex < samples.length) {
//                windows[i] = getEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
//                    isBigEndian, isSigned);
//                avg += windows[i];
//            }
//        }
//
//        for (int index = 0; index < samples.length; index += jumpInBytes) {
//
//            // TODO: Tahle metoda vypocita jen cast FFT o dane velikosti (tedy vraci pole doublu)
//            // TODO: V obecnem pripade tahle metoda bude bud taky vracet double[] s tim ze proste vezme hodnoty
//            // TODO: kanalu (pres preskakovani tj numberOfChannels * sampleSize je dalsi index ktery mam vzit)
//            // TODO: nebo proste tu metodu udelat tak aby vratila double[][] kde to bude double[numberOfChannels][windowSize]
//            // TODO: takze tam musim dat index
////            double[] fft = calculateFFTOnlyMeasuresGetOnlyOnePart(samples, sampleSize, sampleSizeInBits, windowSize, isBigEndian, isSigned);
//            // TODO: !!!!!!!!! Tak jeste jinak ... rovnou spocitam energie tech subbandu ... zase jen v ty jedny casti
//            // TODO: !!!!!!!!! Pro vic kanalu zase musim pres double[][]
//            double[] subbandEnergies = getSubbandEnergiesUsingFFT(...); // TODO: !!!! Zase to delat spis jen pres referenci
//
//        }
//        return bpm;
// */
//
//
//// TODO:
//        int numberOfChannels = 1;
//        int frameSize = sampleSize;
//// TODO:
//
//        int subbandCount = subbandEnergies[0].length;
//        int historySubbandsCount = subbandEnergies.length;
//
//        double[] fftArr = new double[windowSize];
//
//        // TODO: Zbytecny staci aby to pole melo polovicni velikost (viz kod pod tim)
////double[] measuresArr = new double[windowSize];        // TODO: Muzu pouzit fftArr jako measuresArr, ale takhle to je prehlednesji a navic pak chci ty vysledky fft ulozit do souboru abych to uz nemusel pocitat
//        double[] measuresArr;
//        if(windowSize % 2 == 0) {			// It's even
//            measuresArr = new double[windowSize / 2 + 1];
//        } else {
//            measuresArr = new double[(windowSize + 1) / 2];
//        }
//
//
//        int bpm = 0;
//        int sampleIndex = 0;
//        int i;
//        int windowSizeInBytes = windowSize * sampleSize;     // TODO: frameSize v vice multichannel variante
//        int nextSampleIndex = windowSizeInBytes;
//        //TODO: Asi zase predat jako argument ... tady je to pole protoze kazdy subband ma vlastni average
//        double[] avgs = new double[subbandCount];  // TODO: Pro vice kanalove to bude double[][]
//        double[] currEnergies = new double[subbandCount];
//        for(i = 0; i < subbandEnergies.length; // TODO: U multi-channel varianty to bude subbandEnergies[0].length
//            i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
//            if(nextSampleIndex < samples.length) {
//                getSubbandEnergiesUsingFFT(samples, subbandEnergies[i], sampleIndex,//int startIndex,
//                    numberOfChannels, sampleSize, frameSize, mask, fft, fftArr, measuresArr,
//                    maxAbsoluteValue, isBigEndian, isSigned, splitter);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
//// TODO:                subbandEnergies[i] = currEnergies;
//                for(int j = 0; j < subbandEnergies[i].length; j++) {
//                    avgs[j] += subbandEnergies[i][j];
//                }
//            }
//        }
//
//        double coef = 20;
//        double avgAfterDiv;
//
//        int oldestIndexInSubbands = 0;
//        while(nextSampleIndex < samples.length) {
//            getSubbandEnergiesUsingFFT(samples, currEnergies, sampleIndex,//int startIndex,
//                numberOfChannels, sampleSize, frameSize, mask, fft, fftArr, measuresArr,
//                maxAbsoluteValue, isBigEndian, isSigned, splitter);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
//            //            currEnergies = getSubbandEnergiesUsingFFT(...);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
//
//            int j = 0;
//            for(; j < currEnergies.length; j++) {
//                avgAfterDiv = avgs[j] / historySubbandsCount; // TODO:
//                System.out.println(currEnergies[j] + ":\t" + avgAfterDiv + ":\t" + (coef * avgAfterDiv));
//                if (currEnergies[j] > coef * avgAfterDiv) {        // TODO: Tady beru ze kdyz je beat na libovolnym mistem - pak typicky budu chtit brat beaty jen z urcitych frekvencnich pasem
//                    bpm++;
//                    break;
//                }
//                updateEnergySumsAndSubbands(j, oldestIndexInSubbands, avgs, currEnergies[j], subbandEnergies);
//            }
//
//            // TODO: I do this because of the break, I found beat but I still have to update the values
//            // TODO: Ideally I want to do this in the previous for cycle,
//            for(; j < currEnergies.length; j++) {
//                updateEnergySumsAndSubbands(j, oldestIndexInSubbands, avgs, currEnergies[j], subbandEnergies);
//            }
//
//            oldestIndexInSubbands++;
//            sampleIndex = nextSampleIndex;
//            nextSampleIndex += windowSizeInBytes;
//
//
//            // Again optimize the case when windows.length is power of 2
//            if (historySubbandsCount % 2 == 0) {       // TODO: U multi-channel verze chci subbandEnegies[i].length
//                oldestIndexInSubbands %= historySubbandsCount; // TODO: U multi-channel verze chci subbandEnegies[i].length
//            } else {
//                if (oldestIndexInSubbands >= historySubbandsCount) { // TODO: U multi-channel verze chci subbandEnegies[i].length
//                    oldestIndexInSubbands = 0;
//                }
//            }
//        }
//
//        return bpm;
//    }


    // TODO: Dont create new array in FFT only measures
    // TODO: Verze s tim ze se to bude delat po 2jicich ta FFT - s realnou i komplexni casti
    // TODO: THIS IS VERSION FOR MONO SIGNAL
    // TODO: double[][][] subbandEnergies in multiple channel case
    public static int calculateBPMSimpleWithFreqBands(byte[] samples, int sampleSize, int sampleRate,
                                                      int windowSize, boolean isBigEndian, boolean isSigned,
                                                      int mask, int maxAbsoluteValue, DoubleFFT_1D fft, SubbandSplitterIFace splitter,
                                                      double[][] subbandEnergies, // TODO: 1D are the past values, 2D are the subbands
                                                      double coef, int windowsBetweenBeats, double varianceLimit
    ) throws IOException { // TODO: Predpokladam ,ze subbandEnergies uz je alokovany pole o spravny velikosti
        // TODO: REMOVE
        final double oldCoef = coef;      // TODO: OLD COEF
        double todoMaxEnergy = -1;
        // TODO: REMOVE



        //                double coefBasedOnSampleRate = coef / 1.3;//Math.pow(1., 44100 / (double)sampleRate - 1);

        double divFactor = 1;
        if(sampleRate < 44100) {
            // TODO: REMOVE
//                    divFactor = 1 + 0.3 * ((44100 / (double) sampleRate) - 1);
//                    divFactor = 1.825;
            // TODO: REMOVE

            double log = Program.logGeneral((44100 / (double) sampleRate) - 1, 2.36);
            divFactor = 1 + 0.3 * (log + 1);

// TODO: REMOVE
//                    double log = Program.logGeneral((44100 / (double) sampleRate) - 1, 1.5);
//                    divFactor = 2;
//                    divFactor = 1.5;
//                    divFactor = 1 + 0.49 * (log + 1);
// TODO: REMOVE
        }
        double coefBasedOnSampleRate = coef / divFactor;        // Has to be done because, the lower the sample rate, the lower needs to be the coefficient

/*
        int beatCount = 0;
        double fft;
        int windowSizeInBytes = sampleSize * windowSize;        // TODO: * frameSize
        for(i = 0; i < windows.length; i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
            if(nextSampleIndex < samples.length) {
                windows[i] = getEnergy(samples, windowSize, numberOfChannels, sampleSize, sampleIndex, mask,
                    isBigEndian, isSigned);
                avg += windows[i];
            }
        }

        for (int index = 0; index < samples.length; index += jumpInBytes) {

            // TODO: Tahle metoda vypocita jen cast FFT o dane velikosti (tedy vraci pole doublu)
            // TODO: V obecnem pripade tahle metoda bude bud taky vracet double[] s tim ze proste vezme hodnoty
            // TODO: kanalu (pres preskakovani tj numberOfChannels * sampleSize je dalsi index ktery mam vzit)
            // TODO: nebo proste tu metodu udelat tak aby vratila double[][] kde to bude double[numberOfChannels][windowSize]
            // TODO: takze tam musim dat index
//            double[] fft = calculateFFTOnlyMeasuresGetOnlyOnePart(samples, sampleSize, sampleSizeInBits, windowSize, isBigEndian, isSigned);
            // TODO: !!!!!!!!! Tak jeste jinak ... rovnou spocitam energie tech subbandu ... zase jen v ty jedny casti
            // TODO: !!!!!!!!! Pro vic kanalu zase musim pres double[][]
            double[] subbandEnergies = getSubbandEnergiesUsingFFT(...); // TODO: !!!! Zase to delat spis jen pres referenci

        }
        return beatCount;
 */


// TODO:
        int numberOfChannels = 1;
        int frameSize = sampleSize;
// TODO:
// TODO:        double varianceLimit = 0;     // TODO:
        int windowsFromLastBeat = windowsBetweenBeats;
        int subbandCount = subbandEnergies[0].length;
        int historySubbandsCount = subbandEnergies.length;
        double[] fftArr = new double[windowSize];

        // TODO: Zbytecny staci aby to pole melo polovicni velikost (viz kod pod tim)
//double[] measuresArr = new double[windowSize];        // TODO: Muzu pouzit fftArr jako measuresArr, ale takhle to je prehlednesji a navic pak chci ty vysledky fft ulozit do souboru abych to uz nemusel pocitat
        double[] measuresArr = new double[Program.getBinCountRealForward(windowSize)];


        int beatCount = 0;
        int sampleIndex = 0;
        int i;
        int windowSizeInBytes = windowSize * sampleSize;     // TODO: frameSize v vice multichannel variante
        int nextSampleIndex = windowSizeInBytes;
        //TODO: Asi zase predat jako argument ... tady je to pole protoze kazdy subband ma vlastni average
        double[] energySums = new double[subbandCount];  // TODO: Pro vice kanalove to bude double[][]
        double[] currEnergies = new double[subbandCount];
        for(i = 0; i < subbandEnergies.length; // TODO: U multi-channel varianty to bude subbandEnergies[0].length
            i++, sampleIndex = nextSampleIndex, nextSampleIndex += windowSizeInBytes) {
            if(nextSampleIndex < samples.length) {
                // TODO: Vymazat ten startIndex
                getSubbandEnergiesUsingFFT(samples, subbandEnergies[i], sampleIndex,//int startIndex,
                    numberOfChannels, sampleSize, frameSize, mask, fft, fftArr, measuresArr,
                    maxAbsoluteValue, isBigEndian, isSigned, splitter);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
// TODO:                subbandEnergies[i] = currEnergies;
                for(int j = 0; j < subbandEnergies[i].length; j++) {
                    energySums[j] += subbandEnergies[i][j];
                }
            }
        }

        double avg;


        int oldestIndexInSubbands = 0;
        while(nextSampleIndex < samples.length) {
            // TODO: BPM NOVY
            boolean hasBeat = false;
            // TODO: BPM NOVY
            // TODO: Ten startIndex pod timhle dat pryc
            getSubbandEnergiesUsingFFT(samples, currEnergies, sampleIndex,//int startIndex,
                numberOfChannels, sampleSize, frameSize, mask, fft, fftArr, measuresArr,
                maxAbsoluteValue, isBigEndian, isSigned, splitter);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy
            //            currEnergies = getSubbandEnergiesUsingFFT(...);       // TODO: Chci predat subbandEnergies[i] referenci - urcite nechci vytvaret novy

            // This is version for Constant splitter The commented coef = 2.5 ... is for logaritmic, but the version with constant seems to work very good
            int j = 0;
            for(; j < currEnergies.length; j++) {
                todoMaxEnergy = Math.max(currEnergies[j], todoMaxEnergy);       // TODO: Finding the difference in coefs

                avg = energySums[j] / historySubbandsCount; // TODO:
                double variance = getVariance(avg, subbandEnergies, j);
                // TODO: OLD - REMOVE
//                coef = 3;
//                    coef = 6;
                // TODO: OLD - REMOVE

         //       coef = 2.5 + 10000 * variance; For logarithmic with subbandCount == 32 and that version doesn't contain the if with varianceLimit
//                System.out.println(currEnergies[j] + ":\t" + avg + ":\t" + (coef * avg));

                // TODO: DEBUG
//                if(variance > 150) {
//                    ProgramTest.debugPrint("Variance >150:", variance);
//                }
//                if(energySums[j] > 50) {
//                    ProgramTest.debugPrint("energy >50:", energySums[j]);
//                }
//
//                ProgramTest.debugPrint("Variance:", variance);
//                ProgramTest.debugPrint("energy:", energySums[j]);
                // TODO: DEBUG


                // TODO: ENERGIE TED
//                variance *= 5000;
//                coef = oldCoef - variance * (0.0025714 / 2);

//                coef = oldCoef - variance;

                // Code from BPM Simple
//                variance *= 10000;
//                coef = -0.0025714 * variance + 1.8;
                // Code from BPM Simple

                // Modified Code from BPM Simple
//                variance *= 5000;
//                coef = -0.0025714 * variance + 3.6;
//                coef = 10;

//                coef = 3;
                // Modified Code from BPM Simple
                // TODO: ENERGIE TED

                // TODO: DEBUG
                // TODO: Tady beru ze kdyz je beat na libovolnym mistem - pak typicky budu chtit brat beaty jen z urcitych frekvencnich pasem
                if (currEnergies[j] > coefBasedOnSampleRate * avg) {
//                if (currEnergies[j] > coef / Math.max(1, (((44100 / (double)sampleRate) - 1)) * 1) * avg) {
                    // TODO: DEBUG
//                    System.out.println("---------------" + variance);
                    // TODO: DEBUG
                    // TODO: not used anymore - the variance just doesn't seem to work.
//                    double varianceLimit = 0.0000001;
//                    varianceLimit = 1;
//                    varianceLimit = 20;
//                    varianceLimit = 40;
//                    varianceLimit = 75;
//                    varianceLimit = 150;
//                    varianceLimit = 250;
//                    varianceLimit = 300;
                    // TODO: not used anymore - the variance just doesn't seem to work.

/*// TODO: K nicemu, lepsi je mit varianci zahrnutou v tom coef                   */ if(variance > varianceLimit) {
    // TODO: BPM NOVY
//    if(!hasBeat) {
//        beatCount++;
//        hasBeat = true;
//    }
    ////////////
                        if(windowsFromLastBeat >= windowsBetweenBeats) {
//                            System.out.println(sampleIndex + ":\t" + j + ":\t" + samples.length);
                            beatCount++;
                            windowsFromLastBeat = -1;
                            hasBeat = true;
                            break;
/*// TODO:                        */}
    // TODO: BPM NOVY
                    }
                }
                updateEnergySumsAndSubbands(j, oldestIndexInSubbands, energySums, currEnergies[j], subbandEnergies);
            }

            if(hasBeat) {
                for (; j < currEnergies.length; j++) {
                    updateEnergySumsAndSubbands(j, oldestIndexInSubbands, energySums, currEnergies[j], subbandEnergies);
                }
            }

            oldestIndexInSubbands++;
            sampleIndex = nextSampleIndex;
            nextSampleIndex += windowSizeInBytes;
            windowsFromLastBeat++;


            // Again optimize the case when windows.length is power of 2
            if (historySubbandsCount % 2 == 0) {       // TODO: U multi-channel verze chci subbandEnegies[i].length
                oldestIndexInSubbands %= historySubbandsCount; // TODO: U multi-channel verze chci subbandEnegies[i].length
            } else {
                if (oldestIndexInSubbands >= historySubbandsCount) { // TODO: U multi-channel verze chci subbandEnegies[i].length
                    oldestIndexInSubbands = 0;
                }
            }
        }

        ProgramTest.debugPrint("MAX_ENERGY:", todoMaxEnergy);
        int bpm = convertBPM(beatCount, samples.length, sampleSize, numberOfChannels, sampleRate);
        return bpm;
    }

    private static double getVariance(double average, double[][] values, int subbandIndex) {
        double variance = 0;
        double val;
        for(int i = 0; i < values.length; i++) {
            val = values[i][subbandIndex] - average;
            variance += val*val;
        }

        return variance / values.length;
    }


    public static void getSubbandEnergiesUsingFFT(byte[] samples, double[] currEnergies,
                                                  int startIndex,
                                                  int numberOfChannels,
                                                  int sampleSize,
                                                  int frameSize,
                                                  int mask,
                                                  DoubleFFT_1D fft,
                                                  double[] fftArray, double[] fftArrayMeasures,
                                                  int maxAbsoluteValue,
                                                  boolean isBigEndian,
                                                  boolean isSigned,
                                                  SubbandSplitterIFace splitter) {
        calculateFFTRealForward(samples, startIndex, numberOfChannels, sampleSize,
                frameSize, mask, fft, fftArray, maxAbsoluteValue, isBigEndian, isSigned);


        // TODO: NORMALIZACE
//        for(int i = 0; i < fftArray.length; i++) {
//            fftArray[i] /= (fftArray.length / 2);
//        }
        // TODO: NORMALIZACE



        convertResultsOfFFTToRealRealForward(fftArray, fftArrayMeasures);
        for(int subband = 0; subband < currEnergies.length; subband++) {
            currEnergies[subband] = splitter.getSubbandEnergy(fftArrayMeasures, currEnergies.length, subband);
        }
    }

    // The oldestIndexInSubbands should already be in range from 0 to energySums.length (== subbandCount)
    private static void updateEnergySumsAndSubbands(int subbandInd, int oldestIndexInSubbands, double[] energySums,
                                                    double currEnergy, double[][] subbandEnergies) {
        energySums[subbandInd] += -subbandEnergies[oldestIndexInSubbands][subbandInd] + currEnergy;
        subbandEnergies[oldestIndexInSubbands][subbandInd] = currEnergy;
    }



    ////////////////////////////////////////////////////
    // BPM Algorithms 3 - are implementing getBPMUsingCombFilterIFace
    ////////////////////////////////////////////////////


    // TODO: Remove ... nepotrebuju measury
    // TODO: Tyhle pole si chci urcite uchovat abych je nemusel pro ten comb filter delat pokazdy znova
    // TODO: We can do small trick for better memory managment, 1 sample == 1 byte, and the non-zero value would be
    // TODO: max value of size sampleSize
    // TODO: Returns THE FFT RESULTS!!!
    public static double[][][] getBPMArraysFFTMeasures(int lowerBoundBPM, int upperBoundBPM, int jumpBPM, int sampleRate,
                                                       double numberOfSeconds, int fftWindowSize, int numberOfBeats) {
        if(upperBoundBPM < lowerBoundBPM) {
            return null;
        }
        int arrayCount = 1 + (upperBoundBPM - lowerBoundBPM) / jumpBPM;
        int arrayLen = (int)(sampleRate * numberOfSeconds);
        DoubleFFT_1D fft = new DoubleFFT_1D(fftWindowSize);
        int fftWindowsCount = arrayLen / fftWindowSize;     // TODO: Maybe solve special case when fftWindowsCount == 0
        double[][][] bpmFFTArrays = new double[arrayCount][fftWindowsCount][];
        double[] fftArr = new double[fftWindowSize];

        int impulsePeriod;
        for(int i = 0, currBPM = lowerBoundBPM; i < bpmFFTArrays.length; i++, currBPM += jumpBPM) {
            int totalIndexInBpm = 0;
            impulsePeriod = (60 * sampleRate) / currBPM;
            int beatCount = 0;
            for(int j = 0; j < fftWindowsCount; j++) {
                for (int k = 0; k < fftArr.length; k++, totalIndexInBpm++) {
                    if(beatCount < numberOfBeats) {
                        if ((totalIndexInBpm % impulsePeriod) == 0) {
                            fftArr[k] = 1;
                            beatCount++;
                        }
                        else {
                            fftArr[k] = 0;
                        }
                    }
                    else {
                        fftArr[k] = 0;
                    }
                }

// TODO:                System.out.println(currBPM + "\tImpulsePeriod:\t" + impulsePeriod);
                fft.realForward(fftArr);
                bpmFFTArrays[i][j] = convertResultsOfFFTToRealRealForward(fftArr);
            }
        }

        return bpmFFTArrays;
    }

    // TODO: Tyhle pole si chci urcite uchovat abych je nemusel pro ten comb filter delat pokazdy znova
    // TODO: We can do small trick for better memory managment, 1 sample == 1 byte, and the non-zero value would be
    // TODO: max value of size sampleSize
    // TODO: Returns THE FFT RESULTS!!!
    public static double[][][] getBPMArraysFFT(int lowerBoundBPM, int upperBoundBPM, int jumpBPM, int sampleRate,
                                               double numberOfSeconds, int fftWindowSize, int numberOfBeats) {      // TODO: Maybe later pass the fft with the length, so it doesn't have to allocated over and over again ... but it's only smal optimazation
        if(upperBoundBPM < lowerBoundBPM) {
            return null;
        }


        int arrayCount = 1 + (upperBoundBPM - lowerBoundBPM) / jumpBPM;
        int arrayLen = (int)(sampleRate * numberOfSeconds);
        DoubleFFT_1D fft = new DoubleFFT_1D(fftWindowSize);
        int fftWindowsCount = arrayLen / fftWindowSize;     // TODO: Maybe solve special case when fftWindowsCount == 0
        double[][][] bpmFFTArrays = new double[arrayCount][fftWindowsCount][];

        int impulsePeriod;
        for(int i = 0, currBPM = lowerBoundBPM; i < bpmFFTArrays.length; i++, currBPM += jumpBPM) {
            int totalIndexInBpm = 0;
            impulsePeriod = (60 * sampleRate) / currBPM;
            int beatCount = 0;
            for(int j = 0; j < fftWindowsCount; j++) {
                double[] fftArr = new double[fftWindowSize];
                // TODO: Koment po dlouhy dobe - nechapu proc to nenastavuju na 1 pres modulo
                for (int k = 0; k < fftArr.length; k++, totalIndexInBpm++) {
                    int mod = totalIndexInBpm % impulsePeriod;

                    if(beatCount < numberOfBeats) {
                        if (mod == 0) { // TODO: !!!!!!!!! Kdyz ted vim ze se nedela FFT z tech kousku ale z celyho tak muzu nastavit proste kazdej sample na nasobku impulsePeriod na 1
                            fftArr[k] = 1;
                            beatCount++;
// TODO:                            System.out.println("bpmArrs:\t" + beatCount + "\t" + totalIndexInBpm + "\t" + currBPM);
                        }
                    }
                    else {
// TODO:                        System.out.println("break");
                        break;
                    }

/*

                    // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!

                    int sizeOfPeak = 0;      // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!
                    if (mod <= sizeOfPeak || mod > impulsePeriod - sizeOfPeak) {
                        fftArr[k] = 1;
                    }
*/
                }

//                System.out.println(currBPM + "\tImpulsePeriod:\t" + impulsePeriod);
                fft.realForward(fftArr);

//                fftArr = convertResultsOfFFTToRealRealForward(fftArr);  // TODO:

                bpmFFTArrays[i][j] = fftArr;
// TODO: Vymazat
/*
if(currBPM == 60) {
    for (int l = 0; l < fftArr.length; l++) {
        System.out.println(fftArr[l]);
    }
}
*/
            }
        }

        return bpmFFTArrays;
    }




    // TODO: Melo by to byt double[][] u obou? u toho bpmArray to zavisi na velikosti okna ... kdyz je to mocnina 2ky tak pak jsou vsechny ty pole stejny
    // TODO: ... ale tak to furt muzu mit double[][] akorat si budou odkazovat na stejny pole ... takze skoro zadarmo jen par referenci me to bude stat
    public static double getCombFilterEnergyRealForward(double[][] fftResults, double[][] bpmArray) {
        double energy = 0;

// TODO: Jen debug        if (fftResults.length != 1 || bpmArray.length != 1) System.exit(-10);       // TODO:


        for(int i = 0; i < fftResults.length; i++) {
            energy += getCombFilterEnergyRealForward(fftResults[i], bpmArray[i]);
        }

        return energy;
    }


    // TODO:
    public static void getCombFilterEnergies(double[] fftResult, double[][][] bpmArray, double[] energies) {
        for (int i = 0; i < bpmArray.length; i++) {
            for(int j = 0; j < bpmArray[i].length; j++) {
               energies[i] += getCombFilterEnergyRealForward(fftResult, bpmArray[i][j]);
            }
        }
    }


    // TODO: Ted nevim jestli to ma byt stejne jako u getCombFilterEnergyRealForward ... kde pocitam measury misto toho co delam tady (tj nejdriv to vynasobim a pak z vysledku vezmu measury)
    public static double getCombFilterEnergyRealForwardFull(double[] fftResult, double[] bpmArray) {      // TODO: "Stereo" verze
        double real;
        double imag;
        double energy = 0;

        for(int i = 0; i < fftResult.length; i = i + 2) {
            real = fftResult[i] * bpmArray[i] - fftResult[i+1] * bpmArray[i+1];
            real *= real;
            imag = fftResult[i] * bpmArray[i+1] + fftResult[i+1] * bpmArray[i];
            imag *= imag;
            energy += real + imag;
        }

        return energy;
    }

//     From documentation:
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]
    // TODO: Tohle je skoro konvoluce, akorat vysledky neukladame do pole ktere bude obsahovat vysledek konvoluce ale pocitame rovnou energii
    // TODO: A energii pocitame tak ze bereme vysledky konvoluce na druhou (realnou a imaginarni slozku zvlast) (protoze pocitame absolutni hodnotu)
    public static double getCombFilterEnergyRealForward(double[] fftResult, double[] bpmArray) {      // TODO: Monoverze
        double energy;              // TODO: mozna takhle prepsat i ten prevod na realny ... je to prehlednejsi
        double real;                // TODO: Ten prevod na realny mozna ani nebude dobre
        double imag;
        if(fftResult.length % 2 == 0) {			// It's even
            real = fftResult[0] * bpmArray[0];
            energy = calculateComplexNumMeasure(real, 0);
            real = fftResult[1] * bpmArray[1];      // TODO: Prehozeny poradi bylo to zatim for cyklem ... v te convertImagToReal to delat nemusim protoze tam to prevadim do pole polovicni velikosti
            energy += calculateComplexNumMeasure(real, 0);
            for(int i = 2; i < fftResult.length; i = i + 2) {
                real = fftResult[i] * bpmArray[i] - fftResult[i+1] * bpmArray[i+1];
                imag = fftResult[i] * bpmArray[i+1] + fftResult[i+1] * bpmArray[i];
                energy += calculateComplexNumMeasure(real, imag);
            }
        } else {
            real = fftResult[0] * bpmArray[0];
            energy = calculateComplexNumMeasure(real, 0);
            for(int i = 2; i < fftResult.length - 1; i = i + 2) {
                real = fftResult[i] * bpmArray[i] - fftResult[i+1] * bpmArray[i+1];
                imag = fftResult[i] * bpmArray[i+1] + fftResult[i+1] * bpmArray[i];
                energy += calculateComplexNumMeasure(real, imag);
            }

            real =  fftResult[fftResult.length - 1] * bpmArray[fftResult.length - 1] - fftResult[1] * bpmArray[1];
            imag = fftResult[fftResult.length - 1] * bpmArray[1] + fftResult[1] * bpmArray[fftResult.length - 1];
            energy += calculateComplexNumMeasure(real, imag);
        }

        return energy;
    }


//    // From documentation:
////	if n is even then
////	 a[2*k] = Re[k], 0<=k<n/2
////	 a[2*k+1] = Im[k], 0<k<n/2
////	 a[1] = Re[n/2]
////
////
////	if n is odd then
////	 a[2*k] = Re[k], 0<=k<(n+1)/2
////	 a[2*k+1] = Im[k], 0<k<(n-1)/2
////	 a[1] = Im[(n-1)/2]
//    // TODO: Tohle je skoro konvoluce, akorat vysledky neukladame do pole ktere bude obsahovat vysledek konvoluce ale pocitame rovnou energii
//    // TODO: A energii pocitame tak ze bereme vysledky konvoluce na druhou (realnou a imaginarni slozku zvlast) (protoze pocitame absolutni hodnotu)
//    public static double getCombFilterEnergyRealForward(double[] fftResult, double[] bpmArray) {      // TODO: Monoverze
//        double energy;              // TODO: mozna takhle prepsat i ten prevod na realny ... je to prehlednejsi
//        double real;                // TODO: Ten prevod na realny mozna ani nebude dobre
//        double imag;
//        if(fftResult.length % 2 == 0) {			// It's even
//            real = fftResult[0] * bpmArray[0];
//            energy = calculateComplexNumMeasure(real, 0);
//            real = fftResult[1] * bpmArray[1];      // TODO: Prehozeny poradi bylo to zatim for cyklem ... v te convertImagToReal to delat nemusim protoze tam to prevadim do pole polovicni velikosti
//            energy += calculateComplexNumMeasure(real, 0);
//            for(int i = 2; i < fftResult.length; i = i + 2) {
//                real = fftResult[i] * bpmArray[i];
//                imag = fftResult[i+1] * bpmArray[i+1];
//                energy += calculateComplexNumMeasure(real, imag);
//            }
//        } else {
//            real = fftResult[0] * bpmArray[0];
//            energy = calculateComplexNumMeasure(real, 0);
//            for(int i = 2; i < fftResult.length - 1; i = i + 2) {
//                real = fftResult[i] * bpmArray[i];
//                imag = fftResult[i+1] * bpmArray[i+1];
//                energy += calculateComplexNumMeasure(real, imag);
//            }
//
//            real =  fftResult[fftResult.length - 1] * bpmArray[fftResult.length - 1];
//            imag = fftResult[1] * bpmArray[1];
//            energy += calculateComplexNumMeasure(real, imag);
//        }
//
//        return energy;
//    }


    public static double[] convolutionInFreqDomainRealForwardFull(double[] fftResult, double[] bpmArray) {  // TODO: "Stereo" verze
        double[] result = new double[fftResult.length];
        convolutionInFreqDomainRealForward(fftResult, bpmArray, result);
        return result;
    }

    // TODO: Ted nevim jestli to ma byt stejne jako u getCombFilterEnergyRealForward ... kde pocitam measury misto toho co delam tady (tj nejdriv to vynasobim a pak z vysledku vezmu measury)
    public static void convolutionInFreqDomainRealForwardFull(double[] fftResult, double[] bpmArray, double[] result) {      // TODO: "Stereo" verze
        double real;
        double imag;
        for(int i = 0; i < fftResult.length;) {
            real = fftResult[i] * bpmArray[i] - fftResult[i+1] * bpmArray[i+1];
            imag = fftResult[i] * bpmArray[i+1] + fftResult[i+1] * bpmArray[i];
            result[i++] = real;
            result[i++] = imag;
        }
    }





    // From documentation:
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]
    // TODO: Ted nevim jestli to ma byt stejne jako u getCombFilterEnergyRealForward ... kde pocitam measury misto toho co delam tady (tj nejdriv to vynasobim a pak z vysledku vezmu measury)
    public static void convolutionInFreqDomainRealForward(double[] arr1, double[] arr2, double[] result) {      // TODO: Monoverze
        double real;
        double imag;
        if(arr1.length % 2 == 0) {			// It's even
            real = arr1[0] * arr2[0];
            result[0] = real;
            real = arr1[1] * arr2[1];
            result[1] = real;           // TODO: Prehozeny poradi bylo to zatim for cyklem ... v te convertImagToReal to delat nemusim protoze tam to prevadim do pole polovicni velikosti
            for(int i = 2; i < arr1.length;) {
                real = arr1[i] * arr2[i] - arr1[i+1] * arr2[i+1];
                imag = arr1[i] * arr2[i+1] + arr1[i+1] * arr2[i];
                result[i++] = real;
                result[i++] = imag;
            }
        }
        else {
            real = arr1[0] * arr2[0];
            result[0] = real;
            for(int i = 2; i < arr1.length - 1;) {
                real = arr1[i] * arr2[i] - arr1[i+1] * arr2[i+1];
                imag = arr1[i] * arr2[i+1] + arr1[i+1] * arr2[i];
                result[i++] = real;
                result[i++] = imag;
            }
            real = arr1[arr1.length - 1] * arr2[arr1.length - 1] - arr1[1] * arr2[1];
            imag = arr1[arr1.length - 1] * arr2[1] + arr1[1] * arr2[arr2.length - 1];
            result[result.length - 1] = real;
            result[1] = imag;
        }
    }



    // TODO: Stara verze konvoluce - nasobilo se real a imag cast zvlast, tj. nechovalo se to jako nasobeni
    // TODO: KOMPLEXNICH CISEL
//    // From documentation:
////	if n is even then
////	 a[2*k] = Re[k], 0<=k<n/2
////	 a[2*k+1] = Im[k], 0<k<n/2
////	 a[1] = Re[n/2]
////
////
////	if n is odd then
////	 a[2*k] = Re[k], 0<=k<(n+1)/2
////	 a[2*k+1] = Im[k], 0<k<(n-1)/2
////	 a[1] = Im[(n-1)/2]
//    // TODO: Ted nevim jestli to ma byt stejne jako u getCombFilterEnergyRealForward ... kde pocitam measury misto toho co delam tady (tj nejdriv to vynasobim a pak z vysledku vezmu measury)
//    public static void convolutionInFreqDomainRealForward(double[] arr1, double[] arr2, double[] result) {      // TODO: Monoverze
//        double real;
//        double imag;
//        if(arr1.length % 2 == 0) {			// It's even
//            real = arr1[0] * arr2[0];
//            result[0] = real;
//            real = arr1[1] * arr2[1];
//            result[1] = real;           // TODO: Prehozeny poradi bylo to zatim for cyklem ... v te convertImagToReal to delat nemusim protoze tam to prevadim do pole polovicni velikosti
//            for(int i = 2; i < arr1.length;) {
//                real = arr1[i] * arr2[i];
//                imag = arr1[i+1] * arr2[i+1];
//                result[i++] = real;
//                result[i++] = imag;
//            }
//        }
//        else {
//            real = arr1[0] * arr2[0];
//            result[0] = real;
//            for(int i = 2; i < arr1.length - 1;) {
//                real = arr1[i] * arr2[i];
//                imag = arr1[i+1] * arr2[i+1];
//                result[i++] = real;
//                result[i++] = imag;
//            }
//            real = arr1[arr1.length - 1] * arr2[arr1.length - 1];
//            imag = arr1[1] * arr2[1];
//            result[result.length - 1] = real;
//            result[1] = imag;
//        }
//    }
    // TODO: Stara verze konvoluce - nasobilo se real a imag cast zvlast, tj. nechovalo se to jako nasobeni
    // TODO: KOMPLEXNICH CISEL




    // TODO: Tahle obecna verze je jen prepsana ta jednoducha s tim rozdilem ze se berou odpovidajici indexy
    // From documentation:
//	if n is even then
//	 a[2*k] = Re[k], 0<=k<n/2
//	 a[2*k+1] = Im[k], 0<k<n/2
//	 a[1] = Re[n/2]
//
//
//	if n is odd then
//	 a[2*k] = Re[k], 0<=k<(n+1)/2
//	 a[2*k+1] = Im[k], 0<k<(n-1)/2
//	 a[1] = Im[(n-1)/2]
    // TODO: Ted nevim jestli to ma byt stejne jako u getCombFilterEnergyRealForward ... kde pocitam measury misto toho co delam tady (tj nejdriv to vynasobim a pak z vysledku vezmu measury)
    public static void convolutionInFreqDomainRealForward(double[] arr1, int arr1StartIndex,
                                                          double[] arr2, int arr2StartIndex,
                                                          double[] result, int resultStartIndex, int convolutionLen) {      // TODO: Monoverze
        double real;
        double imag;
        if(arr1.length % 2 == 0) {			// It's even
            real = arr1[arr1StartIndex++] * arr2[arr2StartIndex++];
            result[resultStartIndex++] = real;
            real = arr1[arr1StartIndex++] * arr2[arr2StartIndex++];
            result[resultStartIndex++] = real;           // TODO: Prehozeny poradi bylo to zatim for cyklem ... v te convertImagToReal to delat nemusim protoze tam to prevadim do pole polovicni velikosti
            for(int i = 2; i < convolutionLen; i += 2, arr1StartIndex += 2, arr2StartIndex += 2) {
                real = arr1[arr1StartIndex] * arr2[arr2StartIndex] - arr1[arr1StartIndex+1] * arr2[arr2StartIndex+1];
                imag = arr1[arr1StartIndex] * arr2[arr2StartIndex+1] + arr1[arr1StartIndex+1] * arr2[arr2StartIndex];
                result[resultStartIndex++] = real;
                result[resultStartIndex++] = imag;
            }
        } else {
            real = arr1[arr1StartIndex++] * arr2[arr2StartIndex++];
            result[resultStartIndex++] = real;
            int resultIndex = resultStartIndex + 1;
            int arr1Index = arr1StartIndex + 1;
            int arr2Index = arr2StartIndex + 1;

            for(int i = 2; i < convolutionLen - 1; i += 2, arr1Index += 2, arr2Index += 2) {
                real = arr1[arr1Index] * arr2[arr2Index] - arr1[arr1Index+1] * arr2[arr2Index+1];
                imag = arr1[arr1Index] * arr2[arr2Index+1] + arr1[arr1Index+1] * arr2[arr2Index];
                result[resultIndex++] = real;
                result[resultIndex++] = imag;
            }
            real = arr1[arr1Index] * arr2[arr2Index] - arr1[arr1StartIndex] * arr2[arr2StartIndex];
            imag = arr1[arr1Index] * arr2[arr2StartIndex] + arr1[arr1StartIndex] * arr2[arr2Index];
            result[resultIndex] = real;
            result[resultStartIndex] = imag;
        }
    }


    public static double[] convolutionInFreqDomainRealForward(double[] fftResult, double[] bpmArray) {      // TODO: Monoverze
        double[] result = new double[fftResult.length];
        convolutionInFreqDomainRealForward(fftResult, bpmArray, result);
        return result;
    }



    public static double[][] getIFFTBasedOnSubbands(double[] fftResult, int subbandCount, DoubleFFT_1D fft,
                                                    SubbandSplitterIFace splitter) {
        double[][] result = new double[subbandCount][fftResult.length];
        getIFFTBasedOnSubbands(fftResult, subbandCount, fft, splitter, result);
        return result;
    }
    public static void getIFFTBasedOnSubbands(double[] fftResult, int subbandCount, DoubleFFT_1D fft,
                                             SubbandSplitterIFace splitter, double[][] result) {
        for(int subband = 0; subband < subbandCount; subband++) {
            splitter.getSubband(fftResult, subbandCount, subband, result[subband]);

//            // TODO:
//            System.out.println("\n\n\n\n" + subband);
//            for(int i = 0; i < result[subband].length; i++) {
//                System.out.println(i + "\t" + result[subband][i]);
//            }

            calculateIFFTRealForward(result[subband], fft, true);      // TODO: To skalovani nevim
            // TODO: Tady bych mel volat tu metodu podtim asi
        }
    }

    public static void getIFFTBasedOnSubband(double[] fftResult, int subbandCount, int subband, DoubleFFT_1D fft,
                                             SubbandSplitterIFace splitter, double[] result) {
        splitter.getSubband(fftResult, subbandCount, subband, result);
        calculateIFFTRealForward(result, fft, true);     // TODO: To skalovani ... asi se ma davat true, ale nevim proc ... no vzdycky to muze prevadet jako parametr
    }

    // TODO: To je p[odle me jen napsana ta jednoducha verze ... muzu to pak vymazat
//        int bpm = 0;
//        int[] maxBPMIndexes = 0;
//        double[] maxEnergies = 0;
//        double[] energies = new double[subbandCount];
//        for(int i = 0; i < bpmArrays.length; i++) {
//            double[][] fftResults = calculateFFTRealForwardOnlyMeasures(samples, sampleSize, sampleSizeInBits, // TODO: Tahle metoda se casto pouziva se stejnym FFT oknem ... nema smysl vytvaret porad ten samy
//                windowSize, isBigEndian, isSigned);     // TODO: tohle vraci measury ... nikoliv imag a real cast ... prizpusobit k tomu tu metodu
//            // TODO: A jeste ten nechci volat na cely song ... vypocetne narocny ... melo by se to delat na nejakou 5ti sekundovou cast
//            // TODO: A funguje na mono
//            // TODO: !!!!!!!!!!!!!!
//            getCombFilterEnergyRealForward(fftResults, bpmArrays[i], energies);
//            for(int j = 0; j < energies.length; j++) {
//                if (energies[j] > maxEnergies[j]) {
//                    maxEnergies[j] = energies[j];
//                    maxBPMIndexes[j] = i;
//                }
//            }
//        }
//
//        return maxEnergy;
//    }


    public static int geBPMMyVersion(byte[] samples) {
        int bpm = 0;


        return bpm;
    }

////////////////////////////////////////////////
    ////// Implementation of rectification process: https://en.wikipedia.org/wiki/Rectifier
////////////////////////////////////////////////


    public static int getZeroValue(boolean isSigned, int sampleSize) {
        if(isSigned) {
            return 0;
        }
        else {
            int maxValue = getMaxAbsoluteValueUnsigned(8 * sampleSize);
            return maxValue / 2;
        }
    }

    public static int getZeroValue(byte[] zeroValueBytesResult, boolean isBigEndian, boolean isSigned, int sampleSize) {
        int zeroValue = 0;
        if(isSigned) {
            for(int i = 0; i < zeroValueBytesResult.length; i++) {
                zeroValueBytesResult[i] = 0;
            }
        }
        else {
            int maxValue = getMaxAbsoluteValueUnsigned(8 * sampleSize);
            zeroValue = maxValue / 2;
            convertIntToByteArr(zeroValueBytesResult, zeroValue, isBigEndian);
        }

        return zeroValue;
    }

    public static void halfWaveRectificationInt(int[] samples, boolean passPositive, int sampleSize, boolean isSigned) {
        int zeroValue = getZeroValue(isSigned, sampleSize);

        for(int i = 0; i < samples.length; i++) {
            if(passPositive) {
                if(samples[i] < zeroValue) {
                    samples[i] = zeroValue;
                }
            }
            else {
                if(samples[i] > zeroValue) {
                    samples[i] = zeroValue;
                }
            }
        }
    }

    // Double values are shifted so they are between -1 and 1
    public static void halfWaveRectificationDouble(double[] samples, boolean passPositive) {
        for(int i = 0; i < samples.length; i++) {
            if(passPositive) {
                if(samples[i] < 0) {
                    samples[i] = 0;
                }
            }
            else {
                if(samples[i] > 0) {
                    samples[i] = 0;
                }
            }
        }
    }

    public static void halfWaveRectificationBytes(byte[] samples, boolean passPositive, int mask, int sampleSize,
                                                  boolean isBigEndian, boolean isSigned) {
        byte[] zeroValueBytes = new byte[sampleSize];
        int zeroValue = getZeroValue(zeroValueBytes, isBigEndian, isSigned, sampleSize);

        int sample;
        for(int i = 0; i < samples.length;) {
            sample = convertBytesToInt(samples, sampleSize, mask, i, isBigEndian, isSigned);
            if(passPositive) {
                if(sample < zeroValue) {
                    i = setArrayValues(samples, zeroValueBytes, i);
                }
                else {
                    i += sampleSize;
                }
            }
            else {
                if(sample > zeroValue) {
                    if(sample < zeroValue) {
                        i = setArrayValues(samples, zeroValueBytes, i);
                    }
                }
                else {
                    i += sampleSize;
                }
            }
        }
    }


    // TODO: Zamyslet se jestli dava smysl tohle pouzivat - v C++ by davalo protoze bych index predal pointerem a neresil bych to
    // TODO: Tady ale pak ten index musim zvysit i mimo metodu ... hodne maly zpomaleni - ale kdyz se provede milionkrat
    // TODO: Tak se to nascita, ... TODO: Podivat se jestli to pouzivam i jinde
    public static int setArrayValues(byte[] array, byte[] arrayWithSetValues, int index) {
        for(int j = 0; j < arrayWithSetValues.length; j++, index++) {
            array[index] = arrayWithSetValues[j];
        }

        return index;
    }

    public static void fullWaveRectificationDouble(double[] samples, boolean passPositive) {
        for(int i = 0; i < samples.length; i++) {
            if(passPositive) {
                samples[i] = Math.abs(samples[i]);
            }
            else {
                samples[i] = -Math.abs(samples[i]);
            }
        }
    }

    public static void fullWaveRectificationInt(int[] samples, boolean passPositive, int sampleSize, boolean isSigned) {
        int zero = getZeroValue(isSigned, sampleSize);

        for(int i = 0; i < samples.length; i++) {
            samples[i] = getAbsoluteValueGeneral(samples[i], zero, passPositive);
        }
    }

    // TODO:
    public static void fullWaveRectificationByte(byte[] samples, boolean passPositive, int mask, int sampleSize,
                                                 boolean isBigEndian, boolean isSigned) {
        byte[] sampleBytes = new byte[sampleSize];
        int zeroValue = getZeroValue(isSigned, sampleSize);

        int sample;
        for(int i = 0; i < samples.length;) {
            sample = convertBytesToInt(samples, sampleSize, mask, i, isBigEndian, isSigned);
            getAbsoluteValueGeneral(sample, zeroValue, passPositive, sampleBytes, sampleSize, isBigEndian);

            if(passPositive) {
                if(sample < zeroValue) { // TODO: Tohle je spatne ... 2x delam tutez kontrolu ifu
                    // TODO: ... a kdyz to budu delat bez ifu tak ty hodnoty nastavim i kdyz nemusim protoze jsou stejny
                    // TODO: ... Takze bud se podivat jestli dela prekladac omptimalizaci, nebo to prepsat nejak osklive
                    i = setArrayValues(samples, sampleBytes, i);
//                    for(int j = 0; j < zeroValueBytes.length; j++, i++) {
//                        samples[i] = zeroValueBytes[j];
//                    }
                }
                else {
                    i += sampleSize;
                }
            }
            else {
                if(sample > zeroValue) {
                    i = setArrayValues(samples, sampleBytes, i);
//                        for(int j = 0; j < zeroValueBytes.length; j++, i++) {
//                            samples[i] = zeroValueBytes[j];
//                        }
                }
                else {
                    i += sampleSize;
                }
            }
        }
    }


    public static int getAbsoluteValueGeneral(int value, int zero, boolean isPositive,
                                              byte[] resultInBytes, int sampleSize, boolean isBigEndian) {
        int retVal;
        if(isPositive) {
            retVal = getAbsoluteValueGeneralPositive(value, zero);
        }
        else {
            retVal = getAbsoluteValueGeneralNegative(value, zero);
        }

        convertIntToByteArr(resultInBytes, sampleSize, isBigEndian);
        return retVal;
    }

    public static int getAbsoluteValueGeneral(int value, int zero, boolean isPositive) {
        if(isPositive) {
            return getAbsoluteValueGeneralPositive(value, zero);
        }
        else {
            return getAbsoluteValueGeneralNegative(value, zero);
        }
    }

    public static int getAbsoluteValueGeneralPositive(int value, int zero) {
        // Version without branching
        int dif = value - zero;
        int sign = Integer.signum(dif);
        int returnVal = zero + (sign * dif);      // Doesn't need if branching
        return returnVal;
// TODO: Testuju v ProgramTest
//        // Version with branching
//        if(value > zero) {
//            return value;
//        }
//        else {
//            return zero + (zero - value); // zero - value tells how much it is under zero, so we just add that number to zero, to get the positive one
//        }
    }

    public static int getAbsoluteValueGeneralNegative(int value, int zero) {
        // Version without branching
        int dif = value - zero;
        int sign = -Integer.signum(dif);
        int returnVal = zero + (sign * dif);      // Doesn't need if branching
        return returnVal;
// TODO: Testuju v ProgramTest
//        // Version with branching
//        if(value > zero) {
//            return zero - (value - zero); // value - zero tells how much it is above zero, so we subtract that number from zero
//        }
//        else {
//            return value;
//        }
    }



    public static double[] get2CoefWindowUniversalWithoutLimit(int arrayLen, int startIndexForWindowCalculation, double a0, double a1, int windowSize) {
        double[] result = new double[arrayLen];
        get2CoefWindowUniversalWithoutLimit(startIndexForWindowCalculation, a0, a1, result, windowSize);
        return result;
    }
    public static void get2CoefWindowUniversalWithoutLimit(int startIndexForWindowCalculation, double a0, double a1, double[] result, int windowSize) {
        int windowSizePlus1 = windowSize + 1;

        for(int i = 0; i < result.length; i++, startIndexForWindowCalculation++) {
            result[i] = calculateWindowValue(a0, a1, startIndexForWindowCalculation, windowSizePlus1);
        }
    }

    public static double[] get2CoefWindowUniversalWithLimit(int arrayLen, int startIndexForWindowCalculation, double a0, double a1, int windowSize) {
        double[] result = new double[arrayLen];
        get2CoefWindowUniversalWithLimit(startIndexForWindowCalculation, a0, a1, result, windowSize);
        return result;
    }
    public static void get2CoefWindowUniversalWithLimit(int startIndexForWindowCalculation, double a0, double a1, double[] result, int windowSize) {
        int windowSizePlus1 = windowSize + 1;

        int i = 0;
        for(; startIndexForWindowCalculation <= windowSize; i++, startIndexForWindowCalculation++) { // TODO: podle me tu ma byt <=
            result[i] = calculateWindowValue(a0, a1, startIndexForWindowCalculation, windowSizePlus1);
        }
        for(; i < result.length; i++) {
            result[i] = 0;
        }
    }


    public static double[] getHahnWindowWithLimit(int arrayLen, int startIndexForWindowCalculation, int windowSize) {
        return get2CoefWindowUniversalWithLimit(arrayLen, startIndexForWindowCalculation, 0.5, 0.5, windowSize);
    }
    public static void getHahnWindowWithLimit(int startIndexForWindowCalculation, double[] result, int windowSize) {
        get2CoefWindowUniversalWithLimit(startIndexForWindowCalculation, 0.5, 0.5, result, windowSize);
    }
    public static double[] getHammingWindowWithLimit(int arrayLen, int startIndexForWindowCalculation, int windowSize) {
        return get2CoefWindowUniversalWithLimit(arrayLen, startIndexForWindowCalculation, 0.53836,  0.46164, windowSize);
    }
    public static void getHammingWindowWithLimit(int startIndexForWindowCalculation, double[] result, int windowSize) {
        get2CoefWindowUniversalWithLimit(startIndexForWindowCalculation, 0.53836,  0.46164, result, windowSize);
    }


    public static double[] getHahnWindowWithoutLimit(int arrayLen, int startIndexForWindowCalculation, int windowSize) {
        return get2CoefWindowUniversalWithoutLimit(arrayLen, startIndexForWindowCalculation, 0.5, 0.5, windowSize);
    }
    public static void getHahnWindowWithoutLimit(int startIndexForWindowCalculation, double[] result, int windowSize) {
        get2CoefWindowUniversalWithoutLimit(startIndexForWindowCalculation, 0.5, 0.5, result, windowSize);
    }
    public static double[] getHammingWindowWithoutLimit(int arrayLen, int startIndexForWindowCalculation, int windowSize) {
        return get2CoefWindowUniversalWithoutLimit(arrayLen, startIndexForWindowCalculation, 0.53836,  0.46164, windowSize);
    }
    public static void getHammingWindowWithoutLimit(int startIndexForWindowCalculation, double[] result, int windowSize) {
        get2CoefWindowUniversalWithoutLimit(startIndexForWindowCalculation, 0.53836,  0.46164, result, windowSize);
    }


    public static double calculateWindowValue(double a0, double a1, int index, int lengthPlus1) {
// TODO: DEBUG        System.out.println((a0 - a1 * Math.cos(2 * Math.PI * index / lengthPlus1)) + "\t" + lengthPlus1);
        return a0 - a1 * Math.cos(2 * Math.PI * index / lengthPlus1);
    }

    // TODO: Kdyztak implementovat https://en.wikipedia.org/wiki/Window_function#Hann_and_Hamming_windows
    //  ... cosine-sum windows obecne ... ma to ale nevyhodu, ze to nebude uplne rychly (muselo by se resit bud pres pole
    //  nebo pres promenny pocet parametru, tj. ...)









    ////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// Audio visualization
    ////////////////////////////////////////////////////////////////////////////////////
    // Very important information - The maximum energy of FFT bin is equal to window size and it is in case, when
    // there are only ones in window. !!! But this only applies to case where the input double values are normalized between -1 and 1.


    // TODO: Newly returns the stringStartX
    public static int drawStringWithSpace(Graphics g, Color color, String s, int currX, int binWidth, int y) {
        FontMetrics fontMetrics = g.getFontMetrics();
        g.setColor(color);
        int textLen = fontMetrics.stringWidth(s);
        int textStart = (binWidth - textLen) / 2;
        int stringStartX = currX + textStart;
        g.drawString(s, stringStartX, y);

        return stringStartX;

// TODO: Vymazat, jen DEBUG testovani neceho
//        int x = stringStartX + textLen / 2;
//        g.drawLine(x, 0, x, 400);
//
//        g.setColor(Color.red);
//        x = currX + binWidth / 2;
//        g.drawLine(x, 0, x, 400);
    }

    public static void setLabelLocWithSpace(JLabel label, int startX, int binWidth, int y) {
        int textLen = label.getWidth();
        int textStart = (binWidth - textLen) / 2;
        int labetStartX = startX + textStart;
        label.setLocation(labetStartX, y);
    }

    public static void setLabelLocWithSpace(JLabel label, int startX, int startY, int binWidth, int binHeight) {
        FontMetrics fm = label.getFontMetrics(label.getFont());
        int textLen =  fm.stringWidth(label.getText());
        int textStartX = (binWidth - textLen) / 2;
        int x = startX + textStartX;

        int textHeight = fm.getHeight();
        int textStartY = (binHeight - textHeight) / 2;
        int y = startY + textStartY;

        label.setLocation(x, y);
    }


    public static void getBiggestFontToFitSize(JLabel label, int maxWidth, int maxHeight) {
        Font oldFont = label.getFont();
        int currFontSize = oldFont.getSize();
        FontMetrics fm = label.getFontMetrics(oldFont);

        Font newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
        int textWidth = fm.stringWidth(label.getText());
        if(textWidth < maxWidth && textWidth >= 0) {
            while(currFontSize < DiasynthTabbedPanel.MAX_LABEL_FONT_SIZE) {
                // TODO: DEBUG
                //ProgramTest.debugPrint("Font:", newFont, "w and h", maxWidth, maxHeight);
                // TODO: DEBUG
                currFontSize++;
                newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
                fm = label.getFontMetrics(newFont);
                textWidth = fm.stringWidth(label.getText());
                if(textWidth > maxWidth) {
                    currFontSize = Math.max(1, currFontSize - 1);
                    newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
                    fm = label.getFontMetrics(newFont);
                    label.setFont(newFont);
                    if(fm.getHeight() > maxHeight) {
                        getBiggestFontToFitMaxHeight(label, maxHeight);
                    }
                    return;
                }
                else if(textWidth < 0) {
                    break;
                }
            }

            // If we get here then the maximum label size was reached
            newFont = new Font(oldFont.getName(), oldFont.getStyle(), DiasynthTabbedPanel.MAX_LABEL_FONT_SIZE);
            label.setFont(newFont);
            getBiggestFontToFitMaxHeight(label, maxHeight);
        }
        else if (textWidth > maxWidth) {
            while(currFontSize > 1) {
                // TODO: DEBUG
                //ProgramTest.debugPrint("Font:", newFont, "w and h", maxWidth, maxHeight);
                // TODO: DEBUG
                currFontSize--;
                newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
                fm = label.getFontMetrics(newFont);
                if(fm.stringWidth(label.getText()) < maxWidth) {
                    break;
                }
            }

            label.setFont(newFont);
            if(fm.getHeight() > maxHeight) {
                getBiggestFontToFitMaxHeight(label, maxHeight);
            }
            return;
        }
    }

    // On My system it is java.awt.Font[family=Dialog,name=Dialog,style=bold,size=26822]
    public static int findMaxFontSize(JLabel label) {
        Font oldFont = label.getFont();
        int currFontSize = 0;
        FontMetrics fm ;
        Font newFont;
        while(true) {
            currFontSize++;
            newFont = new Font(oldFont.getName(), oldFont.getStyle(), currFontSize);
            fm = label.getFontMetrics(newFont);
            // TODO: DEBUG
            //ProgramTest.debugPrint("Font:", newFont, "stringWidth", fm.stringWidth(label.getText()));
            // TODO: DEBUG
            if(fm.stringWidth(label.getText()) < 0) {
                return currFontSize - 1;
            }
        }
    }


    public static void getBiggestFontToFitMaxHeight(JLabel label, int maxHeight) {
        Font newFont = label.getFont();
        int currFontSize = newFont.getSize();
        FontMetrics fm = label.getFontMetrics(newFont);
        while(fm.getHeight() >= maxHeight) {
            currFontSize--;
            newFont = new Font(newFont.getName(), newFont.getStyle(), currFontSize);
            fm = label.getFontMetrics(newFont);
        }

        label.setFont(newFont);
    }




    public static void drawStringWithDefinedMidLoc(Graphics g, Color color, String s, int mid, int y) {
        FontMetrics fontMetrics = g.getFontMetrics();
        g.setColor(color);
        int textLen = fontMetrics.stringWidth(s) - 1;       // -1 because it pushes more to the middle
        int stringStartX = mid - textLen / 2;
        g.drawString(s, stringStartX, y);
    }

    public static void drawLabelWithDefinedMidLoc(Graphics g, JLabel label, int mid, int y) {

        int textLen = label.getWidth() - 1;       // -1 because it pushes more to the middle
        int labetStartX = mid - textLen / 2;
        label.setLocation(labetStartX, y);
    }


    public static void setFontSize(JLabel label, int oldWidth, int newWidth) {
        float ratio = newWidth / (float)oldWidth;
        // TODO: DEBUG
        //ProgramTest.debugPrint("Old font:", label.getFont());
        // TODO: DEBUG
        Font oldFont = label.getFont();
        Font newFont = oldFont.deriveFont(ratio * oldFont.getSize2D());
        label.setFont(newFont);
        // TODO: DEBUG
        //ProgramTest.debugPrint("New font:", label.getFont());
        // TODO: DEBUG
    }





    // Get frequencies in khz
    public static String[] getFreqs(int binCount, double freqJump, double startFreq, int takeEveryNthFreq, int precision) {
        int len = 1 + (binCount - 1) / takeEveryNthFreq;  // -1 Because for example for binCount = takeEveryNthFreq = 4 I'd have 2 without the -1
        String[] binFreqs = new String[len];
        double currFreqHz = startFreq;
        for(int i = 0; i < binFreqs.length; i++, currFreqHz += freqJump * takeEveryNthFreq) {
            double currFreqKhz = currFreqHz / 1000;
            String freqString = String.format("%." + precision +"f", currFreqKhz);
            binFreqs[i] = freqString;
        }

        return binFreqs;
    }


    // Returns fontSize
    public static int getFont(int startFontSize, Graphics g, String[] texts, int maxWidth, int maxHeight, int checkNthIndexes) {
        int fontSize = startFontSize;
        FontMetrics fontMetrics;
        for(int i = 0; fontSize > 0; fontSize--) {
            g.setFont(new Font("Serif", Font.BOLD, fontSize));
            fontMetrics = g.getFontMetrics();
            for(; i < texts.length; i++) {
                if(i % checkNthIndexes == 0) {
                    if (fontMetrics.stringWidth(texts[i]) > maxWidth || fontMetrics.getHeight() > maxHeight) {
                        break;
                    }
                }
            }

            if(i >= texts.length) {
                break;
            }
        }

        return fontSize;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    ///////////////////// Spectrogram
    ////////////////////////////////////////////////////////////////////////////////////
    public static double getFreqJump(int sampleRate, double windowSize) {
        double result = sampleRate / (double) windowSize;
        return result;
    }


    // TODO: VYMAZAT TEN SPECTROGRAM
    // TODO: VYMAZAT TEN SPECTROGRAM
    // TODO: VYMAZAT TEN SPECTROGRAM
    // TODO: VYMAZAT TEN SPECTROGRAM

    public static BufferedImage createSpectrogram(double[] song, int numberOfChannels, int windowSize, int windowShift,
                                                  int startIndex, int endIndex, double freqJump,
                                                  int pixelWidthForWindow, int pixelHeightForBin) {
        DoubleFFT_1D fft = new DoubleFFT_1D(windowSize);
        return createSpectrogram(song, numberOfChannels, windowSize, windowShift,
            startIndex, endIndex, freqJump, fft, pixelWidthForWindow, pixelHeightForBin);
    }


//    public static BufferedImage createSpectrogram(double[] song, int numberOfChannels, int windowSize, int windowShift,
//                                                  int startIndex, int endIndex, double freqJump, DoubleFFT_1D fft,
//                                                  int pixelWidthForWindow, int pixelHeightForBin) {
//        double[] fftResult = new double[windowSize];
//
//        int spectrogramWidth = (endIndex - startIndex) / windowSize;
//        int spectrogramWidthInPixels = spectrogramWidth * pixelWidthForWindow;
//        int binCount = getBinCountRealForward(windowSize);
//        int spectrogramHeightInPixels = binCount * pixelHeightForBin;
//
//
//        int spectrogramWidthInPixelsWithReference = spectrogramWidthInPixels + 100;    // TODO:
//        int spectrogramStart = 100;
//        int spectrogramWidthInPixelsWithReferenceAndHzLabels = spectrogramWidthInPixelsWithReference + spectrogramStart;
//
//        BufferedImage spectrogram = new BufferedImage(spectrogramWidthInPixelsWithReferenceAndHzLabels, spectrogramHeightInPixels, BufferedImage.TYPE_INT_RGB);
//        Graphics g = spectrogram.getGraphics();
//        g.setColor(Color.black);
//        g.fillRect(0, 0, spectrogram.getWidth(), spectrogram.getHeight());
//        double windowOverlapCountForOneWindow = windowSize / (double)windowShift;
//        int windowOverlapCountForOneWindowInt = (int)windowOverlapCountForOneWindow;    // TODO: Asi pres Math.ceil
//        int pixelWidthForWindowPart;
//        double[][] currentlyCalculatedMeasures;
////        if(windowOverlapCountForOneWindowInt == 0) {
////            // TODO:
////            currentlyCalculatedMeasures = new double[binCount][1];
////            pixelWidthForWindowPart = (int)(pixelWidthForWindow / windowOverlapCountForOneWindow);
////            pixelWidthForWindow = pixelWidthForWindowPart;
////            // TODO:
////        }
////        else {
//            currentlyCalculatedMeasures = new double[binCount][windowOverlapCountForOneWindowInt];
//            // TODO:
//            pixelWidthForWindowPart = pixelWidthForWindow / windowOverlapCountForOneWindowInt;
//            System.out.println("aa:\t" + pixelWidthForWindow + "\t" + pixelWidthForWindowPart);
//            pixelWidthForWindow = pixelWidthForWindowPart;
//            // TODO:
////        }
//
//        int logarithmBase = 2;
//        double[] fftMeasures = new double[binCount];
//        int currWindowCalculatedInd = 0;       // Is the index for which we are no calculating the values - l - Lies between 0 and windowOverlapCountForOneWindowInt
//        for(int i = startIndex, currX = spectrogramStart, currWindow = 0; i < endIndex; i += windowShift, currX += pixelWidthForWindow, currWindow++) {
//            calculateFFTRealForward(song, i, numberOfChannels, fft, fftResult);
//            convertResultsOfFFTToRealRealForward(fftResult, fftMeasures);
//            for(int j = 0; j < fftMeasures.length; j++) {
//                fftMeasures[j] += 1;      // Not the minimum energy is 1, so the minimum log == 0
//            }
//
//            Program.performOperationOnSamples(fftMeasures, logarithmBase, ArithmeticOperation.LOG);
//// TODO: Asi nemusim a jestli musim, tak to je vypocet navic, protoze tam posilam sqrt stejne
//            for(int j = 0; j < fftMeasures.length; j++) {
//// TODO: Debug print
////                if(fftMeasures[j] > 0) {
////                    System.out.println(j + "\t" + fftMeasures[j]);
////                }
//                // TODO: jen jse mto delal abych prisel na to jak to udelat obecne
////                if(currWindow == 0) {
////                    // Print 0 index
////                    for(int k = 0; k < currentlyCalculatedMeasures[j].length; k++) {
////                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / (k+1);
////                    }
////                }
////                else if(currWindow == 1) {
////                    currentlyCalculatedMeasures[j][0] = fftMeasures[j] / currentlyCalculatedMeasures[j].length;
////                    // Print 1 index
////                    for(int k = 1; k < currentlyCalculatedMeasures[j].length; k++) {
////                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / (k+1);
////                    }
////                }
////                else if(currWindow == 2) {
////                    currentlyCalculatedMeasures[j][0] += fftMeasures[j] / currentlyCalculatedMeasures[j].length;
////                    currentlyCalculatedMeasures[j][1] = fftMeasures[j] / currentlyCalculatedMeasures[j].length;
////                    // Print 2 index
////                    for(int k = 2; k < currentlyCalculatedMeasures[j].length; k++) {
////                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / (k+1);
////                    }
////                }
//                if(currWindow < currentlyCalculatedMeasures[j].length) {
//                    int k;
//                    for(k = 0; k < currWindow - 1; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / currentlyCalculatedMeasures[j].length;
//                    }
//// TODO: Vynuluju az potom
////                    if(k != 0) {
////                        currentlyCalculatedMeasures[j][k] = fftMeasures[j] / currentlyCalculatedMeasures[j].length;
////                        k++;
////                    }
//
//                    for(; k < currentlyCalculatedMeasures[j].length; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / (k+1);
//                    }
//                }
//                else {
//                    for(int k = 0; k < currentlyCalculatedMeasures[j].length; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / currentlyCalculatedMeasures[j].length;
//                    }
//                }
//
//
//
//
//// TODO:
////                if(fftMeasures[j] > 0x00ffffff) {       // TODO: To neni dobre asi
////                    fftMeasures[j] = 0x00ffffff;
////                }
////                else {
////                    fftMeasures[j] %= 0x00ffffff;
////                }
////                fftMeasures[j] *= fftMeasures[j];
//            }
//
//            // TODO: nextY useless
//            // TODO:
//            // If we want to have spectrogram with lower frequencies at top and higher at bottom then use this
//            // for(int bin = 0, currY = 0, nextY = pixelHeightForBin; bin < binCount; bin++, currY = nextY, nextY += pixelHeightForBin) {
//            for(int bin = 0, currY = spectrogramHeightInPixels, nextY = currY - pixelHeightForBin; bin < binCount; bin++, currY = nextY, nextY -= pixelHeightForBin) {
//                // So the max is 1, but I am not really sure if I shouldn't do something further, like multiply it by some factor so the maximum is == 1.
//                // If I don't do anything then the maximum is reached only if the wave is constant wave at 1.
//                double val;
//                val = currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length];
//                // TODO: 2*windowSize because of the +1 for each fftMeasures
//                double windowSizeLog = Math.log(2 * windowSize) / Math.log(logarithmBase);
//                val /= windowSizeLog;
//                //val /= (windowSize / 2);        // TODO:
//                //val *= 30;
//                Color c;
//                c = Color.getHSBColor(1-(float)val, (float)val, (float)val);
////                c = Color.getHSBColor(1,(float)val,(float)val);
////                c = Color.getHSBColor(-1+(float)val, (float)-val, (float)-val);
//                //val = Double.NEGATIVE_INFINITY;
//                //c = Color.getHSBColor((float)(1-val),(float)val,(float)val);
////                System.out.println(val + "\t" + currentlyCalculatedMeasures[bin].length);
//                g.setColor(c);
//                g.fillRect(currX, currY, pixelWidthForWindow, pixelHeightForBin);
//
////
////                System.out.println(currX + "\t" + currY + "\t" + i + "\t" + endIndex);
////                System.out.println(spectrogramWidthInPixels + "\t" + spectrogramHeightInPixels);
////                System.out.println();
////
////                int rgb = spectrogram.getRGB(currX, currY);
////                rgb *= Math.ceil(fftMeasures[bin]);
////                g.fillRect(currX, currY, pixelWidthForWindow, pixelHeightForBin);
////
////                int red = (rgb & 0x00ff0000) >> 16;
////                int green = (rgb & 0x0000ff00) >> 8;
////                int blue =  rgb & 0x000000ff;
////
////                int redBin;
////                int greenBin;
////                int blueBin;
////
////                if(fftMeasures[bin] < 0x000000ff) {
////                    blueBin = (int)Math.ceil(fftMeasures[bin] / windowOverlapCountForOneWindow);
////                    blue += blueBin;
////                }
////                else if((greenBin = ((int)Math.ceil(fftMeasures[bin]) & 0x0000ff00) >> 8) > 0) {
////                    greenBin /= windowOverlapCountForOneWindow;
////                    green += greenBin;
////
////                    blueBin = (int)Math.ceil((0x000000ff / windowOverlapCountForOneWindow));
////                    blue += blueBin;
////                }
////                else {
////                    redBin = ((int)Math.ceil(fftMeasures[bin]) & 0x00ff0000) >> 16;
////                    redBin /= windowOverlapCountForOneWindow;
////                    red += redBin;
////
////
////                    blueBin = (int)Math.ceil((0x000000ff / windowOverlapCountForOneWindow));
////                    blue += blueBin;
////                    greenBin = blueBin;
////                    green += greenBin;
////                }
//
//                currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length] = 0;  // It was used and is now the latest unused, so it is set to 0 and will be set to 0 again after passing windowOverlapCountForOneWindowInt windows
//            }
//        }
//
//
//
//         // TODO: Timhle se dela to spektrum na prostor, tj. ten prouzek
////        int pixelSkip = 0x00ffffff / spectrogramWidthInPixels;
////        double pixelSkipDouble = 1.0 / spectrogramWidthInPixels;
////        System.out.println(pixelSkip + "\t" + 0x00ffffff + "\t" + pixelSkipDouble);
////
////        double currHue = 0;
////        for(int rgb = 0, x = 0; rgb < 0x00ffffff; rgb += pixelSkip, x++, currHue += pixelSkipDouble) {
/////*
////            int red = (rgb & 0x00ff0000) >> 16;
////            int green = (rgb & 0x0000ff00) >> 8;
////            int blue =  rgb & 0x000000ff;
////            System.out.println(red + "\t" + green + "\t" + blue);
////            System.out.println(x);
////            g.setColor(new Color(red, green, blue));
////*/
////            Color c;
////
/////*            // B should be 1 or currHue
////            c = Color.getHSBColor((float)currHue,1-(float)currHue, (float)currHue); // Black to white
////
////            c = Color.getHSBColor((float)currHue,1-(float)currHue, 1);          // Red to white
////
////            c = Color.getHSBColor((float)currHue, (float)currHue, 1);               // White to red
////
////            c = Color.getHSBColor((float)currHue, (float)currHue, (float)currHue);      // Black to red
////
////
////
////
////            c = Color.getHSBColor(1-(float)currHue,1-(float)currHue, (float)currHue); // Black to white
////
////            c = Color.getHSBColor(1-(float)currHue,1-(float)currHue, 1);           // Red to white
////
////            c = Color.getHSBColor(1-(float)currHue, (float)currHue, 1);               // White to red
////*/
////            c = Color.getHSBColor(1-(float)currHue, (float)currHue, (float)currHue);        // Good, from black to red
/////*
////
////
////
////// S = 1
////            c = Color.getHSBColor((float)currHue,1, (float)currHue); // Ok - Black to red
////
////            c = Color.getHSBColor((float)currHue, 1, 1);               // NO - Red to red
////
////            c = Color.getHSBColor((float)currHue, 1, (float)currHue);      // Black to red
////
////
////
////            c = Color.getHSBColor(1-(float)currHue,1, (float)currHue); // Black to red
////
////            c = Color.getHSBColor(1-(float)currHue, 1, 1);               // NO - Red to red
////
////            c = Color.getHSBColor(1-(float)currHue, 1, (float)currHue);        // Good, from black to red
////*/
////
////            g.setColor(c);
////            g.fillRect(x, 0, 1, spectrogramHeightInPixels);
////           // g.fillRect(50,0, spectrogramWidthInPixels, spectrogramHeightInPixels);
//////            g.setColor(Color.red);
//////            g.fillRect(0,0, spectrogramWidthInPixels / 8, spectrogramHeightInPixels);
////
////        }
//
//        // Draw the measure reference
//        g.setColor(Color.white);
//        int startXReference = spectrogramStart + spectrogramWidthInPixels;     // TODO: V tehle jmenech promennych je bordel
//
//        // TODO: bud to mit oddeleny jen bilou carou ... to je to zakomentovany nebo tam mit proste bilej obdelnik to je to nezakomentovany ... jedna se jen o tyhle 3 nasledujici radky
//        // TODO: +20 kdyz chci tu carku
//        // TODO: Ten ctverec je lepsi ... podle me
//        //int startXReference = spectrogramStart + spectrogramWidthInPixels + 20;
////        g.drawLine(startXReference, 0, startXReference, spectrogramHeightInPixels);
//        g.fillRect(startXReference, 0, spectrogramWidthInPixelsWithReferenceAndHzLabels, spectrogramHeightInPixels);
//
//        double pixelSkipDouble = 1.0 / spectrogramHeightInPixels;
//        int x = startXReference + 20;
//        int visibleWidth = 60;
//        int height = 1;
//        double colorVal = 0;
//        for(int y = spectrogramHeightInPixels; y >= 0; y--, colorVal += pixelSkipDouble) {
//            Color c = Color.getHSBColor(1 - (float) colorVal, (float) colorVal, (float) colorVal);
//            g.setColor(c);
//            g.fillRect(x, y, visibleWidth, height);
//            System.out.println("test\t" + x + "\t" + y + "\t" + pixelSkipDouble + "\t" + colorVal + "\t" + (colorVal + pixelSkipDouble));
//        }
//
//
//        // Draw frequency labels
//        int n = 8;      // take every nth bin
//        int maxTextHeight = n * pixelHeightForBin;
//        String[] binFreqs = getFreqs(binCount, freqJump, 0, n);
//        getFont(24, g, binFreqs, spectrogramStart - 15, maxTextHeight);
//        FontMetrics fontMetrics = g.getFontMetrics();
//
//        g.setColor(Color.white);
//        g.fillRect(0, 0, spectrogramStart, spectrogramHeightInPixels);
//        int lineXSmall = spectrogramStart - 5;
//        int lineXBig = lineXSmall - 5;
//        g.drawLine(lineXSmall, 0, lineXSmall, spectrogramHeightInPixels);
//        g.setColor(Color.black);
//
//        // Draw the small lines
//        for(int i = 0, currY = 0, midY = pixelHeightForBin / 2;
//            i < binCount;
//            i++, currY += pixelHeightForBin, midY += pixelHeightForBin)
//        {
//            g.drawLine(lineXSmall, midY, spectrogramStart, midY);
//        }
//
//        int textHeight = fontMetrics.getHeight();
//        int freeSpace = maxTextHeight - textHeight;
//        freeSpace /= 2;
//        // Draw the big lines + text
//        for(int bin = binFreqs.length - 1, currY = 0, midY = pixelHeightForBin / 2;
//            bin >= 0;
//            bin--, currY += maxTextHeight, midY += maxTextHeight)
//        {
//            System.out.println(bin + "\t"  + currY);
//            g.drawLine(lineXBig, midY, spectrogramStart, midY);
//            int textLen = fontMetrics.stringWidth(binFreqs[bin]);
//            if(bin == binFreqs.length - 1) {        // Special cases at top and at bottom (0Hz and nyquist freq)
//                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, 15);   // TODO: y == 15 because of the top ledge (the ledge with cross)
//            }
//            else if(bin == 0) {
//                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, spectrogramHeightInPixels);
//            }
//            else {
//                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, currY + freeSpace);
//            }
//        }
//
//
//        // Draw KHz to the top left
//        g.drawString("KHz", 0, spectrogramHeightInPixels - maxTextHeight / 2);      // TODO: Bude tam obcas prekryv
//
//        return spectrogram;
//    }


    public static BufferedImage createSpectrogram(double[] song, int numberOfChannels, int windowSize, int windowShift,
                                                  int startIndex, int endIndex, double freqJump, DoubleFFT_1D fft,
                                                  int spectrogramWidthInPixels, int spectrogramHeightInPixels) {
        double[] fftResult = new double[windowSize];

        int windowCount = (endIndex - startIndex) / windowSize;        // TODO: Tady vzit Math.ceil abych nabral i to posledni okno
        double pixelWidthForWindow = spectrogramWidthInPixels / (double)windowCount;
        int binCount = getBinCountRealForward(windowSize);
        double pixelHeightForBin = spectrogramHeightInPixels / (double)binCount;


        int spectrogramWidthInPixelsWithReference = spectrogramWidthInPixels + 100;    // TODO:
        int spectrogramStart = 100;
        int spectrogramWidthInPixelsWithReferenceAndHzLabels = spectrogramWidthInPixelsWithReference + spectrogramStart;

        BufferedImage spectrogram = new BufferedImage(spectrogramWidthInPixelsWithReferenceAndHzLabels, spectrogramHeightInPixels, BufferedImage.TYPE_INT_RGB);
        Graphics g = spectrogram.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, spectrogram.getWidth(), spectrogram.getHeight());
        double windowOverlapCountForOneWindow = windowSize / (double)windowShift;
//        int windowOverlapCountForOneWindowInt = (int)windowOverlapCountForOneWindow;    // TODO: Asi pres Math.ceil
        double pixelWidthForWindowPart;
        double[][] currentlyCalculatedMeasures;
        double lastWindowReminder = windowOverlapCountForOneWindow % 1;
        double pixelWidthForOneWindowInCaseItIsSmallerThanOneIThenHaveAddThisValueSomehowToThecurrentlyCalculatedMeasuresSize;



        double[] windowsOverlaps = null;
        double[] lastWindowParts = null;
        double[] counters = null;
        if(lastWindowReminder == 0) {
            currentlyCalculatedMeasures = new double[binCount][(int) windowOverlapCountForOneWindow];
        }
        else {
            currentlyCalculatedMeasures = new double[binCount][(int)Math.ceil(windowOverlapCountForOneWindow)];

            windowsOverlaps = new double[(int)Math.ceil(windowOverlapCountForOneWindow)];
            lastWindowParts = new double[windowsOverlaps.length];   // TODO: Not needed but it is easier to understand if I have separater array for it
            counters = new double[lastWindowParts.length];

/*
            // TODO:
            windowsOverlaps = new double[6];
            lastWindowParts = new double[windowsOverlaps.length];   // TODO: Not needed but it is easier to understand if I have separater array for it
            // TODO:
*/
            windowsOverlaps[0] = windowOverlapCountForOneWindow;
            lastWindowParts[0] = lastWindowReminder;
            System.out.println(0 + "\t" + windowsOverlaps[0]);
            for (int i = 1; i < windowsOverlaps.length; i++) {
                windowsOverlaps[i] = (int)Math.ceil(windowsOverlaps[i - 1]) - windowsOverlaps[i - 1];
                windowsOverlaps[i] = windowsOverlaps[0] - windowsOverlaps[i];
                lastWindowParts[i] = windowsOverlaps[i] % 1; // TODO: Not sure maybe not even needed

                counters[i] = (1 - lastWindowParts[i - 1]);
                System.out.println(i + "\t" + windowsOverlaps[i] + "\t" + ((int)Math.ceil(windowsOverlaps[i - 1]) - windowsOverlaps[i - 1]));
            }
// TODO:            System.exit((int)Math.ceil(1.00));
        }
            // TODO:
            pixelWidthForWindowPart = pixelWidthForWindow / windowOverlapCountForOneWindow;
            System.out.println("aa:\t" + pixelWidthForWindow + "\t" + pixelWidthForWindowPart);
            pixelWidthForWindow = pixelWidthForWindowPart;         //TODO: Taky be melo byt double
            // TODO:

        double windowsPerPixel = 1;
        if(pixelWidthForWindowPart < 1) {
            windowsPerPixel = 1 / pixelWidthForWindowPart;
        }



//        if(windowOverlapCountForOneWindowInt == 0) {
//            // TODO:
//            currentlyCalculatedMeasures = new double[binCount][1];
//            pixelWidthForWindowPart = (int)(pixelWidthForWindow / windowOverlapCountForOneWindow);
//            pixelWidthForWindow = pixelWidthForWindowPart;
//            // TODO:
//        }
//        else {
//            currentlyCalculatedMeasures = new double[binCount][windowOverlapCountForOneWindowInt];
//        // TODO:
//            pixelWidthForWindowPart = pixelWidthForWindow / windowOverlapCountForOneWindowInt;
//            System.out.println("aa:\t" + pixelWidthForWindow + "\t" + pixelWidthForWindowPart);
//            pixelWidthForWindow = pixelWidthForWindowPart;
//        // TODO:
//        }


        // TODO:
        double[][][] tmpArrs = new double[windowsOverlaps.length][2][binCount];
//        double[][][] tmpArrs = null;

        // TODO:
        int oldestWindow = 0;
        int newOldestWindow = 0;
        boolean oldestWindowSet = false;

        int newestWindow = 0;
        // TODO:

        int logarithmBase = 2;
        double[] fftMeasures = new double[binCount];
        double[] tmpArr = new double[fftMeasures.length];
        double[] tmpArr2 = new double[fftMeasures.length];
        int currWindowCalculatedInd = 0;       // Is the index for which we are no calculating the values - l - Lies between 0 and windowOverlapCountForOneWindowInt
        double currX = spectrogramStart;
        int nextPixel = (int)(currX + 1);    // Used only if (pixelWidthForWindowPart < 1)

        double multiplyFactor = lastWindowReminder;
        int indexWhereToPutNotFullValue = currentlyCalculatedMeasures[0].length - 1;
        double[] arrWithNotFullMeasures = new double[fftMeasures.length];
TODO: // TODO: Just debug label

        for(int i = startIndex, currWindow = 0; i < endIndex; i += windowShift, currWindow++, indexWhereToPutNotFullValue++) {
            indexWhereToPutNotFullValue %= currentlyCalculatedMeasures[0].length;
            oldestWindow = newOldestWindow;
            oldestWindowSet = false;

            double windowPartsCount = windowOverlapCountForOneWindow;       // TODO:
            if (pixelWidthForWindowPart < 1) {
                currWindow = 0; // Because currWindow says which window is currently drawn, and we want to always draw the first in this case
                System.out.println("DEBUG:\t" + currX + "\t" + nextPixel);


// TODO:                double windowPartsCount = windowOverlapCountForOneWindow;
                double lastWindowPart;
                if(lastWindowReminder == 0) {
// TODO:                    windowPartsCount = currentlyCalculatedMeasures[0].length;
                    lastWindowPart = 0;
                }
                else {
// TODO:                    windowPartsCount = (int)Math.ceil(windowsOverlaps[0]);
                    lastWindowPart = lastWindowParts[0];
                }

                if (currX < nextPixel) {
                    System.out.println("NEWMEASURES:\t" + currentlyCalculatedMeasures[0][0]);
                    addCurrentMeasures(song, i, numberOfChannels, fft, fftResult, fftMeasures, logarithmBase,
                        currWindow, currentlyCalculatedMeasures, tmpArr, tmpArr2, multiplyFactor,
                        windowPartsCount, true, counters, windowsOverlaps, lastWindowParts, tmpArrs,
                        indexWhereToPutNotFullValue, arrWithNotFullMeasures);
                    continue;           // Just keep calculating the values until we can draw the pixel value
                }

                nextPixel++;
            }
            else {
                if(lastWindowReminder == 0) {
//                    int windowPartsCount = currentlyCalculatedMeasures[0].length;
                    addCurrentMeasures(song, i, numberOfChannels, fft, fftResult, fftMeasures, logarithmBase,
                        currWindow, currentlyCalculatedMeasures, tmpArr, tmpArr2, 0,
                        windowPartsCount, false, counters, windowsOverlaps, lastWindowParts, tmpArrs,
                        indexWhereToPutNotFullValue, arrWithNotFullMeasures);
                }
                else {
                    int mod = currWindow % currentlyCalculatedMeasures[0].length;
//                    int windowPartsCount = (int)Math.ceil(windowsOverlaps[mod]);
                    addCurrentMeasures(song, i, numberOfChannels, fft, fftResult, fftMeasures, logarithmBase,
                        currWindow, currentlyCalculatedMeasures, tmpArr, tmpArr2, multiplyFactor,
                        windowPartsCount, false, counters, windowsOverlaps, lastWindowParts, tmpArrs,
                        indexWhereToPutNotFullValue, arrWithNotFullMeasures);
                }
            }
            // TODO: Nahrazeno funkci
//            calculateFFTRealForward(song, i, numberOfChannels, fft, fftResult);
//            convertResultsOfFFTToRealRealForward(fftResult, fftMeasures);
//            for(int j = 0; j < fftMeasures.length; j++) {
//                fftMeasures[j] += 1;      // Not the minimum energy is 1, so the minimum log == 0
//            }
//
//            Program.performOperationOnSamples(fftMeasures, logarithmBase, ArithmeticOperation.LOG);
//// TODO: Asi nemusim a jestli musim, tak to je vypocet navic, protoze tam posilam sqrt stejne
//            for(int j = 0; j < fftMeasures.length; j++) {
//// TODO: Debug print
////                if(fftMeasures[j] > 0) {
////                    System.out.println(j + "\t" + fftMeasures[j]);
////                }
//
//                if(currWindow < currentlyCalculatedMeasures[j].length) {
//                    int k;
//                    for(k = 0; k < currWindow - 1; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / currentlyCalculatedMeasures[j].length;
//                    }
//                    for(; k < currentlyCalculatedMeasures[j].length; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / (k+1);
//                    }
//                }
//                else {
//                    for(int k = 0; k < currentlyCalculatedMeasures[j].length; k++) {
//                        currentlyCalculatedMeasures[j][k] += fftMeasures[j] / currentlyCalculatedMeasures[j].length;
//                    }
//                }




// TODO:
//                if(fftMeasures[j] > 0x00ffffff) {       // TODO: To neni dobre asi
//                    fftMeasures[j] = 0x00ffffff;
//                }
//                else {
//                    fftMeasures[j] %= 0x00ffffff;
//                }
//                fftMeasures[j] *= fftMeasures[j];
//            }

            System.out.println("counter0\t" + counters[0] + "\t" + lastWindowParts[0] + "\t" + windowsOverlaps[0]);
            System.out.println("counter1\t" + counters[1] + "\t" + lastWindowParts[1] + "\t" + windowsOverlaps[1]);
            // TODO: nextY useless
            // TODO: window = oldestWindow ??? ... asi newest ale u neho je zase problem s -Infinity
//            for(int window = oldestWindow; window < windowsOverlaps.length; window++) {
//                double currY = spectrogramHeightInPixels - pixelHeightForBin;
//                if (counters[window] >= windowOverlapCountForOneWindow) {
//                    newestWindow = window;
//                    if (!oldestWindowSet) {
//                        oldestWindowSet = true;
//                        newOldestWindow = window;
//                    }
////TODO:System.exit(currWindow);
//                    currX = Program.drawOneWindowInSpectrogram(currY, counters, windowsOverlaps, lastWindowParts, currentlyCalculatedMeasures,
//                        window, oldestWindowSet, newOldestWindow, currWindow, pixelHeightForBin, binCount,
//                        windowSize, logarithmBase, g, currX, pixelWidthForWindow, lastWindowReminder,
//                        tmpArrs, windowOverlapCountForOneWindow);
//                }
//            }
//
//
//            for(int window = 0; window < oldestWindow; window++) {
                double currY = spectrogramHeightInPixels - pixelHeightForBin;
//                if (counters[window] >= windowOverlapCountForOneWindow) {
//                    newestWindow = window;
//                    if (!oldestWindowSet) {
//                        oldestWindowSet = true;
//                        newOldestWindow = window;
//                    }
////TODO:System.exit(currWindow);
                    currX = Program.drawOneWindowInSpectrogram(currY, counters, windowsOverlaps, lastWindowParts, currentlyCalculatedMeasures,
                        oldestWindowSet, newOldestWindow, currWindow, pixelHeightForBin, binCount,
                        windowSize, logarithmBase, g, currX, pixelWidthForWindow, lastWindowReminder,
                        tmpArrs, windowOverlapCountForOneWindow, windowsPerPixel);
//                }
//            }

            System.out.println("counter0\t" + counters[0] + "\t" + lastWindowParts[0] + "\t" + windowsOverlaps[0]);
            System.out.println("counter1\t" + counters[1] + "\t" + lastWindowParts[1] + "\t" + windowsOverlaps[1]);
System.out.println();



//                if(counters[window] >= windowsOverlaps[window]) {
//                    if(!oldestWindowSet) {
//                        oldestWindowSet = true;
//                        newOldestWindow = window;
//                    }
//                    System.out.println("COUNTER:\t" + currWindow + "\t" + window);
//                    System.out.println(counters[1] + "\t" + windowsOverlaps[1] + "\t" +  lastWindowParts[1]);
//                    for (int bin = 0; bin < binCount; bin++, currY -= pixelHeightForBin) {
//                        // So the max is 1, but I am not really sure if I shouldn't do something further, like multiply it by some factor so the maximum is == 1.
//                        // If I don't do anything then the maximum is reached only if the wave is constant wave at 1.
//                        double val;
//                        val = currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length];
//                        val = currentlyCalculatedMeasures[bin][window];     // TODO:
//                        // windowSize + 1 because the max value is raised by +1 for each fftMeasures, it is because of the log,
//                        // so now bin with min energy has value of log(1) == 0
//                        //TODO:DEBUGSystem.out.println(windowSize * 2); System.exit(1);
//                        double windowSizeLog = Math.log(windowSize + 1) / Math.log(logarithmBase);
//
//                        System.out.println(val);
//                        System.out.println(windowSizeLog);
//                        val /= windowSizeLog;
//                        //val /= 2;
//                        //val /= (windowSize / 2);        // TODO:
//                        //val *= 300000;
//                        //if(val > 1) { System.out.println("Bigger than 1:\t" + val + "\t" + currWindow + "\t" + bin); System.exit(555); }
//// TODO:                if(currX > spectrogramStart) break TODO;
//                        Color c;
//                        c = Color.getHSBColor(1 - (float) val, (float) val, (float) val);
////                c = Color.getHSBColor(1,(float)val,(float)val);
////                c = Color.getHSBColor(-1+(float)val, (float)-val, (float)-val);
//                        //val = Double.NEGATIVE_INFINITY;
//                        //c = Color.getHSBColor((float)(1-val),(float)val,(float)val);
////                System.out.println(val + "\t" + currentlyCalculatedMeasures[bin].length);
//                        g.setColor(c);
//                        //g.setColor(Color.red);// currY = spectrogramHeightInPixels - pixelHeightForBin;
//                        // TODO: Mozna Math.ceil
//                        g.fillRect((int) currX, (int) currY, (int) Math.ceil(pixelWidthForWindow), (int) Math.ceil(pixelHeightForBin));        // TODO: asi jeste udelat i pro tu y souradnici (tj ty biny) to spojovani do 1 pixelu, kdyz to je moc velky
//                        //TODO: DEBUGSystem.out.println("Drawn rectangle:\t" + currX + "\t" + currY + "\t" + pixelWidthForWindow + "\t" + pixelHeightForBin);
////
////                System.out.println(currX + "\t" + currY + "\t" + i + "\t" + endIndex);
////                System.out.println(spectrogramWidthInPixels + "\t" + spectrogramHeightInPixels);
////                System.out.println();
///
//
//                        // It was used and is now the latest unused, now there are 2 cases:
//                        // a) lastWindowReminder == 0 then it is set to 0 and will be set to 0 again after passing windowOverlapCountForOneWindowInt windows
//                        // b) lastWindowReminder != 0 then it is set to the reminder of the window which wasn't used before, which is original fftMeasures[bin] * (1-reminder) to the result add 1 and take logarithm
//                        if (lastWindowReminder == 0) {
//                            currentlyCalculatedMeasures[bin][window] = 0;
//                        } else {
//                            if (lastWindowParts[currWindow % windowsOverlaps.length] != 0) {
//                                currentlyCalculatedMeasures[bin][window] = tmpArrs[window][1][bin] / windowOverlapCountForOneWindow;
////                        currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length] = 0;
//                            } else {
//                                currentlyCalculatedMeasures[bin][window] = 0;
//                            }
//                        }
//                    }
//
//                    if (lastWindowReminder != 0) {
//                        int mod = window % windowsOverlaps.length;          // TODO:
//                        if (windowsOverlaps[0] == 0) {
//                            windowsOverlaps[0] = windowOverlapCountForOneWindow;
//                            lastWindowParts[0] = lastWindowReminder;
//                        } else {
//                            if (mod == 0) {
//                                windowsOverlaps[0] = (int) Math.ceil(windowsOverlaps[windowsOverlaps.length - 1]) - windowsOverlaps[windowsOverlaps.length - 1];
//                                windowsOverlaps[0] = windowOverlapCountForOneWindow - windowsOverlaps[0];
//                            } else {
//                                System.out.println("OVERLAP:\t" + windowsOverlaps[mod] + "\t" + windowsOverlaps[mod - 1] + "\t" + windowOverlapCountForOneWindow);
//                                windowsOverlaps[mod] = (int) Math.ceil(windowsOverlaps[mod - 1]) - windowsOverlaps[mod - 1];
//                                windowsOverlaps[mod] = windowOverlapCountForOneWindow - windowsOverlaps[mod];
//                                System.out.println("OVERLAP:\t" + windowsOverlaps[mod]);
//                            }
//                            lastWindowParts[mod] = windowsOverlaps[mod] % 1;
//                        }
//                    }
//
//
//
//                    if (lastWindowReminder == 0) {
//                        counters[window] = 0;
//                    } else {
//                        if (lastWindowParts[currWindow % windowsOverlaps.length] != 0) {
//                            counters[window] = lastWindowParts[window];
//                            //counters[window] = 0;
//                        } else {
//                            counters[window] = 0;
//                        }
//                    }
//
//
//
//                    currX += pixelWidthForWindow;
//                }
//            }
        }



        // Draw the measure reference
        g.setColor(Color.white);
        int startXReference = spectrogramStart + spectrogramWidthInPixels;     // TODO: V tehle jmenech promennych je bordel

        // TODO: bud to mit oddeleny jen bilou carou ... to je to zakomentovany nebo tam mit proste bilej obdelnik to je to nezakomentovany ... jedna se jen o tyhle 3 nasledujici radky
        // TODO: +20 kdyz chci tu carku
        // TODO: Ten ctverec je lepsi ... podle me
        //int startXReference = spectrogramStart + spectrogramWidthInPixels + 20;
//        g.drawLine(startXReference, 0, startXReference, spectrogramHeightInPixels);
        g.fillRect(startXReference, 0, spectrogramWidthInPixelsWithReferenceAndHzLabels, spectrogramHeightInPixels);

        double pixelSkipDouble = 1.0 / spectrogramHeightInPixels;
        int x = startXReference + 20;
        int width = 60;
        int height = 1;
        double colorVal = 0;
        for(int y = spectrogramHeightInPixels; y >= 0; y--, colorVal += pixelSkipDouble) {
            Color c = Color.getHSBColor(1 - (float) colorVal, (float) colorVal, (float) colorVal);
            g.setColor(c);
            g.fillRect(x, y, width, height);
            System.out.println("test\t" + x + "\t" + y + "\t" + pixelSkipDouble + "\t" + colorVal + "\t" + (colorVal + pixelSkipDouble));
        }


        // Draw frequency labels
        int n = 8;      // take every nth bin
        double maxTextHeight = n * pixelHeightForBin;
        String[] binFreqs = getFreqs(binCount, freqJump, 0, n, 2);
        getFont(24, g, binFreqs, spectrogramStart - 15, (int)maxTextHeight, 1);
        FontMetrics fontMetrics = g.getFontMetrics();

        g.setColor(Color.white);
        g.fillRect(0, 0, spectrogramStart, spectrogramHeightInPixels);
        int lineXSmall = spectrogramStart - 5;
        int lineXBig = lineXSmall - 5;
        g.drawLine(lineXSmall, 0, lineXSmall, spectrogramHeightInPixels);
        g.setColor(Color.black);

        // Draw the small lines
        double currY = 0;
        double midY = pixelHeightForBin / 2;
        for(int i = 0; i < binCount; i++, currY += pixelHeightForBin, midY += pixelHeightForBin) {
            if(i % n!= 0) {
                g.drawLine(lineXSmall, (int) midY, spectrogramStart, (int) midY);   // TODO: Zase mozna Math.ceil
            }
        }

        int textHeight = fontMetrics.getHeight();
        double freeSpace = maxTextHeight - textHeight;
        freeSpace /= 2;
        // Draw the big lines + text
        currY = 0;
        midY = pixelHeightForBin / 2;
        for(int bin = binFreqs.length - 1; bin >= 0; bin--, currY += maxTextHeight, midY += maxTextHeight)
        {
            System.out.println(bin + "\t"  + currY);
            g.drawLine(lineXBig, (int)midY, spectrogramStart, (int)midY);   // TODO: Zase mozna Math.ceil
            int textLen = fontMetrics.stringWidth(binFreqs[bin]);
            if(bin == binFreqs.length - 1) {        // Special cases at top and at bottom (0Hz and nyquist freq)
                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, 15);   // TODO: y == 15 because of the top ledge (the ledge with cross)
            }
            else if(bin == 0) {
                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, spectrogramHeightInPixels);
            }
            else {
                g.drawString(binFreqs[bin], spectrogramStart - textLen - 15, (int)(currY + freeSpace));    // TODO: Zase mozna Math.ceil
            }
        }


        // Draw KHz to the top left
        g.drawString("KHz", 0, (int)(spectrogramHeightInPixels - maxTextHeight / 2));      // TODO: Bude tam obcas prekryv

        return spectrogram;
    }


    private static void resetDoubleArr(double[][] arr) {
        for(int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                arr[i][j] = 0;
            }
        }
    }



    private static double drawOneWindowInSpectrogram(double currY,
                                                     double[] counters, double[] windowsOverlaps, double[] lastWindowParts,
                                                     double[][] currentlyCalculatedMeasures,
                                                     boolean oldestWindowSet, int newOldestWindow,
                                                     int currWindow, double pixelHeightForBin, int binCount,
                                                     int windowSize, int logarithmBase, Graphics g,
                                                     double currX, double pixelWidthForWindow, double lastWindowReminder,
                                                     double[][][] tmpArrs, double windowOverlapCountForOneWindow, double windowsPerPixel) {
        System.out.println(counters[1] + "\t" + windowsOverlaps[1] + "\t" + lastWindowParts[1]);
        for (int bin = 0; bin < binCount; bin++, currY -= pixelHeightForBin) {
            // So the max is 1, but I am not really sure if I shouldn't do something further, like multiply it by some factor so the maximum is == 1.
            // If I don't do anything then the maximum is reached only if the wave is constant wave at 1.
            double val;
            val = currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length];
            // windowSize + 1 because the max value is raised by +1 for each fftMeasures, it is because of the log,
            // so now bin with min energy has value of log(1) == 0
            //TODO:DEBUGSystem.out.println(windowSize * 2); System.exit(1);
            double windowSizeLog = Math.log(windowSize + 1) / Math.log(logarithmBase);

            System.out.println("VAL:\t" + currWindow + "\t" + bin + "\t" + val);
            System.out.println(windowSizeLog);
            val /= windowSizeLog;
    //        val /= windowsPerPixel;
            //val /= 2;
            //val /= (windowSize / 2);        // TODO:
            //val *= 300000;
           //if(val > 1) { System.out.println("Bigger than 1:\t" + val + "\t" + currWindow + "\t" + bin); System.exit(555); }
// TODO:                if(currX > spectrogramStart) break TODO;
            Color c;
            c = Color.getHSBColor(1 - (float) val, (float) val, (float) val);
//                c = Color.getHSBColor(1,(float)val,(float)val);
//                c = Color.getHSBColor(-1+(float)val, (float)-val, (float)-val);
            //val = Double.NEGATIVE_INFINITY;
            //c = Color.getHSBColor((float)(1-val),(float)val,(float)val);
//                System.out.println(val + "\t" + currentlyCalculatedMeasures[bin].length);
            g.setColor(c);
            //g.setColor(Color.red);// currY = spectrogramHeightInPixels - pixelHeightForBin;
            // TODO: Mozna Math.ceil
            g.fillRect((int) currX, (int) currY, (int) Math.ceil(pixelWidthForWindow), (int) Math.ceil(pixelHeightForBin));        // TODO: asi jeste udelat i pro tu y souradnici (tj ty biny) to spojovani do 1 pixelu, kdyz to je moc velky
            //TODO: DEBUGSystem.out.println("Drawn rectangle:\t" + currX + "\t" + currY + "\t" + pixelWidthForWindow + "\t" + pixelHeightForBin);
//
//                System.out.println(currX + "\t" + currY + "\t" + i + "\t" + endIndex);
//                System.out.println(spectrogramWidthInPixels + "\t" + spectrogramHeightInPixels);
//                System.out.println();


            // It was used and is now the latest unused, now there are 2 cases:
            // a) lastWindowReminder == 0 then it is set to 0 and will be set to 0 again after passing windowOverlapCountForOneWindowInt windows
            // b) lastWindowReminder != 0 then it is set to the reminder of the window which wasn't used before, which is original fftMeasures[bin] * (1-reminder) to the result add 1 and take logarithm
//            if (lastWindowReminder == 0) {
//                currentlyCalculatedMeasures[bin][window] = 0;
//            } else {
//                if (lastWindowParts[window] != 0) {
//                    System.out.println(":))))\t" + bin + "\t" + window + "\t" + currentlyCalculatedMeasures[bin][window]);
//                    currentlyCalculatedMeasures[bin][window] = tmpArrs[window][1][bin] / windowOverlapCountForOneWindow;
//                    System.out.println(":))))\t" + bin + "\t" + window + "\t" + currentlyCalculatedMeasures[bin][window]);
////                        currentlyCalculatedMeasures[bin][currWindow % currentlyCalculatedMeasures[bin].length] = 0;
//                } else {
//                    currentlyCalculatedMeasures[bin][window] = 0;
//                }
//            }
//        }
//
//
//
//
//        int mod = window % windowsOverlaps.length;          // TODO: neni nutny ted uz, driv pro currWindow bylo, ale ted to delam primo pres window bez mod
//        System.out.println("OVERLAP1:\t" + mod + "\t" + windowsOverlaps[mod] + "\t" + lastWindowParts[mod] + "\t" + counters[mod] + "\t" + ((counters[mod] - 1) + lastWindowParts[mod]));
//        if (lastWindowReminder == 0) {
//            counters[window] = 0;
//        } else {
//            if (lastWindowParts[mod] != 0) {
//                counters[window] = 1 - lastWindowParts[window];
//                //counters[window] = 0;
//            } else {
//                counters[window] = 0;
//            }
//        }
//        System.out.println(mod + "\t" + counters[mod]);
//
//        if (lastWindowReminder != 0) {
//            if (windowsOverlaps[0] == 0) {
//                windowsOverlaps[0] = windowOverlapCountForOneWindow;
//                lastWindowParts[0] = lastWindowReminder;
//            } else {
//                if (mod == 0) {
//                    windowsOverlaps[0] = (int) Math.ceil(windowsOverlaps[windowsOverlaps.length - 1]) - windowsOverlaps[windowsOverlaps.length - 1];
//                    windowsOverlaps[0] = windowOverlapCountForOneWindow - windowsOverlaps[0];
//                } else {
////                    System.out.println("OVERLAP:\t" + windowsOverlaps[mod] + "\t" + windowsOverlaps[mod - 1] + "\t" + windowOverlapCountForOneWindow);
//                    windowsOverlaps[mod] = (int) Math.ceil(windowsOverlaps[mod - 1]) - windowsOverlaps[mod - 1];
//                    windowsOverlaps[mod] = windowOverlapCountForOneWindow - windowsOverlaps[mod];
////                    System.out.println("OVERLAP:\t" + windowsOverlaps[mod]);
//                }
//                lastWindowParts[mod] = windowsOverlaps[mod] % 1;
//            }
//
//            System.out.println("OVERLAP2:\t" + windowsOverlaps[mod] + "\t" + lastWindowParts[mod] + "\t" + ((counters[mod] - 1) + lastWindowParts[mod]));
        }


        currX += pixelWidthForWindow;
        return currX;
    }


    // TODO: Asi je nutny so nasobit jeste predtim logaritmovanim - pred tim to fungovalo protoze jsem bral jakoby prumery, ted uz je neberu, takze dostavam hodnoty co jsou mimo range
    // TODO: To cim to mam delit je vlastne windowsPerPixel * multiplyFactor ... ale kdybych to chtel delit uz predtim tak ty addCurrentMeasures metody musim jakoby presunout pred to logaritmovani a v tamtech metodach jen prictu uz bez deleni
    private static void addCurrentMeasures(double[] song, int currSongIndex, int numberOfChannels, DoubleFFT_1D fft,
                                           double[] fftResult, double[] fftMeasures, double logarithmBase, int currWindow,
                                           double[][] currentlyCalculatedMeasures,
                                           double[] tmpArr, double[] tmpArr2, double multiplyFactor,
                                           double windowOverlapCountForOneWindow, boolean moreWindowsPerPixel,
                                           double[] counters, double[] windowsOverlaps, double[] multiplyFactors,
                                           double[][][] tmpArrs, int indexWhereToPutNotFullValue, double[] arrWithNotFullMeasures) {
        int mod = currWindow % currentlyCalculatedMeasures[0].length;
        // TODO: int mod = currWindow % currentlyCalculatedMeasures[bin].length; takhle to bylo predtim, ale vzhledem k tomu ze to ma stejny rozmery tak to nevadi
//        for(int i = currSongIndex; i <= currSongIndex + fftResult.length; i++) {
//            song[i] = 1;
//        }
        calculateFFTRealForward(song, currSongIndex, fftResult.length, numberOfChannels, fft, fftResult);         // TODO: Tahle vicekanalova verze se mi vubec nelibi
        convertResultsOfFFTToRealRealForward(fftResult, fftMeasures);
        System.out.println("TODO:\t" + currentlyCalculatedMeasures[1][1]);
        System.out.println("--------------------------");
        for(int i = 0; i < windowsOverlaps.length; i++) {
            if(currWindow >= windowsOverlaps.length) {      // TODO:
                counters[i]++;
                System.out.println("COUNTERS:\t" + i + "\t" + counters[i] + "\t" + windowsOverlaps[i] + "\t" + multiplyFactors[i]);
            }
            if((multiplyFactors[i] != 0 && currWindow >= windowsOverlaps.length) || moreWindowsPerPixel) {
                System.out.println(i + "\t" + multiplyFactors[i] + "\t" + (multiplyFactors[i] != 0) + "\t" + (currWindow >= windowsOverlaps.length));
                if (counters[i] >= windowOverlapCountForOneWindow) {
                    for(int bin = 0; bin < fftMeasures.length; bin++) {
                        tmpArrs[i][0][bin] = fftMeasures[bin] * multiplyFactors[i];
                        tmpArrs[i][1][bin] = fftMeasures[bin] * (1 - multiplyFactors[i]);
                        tmpArrs[i][0][bin]++;
                        tmpArrs[i][1][bin]++;
                    }

//TODO:
//                    if(currWindow == 9 && i == 1) {
//                        System.out.println("TmpArrs1:\t" + i + "\t" + tmpArrs[i][0][1]);
//                        System.exit(11111);
//                    }
                }
            }
//TODO:
//            System.out.println("TmpArrs0:\t" + i + "\t" + tmpArrs[i][0][1]);
//            if(currWindow == 9 && i == 1) {
//                System.out.println(counters[i] + "\t" + windowOverlapCountForOneWindow + "\t" + (multiplyFactors[i]) +
//                    "\t" + fftMeasures[0] + "\t" + (fftMeasures[0] / windowOverlapCountForOneWindow) +
//                    "\t" + fftMeasures[1] + "\t" + (fftMeasures[1] / windowOverlapCountForOneWindow));
//                System.exit(86666);
//            }
        }


        for(int bin = 0; bin < fftMeasures.length; bin++) {
            if(multiplyFactor != 0 || moreWindowsPerPixel) {
                arrWithNotFullMeasures[bin] = fftMeasures[bin] * multiplyFactor;
                arrWithNotFullMeasures[bin]++;
            }
            if((multiplyFactor != 0 && currWindow >= currentlyCalculatedMeasures[bin].length) || moreWindowsPerPixel) {
                tmpArr[bin] = fftMeasures[bin] * multiplyFactor;
                tmpArr2[bin] = fftMeasures[bin] * (1 - multiplyFactor);
                tmpArr[bin]++;
                tmpArr2[bin]++;
            }

            fftMeasures[bin]++;      // Now the minimum energy is 1, so the minimum log == 0
        }

        double TODOsum = 0;
        for(int i = 0; i < fftMeasures.length; i++) {
            if(fftMeasures[i] > 0) System.out.println("MEASURES:\t" + i +"\t" + fftMeasures[i]);
            TODOsum += fftMeasures[i];
        }
        System.out.println(TODOsum);
        System.out.println(fftMeasures[0]);
        //TODO:System.exit(-666);


        // TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!! Vymazat tmpArrs a vsechny tyhle veci
        System.out.println("TODO:\t" + currentlyCalculatedMeasures[1][1]);
//            for (int window = 0; window < tmpArrs.length; window++) {
//                if((multiplyFactors[window] != 0 && currWindow >= windowsOverlaps.length) || moreWindowsPerPixel) {
//                    if (counters[window] >= windowOverlapCountForOneWindow) {
//                        Program.performOperationOnSamples(tmpArrs[window][0], logarithmBase, ArithmeticOperation.LOG);
//                        Program.performOperationOnSamples(tmpArrs[window][1], logarithmBase, ArithmeticOperation.LOG);
//                    }
//                }
//            }
//TODO:        System.out.println("TmpArrs1:\t" + tmpArrs[1][0][1]);

// TODO: PROGRAMO - REDOING operations
        Program.performOperationOnSamples(fftMeasures, fftMeasures, 0, 0,
            fftMeasures.length, logarithmBase, ArithmeticOperation.LOG);
        //Program.operationOnSamplesByReference(fftMeasures, logarithmBase, ArithmeticOperation.LOG);
        if((multiplyFactor != 0 && currWindow >= currentlyCalculatedMeasures[0].length) || moreWindowsPerPixel) {
            Program.performOperationOnSamples(tmpArr, tmpArr, 0, 0,
                tmpArr.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(tmpArr, logarithmBase, ArithmeticOperation.LOG);
            Program.performOperationOnSamples(tmpArr2, tmpArr2, 0, 0,
                tmpArr2.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(tmpArr2, logarithmBase, ArithmeticOperation.LOG);
        }

        if(multiplyFactor != 0 || moreWindowsPerPixel) {
            Program.performOperationOnSamples(arrWithNotFullMeasures, arrWithNotFullMeasures, 0, 0,
                arrWithNotFullMeasures.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(arrWithNotFullMeasures, logarithmBase, ArithmeticOperation.LOG);
        }
// TODO: PROGRAMO - REDOING operations

        for(int i = 0; i < fftMeasures.length; i++) {
            if(fftMeasures[i] < 0) {
                System.exit(45679);
            }
        }

        System.out.println("....:\t" + currentlyCalculatedMeasures[0][1]);
// TODO: Asi nemusim a jestli musim, tak to je vypocet navic, protoze tam posilam sqrt stejne
        for(int bin = 0; bin < fftMeasures.length; bin++) {
// TODO: Debug print
                if(fftMeasures[bin] > 0) {
                    System.out.println(bin + "\t" + fftMeasures[bin]);
                }
            if(moreWindowsPerPixel) {
                addCurrentMeasures(mod, currentlyCalculatedMeasures[bin], fftMeasures[bin], bin, tmpArr, multiplyFactor,
                    windowOverlapCountForOneWindow, counters, windowsOverlaps, tmpArrs, multiplyFactors,
                    indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin]);
            }
            else {
                if (currWindow < currentlyCalculatedMeasures[bin].length) {
                    addCurrentMeasures(currWindow, currentlyCalculatedMeasures[bin], fftMeasures[bin], windowOverlapCountForOneWindow,
                        indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin], multiplyFactor);

                } else {
                    addCurrentMeasures(mod, currentlyCalculatedMeasures[bin], fftMeasures[bin], bin, tmpArr, multiplyFactor,
                        windowOverlapCountForOneWindow, counters, windowsOverlaps, tmpArrs, multiplyFactors,
                    indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin]);
                }
            }


// TODO:
//            double res = currentlyCalculatedMeasures[bin][1];
//            if(res > 10) {
//                System.out.println(".....\t" + bin + "\t" + arrWithNotFullMeasures[bin] +
//                    "\t" + windowOverlapCountForOneWindow + "\t" + (arrWithNotFullMeasures[bin] / windowOverlapCountForOneWindow));
//                System.exit(-123456);
//            }


            System.out.println("bin :]\t" + bin);
//            if(currWindow == 9) {
//                System.out.println(counters[1] + "\t" + windowOverlapCountForOneWindow + "\t" + (multiplyFactors[1]) +
//                    "\t" + fftMeasures[0] + "\t" + (fftMeasures[0] / windowOverlapCountForOneWindow) + "\t" +
//                    Double.isNaN(currentlyCalculatedMeasures[bin][1]));
//                if(Double.isNaN(currentlyCalculatedMeasures[bin][1]))                System.exit(8743100);
//            }
// TODO: DEBUG
//            for(int i = 0; i < currentlyCalculatedMeasures[bin].length; i++) {
//                System.out.println("currMeasures:\t" + i + ":\t" + bin + "\t" + currentlyCalculatedMeasures[bin][i] +
//                    "\t" + tmpArrs[i][0][bin] + "\t" + fftMeasures[bin] + "\t" + multiplyFactors[1] + "\t" +
//                    (fftMeasures[bin] * multiplyFactors[1]) + "\t" + windowOverlapCountForOneWindow + "\t" +
//                    (tmpArrs[i][0][bin] / windowOverlapCountForOneWindow));
//
//                System.out.println(currWindow);
//                if(currentlyCalculatedMeasures[bin][i] < 0) {
//                    System.exit(123456);
//                }
//                if(Double.isNaN(currentlyCalculatedMeasures[bin][i]) || Double.isInfinite(currentlyCalculatedMeasures[bin][i])) System.exit(98989);
//
//               // if(Double.isNaN(tmpArrs[i][0][bin])) System.exit(989890);
//            }
        }
// TODO: DEBUG        System.out.println("....\t" + currentlyCalculatedMeasures[0][1]);

    }



    private static void addCurrentMeasures(int currWindow, double[] currentlyCalculatedMeasures,
                                           double fftMeasure, double numberOfWindowParts,
                                           int indexWhereToPutNotFullValue, double notFullMeasure, double multiplyFactor) {
        int i;
        for(i = 0; i < currWindow; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / numberOfWindowParts;
        }

        for(; i < currentlyCalculatedMeasures.length - 1; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / (i+1);        // TODO: To se mi nezda
        }
        currentlyCalculatedMeasures[i] += fftMeasure / numberOfWindowParts;
        if(multiplyFactor != 0) {
            currentlyCalculatedMeasures[indexWhereToPutNotFullValue] = notFullMeasure / numberOfWindowParts;
            currentlyCalculatedMeasures[indexWhereToPutNotFullValue] = multiplyFactor * fftMeasure / numberOfWindowParts;
        }

// TODO:
//        System.out.println("LOL:\t" + currentlyCalculatedMeasures[1]);
//        if(currWindow == 1) System.exit(indexWhereToPutNotFullValue);

//        if(currWindow == 0 && multiplyFactor != 0) {
//            currentlyCalculatedMeasures[currentlyCalculatedMeasures.length - 1] = notFullMeasure / numberOfWindowParts;
//        }
    }

    private static void addCurrentMeasures(int currWindowMod, double[] currentlyCalculatedMeasures,
                                           double fftMeasure, int bin, double[] tmpArr,
                                           double multiplyFactor, double windowOverlapCountForOneWindow,
                                           double[] counters, double[] numberOfWindowParts,
                                           double[][][]tmpArrs, double[] multiplyFactors,
                                           int indexWhereToPutNotFullValue, double notFullMeasure) {
        if(bin == 0) {
            System.out.println(bin + "\t" + fftMeasure + "\t:::::\t" + notFullMeasure);
            for(int i = 0; i < currentlyCalculatedMeasures.length; i++) {
                if (currentlyCalculatedMeasures[i] > 11) {
                    System.out.println("OVER:\t" + i + "\t" + currentlyCalculatedMeasures[i]);
                }
            }
        }


        int i = 0;
        for(; i < indexWhereToPutNotFullValue; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
        }

        if(multiplyFactor != 0) {
            currentlyCalculatedMeasures[i] = notFullMeasure / windowOverlapCountForOneWindow;
            currentlyCalculatedMeasures[i] = multiplyFactor * fftMeasure / windowOverlapCountForOneWindow;
            i++;
        }

        for(; i < currentlyCalculatedMeasures.length; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
        }

//System.out.println("IN1:\t" + multiplyFactors[1]);
//        for(; i < currentlyCalculatedMeasures.length; i++) {
//            System.out.println("IN2:\t" + i + "\t" + bin + "\t" + currentlyCalculatedMeasures[i]);
//            if(multiplyFactors[i] == 0) {
//                currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
//                System.out.println("IN:\t" + i + "\t" + bin + "\t" + currentlyCalculatedMeasures[i] + "\t" + fftMeasure);
//            }
//            else {
//                if (counters[i] >= windowOverlapCountForOneWindow) {
//                    currentlyCalculatedMeasures[i] += tmpArrs[i][0][bin] / windowOverlapCountForOneWindow;
//                } else {
//                    currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
//                }
//            }
//        }
    }







    private static void addCurrentMeasuresMoreWindowsPerPixel(double[] song, int currSongIndex, int numberOfChannels, DoubleFFT_1D fft,
                                           double[] fftResult, double[] fftMeasures, double logarithmBase, int currWindow,
                                           double[][] currentlyCalculatedMeasures,
                                           double[] tmpArr, double[] tmpArr2, double multiplyFactor,
                                           double windowOverlapCountForOneWindow, boolean moreWindowsPerPixel,
                                           double[] counters, double[] windowsOverlaps, double[] multiplyFactors,
                                           double[][][] tmpArrs, int indexWhereToPutNotFullValue, double[] arrWithNotFullMeasures) {
        int mod = currWindow % currentlyCalculatedMeasures[0].length;
        // TODO: int mod = currWindow % currentlyCalculatedMeasures[bin].length; takhle to bylo predtim, ale vzhledem k tomu ze to ma stejny rozmery tak to nevadi
        for(int i = currSongIndex; i <= currSongIndex + fftResult.length; i++) {
            song[i] = 1;
        }
        calculateFFTRealForward(song, currSongIndex, fftResult.length, numberOfChannels, fft, fftResult);         // TODO: Tahle vicekanalova verze se mi vubec nelibi
        convertResultsOfFFTToRealRealForward(fftResult, fftMeasures);
        System.out.println("TODO:\t" + currentlyCalculatedMeasures[1][1]);
        System.out.println("--------------------------");
        for(int i = 0; i < windowsOverlaps.length; i++) {
            if(currWindow >= windowsOverlaps.length) {      // TODO:
                counters[i]++;
                System.out.println("COUNTERS:\t" + i + "\t" + counters[i] + "\t" + windowsOverlaps[i] + "\t" + multiplyFactors[i]);
            }
            if((multiplyFactors[i] != 0 && currWindow >= windowsOverlaps.length) || moreWindowsPerPixel) {
                System.out.println(i + "\t" + multiplyFactors[i] + "\t" + (multiplyFactors[i] != 0) + "\t" + (currWindow >= windowsOverlaps.length));
                if (counters[i] >= windowOverlapCountForOneWindow) {
                    for(int bin = 0; bin < fftMeasures.length; bin++) {
                        tmpArrs[i][0][bin] = fftMeasures[bin] * multiplyFactors[i];
                        tmpArrs[i][1][bin] = fftMeasures[bin] * (1 - multiplyFactors[i]);
                        tmpArrs[i][0][bin]++;
                        tmpArrs[i][1][bin]++;
                    }
                }
            }
        }


        for(int bin = 0; bin < fftMeasures.length; bin++) {
            if(multiplyFactor != 0 || moreWindowsPerPixel) {
                arrWithNotFullMeasures[bin] = fftMeasures[bin] * multiplyFactor;
                arrWithNotFullMeasures[bin]++;
            }
            if((multiplyFactor != 0 && currWindow >= currentlyCalculatedMeasures[bin].length) || moreWindowsPerPixel) {
                tmpArr[bin] = fftMeasures[bin] * multiplyFactor;
                tmpArr2[bin] = fftMeasures[bin] * (1 - multiplyFactor);
                tmpArr[bin]++;
                tmpArr2[bin]++;
            }

            fftMeasures[bin]++;      // Now the minimum energy is 1, so the minimum log == 0
        }

        double TODOsum = 0;
        for(int i = 0; i < fftMeasures.length; i++) {
            if(fftMeasures[i] > 0) System.out.println("MEASURES:\t" + i +"\t" + fftMeasures[i]);
            TODOsum += fftMeasures[i];
        }
        System.out.println(TODOsum);
        System.out.println(fftMeasures[0]);
        //TODO:System.exit(-666);

// TODO: PROGRAMO - REDOING operations
        System.out.println("TODO:\t" + currentlyCalculatedMeasures[1][1]);
        for (int window = 0; window < tmpArrs.length; window++) {
            if((multiplyFactors[window] != 0 && currWindow >= windowsOverlaps.length) || moreWindowsPerPixel) {
                if (counters[window] >= windowOverlapCountForOneWindow) {
                    Program.performOperationOnSamples(tmpArrs[window][0], tmpArrs[window][0], 0, 0,
                        tmpArrs[window][0].length, logarithmBase, ArithmeticOperation.LOG);
                    //Program.operationOnSamplesByReference(tmpArrs[window][0], logarithmBase, ArithmeticOperation.LOG);
                    Program.performOperationOnSamples(tmpArrs[window][1], tmpArrs[window][1], 0, 0,
                        tmpArrs[window][1].length, logarithmBase, ArithmeticOperation.LOG);
                    //Program.operationOnSamplesByReference(tmpArrs[window][1], logarithmBase, ArithmeticOperation.LOG);
                }
            }
        }
//TODO:        System.out.println("TmpArrs1:\t" + tmpArrs[1][0][1]);

        Program.performOperationOnSamples(fftMeasures, fftMeasures, 0, 0,
            fftMeasures.length, logarithmBase, ArithmeticOperation.LOG);
        //Program.operationOnSamplesByReference(fftMeasures, logarithmBase, ArithmeticOperation.LOG);
        if((multiplyFactor != 0 && currWindow >= currentlyCalculatedMeasures[0].length) || moreWindowsPerPixel) {
            Program.performOperationOnSamples(tmpArr, tmpArr, 0, 0,
                tmpArr.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(tmpArr, logarithmBase, ArithmeticOperation.LOG);
            Program.performOperationOnSamples(tmpArr2, tmpArr2, 0, 0,
                tmpArr2.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(tmpArr2, logarithmBase, ArithmeticOperation.LOG);
        }

        if(multiplyFactor != 0 || moreWindowsPerPixel) {
            Program.performOperationOnSamples(arrWithNotFullMeasures, arrWithNotFullMeasures, 0, 0,
                arrWithNotFullMeasures.length, logarithmBase, ArithmeticOperation.LOG);
            //Program.operationOnSamplesByReference(arrWithNotFullMeasures, logarithmBase, ArithmeticOperation.LOG);
        }
// TODO: PROGRAMO - REDOING operations

        for(int i = 0; i < fftMeasures.length; i++) {
            if(fftMeasures[i] < 0) {
                System.exit(45679);
            }
        }

        System.out.println("....:\t" + currentlyCalculatedMeasures[0][1]);
// TODO: Asi nemusim a jestli musim, tak to je vypocet navic, protoze tam posilam sqrt stejne
        for(int bin = 0; bin < fftMeasures.length; bin++) {
// TODO: Debug print
            if(fftMeasures[bin] > 0) {
                System.out.println(bin + "\t" + fftMeasures[bin]);
            }
            if(moreWindowsPerPixel) {
                addCurrentMeasures(mod, currentlyCalculatedMeasures[bin], fftMeasures[bin], bin, tmpArr, multiplyFactor,
                    windowOverlapCountForOneWindow, counters, windowsOverlaps, tmpArrs, multiplyFactors,
                    indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin]);
            }
            else {
                if (currWindow < currentlyCalculatedMeasures[bin].length) {
                    addCurrentMeasures(currWindow, currentlyCalculatedMeasures[bin], fftMeasures[bin], windowOverlapCountForOneWindow,
                        indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin], multiplyFactor);

                } else {
                    addCurrentMeasures(mod, currentlyCalculatedMeasures[bin], fftMeasures[bin], bin, tmpArr, multiplyFactor,
                        windowOverlapCountForOneWindow, counters, windowsOverlaps, tmpArrs, multiplyFactors,
                        indexWhereToPutNotFullValue, arrWithNotFullMeasures[bin]);
                }
            }




            System.out.println("bin :]\t" + bin);
            for(int i = 0; i < currentlyCalculatedMeasures[bin].length; i++) {
                System.out.println("currMeasures:\t" + i + ":\t" + bin + "\t" + currentlyCalculatedMeasures[bin][i] +
                    "\t" + tmpArrs[i][0][bin] + "\t" + fftMeasures[bin] + "\t" + multiplyFactors[1] + "\t" +
                    (fftMeasures[bin] * multiplyFactors[1]) + "\t" + windowOverlapCountForOneWindow + "\t" +
                    (tmpArrs[i][0][bin] / windowOverlapCountForOneWindow));

                System.out.println(currWindow);
                if(currentlyCalculatedMeasures[bin][i] < 0) {
                    System.exit(123456);
                }
                if(Double.isNaN(currentlyCalculatedMeasures[bin][i]) || Double.isInfinite(currentlyCalculatedMeasures[bin][i])) System.exit(98989);

                // if(Double.isNaN(tmpArrs[i][0][bin])) System.exit(989890);
            }
        }
        System.out.println("....\t" + currentlyCalculatedMeasures[0][1]);

    }




    private static void addCurrentMeasuresMoreWindowsPerPixel(double[] currentlyCalculatedMeasures,
                                           double fftMeasure, int bin,
                                           double multiplyFactor, double windowOverlapCountForOneWindow, double[] multiplyFactors,
                                           int indexWhereToPutNotFullValue, double notFullMeasure) {
        if(bin == 0) {
            System.out.println(bin + "\t" + fftMeasure + "\t:::::\t" + notFullMeasure);
            for(int i = 0; i < currentlyCalculatedMeasures.length; i++) {
                if (currentlyCalculatedMeasures[i] > 11) {
                    System.out.println("OVER:\t" + i + "\t" + currentlyCalculatedMeasures[i]);
                }
            }
        }


        int i = 0;
        for(; i < indexWhereToPutNotFullValue; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
        }

        if(multiplyFactor != 0) {
            currentlyCalculatedMeasures[i] = notFullMeasure / windowOverlapCountForOneWindow;
            currentlyCalculatedMeasures[i] = multiplyFactor * fftMeasure / windowOverlapCountForOneWindow;
            i++;
        }

        for(; i < currentlyCalculatedMeasures.length; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / windowOverlapCountForOneWindow;
        }
    }


    private static void addCurrentMeasuresMoreWindowsPerPixel(int currWindow,
                                                              double[] currentlyCalculatedMeasures, double[] currentlyCalculatedMeasuresWithOtherParts,
                                                              double fftMeasure, double numberOfWindowParts,
                                                              int indexWhereToPutNotFullValue, double notFullMeasure, double multiplyFactor) {
        int i;
        for(i = 0; i < currWindow; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / numberOfWindowParts;
        }

        for(; i < currentlyCalculatedMeasures.length - 1; i++) {
            currentlyCalculatedMeasures[i] += fftMeasure / (i+1);        // TODO: To se mi nezda
        }
        currentlyCalculatedMeasures[i] += fftMeasure / numberOfWindowParts;
        if(multiplyFactor != 0) {
            currentlyCalculatedMeasures[indexWhereToPutNotFullValue] = notFullMeasure / numberOfWindowParts;
            currentlyCalculatedMeasures[indexWhereToPutNotFullValue] = multiplyFactor * fftMeasure / numberOfWindowParts;
        }
    }


    public static int convertFrameToSecs(int frame, int sizeOfOneSec) {
        return frame / sizeOfOneSec;
    }

    // TODO: Doesn't work for long audio files - 596 hours+ (more exactly 2 147 483secs / 60 / 60)
    public static int convertFrameToMillis(int frame, int sizeOfOneSec) {
        return (int)(1000 * (double)frame / sizeOfOneSec);
    }




// TODO: Sice pekny, ale nemam cas si to implementovat, jen pouziju uz to naprogramovany convertovani
//    public static void convertFormat(byte[] audio, int oldSampleRate, boolean oldIsBigEndian, boolean oldIsSigned,
//                                     int oldSampleSize, int oldNumberOfChannels,
//                                     int newSampleRate, boolean newIsBigEndian, boolean newIsSigned,
//                                     int newSampleSize, int newNumberOfChannels) {
//        int oldFrameSize = oldSampleSize * oldNumberOfChannels;
//        int newFrameSize = newSampleSize * newNumberOfChannels;
//        if(oldSampleRate != newSampleRate) {
//            audio = convertSampleRate(audio, oldSampleSize, oldFrameSize, oldNumberOfChannels,
//                oldSampleRate, newSampleRate, oldIsBigEndian, oldIsSigned);
//        }
//        if(oldIsBigEndian != newIsBigEndian) {
//            convertEndianity(oldIsBigEndian, newIsBigEndian);
//        }
//        if(oldIsSigned != newIsSigned) {
//            convertSign(oldIsSigned, newIsSigned);
//        }
//        if(oldNumberOfChannels != newNumberOfChannels) {
//            convertNumberOfChannels(oldNumberOfChannels, newNumberOfChannels);
//        }
//    }


    // TODO: Pouzival jsem na hodne mistech a asi ne na vsech jsem to nahradil volanim timhle funkce
    public static int convertToMultipleDown(int val, int multiple) {
        val -= (val % multiple);
        return val;
    }
    public static int convertToMultipleUp(int val, int multiple) {
        val += multiple - (val % multiple);
        return val;
    }


    /**
     * Separates input to extension and name (part without extension). The name as return value of method.
     * @param input is the input name from which will be taken the name.
     * @return Returns the name without extension. If there was no extension returns the original name.
     */
    public static String getNameWithoutExtension(String input) {
        int ind = input.lastIndexOf('.');
        if(ind == -1) {
            return input;
        }
        String name = input.substring(0, ind);
        return name;
    }


    public static String getFileNameFromPath(String path) {
        String fileName;
        int lastIndex = path.lastIndexOf(File.separator);
        if(lastIndex == -1) {
            fileName = path;
        }
        else {
            fileName = path.substring(lastIndex + 1);
        }
        return fileName;
    }


    public static int calculateMaxWidth(char startChar, char endChar, FontMetrics fm) {
        int maxWidth = -1;
        char c = startChar;
        while(c <= endChar) {
            maxWidth = Math.max(maxWidth, fm.charWidth(c));
            c++;
        }

        return maxWidth;
    }

    public static int calculateMaxWidthDigit(FontMetrics fm) {
        return calculateMaxWidth('0', '9', fm);
    }

    public static int calculateMaxWidthAlphabetLowerCase(FontMetrics fm) {
        return calculateMaxWidth('a', 'z', fm);
    }
    public static int calculateMaxWidthAlphabetUpperCase(FontMetrics fm) {
        return calculateMaxWidth('A', 'Z', fm);
    }

    public static int calculateMaxWidthAlfanum(FontMetrics fm) {
        int maxWidth = -1;
        maxWidth = Math.max(maxWidth, calculateMaxWidthDigit(fm));
        maxWidth = Math.max(maxWidth, calculateMaxWidthAlphabetLowerCase(fm));
        maxWidth = Math.max(maxWidth, calculateMaxWidthAlphabetUpperCase(fm));
        return maxWidth;
    }


    /**
     * Creates new array of length originalArrLen * copyCount which contains first originalArrLen indices of array arr and they are contained in the result copyCount times.
     * @param arr
     * @param originalArrLen
     * @param copyCount
     * @return
     */
    public static double[] copyArr(double[] arr, int originalArrLen, int copyCount) {
        double[] result = new double[originalArrLen * copyCount];
        copyArr(arr, originalArrLen, result, copyCount);
        return result;
    }


    /**
     * Copies the first originalArrLen indices copyCount times to resultArr.
     * @param arr
     * @param originalArrLen
     * @param resultArr
     * @param copyCount
     */
    public static void copyArr(double[] arr, int originalArrLen, double[] resultArr, int copyCount) {
        for(int i = 0, c = 0; c < copyCount; c++, i += originalArrLen) {
            System.arraycopy(arr, 0, resultArr, i, originalArrLen);
        }
    }

    /**
     * The method takes first len indices of array arr and copies them until end of array is reached.
     * arr.length % len == 0, otherwise the method throws exception.
     * @param arr
     * @param len
     */
    public static void copyArr(double[] arr, int len) {
        for(int i = len; i < arr.length; i += len) {
            System.arraycopy(arr, 0, arr, i, len);
        }
    }


    public static Dimension calculateMaximizedFrameSize() {
        JFrame f = new JFrame();
        f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        f.setVisible(true);
        Dimension size = f.getSize();
        f.setVisible(false);
        f.dispose();
        return size;
    }


    public static int calculateCharOccurrences(String s, char c) {
        int count = 0;
        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == c) {
                count++;
            }
        }

        return count;
    }
}



// Convolution is like polynom multiplication
//Math.ceil(1) = 1
