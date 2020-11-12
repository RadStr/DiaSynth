package RocnikovyProjektIFace;

import RocnikovyProjektIFace.SpecialSwingClasses.BooleanButton;
import RocnikovyProjektIFace.SpecialSwingClasses.BooleanButtonWithImages;
import Rocnikovy_Projekt.Program;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

@Deprecated
public class AudioPlayer extends JPanel {
    private Program program = null;
    private int currSample;
    private int currSampleUserSelected;
    private JSlider timeSlider;
    private JSlider volumeSlider;
    private FloatControl masterGainControl;
    private BooleanControl soundSwitch;

    private JPanel buttonPanel;
    private BooleanButton playButton;
    private BooleanButton muteButton;
    private JButton[] buttons;			// TODO: not ideal performance wise

    private boolean changeCurrSample;

//    public AudioPlayer(Program p) {
//        this.program = p;
//    }
//
//    public AudioPlayer(Program p, int min, int max) {
//        super(min, max);
//        this.program = p;
//    }
//
//    public AudioPlayer(Program p, int min, int max, int value) {
//        super(min, max, value);
//        this.program = p;
//    }
//
//    public AudioPlayer(Program p, int orientation) {
//        super(orientation);
//        this.program = p;
//    }
//
//    public AudioPlayer(Program p, int orientation, int min, int max, int value) {
//        super(orientation, min, max, value);
//        this.program = p;
//    }
//
//    public AudioPlayer(Program p, BoundedRangeModel brm) {
//        super(brm);
//        this.program = p;
//    }


    int frameSize;      // TODO: Takhle urcite ne

    public AudioPlayer(Program p) {
        this.program = p;
        this.setLayout(new BorderLayout());

        int maxLenTime = 100000;  // TODO: Vymazat, protoze to pak stejne nebudu delat pres slider, ten pohyb v case
        timeSlider = new JSlider(JSlider.HORIZONTAL, 0, maxLenTime, 0);
        this.add(timeSlider, BorderLayout.CENTER);

        // TODO: make the path relative
        String resourcesDir = "resources/images/";

        buttonPanel = new JPanel(new FlowLayout());
        buttons = new JButton[2];
//        playButton = new BooleanButtonWithImages(false, (resourcesDir + "PlayButton.png"), (resourcesDir + "PauseButton.png"));
//        playButton = new BooleanButtonWithImages(false, (resourcesDir + "PlayButton.png"), (resourcesDir + "PauseLargerButton.png"));
        playButton = new BooleanButtonWithImages(false, (resourcesDir + "PlayButtonTrans.png"), (resourcesDir + "PauseLargerButton.png"));
        buttons[0] = playButton;

        muteButton = new BooleanButtonWithImages(false, (resourcesDir + "soundIconOff.png"), (resourcesDir + "soundIconOn.png"));
        buttons[1] = muteButton;

        for(int i = 0; i < buttons.length; i++) {
            buttonPanel.add(buttons[i]);
        }

//        this.add(volumeSlider, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.NORTH);
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
        buttonPanel.add(volumeSlider);
    }

    public void playAudio(byte[] audio, AudioFormat audioFormat, boolean playBackwards, double lineBufferSizeMultiplier) throws LineUnavailableException, IOException {
        int bytesWritten;
        frameSize = audioFormat.getFrameSize();         // TODO: Takhle ne

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(audioFormat);

        soundSwitch = (BooleanControl)line.getControl(BooleanControl.Type.MUTE);
        muteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                soundSwitch.setValue(muteButton.getBoolVar());
            }
        });

        masterGainControl = (FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);      // TODO: Type.VOLUME isn't available control
        addListenerToVolumeSlider();
        volumeSlider.setValue(50);

        line.start();
        if(playBackwards) {
            Program.reverseArr(audio, audioFormat.getSampleSizeInBits() / 8);
        }

        // This bound is here, so the pause button reacts in reasonable time, if it wasn't there, then we will keep
        // writing in the internal buffer and when we click the pause button, there is already whole buffer to be played.
        int minAvailableBytes = (int)(line.getBufferSize() * lineBufferSizeMultiplier);
        int playChunkSize = line.getBufferSize() - minAvailableBytes;
        playChunkSize += frameSize - (playChunkSize % frameSize);
        //playChunkSize = 4 * frameSize;
        byte[] arr = new byte[playChunkSize];
        System.out.println(playChunkSize);
        int len = audio.length - (audio.length % audioFormat.getFrameSize());
        int remainder = len % playChunkSize;			// TODO: !!!!!!!!! Important, there is no check inside the write method
        int lastFullChunkEndIndex = len - remainder;

        addListenerToTimeSlider(lastFullChunkEndIndex);
        System.out.println(lastFullChunkEndIndex + "\t" + audio.length + "\t" + (audio.length - lastFullChunkEndIndex));

        changeCurrSample = true;
        int nextSample = currSample + arr.length;       // TODO: Maybe not that effective
        for(currSample = 0; nextSample < lastFullChunkEndIndex; nextSample = currSample + arr.length) {
            //System.out.println(len + "\t" + i + "\t" + playChunkSize + "\t" + line.available() + "\t" + line.getBufferSize() + "\t" + line.isActive() + "\t" + line.isRunning());
            if(playButton.getBoolVar()) {
                line.drain();
            }
            while(playButton.getBoolVar()) {
                try {
                    Thread.sleep(100);       // Passive waiting
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }

            while(line.available() <= minAvailableBytes) {
                ;
            }

            // Copy the samples to be played to smaller array, because the line.write method probably creates new buffer
            // everytime it is called, if the offset at the array isn't 0, so now it keeps reusing this array.
            // I say probably, because I the method which does that is called nWrite and it is native method, which
            // implementation I couldn't find. But with small playChunkSize and no arr I got clipping, so it is probably right idea.
//            System.out.println("Current sample 1:\t" + currSample);
            for(int j = 0; j < arr.length; j++, currSample++) {
//                System.out.println("Current sample 2:\t" + currSample);
                arr[j] = audio[currSample];
            }
//            System.out.println("Current sample last:\t" + currSample);
            line.write(arr, 0, arr.length);

//            line.write(audio, currSample, playChunkSize);
            //System.out.println(len + "\t" + i + "\t" + playChunkSize + "\t" + line.available() + "\t" + line.getBufferSize() + "\t" + line.isActive() + "\t" + line.isRunning());
            //line.drain();	 TODO: Only call at the end
//	            line.stop();
//	            line.close();
//	            line = null;


            // TODO: V tyhle casti budu resit jestli jsem nezmenil to misto prehravani
//            if(System.in.read() == 'a') {
//                line.drain();
//                line.stop();
//                line.close();
//                line = null;
//                break;
//            }

            if(currSampleUserSelected + arr.length == currSample) {
                currSampleUserSelected = currSample;
                double fraction = currSample / (double)lastFullChunkEndIndex;
                int val = (int) (fraction * timeSlider.getMaximum());
                changeCurrSample = false;
                timeSlider.setValue(val);
                changeCurrSample = true;
            }
            else {
                currSample = currSampleUserSelected;
            }
        }


        line.write(audio, currSample, len - currSample);
        System.out.println(len);
        line.drain();
	    line.stop();
	    line.close();
	    line = null;
    }



    private void addListenerToTimeSlider(int lastFullChunkEndIndex) {
        timeSlider.addChangeListener(new ChangeListener() {
            // Time change
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    if(changeCurrSample) {
                        int val = source.getValue();
                        double time = val / (double) timeSlider.getMaximum();
                        int currSampleRaw = (int) (time * lastFullChunkEndIndex);
                        System.out.println(currSampleUserSelected + "\t" + currSampleRaw + "\t" + (frameSize - (currSampleRaw % frameSize)) + "\t" + val);
                        currSampleUserSelected = currSampleRaw - (currSampleRaw % frameSize);
                        System.out.println(currSampleUserSelected + "\t" + currSampleRaw + "\t" + (frameSize - (currSampleRaw % frameSize)) + "\t" + val);
                    }
                }
            }
        });
    }

    private void addListenerToVolumeSlider() {
        volumeSlider.addChangeListener(new ChangeListener() {
            // Volume change
            @Override
            public void stateChanged(ChangeEvent e) {
                // maxGain is 0, because with sound boost, there could be clipping.
                double maxGain = 0;
                // minGain is / 2 because then the volume is too low and for the sound to be heard there is needed way to big sound amplification from speakers.
                // TODO: But it doesn't have to be / 2
                double minGain = masterGainControl.getMinimum() / 2;
                double minGainAbs = Math.abs(minGain);
                double range = maxGain + minGainAbs;
                double skip = range / volumeSlider.getMaximum();

                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    double val = skip * source.getValue();
                    val -= minGainAbs; // Shift it so the val is between minGain and maxGain

                    // The ifs are just in case there will be some precision error
                    if(val < minGain) {
                        val = minGain;
                    }
                    else if(val > maxGain) {
                        val = maxGain;
                    }
                    System.out.println(val + "\t" + minGainAbs + "\t" + maxGain);
                    masterGainControl.setValue((float) val);
                }
            }
        });
    }


}
