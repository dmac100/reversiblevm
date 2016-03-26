package backend.runtime;

import backend.value.ImmutableValue;

public interface HasImmutableValueProperties {
	ImmutableValue getProperty(String name);
	void setProperty(String name, ImmutableValue value);
}
