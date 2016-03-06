package backend.instruction.jump;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;

public class LabeledJumpIfFalseInstruction implements Instruction {
	private final String label;

	public LabeledJumpIfFalseInstruction(String label) {
		this.label = label;
	}
	
	public static Instruction LabeledJumpIfFalse(String label) {
		return new LabeledJumpIfFalseInstruction(label);
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
		return "LABELEDJUMPIFFALSE: " + label;
	}
}