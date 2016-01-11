package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.BooleanValue;

public class NotInstruction implements Instruction {
	public NotInstruction() {
	}
	
	public static Instruction Not() {
		return new NotInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		BooleanValue value = runtime.checkBooleanValue(stack.popValue());
		stack.push(BooleanValue.Value(!value.getValue()));
	}
	
	public String toString() {
		return "NOT";
	}
}