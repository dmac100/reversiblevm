package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.BooleanValue;

public class AndInstruction implements Instruction {
	public AndInstruction() {
	}
	
	public static Instruction And() {
		return new AndInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		BooleanValue value2 = runtime.popCheckedBooleanValue();
		BooleanValue value1 = runtime.popCheckedBooleanValue();
		stack.push(BooleanValue.Value(value1.getValue() && value2.getValue()));
	}
	
	public String toString() {
		return "AND";
	}
}