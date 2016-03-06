package backend.runtime;

import backend.observer.ValueReadObserver;
import backend.value.Value;

public interface Scope extends HasState {
	public Value get(String name, ValueReadObserver valueReadObserver);
	public void set(String name, Value value);
	public void create(String name);
	public Scope getParentScope();
}