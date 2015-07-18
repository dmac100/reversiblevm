package instruction;

import runtime.Runtime;
import runtime.Stack;
import value.DoubleValue;
import value.Value;

public class UnaryMinusInstruction implements Instruction {
	public UnaryMinusInstruction() {
	}
	
	public static Instruction UnaryMinus() {
		return new UnaryMinusInstruction();
	}
	
	public void execute(Runtime runtime) {
		Stack stack = runtime.getStack();
		Value value = stack.popValue();
		if(value instanceof DoubleValue) {
			double x = ((DoubleValue)value).getValue();
			stack.push(DoubleValue.Value(-x));
		} else {
			runtime.throwError("TypeError: Not a double");
		}
	}
	
	public String toString() {
		return "UNARYMINUS";
	}
}