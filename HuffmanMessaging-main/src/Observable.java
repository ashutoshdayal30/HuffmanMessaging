/**
 * A simple class for making another class observable. Works with the Observer
 * class to send updates when changes are made. Can only have one observer for
 * this implementation.
 * 
 * @author Jakob Garcia
 */
public class Observable {
	Observer observer;

	public void addObserver(Observer anObserver) {
		observer = anObserver;
	}

	public void notifyObservers(Observable theObservable) {
		observer.update(theObservable); // Notify the observer of a change
	}
}
