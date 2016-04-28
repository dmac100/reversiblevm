package backend.instruction.array;

import backend.instruction.Instruction;
import backend.instruction.object.GetPropertyInstruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.DoubleValue;
import backend.value.HasPropertiesObject;
import backend.value.Identifier;
import backend.value.StringValue;

public class GetElementInstruction extends Instruction {
	public GetElementInstruction() {
	}
	
	public static Instruction GetElement() {
		return new GetElementInstruction();
	}
	
	public Instruction copy() {
		return new GetElementInstruction();
	}
	
	/**
	 * Changes stack from [object, index] to [object[index]].
	 */
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		
		// If index is a string, then delegate to GetPropertyInstruction to get an object property.
		if(stack.peekValue(0) instanceof StringValue) {
			StringValue name = runtime.checkStringValue(stack.peekValue(0));
			if(!(stack.peekValue(1) instanceof HasPropertiesObject)) {
				throw new ExecutionException("TypeError: Not an object: " + stack.peekValue(1));
			}
			runtime.getStack().popValue(false, true);
			GetPropertyInstruction getPropertyInstruction = new GetPropertyInstruction(new Identifier(name.getValue()));
			getPropertyInstruction.execute(runtime);
			return;
		}
		
		// Get an array element by index.
		runtime.checkDoubleValue(stack.peekValue(0));
		runtime.checkArrayValue(stack.peekValue(1));
		DoubleValue index = runtime.checkDoubleValue(stack.popValue(false, true));
		ArrayValue array = runtime.checkArrayValue(stack.popValue(false, true));
		runtime.getStack().push(array.get(index, runtime), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "GETELEMENT";
	}
}