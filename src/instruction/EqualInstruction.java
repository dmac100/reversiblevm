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
		Value value1 = stack.popValue();
		Value value2 = stack.popValue();
		if(value1 instanceof BooleanValue && value2 instanceof BooleanValue) {
			stack.push(BooleanValue.Value(((BooleanValue)value1).getValue() == ((BooleanValue)value2).getValue()));
		} else if(value1 instanceof DoubleValue && value2 instanceof DoubleValue) {
			stack.push(BooleanValue.Value(((DoubleValue)value1).getValue() == ((DoubleValue)value2).getValue()));
		} else if(value1 instanceof FunctionValue && value2 instanceof FunctionValue) {
			stack.push(BooleanValue.Value((FunctionValue)value1 == (FunctionValue)value2));
		} else if(value1 instanceof NativeFunctionValue && value2 instanceof NativeFunctionValue) {
			stack.push(BooleanValue.Value((NativeFunctionValue)value1 == (NativeFunctionValue)value2));
		} else if(value1 instanceof NullValue && value2 instanceof NullValue) {
			stack.push(BooleanValue.Value(true));
		} else if(value1 instanceof StringValue && value2 instanceof StringValue) {
			stack.push(BooleanValue.Value(((StringValue)value1).getValue().equals(((StringValue)value2).getValue())));
		} else {
			stack.push(BooleanValue.Value(false));
		}
	}
	
	public String toString() {
		return "EQUAL";
	}
}