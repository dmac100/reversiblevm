package backend.instruction.object;

import backend.instruction.Instruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.HasPropertiesObject;
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
	
	/**
	 * Changes stack from [value, object] to [] and sets object.name to value.
	 */
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		if(!(stack.peekValue(1) instanceof HasPropertiesObject)) {
			throw new ExecutionException("TypeError: Not an object: " + stack.peekValue(1));
		}
		Value value = runtime.getStack().popValue(false, true);
		ObjectValue object = ((HasPropertiesObject)stack.popValue(false, true)).getPropertiesObject();
		object.set(name, value);
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "SETPROPERTY: " + name;
	}
}