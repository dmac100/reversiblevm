package instruction;

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
		Value value = runtime.getStack().popValue(true);
		ObjectValue object = runtime.checkObjectValue(stack.popValue(true));
		object.set(name, value);
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "SETPROPERTY: " + name;
	}
}