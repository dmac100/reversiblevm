package value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class ObjectValue extends Value {
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
	
	public List<String> keys() {
		return new ArrayList<>(values.keySet());
	}
	
	public String toString() {
		return toString(new HashSet<Value>());
	}
	
	public String toString(Set<Value> used) {
		if(used.contains(this)) return "[CYCLIC]";
		used.add(this);
		
		StringBuilder s = new StringBuilder();
		for(String key:values.keySet()) {
			if(s.length() != 0) {
				s.append(", ");
			}
			s.append(key + ":" + values.get(key).toString(used));
		}
		
		used.remove(this);
		
		return "{" + s.toString() + "}";
	}
}
