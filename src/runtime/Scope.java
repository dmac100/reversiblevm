package runtime;

import value.Value;

public interface Scope {
	public Value get(String name);
	public void set(String name, Value value);
}