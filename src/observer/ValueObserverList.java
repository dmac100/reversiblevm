package observer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores lists of value observers group by a key of type T.
 */
public class ValueObserverList<T> {
	private final Map<T, Set<ValueChangeObserver>> valueObservers = new HashMap<>();
	
	/**
	 * Notifies read observers that a value has been read. Passes in a value change
	 * observable so the observer can add itself as an observer for any additional
	 * changes to the same value.
	 */
	public void onReadValue(ValueReadObserver valueReadObserver, final T name) {
		valueReadObserver.onValueRead(new ValueChangeObservable() {
			public void addObserver(ValueChangeObserver observer) {
				if(!valueObservers.containsKey(name)) {
					valueObservers.put(name, new HashSet<>(Arrays.asList(observer)));
				} else {
					valueObservers.get(name).add(observer);
				}
			}
			
			public void removeObserver(ValueChangeObserver observer) {
				valueObservers.get(name).remove(observer);
			}
		});
	}
	
	/**
	 * Notifies change observers that a value has been changed.
	 */
	public void onChangeValue(T name) {
		if(valueObservers.containsKey(name)) {
			for(ValueChangeObserver callback:new ArrayList<>(valueObservers.get(name))) {
				callback.onValueChanged();
			}
		}
	}
}
