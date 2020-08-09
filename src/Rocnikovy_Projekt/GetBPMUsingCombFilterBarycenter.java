package Rocnikovy_Projekt;

import org.jtransforms.fft.DoubleFFT_1D;

public class GetBPMUsingCombFilterBarycenter implements GetBPMUsingCombFilterIFace {

    @Override
    public int calculateBPMFromEnergies(double[][] energies, int startBPM, int jumpBPM, int bpmCount) {
        int bpm = (int) calculateEnergyBarycenter(energies, startBPM, jumpBPM);
        return bpm;
    }


    private static double calculateEnergyBarycenter(double[][] energies, int startBPM, int jumpBPM) {
        double maxEnergy;
        double maxEnergySum = 0;
        double sum = 0;
        int maxBpmInd = -1;
        int bpm;


        for(int i = 0; i < energies.length; i++) {
            maxEnergy = 0;
            for(int bpmInd = 0; bpmInd < energies[i].length; bpmInd++) {
                if(energies[i][bpmInd] > maxEnergy) {
                    maxBpmInd = bpmInd;
                    maxEnergy = energies[i][bpmInd];
                }
            }

            maxEnergySum += maxEnergy;
            bpm = GetBPMUsingCombFilterIFace.calculateBPMFromInd(startBPM, jumpBPM, maxBpmInd);
            System.out.println(bpm + "\t" + maxEnergy + "\t" + energies[i][6]);
            sum += maxEnergy * bpm;
        }

        System.out.println(sum + "\t" + maxEnergySum + "\t" + (sum / maxEnergySum));        // TODO:
        return sum / maxEnergySum;
    }
}
