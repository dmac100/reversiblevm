package backend.runtime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IdentifierValueTest {
	@Test
	public void simple() {
		Runtime runtime = new Runtime();
		new Engine(runtime, Engine.compile("var x = 5; x = 10; var y = 20; y = 30")).run();
		
		assertEquals(null, runtime.getValueAt(1, 2));
		assertEquals("10", runtime.getValueAt(1, 5));
		assertEquals("10", runtime.getValueAt(1, 12));
		assertEquals("30", runtime.getValueAt(1, 24));
		assertEquals("30", runtime.getValueAt(1, 32));
	}
	
	@Test
	public void vizObjects() {
		Runtime runtime = new Runtime();
		new Engine(runtime, Engine.compile("@rect(x: 1)")).run();
		
		assertEquals(null, runtime.getValueAt(1, 10));
		assertEquals("rect(x: 1)", runtime.getValueAt(1, 2));
	}
	
	@Test
	public void vizObjects_for() {
		Runtime runtime = new Runtime();
		new Engine(runtime, Engine.compile("@for(x <- [1, 2]) @rect(x: x)")).run();
		
		assertEquals(null, runtime.getValueAt(1, 20));
		assertEquals("rect(x: 1)\nrect(x: 2)", runtime.getValueAt(1, 2));
	}
	
	@Test
	public void vizObjects_filter() {
		Runtime runtime = new Runtime();
		new Engine(runtime, Engine.compile("@rect[y: 2](x: 3)")).run();
		
		assertEquals(null, runtime.getValueAt(1, 10));
		assertEquals("rect[y: 2](x: 3)", runtime.getValueAt(1, 2));
	}
}
