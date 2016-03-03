package instruction.operator;

import instruction.Instruction;
import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.BooleanValue;
import value.DoubleValue;

public class LessThanEqualInstruction implements Instruction {
	public LessThanEqualInstruction() {
	}
	
	public static Instruction LessThanEqual() {
		return new LessThanEqualInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.checkDoubleValue(stack.popValue(false, true));
		DoubleValue value1 = runtime.checkDoubleValue(stack.popValue(false, true));
		stack.push(BooleanValue.Value(value1.getValue() <= value2.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "LESSTHANEQUAL";
	}
}