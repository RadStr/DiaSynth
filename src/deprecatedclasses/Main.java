package deprecatedclasses;

import javax.swing.*;

@Deprecated
public class Main {
    private static JFrame frame;

    public static JFrame getFrame() {
        return frame;
    }

    // TODO: Tehle text na vic mistech pak vymazat
    // TODO: I will do it this way - user may choose the audioFormat to which will be all the audio waves converted - I could
    // make it like the waves will be in some different formats and in the mixing they will be converted to the
    // output audioFormat but that is too difficult I think

    private static void startProgram() {
//	    String path;
//	    path = "TestingCSV.csv";
//	    //path = "C:\\Users\\Radek\\dl4j-examples\\dl4j-examples\\target\\classes\\classification\\linear_data_train.csv";
//	    try {
//            //path = new ClassPathResource("/classification/linear_data_train.csv").getFile().getPath();
//            System.out.println(path);
//            System.exit(ByteWave.writeSongToFile(path));
//        }
//	    catch (Exception e) {
//	        System.out.println(e.getMessage());
//	        System.exit(-2);
//        }


// TODO: Testovani straigth modelu s pevnymi daty - nejde
//        try {
//            ModelInfo modelInfo;
//            modelInfo = new ModelInfo(ByteWave.fileWithModel + ".xml");
//            int inputAudioLen = modelInfo.getinputAudioLen();
//            ModelAbstract modelClass = ModelAbstract.getModel(modelInfo);
//            MultiLayerNetwork model;
//
//            File file = new File(ByteWave.fileWithModel);          // TODO: Tohle nestaci, potrebuju celou cestu k modelu
//            if (file.exists()) {
//                try {
//                    model = ModelSerializer.restoreMultiLayerNetwork(file, true);
//                } catch (IOException e) {
//                    model = null;
//                }
//            } else {
//                model = null;
//            }
//
//            double[] components;
//            for (int i = 0; i < 11; i++) {
//                components = new double[11];
//                components[i] = 1;
//                String genre = "";
////                for(int j = 0; j < components.length; j++) {
////                    components[j] = i;
////                }
//                INDArray arr = Nd4j.create(components, new int[]{1, components.length});     // Expects matrix (because it has to work universally for 1-n data do be analyzed)
//                INDArray result = model.output(arr, false);
////				INDArray ind = new INDArray(result, 2);
//                //			double[][] doubleArr = new double[1][5];  // TODO:
//                //            doubleArr[0] = components;                    // TODO: Tyhle 3 radky byly potreba u toho samotneho urcovani
//                //              return Nd4j.create(doubleArr);            // TODO:
//                int maxIndex = 0;
//                double maxValue = Double.MIN_NORMAL;
//                for (int j = 0; j < Genre.components().length; j++) {
//                    double value = result.getDouble(j);
//                    System.out.println(value);
//                    if (value > maxValue) {
//                        maxIndex = j;
//                        maxValue = value;
//                    }
//                }
//                if (maxValue < 0.4) {
//                    genre = "probably ";
//                }
//                switch (maxIndex) {
//                    case 0:
//                        genre += "metal";
//                        break;
//                    case 1:
//                        genre += "pop";
//                        break;
//                    case 2:
//                        genre += "rock";
//                        break;
//                    case 3:
//                        genre += "classical";
//                        break;
//                    case 4:
//                        genre += "rap";
//                        break;
//                    default:
//                        throw new IOException("Unknown genre");
//                }
//
//                System.out.println(i + "\t" + genre);
//            }
//        }
//		catch(Exception ex) {
//            new ErrorFrame(frame, "Error in genre recognition:" + ex.getMessage());
//        }
//System.exit(12346);


        // Test
//        if(!Test.testAll()) {
//            System.exit(111);
//        }
//        else {
//            System.exit(222);
//        }

        MenuFrame menu = new MenuFrame(800, 600);
        menu.setVisible(true);

//TODO: Testovani SongLibraryWindow
//		SongLibraryWindow slw = new SongLibraryWindow(menu, frame);
//		frame.add(slw);
//

//TODO: Testovani AnalyzerWindow
        //	AnalyzerWindow analWind = new AnalyzerWindow(menu, frame);
        //	analWind.setVisible(true);
        //frame.add(analWind);
//

//TODO: Testovani songInfo
//		SongInfoFrame si = new SongInfoFrame((JFrame) menu, 600, 600, "Pisnicka");
//		si.setVisible(true);
//


//		frame.add(menu);			// TODO: tohle tam ma byt
        //frame.setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                startProgram();
            }
        });
    }
}
