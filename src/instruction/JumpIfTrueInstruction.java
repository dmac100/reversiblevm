package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import runtime.StackFrame;
import value.BooleanValue;
import value.DoubleValue;

public class JumpIfTrueInstruction implements Instruction {
	private DoubleValue offset;

	public JumpIfTrueInstruction(DoubleValue offset) {
		this.offset = offset;
	}
	
	public static Instruction JumpIfTrue(DoubleValue offset) {
		return new JumpIfTrueInstruction(offset);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		BooleanValue value = runtime.checkBooleanValue(stack.popValue());
		if(value.getValue()) {
			StackFrame stackFrame = runtime.getCurrentStackFrame();
			stackFrame.setInstructionCounter(stackFrame.getInstructionCounter() + (int)offset.getValue());
		}
	}
	
	public int getOffset() {
		return (int)offset.getValue();
	}
	
	public String toString() {
		return "JUMPIFTRUE: " + (int)offset.getValue();
	}
}