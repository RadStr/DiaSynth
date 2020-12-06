package RocnikovyProjektIFace;

public class TODO_CLASS extends TODO_PACKAGE.TODO_CLASS {
    // This method is here so I can use labels
    public void TODO_METHOD() {
        a:
// playAudio ma zacatku metody to resim jen provizorne - musim mit nejaky vystupni audioFormat - a ty vstupni vlny musi mit stejny audioFormat jako ten vystupni
// Az na pocet kanalu - to 1 vlna = 1 kanal a na vystupu je n kanalu a tu jednu vlnu do nich dam podle slideru

        b:
// Vyresit playChunkSize - to souvisi s tim, ze tam jeste musim dat poradne ten vystupni audioFormat u toho playAudio

        c:
        // Po jakykoliv praci s listem waves v AudioPlayerPanel musim zavolat upgradeTextFieldIfDigitCountChanges()
        // Teoreticky bych si na to mohl napsat metodu a menil bych pocet prvku ve waves jen z ni, ale muzu mit situace kdy pridam i vic prvku, takze bych to pak kontroloval zbytecne

        d:
        // V AudioPlayerPanel: upgradeTextFieldIfDigitCountChanges();      // TODO: Ted ani nevim kde to ma byt

        e:
        // Vymazat JSplitPaneDividerMouseAdapter a vsechny odkazy na nej
        // Problem byl v tom, ze jsem pridaval ten adapter do mouseListeneru misto mouseMotionListener

        f:
        // TODO: Asi se zeptat na stackoverflow
        // JSplit inside JScroll - zvetsovani scroll zvetsovanim splitu: - 3 moznosti jak to vyresit
        // Problem je v tom, ze po zvetsi scrollu nebere dragovani k uvahu ten novy vetsi scroll - ale je to omezeni tim starym
        // 1) Idealni reseni - ale asi neexistuje - proste zavolam nejakou metodu co rekne, ted se to zvetsilo, a uz to pujde ok
        // 2) Pres Robot ze zrusim to draggovani a robotem kliknu znova na ten divider a vratim se na pozici, kde byla puvodne mys
        // Pro uzivatele to bude vypadat ze dragovani pokracovalo. Ale ve skutecnosti se mys pustila, znova stiskla a posunulu na pozici kde mel uzivatel mys puvodne
        // Ale robot nefunguje - neni schopny draggovat Jsplitpane - Viz TODOMETHOD
        // 3) asi jak to vyresim, budu mit promennou drag - nastavim na true kdyz draguju, pak normalne necham uzivatele posouvat
        // a zvetsovat to okno atd, ale ten divider se nebude updatovat kdyz to bude niz nez bylo to puvodni okno, ale jakmile
        // se prestane drzet tlacitko - tj. release tak se v evente podivame jestli jsme draggovali a kdyz ano tak
        // tak ten divider posuneme na tu novou pozici kde mel uzivatel mys kdyz dal mouse button release.

        g:
        // Pozor na to - kdykoliv nastavim novej posledni panel tak mu musim dat novy mouseadapter - abych mohl scrollovat
        // V pripade swapu neni potreba tam jen menim ty top a bottom componenty u splitteru

        h:
        // Jak vyresit to ze pri swapovani komponent se nezachovavaji velikosti
        // Jednoduchy reseni je jen prohodit preferred sizes pak to bude jak se to chovalo ted
        // Kdyz chci aby se nemenila velikost tak musim modifikovat jen s tou setDividerLocation
        // to znamena ke vsem divider location ze vln pod tou swapnutou pricist jeji divider location
        // TODO: Problem v te druhe moznosti byl ze jsem to delal pres preferred size - ale zmena divider location nemeni
        // TODO: preferred size - to menim jen ja kdyz to uz presahuje ven ze scrollu
        j:
        // Kdyz v splitpane klasicky draggovanim divideru zmenim velikost panelu, tak bych mel zmenit i jeho preferred size
        // - at preferred size odpovida velikosti toho okna - a tedy at se zmensi ten scroll aby tam bylo jen to co ma - a nebylo tam misto navic
        // A V tom byl asi i problem, ze se obcas smrstili komponenty do 1, totiz draggovani nemeni preferred size,
        // takze pak pri prerozdelovani (swapovani) to nemuze asi ani nijak vedet, jak velky to ma byt
        k:
        // Problem je v tom ze 1 panel/komponenta nemuze byt soucasti 2 splitPane
        l:
        // // TODO: - v metode tryMoveSwap - neni nutny tam pocitat ten pocet vln, kdyz to budu delat jen po jedny
        m:
        // TODO: Tohle vymazat - je to na vic mistech - ve swap metodach
//        if(panelWithWaves.getViewport().getViewPosition().y < 0) {
//            System.out.println(panelWithWaves.getViewport().getViewSize().height);
//            System.exit(-10000000);    // TODO:
//        }

        n:
        // SOLVED: Vychazi z toho ze viewSize odpovida preferred size
        // Problem je v tom asi ze ten viewSize si v jednom pripade nebere k u vahu nejaky misto navic
        // ma totiz hodnotu 605 coz je presne 50 * 11 + 5 * 10 + 5 coz jsou vlny + mezery mezi nima a jeste + ta jedna mezera na konci
        o:
        // SOLVED: Musim brat viewSize - ostatni vraci furt to samy
        // nesmim brat viewSize ono to vraci preferredSize kdyz zadnou nenastavim - z dokumentace
        // Pouzit bud getSize nebo pouzit getExtentSize - jistejsi je asi getSize
        // ale getExtentSize vypada podle dokumentace taky ok - Returns the size of the visible part of the view in view coordinates.

        p:
        // Musim dat pozor na to ze jak jsou ty splittery slozeny tak ze posunutim jednoho divideru muzu zmenit vic nez jen ty vlny bezprostredne vedle sebe -
        // Ale kdyz navic podlezu tu minimalni velikost ty vlny vedle tak to uz musim propagovat ten prebytek do ty dalsi - tohle plati rekurzivne
        q:
        // Kdyz vytvorim ty novy splittery tak jim musim nastavit znova ty listenery - u toho poseldniho pro dragoovani
        // U tech ostatnich aby se pri change value vente zmenili ty preferred sizes

        r:
        // TEN VELKEJ PROBLEM SE SWAPOVANIM A NEZACHOVANIM VELIKOSTI - resetToPref nefungovalo - to nevim proc
        // A ta varianta s primym nastavovanim nefungovala protoze to upgradne i tu preferedSize zmena size

        s:
        // Jmeno draw hodnot cachovaneho souboru se dela podle jmena originalniho souboru
        // Kdyz chci ulozit namixovanou pisnicku tak rovnou ulozim i ty cached draw values

        LOGIC_BEHIND_CACHING_VALUES_FOR_DRAWING_OF_WAVE:
        // Hodnota maxZoom je urcena audiem - totiz podle delky audia se spocita kolik je nutno nacachovat zoomu
        // Z toho plyne jednoducha vlastnost - nutnost cachovani je pouze pri zmene velikosti framu/panelu
        // A taky ze kdyz tam dam delete na vlnu - tak to musim prepocitat - a to navic netrivialne ve smyslu
        // Ze nacachovany hodnoty musim ulozit pod jinym jmenem
        ZOOMOVANI:
//        69 854 400 000 (440 * 60 * 60 * sampleRate)‬ coz je 558 835 200 000‬ bytů == 545 737 500 KB == 532 946 MB == 520,455837249755859375‬ GB
//
//        Pro 440 hodinovou stopu mam: 69 854 400 000‬ samplu na coz je potreba 37 zoomu (2^37 == 137 438 953 472‬)
//        a tedy na zapamatovani celeho zoomu potrebuju delka vlny * maxZoom, coz je
//        Pocet zoomu se vypocita tak ze chci spocitat kolikrat musim priblizit (kde priblizeni snizi viditelnou cast na polovinu) abych mel ve
//        viditelne casti min samplu nez je velikost okna
        ZOOMOVANI_KDYZ_JE_TO_MOC_VELKY:
        // Tak si proste zacnu tim nejvetsim zoomem a mam nejakej mensi buffer a delam to po castech otevru si treba jen 5 souboru (zoomu pod tim)
        // A pak ty dalsi resim tak ze az dodelam tech 5 zoomu tak si nactu ten 5. do bufferu a pokracuju estejne jako predtim a udelam zase dalsich 5, atd.
        // Protoze muzu resit ty casti nezavisle - diky tomu muzu mit mensi buffer a resit nejake blizke zoomy a rovnou je zapisovat do souboru
        // A jakmile vyresim jednu cast zoomu tak se posunu na dalsi. A kdyz vyresim cely 1 (resp. 5 zoomu) tak si postupne budu nacitat
        // ten posledni zapsany ze souboru a resit zase tech 5 mensich, ........................

        RESENI_ZOOMOVANI_PRI_SCROLLU:
        // TODO:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Proste provolam vsechny vlny a zmenim start/end podle scrollu
        // Podoble totalWaveWidth upgradnu ale jen pri zoomu.

        ZOOMOVANI_BY_MELO_BYT_VE_WAVE_TRIDE:


//        ... TODO: Podivat se na to testovani toho getAbsoluteValue
//
//
//!!            TODO: HNED - u veci ktery jsou asi spatne, nebo potrebuji byt pouzity jeste nekde jinde napr u ZoomValuesInfo
//
//
//!!            TODO: fillBufferWithPureSamples zmenit nebudu vracet double pole ale proste si jen vezmu range z toho hlavniho pole ktery prehravam/vykresluju
//!!            TODO: TOTEZ PRO READ DOUBLES
//
//        // TODO: - v updateZoom: mozna na konci zavolat repaint
//
//
//            // TODO: ZOOM A PERFORM ZOOM - VSECHNOY SE ZOOMEM POTEBUJE LOKACI MYSI ABY SE MOHLO ZAZOOMOVAT
//        // TODO: PERFORM ZOOM mozna ani nepotrebuje oldZoom a newZoom - ono si to proste nacte cely znova
//        TODO: NEBO TO SPIS UDELAT TAK ZE JEN ZMENIM TEN START INDEX A NACTU SI TO ZNOVA S TIM NOVYM ZOOMEM
//
//            VSECHNO_DELAT_V_TOM_WRAPPERU_A_TEN_SI_JEN_BUDE_PRES_IFACE_BRAT_DOUBLE_HODNOTY_KDYZ_BUDE_POTREBOVAT:
//
//            //  TODO: Nahodit nejakou zoom tridu do ktery tohle schovam a ta se mi bude starat o zoomovani a pohyb toho startIndexu
//            // TODO: Asi je lepsi mezi trida na ktery se jen budou volat metody protoze to pak muzu vyuzit i jinde


//        totalWaveWidthInPixels v wrapperech vyhodit pryc prootze je podle me totozna s visibleWaveWidth
//        TODO: updateZoom() - int startIndexInValues = 0;  RIKA INDEX DOVNITR TOHO BUFFERU, KDE SE MA ZACIT - TO BY ASI MEL BYT TEN MIDDLE INDEX
//        TODO: updateZoom() - int valueCount = song.length;       // TODO: NEVIM

// setCurrentDrawValuesBasedOnZoom() - dat do updateZoom ???


        // TODO: CACHOVANI TED DELAT NEBUDU
        ViewportChangeListener_V_AudioPlayerPanelImplementation:  // TODO: HNED


        Zooming_And_Sliding_In_Audio_Player:
        // Kdyz davam zoom/slide tak provolat zoom/slide na vsech tridach v tom WaveMainPanel - tj. wave, values, ...
        // A jeste zavolat zoom/slide na tech timestamps


        ZoomVariablesOneWave_Udelat_Lip:
        // Budu tam volat zoom (change metody) - co zmeni max zoom podle toho jak jsem modifikoval audio vlnu


        return;
    }
}
