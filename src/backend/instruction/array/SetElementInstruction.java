package backend.instruction.array;

import backend.instruction.Instruction;
import backend.instruction.variable.StoreInstruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.ArrayValue;
import backend.value.DoubleValue;
import backend.value.Value;

public class SetElementInstruction extends Instruction {
	public SetElementInstruction() {
	}
	
	public static Instruction GetElementInstruction() {
		return new SetElementInstruction();
	}
	
	public Instruction copy() {
		return new SetElementInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		runtime.checkDoubleValue(stack.peekValue(1));
		runtime.checkArrayValue(stack.peekValue(2));
		Value value = runtime.getStack().popValue(false, true);
		DoubleValue index = runtime.checkDoubleValue(stack.popValue(false, true));
		ArrayValue array = runtime.checkArrayValue(stack.popValue(false, true));
		array.set(index, value);
	}
	
	public void undo(Runtime runtime) {
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "SETELEMENT";
	}
}