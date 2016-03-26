package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.BooleanValue;
import backend.value.DoubleValue;
import backend.value.StringValue;
import backend.value.Value;

public class LessThanInstruction extends Instruction {
	public LessThanInstruction() {
	}
	
	public static Instruction LessThan() {
		return new LessThanInstruction();
	}
	
	public Instruction copy() {
		return new LessThanInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value1 = stack.peekValue(0);
		Value value2 = stack.peekValue(1);
		if(value1 instanceof StringValue && value2 instanceof StringValue) {
			StringValue stringValue2 = runtime.checkStringValue(stack.popValue(false, true));
			StringValue stringValue1 = runtime.checkStringValue(stack.popValue(false, true));
			stack.push(BooleanValue.Value(stringValue1.getValue().compareTo(stringValue2.getValue()) < 0), false);
		} else {
			runtime.checkDoubleValue(value1);
			runtime.checkDoubleValue(value2);
			DoubleValue doubleValue2 = runtime.checkDoubleValue(stack.popValue(false, true));
			DoubleValue doubleValue1 = runtime.checkDoubleValue(stack.popValue(false, true));
			stack.push(BooleanValue.Value(doubleValue1.getValue() < doubleValue2.getValue()), false);
		}
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return super.toString() + "LESSTHAN";
	}
}