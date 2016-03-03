package instruction.object;

import instruction.Instruction;
import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.ObjectValue;
import value.Value;

public class SetPropertyInstruction implements Instruction {
	private final String name;
	
	public SetPropertyInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction SetProperty(String name) {
		return new SetPropertyInstruction(name);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value = runtime.getStack().popValue(false, true);
		ObjectValue object = runtime.checkObjectValue(stack.popValue(false, true));
		object.set(name, value);
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "SETPROPERTY: " + name;
	}
}