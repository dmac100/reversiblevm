package instruction;

import runtime.ExecutionException;
import runtime.Runtime;

public class LabelInstruction implements Instruction {
	private final String label;
	
	public LabelInstruction(String label) {
		this.label = label;
	}
	
	public static Instruction LabelInstruction(String label) {
		return new LabelInstruction(label);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
	}
	
	public String getLabel() {
		return label;
	}
	
	public String toString() {
		return "LABEL: " + label;
	}
}