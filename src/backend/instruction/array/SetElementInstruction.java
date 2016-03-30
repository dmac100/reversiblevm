package backend.instruction.array;

import backend.instruction.Instruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.DoubleValue;
import backend.value.HasPropertiesObject;
import backend.value.ObjectValue;
import backend.value.StringValue;
import backend.value.Value;

public class SetElementInstruction extends Instruction {
	public SetElementInstruction() {
	}
	
	public static Instruction GetElementInstruction() {
		return new SetElementInstruction();
	}
	
	public Instruction copy() {
		return new SetElementInstruction();
	}

	/**
	 * Changes stack from [value, index, object] to [] and sets object[index] to value.
	 */
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		
		// If index is a string, then delegate to SetPropertyInstruction to set an object property.
		if(stack.peekValue(1) instanceof StringValue) {
			StringValue name = runtime.checkStringValue(stack.peekValue(1));
			if(!(stack.peekValue(2) instanceof HasPropertiesObject)) {
				throw new ExecutionException("TypeError: Not an object: " + stack.peekValue(2));
			}
			Value value = runtime.getStack().popValue(false, true);
			runtime.getStack().popValue(false, true);
			ObjectValue object = ((HasPropertiesObject)stack.popValue(false, true)).getPropertiesObject();
			object.set(name.getValue(), value);
			return;
		}
		
		// Set an array element by index.
		runtime.checkDoubleValue(stack.peekValue(1));
		runtime.checkArrayValue(stack.peekValue(2));
		Value value = runtime.getStack().popValue(false, true);
		DoubleValue index = runtime.checkDoubleValue(stack.popValue(false, true));
		ArrayValue array = runtime.checkArrayValue(stack.popValue(false, true));
		array.set(index, value);
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "SETELEMENT";
	}
}