package instruction;

import runtime.ExecutionException;
import runtime.Runtime;

public class LessThanInstruction implements Instruction {
	public LessThanInstruction() {
	}
	
	public static Instruction LessThan() {
		return new LessThanInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
	}
	
	public String toString() {
		return "LESSTHAN";
	}
}