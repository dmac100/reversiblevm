package instruction.object;

import instruction.Instruction;
import runtime.Runtime;
import value.ObjectValue;

public class NewObjectInstruction implements Instruction {
	public NewObjectInstruction() {
	}
	
	public static Instruction NewObject() {
		return new NewObjectInstruction();
	}
	
	public void execute(Runtime runtime) {
		ObjectValue value = new ObjectValue(runtime.getUndoStack());
		value.set("prototype", runtime.getGlobalScope().get("ObjectProto"));
		runtime.getStack().push(value, false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
	}
	
	public String toString() {
		return "NEWOBJECT";
	}
}