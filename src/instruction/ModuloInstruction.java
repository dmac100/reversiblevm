package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.DoubleValue;

public class ModuloInstruction implements Instruction {
	public ModuloInstruction() {
	}
	
	public static Instruction Modulo() {
		return new ModuloInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.checkDoubleValue(stack.popValue(true));
		DoubleValue value1 = runtime.checkDoubleValue(stack.popValue(true));
		stack.push(DoubleValue.Value(value1.getValue() % (int)value2.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false);
	}
	
	public String toString() {
		return "MODULO";
	}
}