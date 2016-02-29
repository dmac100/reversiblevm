package runtime;

import static org.junit.Assert.assertEquals;
import instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StepBackwardTest {
	@Test
	public void test() {
		assertStepBackward("x = 1; x = 2;");
	}

	private static void assertStepBackward(String program) {
		Runtime runtime = new Runtime();
		List<Instruction> instructions = Engine.compile(program);
		
		Engine engine = new Engine(runtime, instructions);
		
		ArrayList<String> states = new ArrayList<>();
		
		states.add(runtime.getState());
		while(runtime.getCurrentStackFrame() != null) {
			engine.stepForward();
			states.add(runtime.getState());
		}
		
		for(int i = states.size() - 1; i >= 0; i--) {
			assertEquals(states.get(i), runtime.getState());
			engine.stepBackward();
		}
	}
}