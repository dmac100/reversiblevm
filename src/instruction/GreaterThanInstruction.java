package instruction;

import runtime.ExecutionException;
import runtime.Runtime;

public class GreaterThanInstruction implements Instruction {
	public GreaterThanInstruction() {
	}
	
	public static Instruction GreaterThan() {
		return new GreaterThanInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
	}
	
	public String toString() {
		return "GREATERTHAN";
	}
}