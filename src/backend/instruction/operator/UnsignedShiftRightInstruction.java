package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.instruction.viz.VizIterateInstruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.DoubleValue;

public class UnsignedShiftRightInstruction extends Instruction {
	public UnsignedShiftRightInstruction() {
	}
	
	public static Instruction UnsignedShiftRight() {
		return new UnsignedShiftRightInstruction();
	}
	
	public Instruction copy() {
		return new UnsignedShiftRightInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		runtime.checkDoubleValue(stack.peekValue(0));
		runtime.checkDoubleValue(stack.peekValue(1));
		DoubleValue value2 = runtime.checkDoubleValue(stack.popValue(false, true));
		DoubleValue value1 = runtime.checkDoubleValue(stack.popValue(false, true));
		stack.push(DoubleValue.Value((int)value1.getValue() >>> (int)value2.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "UNSIGNEDSHIFTRIGHT";
	}
}