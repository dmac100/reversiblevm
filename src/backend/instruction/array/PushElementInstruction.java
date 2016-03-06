package backend.instruction.array;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.Value;

public class PushElementInstruction implements Instruction {
	public PushElementInstruction() {
	}
	
	public static Instruction PushElement() {
		return new PushElementInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value = runtime.getStack().popValue(false, true);
		ArrayValue array = runtime.checkArrayValue(stack.popValue(false, true));
		array.push(value);
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "PUSHELEMENT";
	}
}