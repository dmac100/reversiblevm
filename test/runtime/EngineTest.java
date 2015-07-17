package runtime;

import static instruction.CallInstruction.Call;
import static instruction.LoadInstruction.Load;
import static instruction.PushInstruction.Push;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static value.StringValue.Value;
import instruction.Instruction;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import value.IntValue;

public class EngineTest {
	@Test
	public void printHello() {
		assertOutput("Hello World!", Arrays.asList(
			Load(Value("print")),
			Push(Value("Hello World!")),
			Push(IntValue.Value(1)),
			Call()
		));
	}
	
	private static void assertOutput(String expected, List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		
		new Engine().run(runtime, instructions);
		
		assertEquals(Arrays.asList(expected), runtime.getOutput());
		assertTrue(runtime.getErrors().isEmpty());
		assertTrue(runtime.getStack().isEmpty());
	}
}
