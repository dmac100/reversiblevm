package backend.instruction.array;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.DoubleValue;

public class GetElementInstruction extends Instruction {
	public GetElementInstruction() {
	}
	
	public static Instruction GetElementInstruction() {
		return new GetElementInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		runtime.checkDoubleValue(stack.peekValue(0));
		runtime.checkArrayValue(stack.peekValue(1));
		DoubleValue index = runtime.checkDoubleValue(stack.popValue(false, true));
		ArrayValue array = runtime.checkArrayValue(stack.popValue(false, true));
		runtime.getStack().push(array.get(index, runtime), false);
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