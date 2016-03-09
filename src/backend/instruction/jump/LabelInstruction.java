package backend.instruction.jump;

import backend.instruction.Instruction;
import backend.instruction.operator.AddInstruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;

public class LabelInstruction extends Instruction {
	private final String label;
	
	public LabelInstruction(String label) {
		this.label = label;
	}
	
	public Instruction copy() {
		return new LabelInstruction(label);
	}
	
	public static Instruction LabelInstruction(String label) {
		return new LabelInstruction(label);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String getLabel() {
		return label;
	}
	
	public String toString() {
		return "LABEL: " + label;
	}
}