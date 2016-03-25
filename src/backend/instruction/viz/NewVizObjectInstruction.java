package backend.instruction.viz;

import java.util.ArrayList;
import java.util.List;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.VizObject;

public class NewVizObjectInstruction extends Instruction {
	private final String name;
	
	public NewVizObjectInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction NewVizObjectInstruction(String name) {
		return new NewVizObjectInstruction(name);
	}
	
	public Instruction copy() {
		return new NewVizObjectInstruction(name);
	}
	
	public void execute(Runtime runtime) {
		List<Object> key = new ArrayList<>(runtime.getCurrentVizObjectKey());
		key.add(this);
		VizObject vizObject = new VizObject(name, key);
		runtime.getCurrentVizObjects().add(vizObject);
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return super.toString() + "NEWVIZOBJECT: " + name;
	}
}
