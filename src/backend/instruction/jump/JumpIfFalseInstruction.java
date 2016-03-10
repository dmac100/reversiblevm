package backend.instruction.jump;

import backend.instruction.Instruction;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.runtime.Runtime;
import backend.runtime.StackFrame;
import backend.value.BooleanValue;

public class JumpIfFalseInstruction extends Instruction {
	private final int offset;

	public JumpIfFalseInstruction(int offset) {
		this.offset = offset;
	}
	
	public static Instruction JumpIfFalse(int offset) {
		return new JumpIfFalseInstruction(offset);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		runtime.checkBooleanValue(stack.peekValue(0));
		BooleanValue value = runtime.checkBooleanValue(stack.popValue(false, true));
		if(!value.getValue()) {
			StackFrame stackFrame = runtime.getCurrentStackFrame();
			stackFrame.setInstructionCounter(stackFrame.getInstructionCounter() + offset - 1);
		}
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public int getOffset() {
		return offset;
	}
	
	public String toString() {
		return "JUMPIFFALSE: " + offset;
	}
}