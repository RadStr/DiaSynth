package player.wave;

import player.wave.util.SliderWithLabelPanel;
import util.audio.format.ChannelCount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class WaveMixPanel extends JPanel implements WaveMixPanelUpdaterIFace {
    private SliderWithLabelPanel[] sliders = null;

    private WaveMixPanelUpdaterIFace updater;

    private int orientationMixSlider;
    private int minValMixSlider;
    private int maxValMixSlider;
    private int defValMixSlider;
    private boolean isLabelOnLeft = false;

    public int getNthChannelMixSliderVal(int n) {
        return sliders[n].slider.getValue();
    }
    public double getNthChannelMixSliderNormalizedVal(int n) {
        return sliders[n].getNormalizedValue();
    }

    public WaveMixPanel(int orientationMixSlider, int minValMixSlider, int maxValMixSlider,
                        int defValMixSlider, boolean isLabelOnLeft, ChannelCount numberOfChannels,
                        WaveMixPanelUpdaterIFace updater) {
        this.updater = updater;
        this.orientationMixSlider = orientationMixSlider;
        this.minValMixSlider = minValMixSlider;
        this.maxValMixSlider = maxValMixSlider;
        this.defValMixSlider = defValMixSlider;
        this.isLabelOnLeft = isLabelOnLeft;

        if(orientationMixSlider == SwingConstants.VERTICAL) {
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        }
        else {      // It must be horizontal, else there would be exception on creation of JSlider
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        updateChannelCount(numberOfChannels);
    }


    private void removeOldSliders() {
        if(sliders != null) {
            for(SliderWithLabelPanel slider : sliders) {
                this.remove(slider);
            }
        }
    }

    public void updateChannelCount(ChannelCount channelCount) {
        removeOldSliders();
        sliders = new SliderWithLabelPanel[channelCount.CHANNEL_COUNT];

        switch (channelCount) {
            case MONO:     // Mono
                setSlider(0, "M", "Mono",
                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
                break;
            case STEREO:     // Stereo
                setSlider(0, "L", "Left",
                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
                setSlider(1, "R", "Right",
                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
                break;
// TODO: Currently not supported
//            case QUADRO:
//                setSlider(0, "FL", "Front Left",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(1, "FR", "Front Right",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(2, "SL", "Surround Left",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(3, "SR", "Surround Right",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                break;
//            case FIVE_POINT_ONE:
//                // https://en.wikipedia.org/wiki/5.1_surround_sound
//                setSlider(0, "FL", "Front Left",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(1, "FR", "Front Right",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(2, "C", "Center",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(3, "LFE", "Low-Frequency Effects",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(4, "SL", "Surround Left",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(5, "SR", "Surround Right",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                break;
//            case SEVEN_POINT_ONE:
//                // https://www.gearslutz.com/board/post-production-forum/659911-7-1-channels-order-naming-conventions.html
//                setSlider(0, "FL", "Front Left",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(1, "FR", "Front Right",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(2, "C", "Center",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(3, "LFE", "Low-Frequency Effects",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(4, "LsS", "Left Side Surround",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(5, "RsR", "Right Side Surround",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(6, "LsR", "Left Side Rear",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                setSlider(7, "RsR", "Right Side Rear",
//                    orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
//                break;
// TODO: Currently not supported
            default:
                for(int i = 0; i < sliders.length; i++) {
                    setSlider(i, Integer.toString(i), "i-th channel",
                        orientationMixSlider, minValMixSlider, maxValMixSlider, defValMixSlider);
                }
                break;
        }


        alignLabelsToRight();
        for(int i = 0; i < sliders.length; i++) {
            this.add(sliders[i]);
        }
    }


    private void alignLabelsToRight() {
        int maxWidth = -1;
        for(SliderWithLabelPanel s : sliders) {
            maxWidth = Math.max(maxWidth, s.label.getPreferredSize().width);
        }
        for(SliderWithLabelPanel s : sliders) {
            int dif = maxWidth - s.label.getPreferredSize().width;
            if(dif != 0) {
//                https://stackoverflow.com/questions/27136517/how-to-add-a-space-before-the-text-in-a-jlabel
                s.label.setBorder(new EmptyBorder(0, 0, 0, dif));
            }
        }
    }


    private void setSlider(int ind, String sliderName, String sliderToolTip,
                           int orientation, int minVal, int maxVal, int defVal) {
        sliders[ind] = new SliderWithLabelPanel(orientation, minVal, maxVal, defVal,
                                                sliderName, this, ind, isLabelOnLeft);
        sliders[ind].slider.setToolTipText(sliderToolTip);
    }


    @Override
    public void update(int index, double newValue) {
        updater.update(index, newValue);
    }
}
