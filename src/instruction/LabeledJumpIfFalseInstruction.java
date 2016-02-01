package instruction;

import runtime.ExecutionException;
import runtime.Runtime;

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
	
	public String getLabel() {
		return label;
	}
	
	public String toString() {
		return "LABELEDJUMPIFFALSE: " + label;
	}
}