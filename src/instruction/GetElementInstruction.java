package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.ArrayValue;
import value.DoubleValue;

public class GetElementInstruction implements Instruction {
	public GetElementInstruction() {
	}
	
	public static Instruction GetElementInstruction() {
		return new GetElementInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		DoubleValue index = runtime.checkDoubleValue(stack.popValue(false, true));
		ArrayValue array = runtime.checkArrayValue(stack.popValue(false, true));
		runtime.getStack().push(array.get(index), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "GETELEMENT";
	}
}