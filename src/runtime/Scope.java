package runtime;

import value.Value;
import callback.CanFireValueRead;

public interface Scope extends HasState {
	public Value get(String name, CanFireValueRead canFireValueRead);
	public void set(String name, Value value);
	public void create(String name);
	public Scope getParentScope();
}