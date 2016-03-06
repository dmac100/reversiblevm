package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.DoubleValue;

public class DivideInstruction implements Instruction {
	public DivideInstruction() {
	}
	
	public static Instruction Divide() {
		return new DivideInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.checkDoubleValue(stack.popValue(false, true));
		DoubleValue value1 = runtime.checkDoubleValue(stack.popValue(false, true));
		stack.push(DoubleValue.Value(value1.getValue() / value2.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "DIVIDE";
	}
}