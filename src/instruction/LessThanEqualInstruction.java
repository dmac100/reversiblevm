package instruction;

import runtime.ExecutionException;
import runtime.Runtime;

public class LessThanEqualInstruction implements Instruction {
	public LessThanEqualInstruction() {
	}
	
	public static Instruction LessThanEqual() {
		return new LessThanEqualInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
	}
	
	public String toString() {
		return "LESSTHANEQUAL";
	}
}