package backend.instruction.object;

import backend.instruction.Instruction;
import backend.instruction.operator.ShiftLeftInstruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.ObjectValue;
import backend.value.Value;

public class SetPropertyInstruction extends Instruction {
	private final String name;
	
	public SetPropertyInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction SetProperty(String name) {
		return new SetPropertyInstruction(name);
	}
	
	public Instruction copy() {
		return new SetPropertyInstruction(name);
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		runtime.checkObjectValue(stack.peekValue(1));
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