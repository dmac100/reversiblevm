package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.StackFrame;
import value.DoubleValue;

public class JumpInstruction implements Instruction {
	private DoubleValue offset;

	public JumpInstruction(DoubleValue offset) {
		this.offset = offset;
	}
	
	public static Instruction Jump(DoubleValue offset) {
		return new JumpInstruction(offset);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		StackFrame stackFrame = runtime.getCurrentStackFrame();
		stackFrame.setInstructionCounter(stackFrame.getInstructionCounter() + (int)offset.getValue());
	}
	
	public int getOffset() {
		return (int)offset.getValue();
	}
	
	public String toString() {
		return "JUMP: " + (int)offset.getValue();
	}
}