package callback;

public interface HasCallbacks<T> {
	public void addCallback(T callback);
	public void removeCallback(T callback);
}
