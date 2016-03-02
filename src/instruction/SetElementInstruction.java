package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.ArrayValue;
import value.DoubleValue;
import value.Value;

public class SetElementInstruction implements Instruction {
	public SetElementInstruction() {
	}
	
	public static Instruction GetElementInstruction() {
		return new SetElementInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value = runtime.getStack().popValue(true);
		DoubleValue index = runtime.checkDoubleValue(stack.popValue(true));
		ArrayValue array = runtime.checkArrayValue(stack.popValue(true));
		array.set(index, value);
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "SETELEMENT";
	}
}