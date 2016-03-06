package backend.runtime;

import java.util.Set;

/**
 * Allows dumping of the state of the runtime for testing purposes.
 */
public interface HasState {
	public String getState(String prefix, Set<Object> used);
}
