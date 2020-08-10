package DiagramSynthPackage.Synth.Operators.UnaryOperations.Filters;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.Internals.ConstantTextInternals;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.RectangleShapedPanel;
import DiagramSynthPackage.GUI.MovablePanelsPackage.ShapedPanels.ShapedPanel;
import DiagramSynthPackage.Synth.Operators.UnaryOperations.UnaryOperator;
import DiagramSynthPackage.Synth.Unit;
import Rocnikovy_Projekt.Program;

public class NonRecursiveFilter /*extends UnaryOperator*/ {
//    public NonRecursiveFilter(Unit u) {
//        super(u);
//    }
//
//    public NonRecursiveFilter(JPanelWithMovableJPanels panelWithUnits) {
//        super(panelWithUnits);
//    }
//
//
//    private double[] coefs = Program.calculateCoefForLowPass(400, 32, 44100);
//    private double[] previousInputVals = new double[coefs.length];
//    private int currIndex = 0;
//
//
//    @Override
//    public double unaryOperation(double val) {
//        if(currIndex >= previousInputVals.length) {
//            currIndex = 0;
//        }
//        previousInputVals[currIndex] = val;
//        currIndex++;
//
//        double result = 0;
//        for(int i = 0; i < coefs.length; i++) {
//            result += coefs[i] * previousInputVals[i];
//        }
//
//        return result;
//    }
//
//    @Override
//    public String getDefaultPanelName() {
//        return "NR-FILTER";
//    }
//
//    /**
//     * Creates new shaped panel
//     *
//     * @param panelWithUnits
//     * @return
//     */
//    @Override
//    protected ShapedPanel createShapedPanel(JPanelWithMovableJPanels panelWithUnits) {
//        return new RectangleShapedPanel(panelWithUnits, new ConstantTextInternals(getPanelName()), this);
//    }
//
//    /**
//     * Creates new shaped panel called with corresponding constructor of same signature as this method (+ the internals of course)
//     *
//     * @param relativeX
//     * @param relativeY
//     * @param w
//     * @param h
//     * @param panelWithUnits
//     * @return
//     */
//    @Override
//    protected ShapedPanel createShapedPanel(int relativeX, int relativeY, int w, int h, JPanelWithMovableJPanels panelWithUnits) {
//        return new RectangleShapedPanel(relativeX, relativeY, w, h, panelWithUnits, new ConstantTextInternals(getPanelName()), this);
//    }
//
//    /**
//     * Resets to the default state (as if no sample was ever before played)
//     */
//    @Override
//    public void resetToDefaultState() {
//        Program.setOneDimArr(previousInputVals, 0, previousInputVals.length, 0);
//        currIndex = 0;
//    }
//
//    @Override
//    public String getTooltip() {
//        return "Performs non-recursive filter";
//    }
//
//    @Override
//    public boolean hasProperties() {
//        return false;
//    }
//
//
//    private double maxAbsValue = 0;
//    // TODO: RML
//    nezapomenou zavolat kdykoliv se zmeni coeficienty a nebo i vstupy vlastne ajajaja i kdyz vlastne tim jak to mam naprogramovany to pocitaci koeficenty tak mam zaruceno ze ta hodnota bude nejvys maximalni absolutni hodnota ze vstupu
//    ale tam je vlastne problem ze to nenormalizuju porad jen kdyz to prestrelim takze bud to bude nepresny, nebo to budu normalizovat porad
//    nebo kdykoliv se zmeni hodnoty tj bude exception nebo tamto bude nastaveny na false, tak projedu vsechny units a zavolam na nich nejakou reset metodu
//    ktera u vetsiny nebude nic delat a u tyhle se prepocita maximalni absolutni hodnota
//    // TODO: RML
//
//    private void setMaxAbsValue() {
//        double sum = 0;
//        double inputMaxAbsValue = inputPorts[0].getMaxAbsValue();
//        for(int i = 0; i < coefs.length; i++) {
//            sum += inputMaxAbsValue * coefs[i];
//        }
//
//        maxAbsValue = sum;
//    }
//    @Override
//    public double getMaxAbsValue() {
//        return maxAbsValue;
//    }
}