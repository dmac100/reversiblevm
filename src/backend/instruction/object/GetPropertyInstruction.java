package backend.instruction.object;

import backend.instruction.Instruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.FunctionValue;
import backend.value.HasPropertiesObject;
import backend.value.NativeFunctionValue;
import backend.value.ObjectValue;
import backend.value.StringValue;
import backend.value.Value;

public class GetPropertyInstruction extends Instruction {
	private final String name;
	
	public GetPropertyInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction GetProperty(String name) {
		return new GetPropertyInstruction(name);
	}
	
	public Instruction copy() {
		return new GetPropertyInstruction(name);
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Changes stack from [object] to [object.name].
	 */
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		
		Value value = stack.peekValue(0);
		ObjectValue objectValue;
		if(value instanceof ObjectValue) {
			objectValue = getObjectValue(runtime, stack, null);
		} else if(value instanceof ArrayValue) {
			objectValue = getObjectValue(runtime, stack, "ArrayProto");
		} else if(value instanceof StringValue) {
			objectValue = getObjectValue(runtime, stack, "StringProto");
		} else if(value instanceof FunctionValue || value instanceof NativeFunctionValue) {
			objectValue = getObjectValue(runtime, stack, "FunctionProto");
		} else {
			throw new ExecutionException("TypeError: Not an object: " + value);
		}
		
		runtime.getStack().push(objectValue.get(name, runtime), false);
	}
	
	/**
	 * Returns an object value from the top of the stack, and sets its prototype to the
	 * value of prototypeName.
	 */
	private static ObjectValue getObjectValue(Runtime runtime, Stack stack, String prototypeName) {
		Value value = stack.popValue(false, true);
		if(!(value instanceof HasPropertiesObject)) {
			throw new ExecutionException("TypeError: Not an object: " + value);
		}
		ObjectValue objectValue = ((HasPropertiesObject)value).getPropertiesObject();
		if(prototypeName != null) {
			ObjectValue prototype = runtime.checkObjectValue(runtime.getScope().get(prototypeName, runtime));
			objectValue.set("prototype", prototype);
		}
		return objectValue;
	}

	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "GETPROPERTY: " + name;
	}
}