package backend.runtime;

import static backend.runtime.EngineAsserts.filterError;
import static backend.runtime.EngineAsserts.filterStandard;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import backend.instruction.Instruction;
import backend.value.FunctionValue;

public class RunInstructionsTest {
	@Test
	public void runInstructions() {
		assertOutput("1", "print(1)");
		assertOutput("1\n2", "print(1); print(2)");
		assertOutput("", "@rect()");
	}
	
	@Test
	public void runInstructions_stepBackwards() {
		assertStepBackward("print(1)");
		assertStepBackward("print(1); print(2)");
		assertStepBackward("@rect()");
	}
	
	@Test
	public void runInstructions_stepBackwardsAfterError() {
		assertStepBackward("print(null.length())");
	}

	public static void assertOutput(String expected, String program) {
		assertOutput(expected, Engine.compile(program));
	}
	
	public static void assertOutput(String expected, List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		runtime.addStackFrame(new FunctionValue(new GlobalScope(runtime.getUndoStack()), 0, new ArrayList<Instruction>()));
		
		runtime.runInstructions("info", instructions);
		
		assertEquals(expected, StringUtils.join(filterStandard(runtime.getOutput()), "\n"));
		assertTrue(filterError(runtime.getOutput()).isEmpty());
		assertTrue(runtime.getStack().isEmpty());
	}
	
	private static void assertStepBackward(String program) {
		assertStepBackward(Engine.compile(program));
	}
	
	private static void assertStepBackward(List<Instruction> instructions) {
		Runtime runtime = new Runtime();
		runtime.addStackFrame(new FunctionValue(new GlobalScope(runtime.getUndoStack()), 0, new ArrayList<Instruction>()));
		runtime.runInstructions("", Engine.compile("var x = 1; print(x)"));
		
		String initialState = runtime.getState();
		
		try {
			runtime.runInstructions("", instructions);
		} catch(ExecutionException e) {
		}
		
		for(int i = 0; i < 100; i++) {
			runtime.getUndoStack().undo(runtime);
			if(runtime.getState().equals(initialState)) {
				return;
			}
		}
		
		fail("Iteration limit reached.");
	}
}
