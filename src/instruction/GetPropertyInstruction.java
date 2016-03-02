package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.ArrayValue;
import value.ObjectValue;
import value.StringValue;
import value.Value;

public class GetPropertyInstruction implements Instruction {
	private final String name;
	
	public GetPropertyInstruction(String name) {
		this.name = name;
	}
	
	public static Instruction GetProperty(String name) {
		return new GetPropertyInstruction(name);
	}
	
	public String getName() {
		return name;
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		
		Value value = stack.popValue(true);
		if(value instanceof ObjectValue) {
			runtime.getStack().push(((ObjectValue)value).get(name), false);
		} else if(value instanceof ArrayValue) {
			runtime.getStack().push(runtime.checkObjectValue(runtime.getScope().get("ArrayProto")).get(name), false);
		} else if(value instanceof StringValue) {
			runtime.getStack().push(runtime.checkObjectValue(runtime.getScope().get("StringProto")).get(name), false);
		} else {
			throw new ExecutionException("TypeError: Not an object: " + value);
		}
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false);
	}
	
	public String toString() {
		return "GETPROPERTY: " + name;
	}
}