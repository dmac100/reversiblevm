package backend.instruction.operator;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;
import backend.runtime.Stack;
import backend.value.DoubleValue;
import backend.value.StringValue;
import backend.value.Value;

public class AddInstruction extends Instruction {
	public AddInstruction() {
	}
	
	public static Instruction Add() {
		return new AddInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value2 = stack.popValue(false, true);
		Value value1 = stack.popValue(false, true);
		if(value1 instanceof StringValue || value2 instanceof StringValue) {
			String value = value1.toString() + value2.toString();
			stack.push(StringValue.Value(value), false);
		} else {
			double sum = runtime.checkDoubleValue(value1).getValue() + runtime.checkDoubleValue(value2).getValue();
			stack.push(DoubleValue.Value(sum), false);
		}
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false, false);
		runtime.getUndoStack().undoPopValue(runtime);
		runtime.getUndoStack().undoPopValue(runtime);
	}
	
	public String toString() {
		return "ADD";
	}
}