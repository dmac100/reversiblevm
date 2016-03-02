package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.ArrayValue;
import value.Value;

public class PushElementInstruction implements Instruction {
	public PushElementInstruction() {
	}
	
	public static Instruction PushElement() {
		return new PushElementInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value = runtime.getStack().popValue(true);
		ArrayValue array = runtime.checkArrayValue(stack.popValue(true));
		array.push(value);
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "PUSHELEMENT";
	}
}