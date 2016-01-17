package value;

import java.util.HashSet;
import java.util.Set;

public abstract class Value {
	public String toString() {
		return toString(new HashSet<Value>());
	}
	
	public abstract String toString(Set<Value> used);
}