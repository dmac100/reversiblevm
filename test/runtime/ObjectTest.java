package runtime;

import static runtime.EngineAsserts.assertError;
import static runtime.EngineAsserts.assertOutput;

import org.junit.Test;

public class ObjectTest {
	@Test
	public void prototypes() {
		assertOutput("2", "a = { b: 2 }; b = {}; b.prototype = a; print(b.b);");
		assertError("TypeError: Cyclic prototype", "a = { b: 2 }; b = {}; b.prototype = b;");
	}
}