package backend.runtime;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import backend.instruction.Instruction;
import backend.instruction.stack.PushInstruction;
import backend.value.DoubleValue;
import backend.value.FunctionValue;

public class UndoStackTest {
	private List<Instruction> instructions = new ArrayList<>();
	private Runtime runtime = new Runtime();
	private FunctionValue function;
	private UndoStack undoStack;
	private StackFrame frame;
	
	@Before
	public void before() {
		GlobalScope globalScope = new GlobalScope(runtime.getUndoStack());
		function = new FunctionValue(globalScope, 0, instructions);
		
		undoStack = runtime.getUndoStack();
		
		// Add some dummy values to the undo stack so that it's not empty.
		runtime.getUndoStack().addCommandUndo(null);
		runtime.getUndoStack().addPopStackFrameUndo(null);
		runtime.getUndoStack().addPopValueUndo(null);
		
		// Add initial stack frame.
		runtime.addStackFrame(function);
		frame = runtime.getCurrentStackFrame();
	}
	
	@Test
	public void undoInstructions() {
		instructions.add(new PushInstruction(new DoubleValue(2)));
		instructions.add(new PushInstruction(new DoubleValue(3)));
		
		String initialState = runtime.getState();
		
		// Execute first instruction.
		runtime.getUndoStack().saveUndoPoint(0);
		instructions.get(0).execute(runtime);
		undoStack.addInstructionUndo();
		frame.setInstructionCounter(frame.getInstructionCounter() + 1);
		
		// Execute second instruction.
		runtime.getUndoStack().saveUndoPoint(1);
		instructions.get(1).execute(runtime);
		undoStack.addInstructionUndo();
		frame.setInstructionCounter(frame.getInstructionCounter() + 1);
		
		// Undo twice.
		undoStack.undo(runtime);
		undoStack.undo(runtime);
		
		// Check that state is the same as the initial state.
		assertEquals(initialState, runtime.getState());
	}
}
