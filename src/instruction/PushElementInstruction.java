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
		Value value = runtime.getStack().popValue();
		ArrayValue array = runtime.checkArrayValue(stack.popValue());
		array.push(value);
	}
	
	public String toString() {
		return "PUSHELEMENT";
	}
}