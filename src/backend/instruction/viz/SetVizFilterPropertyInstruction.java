package backend.instruction.viz;

import java.util.List;

import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.instruction.Instruction;
import backend.instruction.variable.StoreInstruction;
import backend.runtime.VizObject;
import backend.value.ImmutableValue;
import backend.value.Value;

public class SetVizFilterPropertyInstruction extends Instruction {
	private final String name;
	
	public SetVizFilterPropertyInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction SetVizFilterPropertyInstruction(String name) {
		return new SetVizFilterPropertyInstruction(name);
	}
	
	public Instruction copy() {
		return new SetVizFilterPropertyInstruction(name);
	}
	
	public void execute(Runtime runtime) {
		List<VizObject> vizObjects = runtime.getCurrentVizObjects();
		VizObject vizObject = vizObjects.get(vizObjects.size() - 1);
		Value value = runtime.getStack().peekValue(0);
		if(!(value instanceof ImmutableValue)) {
			throw new ExecutionException("Property value must be immutable");
		}
		vizObject.setFilterProperty(name, (ImmutableValue) runtime.getStack().popValue(false, true));
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "SETVIZFILTERPROPERTY: " + name;
	}
}
