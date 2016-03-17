package backend.runtime;

import java.util.LinkedHashMap;
import java.util.Map;

import backend.value.ImmutableValue;
import backend.value.NullValue;

public class VizObject {
	private final String name;
	private final Map<String, ImmutableValue> values = new LinkedHashMap<>();
	
	public VizObject(String name) {
		this.name = name;
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
