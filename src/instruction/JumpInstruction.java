package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.StackFrame;

public class JumpInstruction implements Instruction {
	private final int offset;

	public JumpInstruction(int offset) {
		this.offset = offset;
	}
	
	public static Instruction Jump(int offset) {
		return new JumpInstruction(offset);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		StackFrame stackFrame = runtime.getCurrentStackFrame();
		stackFrame.setInstructionCounter(stackFrame.getInstructionCounter() + offset - 1);
	}
	
	public int getOffset() {
		return offset;
	}
	
	public String toString() {
		return "JUMP: " + offset;
	}
}