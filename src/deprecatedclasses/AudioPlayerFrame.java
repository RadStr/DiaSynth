package deprecatedclasses;

import player.FrameWithFocusControl;

@Deprecated
public class AudioPlayerFrame extends FrameWithFocusControl {
//	private static final long serialVersionUID = 1L;
//
//    private AudioPlayerPanel panelWithAudioPlayer;
//    private SwingWorker audioPlayWorker;
//
//
//	public AudioPlayerFrame() {
//        panelWithAudioPlayer = new AudioPlayerPanel(this);
//        this.add(panelWithAudioPlayer);
//        panelWithAudioPlayer.setCurrentFontSize(16);     // TODO: Zatim natvrdo takhle, jen pro testovani
//        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//
//        // I have to call setMinimumSize even when I had overridden the getMinimumSize in the frame, because
//        // java needs some impulse to take the minimum size into consideration
//        // (I could set it to random min size here and it would still have the 1024,768 min size)
//        // But it changes the start size if it is bigger than the min size
//        this.setMinimumSize(new Dimension(minSize.width, minSize.height));
//
//        // The worker is needed, because the window wouldn't otherwise respond (It doesn't respond to events while listener methods are served)
//        audioPlayWorker = new SwingWorker<Void, Void>() {
//            boolean closeAudio = false;
//
//            @Override
//            protected Void doInBackground() throws Exception {
//                addWindowListener(new WindowAdapter() {
//                    // TODO: Takhle to dat aby se to otevrelo se stisknutim tlacitka a tak jak to je ted
////                    @Override
////                    public void windowOpened(WindowEvent e) {
////                        super.windowOpened(e);
////                        worker2.execute();      // I should execute when button is pressed
////                    }
//                    @Override
//                    public void windowClosed(WindowEvent e) {
//                        panelWithAudioPlayer.setStopPlayingCurrAudio(true);
//                        closeAudio = true;
//                    }
//                });
//                while (true) {
//                    //           System.out.println("IN");
//                    if (closeAudio) {
//                        break;
//                    }
//                    // Now Put the data to the audio player panel and start playing the song
//                    try {
////                        ProgramTest.printNTimes(Boolean.toString(panelWithAudioPlayer.getReachedSongEnd()), 100);
////                        ProgramTest.printNTimes(Boolean.toString(panelWithAudioPlayer.getPlayNewAudio()), 100);
//                        if (panelWithAudioPlayer.getPlayNewAudio()) {
//                            ProgramTest.printNTimes("----------------------------", 10000);
//                            panelWithAudioPlayer.setPlayNewAudio(false);
//
//
////                            for(int k = 0; k < 3; k++) {
////                                panelWithAudioPlayer.TODOMETHOD();
////                            }
//                            panelWithAudioPlayer.playAudio(false, AudioPlayerPanel.MULTIPLY_CONST_FOR_AUDIO_PLAY); // TODO: ten multiplier
//                        }
//                        // TODO: Tahle varianta nefunguje protoze to pak nemuzu otevrit dalsi audio pritom co uz mam nejaky otevreny
//                        //panelWithAudioPlayer.playAudio(p.song, p.decodedAudioFormat, false, 9 / (double) 10); // TODO: ten multiplier
//                    } catch (Exception exception) {
//                        // TODO: Tenhle error zmenit
//                        System.out.println("Exception when playing");
//                        System.out.println(exception.getMessage());
//                        System.out.println(exception == null);
//                        System.out.println(exception.getLocalizedMessage());
//                        System.out.println(exception.toString());
//                        exception.printStackTrace();
//                        System.exit(444444);
//                    }
//
//                    Thread.sleep(100);
//                }
//
//                return null;
//            }
//        };
//
//        this.setVisible(true);
//    }
//
//    public void callAfterSizeIsKnown() {
//	    //tohle jsem delal zbytecne - ten problem je totiz v tom ze kdyz vytvarim tu vlnu tak ono to jeste nevi kolik je visible width to se zjisti az potom
//////        TODO: TYHLE 2 veci zavolat az kdyz to je nastartovany ten panel
////        TODO: PROGRAMO TO isFirst v OnlyWave dam pryc proste to rovnou zavolam protoze v ty dobe uz by to melo znat tu velikost hned jak to pridam
////        TODO: PROGRAMO to jen ted pro to testovani to delam takhle - kdy v ty dobe kdy to tam pridavam jeste neznam velikosti -
////        TODO: PROGRAMO - jen si musim dat pozor na to abych to volal jen kdyz uz mam jistotu ze to je skutecne pridany
//        DEBUG_ADDING_SONGS();
//        audioPlayWorker.execute();      // I should execute when button is pressed
//////        TODO: TYHLE 2 veci zavolat az kdyz to je nastartovany ten panel
//    }
//
//
//	public void DEBUG_ADDING_SONGS() {
//        Program p = new Program();
//        DoubleWave doubleWave;
//        // TODO: Vymazat, jen takhle ted pro testovani
//        DoubleWave[] songs = new DoubleWave[3];
//        String testSongPathTODO;
//        if(DEBUG_CLASS.NTB_PATH) {
//            testSongPathTODO = "C:\\Users\\Radek\\Documents\\Anthrax Worship music\\01-Anthrax _ Worship";          // DrawValuesSupplierAggregated song
//        }
//        else {
//            try {
//                for(int i = 0; i < songs.length; i++) {
//                    switch(i % 3) {
//                        case 0:
//                            testSongPathTODO = "D:\\MP3 HEAVY METAL\\Anthrax\\Anthrax Worship music\\01-Anthrax _ Worship";          // DrawValuesSupplierAggregated song
//                            p.setVariables(testSongPathTODO, true);
//                            p.convertToMono();
//                            doubleWave = new DoubleWave(p, false);
//                            songs[i] = doubleWave;
//                            break;
//                        case 1:
//                            testSongPathTODO = "C:\\Users\\Radek\\source\\SDL\\CppKlavesyZapProgram\\70BPMMono.wav";
//                            p.setVariables(testSongPathTODO, true);
//                            p.convertToMono();
//                            doubleWave = new DoubleWave(p, false);
//                            songs[i] = doubleWave;
//                            break;
//                        case 2:
//                            testSongPathTODO = "C:\\Users\\Radek\\source\\SDL\\CppKlavesyZapProgram\\ruzneklavesy.wav";
//                            p.setVariables(testSongPathTODO, true);
//                            p.convertToMono();
//                            doubleWave = new DoubleWave(p, false);
//                            songs[i] = doubleWave;
//                            break;
//                    }
//                }
//
//                panelWithAudioPlayer.addWaves(songs);
//
//// TODO: VYMAZAT POZDEJI - bylo pouzito pro testovani
////            p.song = new byte[p.song.length / 128];
////p.song = new byte[p.sampleSizeInBytes * p.numberOfChannels * 1024];
//
////        double[] songDouble = doubleWave.getSong();
////            for(int i = 0, imod = 0; i < songDouble.length; i++, imod++) {
//////                songDouble[i] = 0.75;
////                //songDouble[i] = i / (double)songDouble.length;
////                //songDouble[i] = Math.random() / 4;
////
////                final int LEN = 40;
////                if(i % LEN == 0) {
////                    imod = 0;
////                }
////                songDouble[i] = imod / (double)LEN;
////                if(i == 0) {
////                    songDouble[i] = 0.5;
////                }
////            }
//// TODO: VYMAZAT POZDEJI - bylo pouzito pro testovani
//            }
//            catch(IOException e) {
//                e.printStackTrace();
//                System.exit(4879);
//            }
//        }
//        //testSongPathTODO = "D:\\MP3 HEAVY METAL\\Anthrax\\Anthrax Worship music\\Anthrax-Worship Music (2011)(wav)(peko)\\04-Anthrax _ Fight 'Em Til You Can't.wav";
//        //testSongPathTODO  = "C:\\Users\\Radek\\Documents\\Anthem Of The Peaceful Army (Album)\\01 Age Of Man.mp3";
//        //testSongPathTODO = "D:\\MP3 HEAVY METAL\\Opeth\\Opeth Studio Discography\\1996 - Morningrise\\01 Advent.mp3";   // Long song
//        //testSongPathTODO = "D:\\MP3 HEAVY METAL\\Napalm death\\Napalm Death\\1987 - Scum\\12 You Suffer.mp3";           // Very short song
//    }
//
//
//    private Dimension minSize = new Dimension(1024, 768);
//    @Override
//    public Dimension getMinimumSize() {
//        return minSize;
//    }
}
