package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.DoubleValue;

public class ShiftRightInstruction implements Instruction {
	public ShiftRightInstruction() {
	}
	
	public static Instruction ShiftRight() {
		return new ShiftRightInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue value2 = runtime.checkDoubleValue(stack.popValue(false, true));
		DoubleValue value1 = runtime.checkDoubleValue(stack.popValue(false, true));
		stack.push(DoubleValue.Value((int)value1.getValue() >> (int)value2.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "SHIFTRIGHT";
	}
}