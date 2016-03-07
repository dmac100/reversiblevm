package backend.instruction.object;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.value.ObjectValue;

public class NewObjectInstruction extends Instruction {
	public NewObjectInstruction() {
	}
	
	public static Instruction NewObject() {
		return new NewObjectInstruction();
	}
	
	public void execute(Runtime runtime) {
		ObjectValue value = new ObjectValue(runtime.getUndoStack());
		value.set("prototype", runtime.getGlobalScope().get("ObjectProto", runtime));
		runtime.getStack().push(value, false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
	}
	
	public String toString() {
		return "NEWOBJECT";
	}
}