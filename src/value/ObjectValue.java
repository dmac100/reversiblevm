package value;

import java.util.SortedMap;
import java.util.TreeMap;

public class ObjectValue implements Value {
	private SortedMap<String, Value> values = new TreeMap<>();
	
	public ObjectValue() {
	}
	
	public Value get(StringValue name) {
		return values.get(name.getValue());
	}
	
	public void set(StringValue name, Value value) {
		values.put(name.getValue(), value);
	}

	public static ObjectValue Value() {
		return new ObjectValue();
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(String key:values.keySet()) {
			if(s.length() != 0) {
				s.append(", ");
			}
			s.append(key + ":" + values.get(key));
		}
		return "{" + s.toString() + "}";
	}
}
