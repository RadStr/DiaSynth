package str.rad.player.mixer;

public class MixerWithPostProcessingSumDivision extends MixerWithPostProcessing {
    public MixerWithPostProcessingSumDivision(double[][] multFactors) {
        update(multFactors);
    }

    private double[] divFactors;

    @Override
    public int postProcessing(int sample, int channel) {
        return (int) (sample / divFactors[channel]);
    }

    @Override
    public double postProcessing(double sample, int channel) {
        return sample / divFactors[channel];
    }

    @Override
    public void update(double[][] multFactors) {
        if (multFactors == null || multFactors.length == 0) {
            divFactors = null;
        }
        else {
            divFactors = new double[multFactors[0].length];
            for (int i = 0; i < multFactors.length; i++) {
                for (int channel = 0; channel < multFactors[i].length; channel++) {
                    divFactors[channel] += multFactors[i][channel];
                }
            }

            for (int i = 0; i < divFactors.length; i++) {
                divFactors[i] = Math.max(divFactors[i], 1);
            }
        }
    }
}
