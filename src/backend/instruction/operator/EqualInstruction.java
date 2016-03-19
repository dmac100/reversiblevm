package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.runtime.ExecutionException;
import backend.runtime.Runtime;
import backend.runtime.Stack;
import backend.value.BooleanValue;
import backend.value.DoubleValue;
import backend.value.FunctionValue;
import backend.value.NativeFunctionValue;
import backend.value.NullValue;
import backend.value.StringValue;
import backend.value.Value;

public class EqualInstruction extends Instruction {
	public EqualInstruction() {
	}
	
	public static Instruction Equal() {
		return new EqualInstruction();
	}
	
	public Instruction copy() {
		return new EqualInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value1 = stack.popValue(false, true);
		Value value2 = stack.popValue(false, true);
		stack.push(new BooleanValue(equals(value1, value2)), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public static boolean equals(Value value1, Value value2) {
		return value1.getKey().equals(value2.getKey());
	}
	
	public String toString() {
		return super.toString() + "EQUAL";
	}
}