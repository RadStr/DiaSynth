package analyzer.observer;

import analyzer.AnalyzerPanel;
import analyzer.AnalyzerXML;
import org.w3c.dom.Node;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataModelSubject implements DataModelSubjectIFace {
	private List<DataModelObserverIFace> observers;
	private JFrame frame;		// TODO: tohle chce fakt resit pres exceptiony
	
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
	public void addObserver(DataModelObserverIFace obs) {
		observers.add(obs);
	}
	
	@Override
	public void notifyObservers(Node node) {
		for(DataModelObserverIFace o : observers) {
			o.update(node);
		}
	}

	@Override
	public void notifyObservers() {
		AnalyzerXML.setXMLDoc(AnalyzerPanel.ANALYZED_AUDIO_XML_FILENAME, frame, "songs");
		for(DataModelObserverIFace o : observers) {
			o.reloadDataModelFromXML();
		}
	}
}
