package DiagramSynthPackage.Synth.Operators.UnaryOperations;

import DiagramSynthPackage.GUI.MovablePanelsPackage.JPanelWithMovableJPanels;
import DiagramSynthPackage.Synth.Unit;

public class Delay /*extends UnaryOperator*/ {
//    public Delay(Unit u) {
//        super(u);
//    }
//
//    public Delay(JPanelWithMovableJPanels panelWithUnits) {
//        super(panelWithUnits);
//    }
//
//    public static class DelayArray {
//        public DelayArray(int sampleRate, double delayTimeInMs) {
//            changeDelay(sampleRate, delayTimeInMs);
//        }
//
//        private double[] delayArr;
//        private int currIndex;
//
//        public void changeDelay(int sampleRate, double delayTimeInMs) {
//            int arrLen = (int)(sampleRate * delayTimeInMs / 1000);
//            if(delayArr == null || delayArr.length < arrLen) {
//                delayArr = new double[arrLen];
//            }
//            currIndex = 0;
//        }
//
//        /**
//         * Storage and replacement, can be the same array
//         * @param storage
//         * @param replacement
//         * @param startIndex
//         * @param endIndex
//         * @return
//         */
//        public int getAndReplace(double[] storage, double[] replacement, int startIndex, int endIndex) {
//            int len = endIndex - startIndex;
//            if(len > delayArr.length) {
//                endIndex = startIndex + delayArr.length;
//                len = delayArr.length;
//            }
//
//            for(int index = startIndex; startIndex < endIndex; index++, currIndex++) {
//                if(currIndex >= delayArr.length) {
//                    currIndex = 0;
//                }
//                double value = delayArr[currIndex];
//                delayArr[currIndex] = replacement[index];
//                storage[index] = value;
//            }
//
//            return len;
//        }
//
//        public int push(double[] pushValues, int startIndex, int endIndex) {
//            int len = endIndex - startIndex;
//            if(len > delayArr.length) {
//                endIndex = startIndex + delayArr.length;
//                len = delayArr.length;
//            }
//            int index = currIndex;
//            for(int i = startIndex; i < endIndex; i++, index++) {
//                if(index >= delayArr.length) {
//                    index = 0;
//                }
//                delayArr[index] = pushValues[i];
//            }
//
//            return len;
//        }
//
//        public int pushDefaultValues(int len) {
//            len = Math.min(len, delayArr.length);
//            for(int i = 0, index = currIndex; i < len; i++, index++) {
//                if(index >= delayArr.length) {
//                    index = 0;
//                }
//                delayArr[index] = 0;
//            }
//
//            return len;
//        }
//    }
//
//
//
//
//
//
//1)    prejmenovat mainPanel na panelWithUnits
//
//        2) // https://maven.apache.org/plugins/maven-compiler-plugin/examples/set-compiler-source-and-target.html
//    // To create Maven - Has to be done first, then just remove all the packages, and make it in such a way it fits
//// this project's structure
//    nefunguje nevim no ten maven je zabava
//
//    ANT: https://www.jetbrains.com/help/idea/executing-ant-target.html
//    https://intellij-support.jetbrains.com/hc/en-us/community/posts/360001508000-JavaFX-Ant-Build-Run-idea-home-lib-does-not-exist
//
//
//
//
//    Takže ten první z eclipsu když jsem tam měl to JRE tak to šlo hned do intellij akorát jsem tam musel něco nastavit, myslim že JDK 1.8 teď nevim
//
//            (Diasynth2) Když jsem to v eclipsu změnil na to JDK tak pak už to moc nešlo do toho intellij nejdřív jsem tam musel nastavit 1.8 JDK pak v tom dalším 8čku (Všechno je v Project Structure) a nakonec jsem musel nastavi output directory pro kompilátor jo a pak zjistim, že to nakonec nemá reference na ty knihovny, takže to je nějaký divný
//
//
//    https://www.edureka.co/community/4028/how-to-import-a-jar-file-in-eclipse
//    https://www.codejava.net/ides/eclipse/how-to-create-ant-build-file-for-existing-java-project-in-eclipse
//    https://www.jetbrains.com/help/idea/adding-build-file-to-project.html
//    https://stackoverflow.com/questions/31506278/unable-to-set-project-sdk-in-intellij
//
//
//
//    ten @NotNull je nějakej podezřelej - ok ja jsem tam mel nejakou SE verzi javy asi hadam bez ni uz to sklo
//
//        3)     // TODO: Ty rozmery nejsou idealni, ale nenapada me jak lip to vyresit, pres pomery nemuzu, kdyz je pak skladam vedle sebe tak musim upravit ty rozmery
//    TODO:
//    Musim vyresit ty cesty jeste
//
//    Musim to vyresit protoze pro distribuci - BooleannButtonWithImages
//
//
//
//    TODO: DODELAT
////https://alexiyorlov.github.io/tutorials/java-plugins.html
//    TODO: DODELAT
//
//    TODO: DODELAT
//    TODO: RML
//
//
//
//
//    TODO: Ty obrazky dat do resources a img
//
//
//
//
//    // TODO: RML
//// TODO: DODELAT
//    Idealne si vezmu ten vystup primo z toho generatoru, at nemusim odcitat
//
//    TODO: RML - veci co se hodi do budoucna, ale zakomentovany by to asi byt nemelo v odevzdavany verzi
//
//    Pak asi vymazat ty veci z draw wave komunikace ale ted se mi to fakt nechce delat
//
//
//    udelat ty RMS je to spatny zase jsem to programoval jako vidlak
//
//    Opravit FM
//
//    Jeste by to chtelo opravit ten bug kdyz otevru stereo .dia soubor ale jsem v monu
//    a ten mono format takovy stereo nepodporuje, ale na mym pocitaci to tak neni takze to je jedno,
//    a navic je dost mozny ze pokud pc podporuje mono toho formatu tak asi podporuje i to stereo ale nevim
//        do toho radsi nebudu moc skakat
//// TODO: DODELAT
//// TODO: RML
//
//
//    TODO: VYMAZAT TY NESMYSLNY VYPISY
//
//    TO pluginovani jeste jde do budoucna vylepsit, ze prsote si to nacte z nejaky directory ty class soubory a ty class soubory si bude mozny stahovat z internetu
//
//
//            https://stackoverflow.com/questions/4955635/how-to-add-local-jar-files-to-a-maven-project
//    Ten první způsob s tim zkopirováním do lokálního mavenu nemá cenu protože světe div se ta lokální repository je v nějakym .m2, celkove mi to vubec nefunguje
}