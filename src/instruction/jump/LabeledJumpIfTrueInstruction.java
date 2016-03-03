package instruction.jump;

import instruction.Instruction;
import runtime.ExecutionException;
import runtime.Runtime;

public class LabeledJumpIfTrueInstruction implements Instruction {
	private final String label;

	public LabeledJumpIfTrueInstruction(String label) {
		this.label = label;
	}
	
	public static Instruction LabeledJumpIfTrue(String label) {
		return new LabeledJumpIfTrueInstruction(label);
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
		return "LABELEDJUMPIFTRUE: " + label;
	}
}