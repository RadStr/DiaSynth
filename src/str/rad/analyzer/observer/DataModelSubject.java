package str.rad.analyzer.observer;

import str.rad.analyzer.AnalyzerPanel;
import str.rad.analyzer.AnalyzerXML;
import org.w3c.dom.Node;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataModelSubject implements DataModelSubjectIFace {
    private List<DataModelObserverIFace> observers;
    private JFrame frame;

    public DataModelSubject(JFrame f) {
        observers = new ArrayList<>();
        frame = f;
    }

    public DataModelSubject(DataModelObserverIFace[] obs, JFrame f) {
        observers = new ArrayList<>(Arrays.asList(obs));
        frame = f;
    }

    public DataModelSubject(List<DataModelObserverIFace> obs, JFrame f) {
        observers = obs;
        frame = f;
    }

    @Override
    public void registerObserver(DataModelObserverIFace obs) {
        observers.add(obs);
    }
    @Override
    public void unregisterObserver(DataModelObserverIFace obs) {
        observers.remove(obs);
    }


    /**
     * Notify observers about removal of given node.
     */
    @Override
    public void notifyObservers(Node removedNode) {
        for (DataModelObserverIFace o : observers) {
            o.update(removedNode);
        }
    }

    /**
     * Notify observers that there was non-trivial change in the data model.
     * Therefore the data needs to be reloaded from the XML to the data model.
     */
    @Override
    public void notifyObservers() {
        AnalyzerXML.setXMLDoc(AnalyzerPanel.ANALYZED_AUDIO_XML_FILENAME, frame, "songs");
        for (DataModelObserverIFace o : observers) {
            o.reloadDataModel();
        }
    }
}
