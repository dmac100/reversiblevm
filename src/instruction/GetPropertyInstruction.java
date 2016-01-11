package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.ObjectValue;
import value.StringValue;

public class GetPropertyInstruction implements Instruction {
	private StringValue name;
	
	public GetPropertyInstruction(StringValue name) {
		this.name = name;
	}
	
	public static Instruction GetProperty(StringValue name) {
		return new GetPropertyInstruction(name);
	}
	
	public String getName() {
		return name.getValue();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		ObjectValue object = runtime.checkObjectValue(stack.popValue());
		runtime.getStack().push(object.get(name));
	}
	
	public String toString() {
		return "GETPROPERTY: " + name;
	}
}