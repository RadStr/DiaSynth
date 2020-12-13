package analyzer.bpm;

public class CombFilterBPMAllSubbandsGetter implements CombFilterBPMGetterIFace {
    @Override
    public int calculateBPMFromEnergies(double[][] energies, int startBPM, int jumpBPM, int bpmCount) {
        int maxBPMIndex = 0;
        double maxEnergy = 0;
        double[] totalEnergies = new double[bpmCount];
        for (int bpmIndex = 0; bpmIndex < energies[0].length; bpmIndex++) {
            totalEnergies[bpmIndex] = calculateEnergySum(energies, bpmIndex);
            // Multiply here if I want to emphasize some frequencies
            if (totalEnergies[bpmIndex] > maxEnergy) {
                maxEnergy = totalEnergies[bpmIndex];
                maxBPMIndex = bpmIndex;
            }
        }

        return CombFilterBPMGetterIFace.getBPMFromIndex(startBPM, jumpBPM, maxBPMIndex);
    }


    private static double calculateEnergySum(double[][] energies, int bpmIndex) {
        double energy = 0;

        for(int i = 0; i < energies.length; i++) {
            energy += energies[i][bpmIndex];
        }

        return energy;
    }
}
