package backend.instruction.viz;

import java.util.List;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.VizObject;

public class EnableVizFilterInstruction extends Instruction {
	public EnableVizFilterInstruction() {
	}
	
	public static Instruction EnableVizFilterInstruction() {
		return new EnableVizFilterInstruction();
	}
	
	public Instruction copy() {
		return new EnableVizFilterInstruction();
	}
	
	public void execute(Runtime runtime) {
		List<VizObject> vizObjects = runtime.getCurrentVizObjects();
		VizObject vizObject = vizObjects.get(vizObjects.size() - 1);
		vizObject.enableFilter();
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return super.toString() + "ENABLEVIZFILTER";
	}
}
