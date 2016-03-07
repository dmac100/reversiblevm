package backend.instruction.jump;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;

public class LabeledJumpIfTrueInstruction extends Instruction {
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