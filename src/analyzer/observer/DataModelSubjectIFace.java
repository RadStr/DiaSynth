package analyzer.observer;

import org.w3c.dom.Node;

public interface DataModelSubjectIFace {
    public void addObserver(DataModelObserverIFace obs);

    public void notifyObservers(Node node);

    public void notifyObservers();
}
