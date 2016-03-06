package callback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ValueChangeCallbacks<T> {
	private final Map<T, Set<ValueChangeCallback>> valueChangeCallbacks = new HashMap<>();
	
	public void fireReadCallbacks(CanFireValueRead callbacks, final T name) {
		callbacks.fireCallbacks(new HasCallbacks<ValueChangeCallback>() {
			public void addCallback(final ValueChangeCallback callback) {
				if(!valueChangeCallbacks.containsKey(name)) {
					valueChangeCallbacks.put(name, new HashSet<>(Arrays.asList(callback)));
				} else {
					valueChangeCallbacks.get(name).add(callback);
				}
			}
			
			public void removeCallback(final ValueChangeCallback callback) {
				valueChangeCallbacks.get(name).remove(callback);
			}
		});
	}
	
	public void fireWriteCallbacks(T name) {
		if(valueChangeCallbacks.containsKey(name)) {
			for(ValueChangeCallback callback:new ArrayList<>(valueChangeCallbacks.get(name))) {
				callback.onValueChanged();
			}
		}
	}
}
