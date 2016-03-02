package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.DoubleValue;

public class BitwiseXorInstruction implements Instruction {
	public BitwiseXorInstruction() {
	}
	
	public static Instruction BitwiseXor() {
		return new BitwiseXorInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.checkDoubleValue(stack.popValue(true));
		DoubleValue value1 = runtime.checkDoubleValue(stack.popValue(true));
		stack.push(DoubleValue.Value((int)value1.getValue() ^ (int)value2.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false);
	}
	
	public String toString() {
		return "BITWISEXOR";
	}
}