package instruction.viz;

import instruction.Instruction;
import runtime.Runtime;
import runtime.VizObject;

public class NewVizObjectInstruction implements Instruction {
	private final String name;
	
	public NewVizObjectInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction NewVizObjectInstruction(String name) {
		return new NewVizObjectInstruction(name);
	}
	
	public void execute(Runtime runtime) {
		runtime.getCurrentVizObjects().add(new VizObject(name));
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "NEWVIZOBJECT: " + name;
	}
}