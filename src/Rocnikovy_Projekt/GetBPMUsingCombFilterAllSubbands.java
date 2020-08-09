package Rocnikovy_Projekt;

public class GetBPMUsingCombFilterAllSubbands implements GetBPMUsingCombFilterIFace {
    @Override
    public int calculateBPMFromEnergies(double[][] energies, int startBPM, int jumpBPM, int bpmCount) {
        int maxBPMIndex = 0;
        double maxEnergy = 0;
        double[] totalEnergies = new double[bpmCount];
        for (int j = 0; j < energies[0].length; j++) {
            totalEnergies[j] = calculateEnergyForSubbandAllEqual(energies, j);
            if (totalEnergies[j] > maxEnergy) {
                maxEnergy = totalEnergies[j];            // TODO: Tady kdybych chtel dat duraz na nejaky frekvence, tak tady to pronasobim asi nejakym koeficientem .. kdkybych se chtel divat primo na nejaky urcity frekvence, tak to bude trochu slozitejsi
                maxBPMIndex = j;
            }
        }

        return GetBPMUsingCombFilterIFace.calculateBPMFromInd(startBPM, jumpBPM, maxBPMIndex);
    }


    private static double calculateEnergyForSubbandAllEqual(double[][] energies, int bpmIndex) {
        double energy = 0;

        for(int i = 0; i < energies.length; i++) {
            energy += energies[i][bpmIndex];
        }

        return energy;
    }
}
