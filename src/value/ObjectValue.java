package value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import runtime.ExecutionException;
import runtime.HasState;

public class ObjectValue extends Value implements HasState {
	private SortedMap<String, Value> values = new TreeMap<>();
	
	public ObjectValue() {
	}
	
	public Value get(String name) {
		if(values.containsKey(name)) {
			return values.get(name);
		} else if(values.get("prototype") instanceof ObjectValue) {
			return ((ObjectValue)values.get("prototype")).get(name);
		} else {
			return new NullValue();
		}
	}
	
	public void set(String name, Value value) throws ExecutionException {
		values.put(name, value);
		checkCyclicPrototype(new HashSet<>());
	}

	private void checkCyclicPrototype(HashSet<Object> used) throws ExecutionException {
		if(used.contains(this)) throw new ExecutionException("TypeError: Cyclic prototype");
		used.add(this);
		
		if(values.get("prototype") instanceof ObjectValue) {
			((ObjectValue)values.get("prototype")).checkCyclicPrototype(used);
		}
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
			if(!key.equals("prototype")) {
				if(s.length() != 0) {
					s.append(", ");
				}
				s.append(key + ":" + values.get(key).toString(used));
			}
		}
		
		used.remove(this);
		
		return "{" + s.toString() + "}";
	}
	
	public String getState(String prefix, Set<Object> used) {
		if(used.contains(this)) return prefix + "[CYCLIC]";
		used.add(this);
		
		StringBuilder s = new StringBuilder();
		s.append(prefix + "Object:").append("\n");
		for(String key:values.keySet()) {
			s.append(prefix + "  Key: " + key).append("\n");
			s.append(prefix + "  Value:").append("\n");
			s.append(values.get(key).getState(prefix + "    ", used)).append("\n");
		}
		
		used.remove(this);
		
		return s.toString().replaceAll("\\s+$", "");
	}
}
