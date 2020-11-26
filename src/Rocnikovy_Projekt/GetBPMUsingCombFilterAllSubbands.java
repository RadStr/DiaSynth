package Rocnikovy_Projekt;

public class GetBPMUsingCombFilterAllSubbands implements GetBPMUsingCombFilterIFace {
    @Override
    public int calculateBPMFromEnergies(double[][] energies, int startBPM, int jumpBPM, int bpmCount) {
        int maxBPMIndex = 0;
        double maxEnergy = 0;
        double[] totalEnergies = new double[bpmCount];
        for (int bpmIndex = 0; bpmIndex < energies[0].length; bpmIndex++) {
            totalEnergies[bpmIndex] = calculateEnergySumForBPMArr(energies, bpmIndex);
            // Multiply here if I want to emphasize some frequencies
            if (totalEnergies[bpmIndex] > maxEnergy) {
                maxEnergy = totalEnergies[bpmIndex];
                maxBPMIndex = bpmIndex;
            }
        }

        return GetBPMUsingCombFilterIFace.calculateBPMFromInd(startBPM, jumpBPM, maxBPMIndex);
    }


    private static double calculateEnergySumForBPMArr(double[][] energies, int bpmIndex) {
        double energy = 0;

        for(int i = 0; i < energies.length; i++) {
            energy += energies[i][bpmIndex];
        }

        return energy;
    }
}
