package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.BooleanValue;
import value.DoubleValue;

public class LessThanInstruction implements Instruction {
	public LessThanInstruction() {
	}
	
	public static Instruction LessThan() {
		return new LessThanInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.checkDoubleValue(stack.popValue(false, true));
		DoubleValue value1 = runtime.checkDoubleValue(stack.popValue(false, true));
		stack.push(BooleanValue.Value(value1.getValue() < value2.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "LESSTHAN";
	}
}