package value;

import java.util.HashMap;
import java.util.Map;

public class ObjectValue implements Value {
	private Map<String, Value> values = new HashMap<>();
	
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
