package instruction;

import runtime.ExecutionException;
import runtime.Runtime;

public class GreaterThanEqualInstruction implements Instruction {
	public GreaterThanEqualInstruction() {
	}
	
	public static Instruction GreaterThanEqual() {
		return new GreaterThanEqualInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
	}
	
	public String toString() {
		return "GREATERTHANEQUAL";
	}
}