package backend.runtime;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import backend.value.ImmutableValue;
import backend.value.NullValue;

/**
 * A visual object that represents an object displayed on the graphical canvas.
 * A VizObject can be created with an statement like "@rect[id: 5](width: 50, height: 50)"
 * Here, the name would be 'rect', the values would be the width and height, and the filters would be the id.
 * If filters are set, then this object applies its values to any other objects containing values matching the filter.
 * The key is a unique object to tell which objects are equal to each other. Each object has a different key, but also
 * if VizObjects are created within a @for instruction, then they have a different key for each iteration.
 */
public class VizObject implements HasImmutableValueProperties {
	private final String name;
	private final Object key;
	private final Map<String, ImmutableValue> values = new LinkedHashMap<>();
	private final Map<String, ImmutableValue> filters = new LinkedHashMap<>();
	
	private boolean filterEnabled = false;
	
	public VizObject(String name) {
		this(name, new Object());
	}
	
	public VizObject(String name, Object key) {
		this.name = name;
		this.key = key;
	}
	
	public String getName() {
		return name;
	}
	
	public ImmutableValue getProperty(String name) {
		if(values.containsKey(name)) {
			return values.get(name);
		} else {
			return new NullValue();
		}
	}
	
	public void setProperty(String name, ImmutableValue value) {
		values.put(name, value);
	}
	
	public void setFilterProperty(String name, ImmutableValue value) {
		filters.put(name, value);
	}
	
	public Object getKey() {
		return key;
	}
	
	public Set<String> getPropertyNames() {
		return Collections.unmodifiableSet(values.keySet());
	}
	
	public Map<String, ImmutableValue> getValues() {
		return Collections.unmodifiableMap(values);
	}
	
	public Map<String, ImmutableValue> getFilters() {
		return Collections.unmodifiableMap(filters);
	}
	
	public void enableFilter() {
		filterEnabled = true;
	}
	
	public boolean isFilterEnabled() {
		return filterEnabled;
	}
	
	public void applyFilter(VizObject vizObject) {
		Map<String, ImmutableValue> filter = vizObject.getFilters();
		
		if(filterEnabled) {
			return;
		}
		
		if(!vizObject.getName().equals(getName())) {
			return;
		}
		
		// Check if the filter in vizObject matches this object.
		for(String key:filter.keySet()) {
			ImmutableValue value1 = filter.get(key);
			ImmutableValue value2 = values.get(key);
			if(value1 != null && value2 != null && !value1.getKey().equals(value2.getKey())) {
				return;
			}
		}
		
		// Copy properties from vizObject to this object.
		Map<String, ImmutableValue> values = vizObject.getValues();
		for(String key:values.keySet()) {
			setProperty(key, values.get(key));
		}
	}
	
	public String toString() {
		String s = name;
		if(!filters.isEmpty()) s += "[" + toString(filters) + "]";
		s += "(" + toString(values) + ")";
		return s;
	}
	
	private static String toString(Map<String, ImmutableValue> map) {
		StringBuilder s = new StringBuilder();
		for(String key:map.keySet()) {
			if(s.length() > 0) {
				s.append(", ");
			}
			s.append(key).append(": ").append(map.get(key).inspect());
		}
		return s.toString();
	}
}
