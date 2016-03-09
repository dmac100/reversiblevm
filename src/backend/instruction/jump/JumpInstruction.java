package backend.instruction.jump;

import backend.instruction.Instruction;
import backend.instruction.operator.AddInstruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.StackFrame;

public class JumpInstruction extends Instruction {
	private final int offset;

	public JumpInstruction(int offset) {
		this.offset = offset;
	}
	
	public static Instruction Jump(int offset) {
		return new JumpInstruction(offset);
	}
	
	public Instruction copy() {
		return new JumpInstruction(offset);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		StackFrame stackFrame = runtime.getCurrentStackFrame();
		stackFrame.setInstructionCounter(stackFrame.getInstructionCounter() + offset - 1);
	}
	
	public void undo(Runtime runtime) {
	}
	
	public int getOffset() {
		return offset;
	}
	
	public String toString() {
		return "JUMP: " + offset;
	}
}