package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.ArrayValue;
import value.DoubleValue;

public class GetElementInstruction implements Instruction {
	public GetElementInstruction() {
	}
	
	public static Instruction GetElementInstruction() {
		return new GetElementInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue index = runtime.checkDoubleValue(stack.popValue());
		ArrayValue array = runtime.checkArrayValue(stack.popValue());
		runtime.getStack().push(array.get(index));
	}
	
	public String toString() {
		return "GETELEMENT";
	}
}