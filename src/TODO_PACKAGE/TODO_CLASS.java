package TODO_PACKAGE;

public class TODO_CLASS {
    // This method is here so I can use labels
    public void TODO_METHOD() {
        OBECNA_ZJISTENI:
        // Kdyz mam mouse moved a dragged, tak pro dragovani se nefiruji mouse moved eventy

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // Testing_Inheritance_Calling_Virtual_Get_Method - v eclipse - UKAZUJE ZE V RAMCI TRIDY BYCH MEL K PROMENNYM,
        // KTERE MAJI GET METODY PRISTUOPOVAT POMOCI GET METOD, Z DUVODU DEDICNOSTI
        // TAKZE TODO: kdekoliv pristupuju k promenny co ma get metodu primo tak prepsat na pristup s get metodou
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // Kdyz mam scrollpane tak poslouchat eventy (jako treba eventy ze se to zvetsilo) na tom co je jako viewport

        // Kdyz mam JscrollPane tak pozor na to ze ten scrollbar ma jiny hodnoty nez to nad cim to je protoze musim vzit
        // scrollbar.getMaximum() + visiblePartWidth - coz je jasny jen jsem si to neuvedomil

        PRIDAVANI_MOUSE_LISTENERU_PRO_JSCROLLPANE:
        // Zavolam na nem mujScrollPane.getViewport().getView().addMouseListener(l);

        SYNTEZA:
        // Pak v tech diagramech povolit uzivateli oznacit uzel za koncovy - to je dost zajimavy protoze tim umoznim ignorovat
        // nejakou cast vypoctu - a tedy muzu umoznit videt jak se zvuk zmeni po pridani nejake casti vypoctu
        // DIAGRAMY: ASI TAKHLE: https://youtu.be/IB_CMiaUlcs?t=537

        OPTIMALIZACE_VYKRESLOVANI_VLNY:
        // Od urciteho priblizeni pocitat average misto min a max protoze uz to je i tak dost presny a casovou a pametovou narocnost zkratim o polovinu
        // Prechod na average bych dal tak od 8 prvku na pixel pripadne vic - ale asi zase nasobek 2ky nejakej

        OPENING_WAVE_CACHE_FILES:
        // I can't keep them open, I have to close them (because there can be too many open at once).

        PROBLEM_KTEREJ_JSEM_UZ_RESIL_V_NEKOLIKA_PROGRAMECH:
        // Uvnitr TimeStamp - kdyz chci brat + 1 kdyz prestrelim (tj nejsem delitelnej)
        //         // + timestampCountBetweenTwoMainTimeStamps - 1 because Every time I go over the label I have to add to the timeInt
        //        // Basicallyhen when startIndex is 1 I am after the first label so the next label I draw will be the one after that
        //        timeInt = (startIndex + timestampCountBetweenTwoMainTimeStamps - 1) / timestampCountBetweenTwoMainTimeStamps * timeJumpInt;

        ZAJIMAVEJ_BUG_KTERYMU_SE_MUZU_DOBUDOUCNA_VYVAROVAT:
        // kdyz je kod if(neco) { return m(); } return -1; ... tak kdyz zapomenu na to return u m() tak to je validni kod
        // ale kdyz dam if else tak uz to neni validni kod - !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


        LONG:
        // Asi bych mel pouzivat long na urcitych mistech - treba delka audia

        // Napsat program co dostane jako parametr adresare, projde je a z kazdy tridy a kazdy metody vtvori DEBUG promennou * abych si mohl selektivne zapinat co chci debugovat
        a:
        // Psat takhle komentare:
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Mono methods - not used, but I program them and they maybe useful - they perform the same operations as the
        // multi-channel variants but with different interface - we need only 1D array for the multFactors arrays
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        b:
        // Pozor na prekryv promennych ted jsem na to dojel v u toho swapovani tech vln

        c:
        // Poznamka - interne v jave je getPreferredSize implementovany tak ze to vraci referenci na interni promennou Dimension PrefSize
        // Takze zmenou toho co mi vrati getPreferredSize ovlivnim tu interni promennou, takze pristi getPreferredSize mi vrati tu zmenenenou promennou


        d:
        //setOneTouchExpandable(boolean n) na JSplitPane muzu pouzit na to menu u toho generatoru - kdyz to nedela uplne to chci - zmensi to tu druhou komponentu to nechci

        SWAPOVANI:
        // Nakonec se ukazalo ze byl velkej problem s tim ze se pri swapnuti zavolali ty moje PropertyChangeListenery
        // A tim se to rozbilo - takze vsechno co jsem delal jsem delal zbytecne
        // Ted vlastne mi jenom staci udelat
        // a) at se mi upgradne (ta jeho preferred size) i ten posledni splitter i kdyz ho nevytahnu ven aby doslo ke scrollovani to je aby se to chovalo jako ty compound v podstate
        // b) a pak jeste udelat to ze jak to posunu smerem nahoru dolu tak aby kdyz to zmensim pod tu min size - tak aby se zmena tech splitteru
        // propagovala (viz pripad, kdy proste to stahnu pres ten posledni splitter uplne nahoru aby byly vsichni na min size)

        EXCEPTIONS_VS_ERRORS:
        // error vs exception - https://stackoverflow.com/questions/10956896/can-an-error-be-handled-in-the-same-way-as-an-exception
        // https://docs.oracle.com/javase/7/docs/api/java/io/FileNotFoundException.html - je exception - takze moc otevrenych souboru se chova jako exception

        CACHING_DRAW_VALUES:
        // The reason why I load new values from cache to buffer only after certain threshold is because, I have
        // to keep in mind that I have multiple tracks and possibly can't have open all files at once.
        // So I would have to open, close file for each movement of scrollbar, which isn't that much of a problem
        // but I would also have to seek on disk for the corresponding file, and that means I would spend a lot of time
        // seeking for files and only little time for actual reading, so in the end it wouldn't be worth it.

        CREATE_MULTIJSPLITPANE:
        // TODO:
        // Vyhodit ten multiJSplitPane ven z ty tridy - muze se hodit pro reusing -
        // a nahodit tam nejakou dedicnost to co tam mam ja je specificka implementace
        return;
    }
}
