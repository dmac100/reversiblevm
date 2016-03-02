package instruction;

import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.BooleanValue;
import value.DoubleValue;
import value.FunctionValue;
import value.NativeFunctionValue;
import value.NullValue;
import value.StringValue;
import value.Value;

public class EqualInstruction implements Instruction {
	public EqualInstruction() {
	}
	
	public static Instruction Equal() {
		return new EqualInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		Stack stack = runtime.getStack();
		Value value1 = stack.popValue(true);
		Value value2 = stack.popValue(true);
		stack.push(new BooleanValue(equals(value1, value2)), false);
	}
	
	public void undo(Runtime runtime) {
		runtime.getStack().popValue(false);
	}
	
	public static boolean equals(Value value1, Value value2) {
		if(value1 instanceof BooleanValue && value2 instanceof BooleanValue) {
			return ((BooleanValue)value1).getValue() == ((BooleanValue)value2).getValue();
		} else if(value1 instanceof DoubleValue && value2 instanceof DoubleValue) {
			return ((DoubleValue)value1).getValue() == ((DoubleValue)value2).getValue();
		} else if(value1 instanceof FunctionValue && value2 instanceof FunctionValue) {
			return ((FunctionValue)value1 == (FunctionValue)value2);
		} else if(value1 instanceof NativeFunctionValue && value2 instanceof NativeFunctionValue) {
			return ((NativeFunctionValue)value1 == (NativeFunctionValue)value2);
		} else if(value1 instanceof NullValue && value2 instanceof NullValue) {
			return true;
		} else if(value1 instanceof StringValue && value2 instanceof StringValue) {
			return ((StringValue)value1).getValue().equals(((StringValue)value2).getValue());
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "EQUAL";
	}
}