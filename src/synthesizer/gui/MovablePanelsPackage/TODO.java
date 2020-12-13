package synthesizer.gui.MovablePanelsPackage;

public class TODO {
    public void m() {
        VYMAZAT:
        // Vymazat vsechno co ma tohle u sebe //TODO: Vymazat

        JPanelWithMovableJPanels_FOCUS:
        // Nutny volat focusLost a focusGained ve spravnych okamzicich - jak je psano v dokumentaci

        NUTNY_NASTAVIT_PREF_SIZE_TOHO_PANELU_CO_MA_MOVABLE_KOMPONENTY:

        V_CHECKFORCOLLISIONS:
        // TODO: Not needed to pass the width/height
        PROC_SE_VYPLATI_MIT_TY_INTY_VINT_PAIR_ULOZENY_PRIMO:
        https://stackoverflow.com/questions/28668348/how-expensive-is-it-to-convert-between-int-and-double

        UKAZOVAT_CTVERCE_JEN_KDYZ_DRAGUJU_A_NAOPAK_DRATY_NEUKAZOVAT_KDYZ_DRAGUJU:
        // Ty cary musim delat pres ty mezery stejne i kdyz nebudu ty policka ukazovat

        POZNAMKA_NA_VYMAZANI:
        // TODO: Problem je v tom ze k ty virtualni pozici kde je levej horni roh toho okna pricitam
        //  pri zoomovani spatnou velikost - beru dvojnasobek z velikosti tech movable panelu a ne velikosti toho celyho okna

        ZRYCHLENI:
        // Misto toho abych mel tu promennou co rika jestli to je v kolizi tak buud mit static ukazatele
        // na toho co je prave v pohybu tim usetrim ify protoze ty opovidajici metody zavolam primo na ty instanci
        // protoze si u kazdy instance nebudu pamatovat to jestli se pohybuje - to zjistim porovnanim instanci
        // a nebudu si u kazdy pamatovat tu posledni pozici pred draggem - to taky bude staticka promenna

        PRIBLIZENI_MOC_BLIZKO:
        // Staticky panely se nemeni

        USAGE_OF_IntPairWithInternalDoubles:
        // It is just a bit more accurate, what that means is that internally we are calculating with doubles
        // But when setting location we use the floor of the doubles.
        // We use the doubles because sometimes there needs to be performed division which would later on destroy the accuracy.
        // Don't forget that I have to use the doubles everywhere and only when setting the location I should use ints.
        // TODO: But probably I should use this only at the size because at the location - when I move using mouse I resetPaths the double part and have only the int part.

        TODO_DOUBLE_PAIR:
        // veci s labelem TODO: Double pair dat pryc - nebo je nechat podle toho jak to vyresim

        STATIC_VS_MOVABLE_PANELY:
        // Bud to mit natvrdo bez static - pak mi nevadi ze to je o 1 pixel vedle protoze ty staticky ctverce tam videt nejsou
        // Nebo mit staticky ctverce ale pak si nebudu pamatovat presny x a y staci si mi pamatovat kolikatej to je statickej ctverec
        // Vuci referencnimu co byl na zacatku na pozici 1 a 1 - tam uz zadny problemy nebudou, protoze jsou
        // ctverce stejne jen v tech statickych pozicich, mimo ne nejsou takze si mi to staci pocitat takhle

        // Tam je proste problem v tom ze jakmile to pretece ten 1 pixel tak ta mezera bude o 1 vetsi nez ma byt

        // Nejlepsi by bylo udelat obe varianty a nechat s uzivatele vybrat - tam je akorat problem ze musim naimplementovat
        // 2 varianty serializace a 2 varianty upgradu pozic a 2 varianty hledani dratu a 2 varianty zobrazovani static - jednou je nezobrazovat a jednou jo

        DRAWING_ON_TOP_OF_CHILD_COMPONENTS:
        // Just override paint method: https://stackoverflow.com/questions/8776540/painting-over-the-top-of-components-in-swing
        // TODO: Also Use LayeredPane and glasspane if I want to have some advanced overlapping of panels.
        // https://docs.oracle.com/javase/tutorial/uiswing/components/rootpane.html
        // https://docs.oracle.com/javase/tutorial/uiswing/components/layeredpane.html

        // Referencni bude mit ty vylepseny lokace, nereferencni bude mit klasicky lokace - normalne inty - a ty se budou menit bud prictenim hodnoty - pri scrollovani
        // nebo pri zoomovani zavolanim te metody moveToPosBasedOnRelativeToRefPanel na main panelu


        // TODO: !!!!!!!!!!!!!!!!!!!!!! setIfNotSet metoda vymazat to podle te TODO promenne co tam je


        // TODO: Kdyz to zneviditelnim tak to po nejakou dobu bere input takze to scrolluje kdyz ukazuju na kraj
        // TODO: MINIMUM UZ MAM TED JESTE UDELAT MAXIMUM - TJ ABY TO NESLO AZ MOC PRIBLIZIT - ono to sice funguje ale je to takovy ne uplne vhodny

        // TODO: V Movable panelu u hledani kolize muzu mit optimalizai !!!!! checkForCollisions
        // TODO: A s tim souvisi dalsi optimalizace ze jak mam to lockovani tak to nemusim vlastne ani delat
        // Staci kdyz si budu pamatovat v tom rectanglu ve width a v relativni pozici toho panelu
        // A pak to jen ulozim az skoncim draggovani



        CO_UDELAT:
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // TODO: JESTE UDELAT ZE TO PRESOUVANI BUDE PRES DOUBLE CLICK PROTOZE BYCH CHTEL ABY PRES SINGLE CLICK SE TO JEN ZVYRAZNILO + SE ZVYRAZNILI TY VSTUPY A V UKAZALI SE TY VECI K TOMU V JTABLE
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // TODO: HNED!!!!!!!!!!!!!!!!! a jeste jsem vymazal v v tom check for collision nebo nejaky takovy metode kod kterej nic nedelal a byl jen na debuggovani
        // TODO: HNED - dat ty HSB konstanty do ty HSB tridy
        // TODO: HNED noConnectionCallback - v OutputPortu

        // TODO: HNED - KOD S HODNE DRATY VEDLE SEBE:
//        g.setColor(Color.black);
//        x = 0;
//        int rectWidth = 4;
//        int spaceBetweenRects = 4;
//        while (x < w) {
//            g.fillRect(x, 0, rectWidth, h);
//            x += rectWidth + spaceBetweenRects;
//        }


        // TODO: SPRAVNE BY MEL BYT ASI TEN PANEL A PORT ROZDELENY NA 2, PROTOZE PAK SI TEN PANEL MYSLI ZE MA VSECHNO K DISPOZICI, ALE TEN SPODEK PREKRESLUJE PORT.

        // TODO: Porty dat k sobe do jedny package a ujasnit si co ma mit jakej port za metody

        // TODO: Right click doesn't have to be always popup ??? v MovableJPanelMouseAdapteru

        // TODO: HNED - NE - v drawHorizontalLine - totiz kdyz mam vic output z jednoho tak ten jeden drat muze zahnout doprava driv a tim padem kolize

        MOVABLE_JPANEL_COPY_PANEL_METODA:
// !!!!!!!!!!!!!!!!!!! // TODO: Az/jestli budu nejak zobrazovat input porty tak tohle udelat stejne jako je ten outputPanel - 28.01.2020 - tj. dat remove toho stareho portu, dat add noveho, odebrat stary MouseListener, pridat na nej MouseListener

        OPTIMALIZACE:
        // Pocitani tech kolizi kabelu je dost drahy - musim projit pro kazdy outputPort vsechny outputPorty
        // A navic to samotny kresleni taky neni uplne rychly - bylo by lepsi by to mit ulozeny v tom

        KOLIZE_VYRESENE:
        // Kdyz resim kolize - tak nesmim brat k uvahu ty, kdy mam kolizi, ale on then kabel na ty samy urovni taky zahne - tady se ta kolize vyresi tim ze nektery kabely jsou vys nez jiny

        TY_KABELY_BARVY_URCUJU_PODLE_ELEVACE_PROSTE_SE_S_NI_INDEXUJU_POLE:

        FEEDBACK:
        // TODO: FEEDBACK az budu chtit delat veci s feedbackem, ale ty mozna delat nebudu - i kdyz to asi neni tak tezky programovat

        ZKAZIL_JSEM_TO_ZE_TO_DELAM_PRES_KLIKANI_PROSTE_TO_DELAT_PRES_DRAGOVANI:
        // TODO: DRAGGING AND OUTPUT_PORT

        // TODO: HNED v ConnectionPlacement
        return;
    }
}
