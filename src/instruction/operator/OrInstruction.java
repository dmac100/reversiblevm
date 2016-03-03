package instruction.operator;

import instruction.Instruction;
import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.BooleanValue;

public class OrInstruction implements Instruction {
	public OrInstruction() {
	}
	
	public static Instruction Or() {
		return new OrInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		BooleanValue value2 = runtime.checkBooleanValue(stack.popValue(false, true));
		BooleanValue value1 = runtime.checkBooleanValue(stack.popValue(false, true));
		stack.push(BooleanValue.Value(value1.getValue() || value2.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "OR";
	}
}