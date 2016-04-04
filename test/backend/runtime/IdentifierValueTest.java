package backend.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IdentifierValueTest {
	@Test
	public void simple() {
		Runtime runtime = new Runtime();
		new Engine(runtime, Engine.compile("var x = 5; x = 10; var y = 20; y = 30")).run();
		
		assertEquals(null, runtime.getValueAt(1, 2));
		assertEquals("10", runtime.getValueAt(1, 5).inspect());
		assertEquals("10", runtime.getValueAt(1, 12).inspect());
		assertEquals("30", runtime.getValueAt(1, 24).inspect());
		assertEquals("30", runtime.getValueAt(1, 32).inspect());
	}
}
