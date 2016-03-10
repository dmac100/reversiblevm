package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.DoubleValue;

public class UnaryMinusInstruction extends Instruction {
	public UnaryMinusInstruction() {
	}
	
	public static Instruction UnaryMinus() {
		return new UnaryMinusInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		runtime.checkDoubleValue(stack.peekValue(0));
		DoubleValue value = runtime.checkDoubleValue(stack.popValue(false, true));
		stack.push(DoubleValue.Value(-value.getValue()), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "UNARYMINUS";
	}
}