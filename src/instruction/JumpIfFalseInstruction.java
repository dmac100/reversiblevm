package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import runtime.StackFrame;
import value.BooleanValue;
import value.DoubleValue;

public class JumpIfFalseInstruction implements Instruction {
	private DoubleValue offset;

	public JumpIfFalseInstruction(DoubleValue offset) {
		this.offset = offset;
	}
	
	public static Instruction JumpIfFalse(DoubleValue offset) {
		return new JumpIfFalseInstruction(offset);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		BooleanValue value = runtime.checkBooleanValue(stack.popValue());
		if(!value.getValue()) {
			StackFrame stackFrame = runtime.getCurrentStackFrame();
			stackFrame.setInstructionCounter(stackFrame.getInstructionCounter() + (int)offset.getValue() - 1);
		}
	}
	
	public int getOffset() {
		return (int)offset.getValue();
	}
	
	public String toString() {
		return "JUMPIFFALSE: " + (int)offset.getValue();
	}
}