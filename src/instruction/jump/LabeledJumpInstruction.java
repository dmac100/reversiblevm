package instruction.jump;

import instruction.Instruction;
import runtime.ExecutionException;
import runtime.Runtime;

public class LabeledJumpInstruction implements Instruction {
	private final String label;

	public LabeledJumpInstruction(String label) {
		this.label = label;
	}
	
	public static Instruction LabeledJump(String label) {
		return new LabeledJumpInstruction(label);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		throw new ExecutionException("Not implemented");
	}
	
	public void undo(Runtime runtime) {
		throw new ExecutionException("Not implemented");
	}
	
	public String getLabel() {
		return label;
	}
	
	public String toString() {
		return "LABELEDJUMP: " + label;
	}
}