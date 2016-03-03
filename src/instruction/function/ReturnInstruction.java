package instruction.function;

import instruction.Instruction;
import runtime.ExecutionException;
import runtime.Runtime;
import runtime.Stack;
import value.DoubleValue;
import value.Value;

public class ReturnInstruction implements Instruction {
	public ReturnInstruction() {
	}
	
	public static Instruction Return() {
		return new ReturnInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
		runtime.popStackFrame();
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "RETURN";
	}
}