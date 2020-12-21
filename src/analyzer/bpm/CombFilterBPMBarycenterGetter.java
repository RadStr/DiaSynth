package analyzer.bpm;

public class CombFilterBPMBarycenterGetter implements CombFilterBPMGetterIFace {

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
            bpm = CombFilterBPMGetterIFace.getBPMFromIndex(startBPM, jumpBPM, maxBpmInd);
            sum += maxEnergy * bpm;
        }

        return sum / maxEnergySum;
    }
}
