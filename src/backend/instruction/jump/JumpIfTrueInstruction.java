package backend.instruction.jump;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.runtime.StackFrame;
import backend.value.BooleanValue;

public class JumpIfTrueInstruction extends Instruction {
	private final int offset;

	public JumpIfTrueInstruction(int offset) {
		this.offset = offset;
	}
	
	public static Instruction JumpIfTrue(int offset) {
		return new JumpIfTrueInstruction(offset);
	}
	
	public Instruction copy() {
		return new JumpIfTrueInstruction(offset);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		runtime.checkBooleanValue(stack.peekValue(0));
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
		return super.toString() + "JUMPIFTRUE: " + offset;
	}
}