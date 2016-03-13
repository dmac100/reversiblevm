package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.instruction.stack.PopInstruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.BooleanValue;

public class NotInstruction extends Instruction {
	public NotInstruction() {
	}
	
	public static Instruction Not() {
		return new NotInstruction();
	}
	
	public Instruction copy() {
		return new NotInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		runtime.checkBooleanValue(stack.peekValue(0));
		BooleanValue value = runtime.checkBooleanValue(stack.popValue(false, false));
		stack.push(BooleanValue.Value(!value.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		Stack stack = runtime.getStack();
		BooleanValue value = runtime.checkBooleanValue(stack.popValue(false, false));
		stack.push(BooleanValue.Value(!value.getValue()), false);
	}
	
	public String toString() {
		return super.toString() + "NOT";
	}
}