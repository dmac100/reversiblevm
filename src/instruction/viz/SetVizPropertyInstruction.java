package instruction.viz;

import java.util.List;

import instruction.Instruction;
import runtime.Runtime;
import runtime.VizObject;

public class SetVizPropertyInstruction implements Instruction {
	private final String name;
	
	public SetVizPropertyInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction SetVizPropertyInstruction(String name) {
		return new SetVizPropertyInstruction(name);
	}
	
	public void execute(Runtime runtime) {
		List<VizObject> vizObjects = runtime.getCurrentVizObjects();
		VizObject vizObject = vizObjects.get(vizObjects.size() - 1);
		vizObject.setProperty(name, runtime.getStack().popValue(false, true));
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "SETVIZPROPERTY: " + name;
	}
}