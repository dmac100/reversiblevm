package instruction;

import runtime.ExecutionException;
import runtime.Runtime;

public class NopInstruction implements Instruction {
	public NopInstruction() {
	}
	
	public static Instruction Nop() {
		return new NopInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
	}
	
	public String toString() {
		return "NOP";
	}
}