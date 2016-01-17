package runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import instruction.Instruction;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class EngineAsserts {
	public static void assertStackValue(String expected, String program) {
		assertStackValue(expected, Engine.compile(program));
	}
	
	public static void assertOutput(String expected, String program) {
		assertOutput(expected, Engine.compile(program));
	}
	
	public static void assertError(String expected, String program) {
		assertError(expected, Engine.compile(program));
	}
	
	public static void assertStackValue(String expected, List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		new Engine().run(runtime, instructions);
		
		assertTrue(runtime.getOutput().isEmpty());
		assertTrue(runtime.getErrors().isEmpty());
		assertEquals(expected, runtime.getStack().popValue().toString());
		assertTrue(runtime.getStack().isEmpty());
	}
	
	public static void assertOutput(String expected, List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		new Engine().run(runtime, instructions);
		
		assertEquals(expected, StringUtils.join(runtime.getOutput(), "\n"));
		assertTrue(runtime.getErrors().isEmpty());
		assertTrue(runtime.getStack().isEmpty());
	}
	
	public static void assertError(String expected, List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		new Engine().run(runtime, instructions);
		
		assertEquals(expected, runtime.getErrors().get(0));
		assertTrue(runtime.getOutput().isEmpty());
	}
}
