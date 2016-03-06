package backend.instruction.function;

import backend.instruction.Instruction;
import backend.runtime.Runtime;
import backend.runtime.ExecutionException;

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