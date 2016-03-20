package backend.runtime;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import backend.value.DoubleValue;
import backend.value.ImmutableValue;
import backend.value.NullValue;

public class VizObject {
	private final String name;
	private final Object key;
	private final Map<String, ImmutableValue> values = new LinkedHashMap<>();
	
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
	
	public Object getKey() {
		return key;
	}
	
	public Set<String> getPropertyNames() {
		return Collections.unmodifiableSet(values.keySet());
	}
	
	public Map<String, ImmutableValue> getValues() {
		return Collections.unmodifiableMap(values);
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(String key:values.keySet()) {
			if(s.length() > 0) {
				s.append(", ");
			}
			s.append(key).append(": ").append(values.get(key));
		}
		
		return name + "(" + s.toString() + ")";
	}
}
