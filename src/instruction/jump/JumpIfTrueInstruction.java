package instruction.jump;

import instruction.Instruction;
import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import runtime.StackFrame;
import value.BooleanValue;

public class JumpIfTrueInstruction implements Instruction {
	private final int offset;

	public JumpIfTrueInstruction(int offset) {
		this.offset = offset;
	}
	
	public static Instruction JumpIfTrue(int offset) {
		return new JumpIfTrueInstruction(offset);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		BooleanValue value = runtime.checkBooleanValue(stack.popValue(false, true));
		if(value.getValue()) {
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
		return "JUMPIFTRUE: " + offset;
	}
}