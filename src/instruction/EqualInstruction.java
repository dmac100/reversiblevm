package instruction;

import runtime.ExecutionException;
import runtime.Runtime;

public class EqualInstruction implements Instruction {
	public EqualInstruction() {
	}
	
	public static Instruction Equal() {
		return new EqualInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
	}
	
	public String toString() {
		return "EQUAL";
	}
}