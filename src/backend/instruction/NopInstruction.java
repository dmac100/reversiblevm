package backend.instruction;

import backend.runtime.ExecutionException;
import backend.runtime.Runtime;

public class NopInstruction implements Instruction {
	public NopInstruction() {
	}
	
	public static Instruction Nop() {
		return new NopInstruction();
	}
	
	public void execute(Runtime runtime) throws ExecutionException {
	}
	
	public void undo(Runtime runtime) {
	}
	
	public String toString() {
		return "NOP";
	}
}