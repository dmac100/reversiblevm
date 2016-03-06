package observer;

public interface ValueChangeObservable {
	public void addObserver(ValueChangeObserver observer);
	public void removeObserver(ValueChangeObserver observer);
}
