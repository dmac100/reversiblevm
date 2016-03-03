package instruction.jump;

import instruction.Instruction;
import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import runtime.StackFrame;
import value.BooleanValue;

public class JumpIfFalseInstruction implements Instruction {
	private final int offset;

	public JumpIfFalseInstruction(int offset) {
		this.offset = offset;
	}
	
	public static Instruction JumpIfFalse(int offset) {
		return new JumpIfFalseInstruction(offset);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
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