package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.instruction.stack.PopInstruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.BooleanValue;

public class OrInstruction extends Instruction {
	public OrInstruction() {
	}
	
	public static Instruction Or() {
		return new OrInstruction();
	}
	
	public Instruction copy() {
		return new OrInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		runtime.checkBooleanValue(stack.peekValue(0));
		runtime.checkBooleanValue(stack.peekValue(1));
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
		return super.toString() + "OR";
	}
}