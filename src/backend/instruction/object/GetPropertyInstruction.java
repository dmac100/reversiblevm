package backend.instruction.object;

import backend.instruction.Instruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.FunctionValue;
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
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		
		Value value = stack.peekValue(0);
		if(value instanceof ObjectValue) {
			ObjectValue objectValue = runtime.checkObjectValue(stack.popValue(false, true));
			runtime.getStack().push(objectValue.get(name, runtime), false);
		} else if(value instanceof ArrayValue) {
			ArrayValue arrayValue = runtime.checkArrayValue(stack.popValue(false, true));
			ObjectValue prototype = runtime.checkObjectValue(runtime.getScope().get("ArrayProto", runtime));
			runtime.getStack().push(prototype.get(name, runtime), false);
		} else if(value instanceof StringValue) {
			StringValue stringValue = runtime.checkStringValue(stack.popValue(false, true));
			ObjectValue prototype = runtime.checkObjectValue(runtime.getScope().get("StringProto", runtime));
			runtime.getStack().push(prototype.get(name, runtime), false);
		} else if(value instanceof FunctionValue || value instanceof NativeFunctionValue) {
			Value functionValue = stack.popValue(false, true);
			ObjectValue prototype = runtime.checkObjectValue(runtime.getScope().get("FunctionProto", runtime));
			runtime.getStack().push(prototype.get(name, runtime), false);
		} else {
			throw new ExecutionException("TypeError: Not an object: " + value);
		}
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "GETPROPERTY: " + name;
	}
}